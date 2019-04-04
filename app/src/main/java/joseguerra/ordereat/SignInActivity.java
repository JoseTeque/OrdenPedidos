package joseguerra.ordereat;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.rey.material.widget.CheckBox;

import java.util.Objects;

import io.paperdb.Paper;
import joseguerra.ordereat.common.Common;
import joseguerra.ordereat.modelo.User;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SignInActivity extends AppCompatActivity {

    private MaterialEditText edtxphone, edtxPassword;
    private Button btnSign;
    private CheckBox checkBoxRemember;
    private TextView txtForgotPass;

    FirebaseDatabase database;
   DatabaseReference myRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        edtxphone=findViewById(R.id.IdPhone);

        edtxPassword=findViewById(R.id.IdPassword);

        checkBoxRemember=findViewById(R.id.Id_CheckBox_Remember);

        btnSign=findViewById(R.id.IdSignIn);

       txtForgotPass=findViewById(R.id.txtOlvidoPass);

        //Init PAPER

        Paper.init(this);

        // añadir firebase a database
      database = FirebaseDatabase.getInstance();

       myRef = database.getReference("user");

        txtForgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showForgotDialog();
            }
        });

        btnSign.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {

                if (Common.isConectInter(getBaseContext())) {

                    //Save user & password with checkbox

                    if (checkBoxRemember.isChecked())
                    {
                        Paper.book().write(Common.User_key, Objects.requireNonNull(edtxphone.getText()).toString());
                        Paper.book().write(Common.Password_key, Objects.requireNonNull(edtxPassword.getText()).toString());
                    }


                    final ProgressDialog dialog = new ProgressDialog(SignInActivity.this);
                    dialog.setMessage("Cargando.....!!");
                    dialog.show();

                    myRef.addListenerForSingleValueEvent(new ValueEventListener() {

                        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            //chequear si el usuario no existe en el database

                            if (dataSnapshot.child((Objects.requireNonNull(edtxphone.getText())).toString()).exists()) {
                                dialog.dismiss();
                                // get user informacion de la base.
                                User user = dataSnapshot.child(edtxphone.getText().toString()).getValue(User.class);
                                assert user != null;
                                user.setPhone(edtxphone.getText().toString()); // set phone
                                if (user.getPassword().equals((Objects.requireNonNull(edtxPassword.getText())).toString())) {
                                    Intent homeintent = new Intent(SignInActivity.this, HomeActivity.class);
                                    Common.currentUser = user;
                                    startActivity(homeintent);
                                    finish();

                                    myRef.removeEventListener(this);


                                } else {
                                    Toast.makeText(SignInActivity.this, "Sign In fallido ", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                }
                            } else {
                                Toast.makeText(SignInActivity.this, "El usuario no existe en la base de datos ", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();

                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }else
                {
                    Toast.makeText(SignInActivity.this, "Check la conection a internet", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private void showForgotDialog() {

        final AlertDialog.Builder builder= new AlertDialog.Builder(this);
        builder.setTitle("Forgot Password");
        builder.setMessage("Enter your secure code");

        LayoutInflater inflater= this.getLayoutInflater();
        @SuppressLint("InflateParams") View forgot_view= inflater.inflate(R.layout.forgot_password,null);

        builder.setView(forgot_view);
        builder.setIcon(R.drawable.ic_security_black_24dp);

        final MaterialEditText edtxPhone= forgot_view.findViewById(R.id.IdPhone);
        final MaterialEditText edtxSecure= forgot_view.findViewById(R.id.IdSecure);

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                 //check if user available

                myRef.addValueEventListener(new ValueEventListener() {
                    @TargetApi(Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            User user = dataSnapshot.child(Objects.requireNonNull(edtxPhone.getText()).toString())
                                    .getValue(User.class);

                        assert user != null;
                        if (user.getSecureCode().equals(Objects.requireNonNull(edtxSecure.getText()).toString()))
                                Toast.makeText(SignInActivity.this, "Your Password is: " + user.getPassword(), Toast.LENGTH_LONG).show();
                            else
                                Toast.makeText(SignInActivity.this, "Wrong secure code", Toast.LENGTH_SHORT).show();
                        }


                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }
        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.show();

    }

}
