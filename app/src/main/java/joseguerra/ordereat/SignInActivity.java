package joseguerra.ordereat;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import joseguerra.ordereat.modelo.User;

public class SignInActivity extends AppCompatActivity {

    private MaterialEditText edtxphone, edtxPassword;
    private Button btnSign;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        edtxphone=findViewById(R.id.IdPhone);
        edtxPassword=findViewById(R.id.IdPassword);

        btnSign=findViewById(R.id.IdSignIn);

        // añadir firebase a database
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        final DatabaseReference myRef = database.getReference("user");

        btnSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final ProgressDialog dialog= new ProgressDialog(SignInActivity.this);
                dialog.setMessage("Cargando.....!!");
                dialog.show();

                myRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        //chequear si el usuario no existe en el database

                          if (dataSnapshot.child(edtxphone.getText().toString()).exists()) {
                              // get user informacion de la base.
                              User user = dataSnapshot.child(edtxphone.getText().toString()).getValue(User.class);
                              if (user.getPassword().equals(edtxPassword.getText().toString())) {
                                  Toast.makeText(SignInActivity.this, "Sign In succes", Toast.LENGTH_SHORT).show();
                                  dialog.dismiss();

                              } else {
                                  Toast.makeText(SignInActivity.this, "Sign In fallido ", Toast.LENGTH_SHORT).show();
                                  dialog.dismiss();
                              }
                          }else {
                              Toast.makeText(SignInActivity.this, "El usuario no existe en la base de datos ", Toast.LENGTH_SHORT).show();
                              dialog.dismiss();

                          }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

    }
}
