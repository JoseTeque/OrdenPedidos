package joseguerra.ordereat;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.Objects;

import joseguerra.ordereat.common.Common;
import joseguerra.ordereat.modelo.User;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class SignUpActivity extends AppCompatActivity {

    private MaterialEditText edtxPhone, edtxName, edtxPassord,edtxSecureCode;
    private Button btnSignUP;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        edtxPhone = findViewById(R.id.IdPhone);
        edtxName = findViewById(R.id.IdNombre);
        edtxPassord = findViewById(R.id.IdPassword);
        edtxSecureCode = findViewById(R.id.IdCodeSecure);

        btnSignUP = findViewById(R.id.IdSignUpp);

        // añadir firebase a database
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        final DatabaseReference myRef = database.getReference("user");

        btnSignUP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Common.isConectInter(getBaseContext())) {

                    final ProgressDialog dialog = new ProgressDialog(SignUpActivity.this);
                    dialog.setMessage("Cargando.....!!");
                    dialog.show();

                    myRef.addValueEventListener(new ValueEventListener() {
                        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            // chequear si existe el numero de cel en la base de datos


                            if (dataSnapshot.child(Objects.requireNonNull(edtxPhone.getText()).toString()).exists()) {
                                Toast.makeText(SignUpActivity.this, "El numero se encuentra registrado en la base", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            } else {
                                dialog.dismiss();
                                User user = new User(Objects.requireNonNull(edtxName.getText()).toString(), Objects.requireNonNull(edtxPassord.getText()).toString(), Objects.requireNonNull(edtxSecureCode.getText()).toString());
                                myRef.child(edtxPhone.getText().toString()).setValue(user);
                                Toast.makeText(SignUpActivity.this, "Usuario registrado con exito", Toast.LENGTH_SHORT).show();
                                finish();

                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                } else {
                    Toast.makeText(SignUpActivity.this, "Check la conection a internet", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }
}


