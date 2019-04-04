package joseguerra.ordereat;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitCallback;
import com.facebook.accountkit.AccountKitError;
import com.facebook.accountkit.AccountKitLoginResult;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;
import joseguerra.ordereat.common.Common;
import joseguerra.ordereat.modelo.User;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 99;
    private Button btnContinue;
    private TextView txtSlogan;

    FirebaseDatabase database;
    DatabaseReference users;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //note: add this code before setContentview method

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/restaurant_font.otf")
                .setFontAttrId(R.attr.fontPath)
                .build());
        setContentView(R.layout.activity_main);

        //firebase
        database= FirebaseDatabase.getInstance();
        users= database.getReference("user");

        printKeyHash(); // obtener el keyhash de facebook


        btnContinue= findViewById(R.id.Id_Btn_continue);


        txtSlogan=findViewById(R.id.IdTxtSlogan);

        Typeface face= Typeface.createFromAsset(getAssets(),"fonts/NABILA.TTF");
        txtSlogan.setTypeface(face);


        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                   StarLoginSystem();

            }
        });

     //check sesion facebook kit
        if (AccountKit.getCurrentAccessToken()!=null)
        {
            //create dialog
            //show dialog
            final AlertDialog dialog= new SpotsDialog.Builder().setContext(MainActivity.this).build();
            dialog.show();
            dialog.setMessage("Please wait");
            dialog.setCancelable(false);

            AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
                @Override
                public void onSuccess(Account account) {
                    users.child(account.getPhoneNumber().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            User loginLocal= dataSnapshot.getValue(User.class);
                            //copy code LoginActivity
                            Intent homeintent = new Intent(MainActivity.this, ListaRestaurantesActivity.class);
                            Common.currentUser = loginLocal;
                            startActivity(homeintent);

                            //dialog dismiss
                            dialog.dismiss();
                            finish();

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }

                @Override
                public void onError(AccountKitError accountKitError) {

                }
            });
        }

    }

    private void StarLoginSystem() {
        Intent intent= new Intent(MainActivity.this, AccountKitActivity.class);
        AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder=
                new AccountKitConfiguration.AccountKitConfigurationBuilder(LoginType.PHONE,AccountKitActivity.ResponseType.TOKEN);
        intent.putExtra(AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION,configurationBuilder.build());
        startActivityForResult(intent,REQUEST_CODE);
    }

    private void printKeyHash() {

        try {
            @SuppressLint("PackageManagerGetSignatures") PackageInfo info= getPackageManager().getPackageInfo("joseguerra.ordereat",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature:info.signatures)
            {
                MessageDigest md= MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("keyHash", Base64.encodeToString(md.digest(),Base64.DEFAULT));
            }

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode== REQUEST_CODE)
        {
            assert data != null;
            AccountKitLoginResult loginResult= data.getParcelableExtra(AccountKitLoginResult.RESULT_KEY);
            if (loginResult.getError()!=null)
            {
                Toast.makeText(this, " "+ loginResult.getError().getErrorType().getMessage(), Toast.LENGTH_SHORT).show();
            }
            else if (loginResult.wasCancelled())
            {
                Toast.makeText(this, "Cancel ", Toast.LENGTH_SHORT).show();
            }
            else
            {
                if (loginResult.getAccessToken()!=null)
                {
                    //show dialog
                    final AlertDialog dialog= new SpotsDialog.Builder().setContext(MainActivity.this).build();
                    dialog.show();
                    dialog.setMessage("Please wait");
                    dialog.setCancelable(false);

                    //get Current phone
                    AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
                        @Override
                        public void onSuccess(Account account) {
                            final String phone= account.getPhoneNumber().toString();
                            //check if exists en firebase user
                            users.orderByKey().equalTo(phone).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (!dataSnapshot.child(phone).exists())//if not exists
                                    {
                                        //we will create new user and login
                                        User newUser= new User();
                                        newUser.setPhone(phone);
                                        newUser.setNombre("");

                                        //Add to firebase

                                        users.child(phone).setValue(newUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(MainActivity.this, "User register successful", Toast.LENGTH_SHORT).show();

                                                    //Login

                                                    users.child(phone).addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            User loginLocal= dataSnapshot.getValue(User.class);
                                                            //copy code LoginActivity
                                                            Intent homeintent = new Intent(MainActivity.this, ListaRestaurantesActivity.class);
                                                            Common.currentUser = loginLocal;
                                                            startActivity(homeintent);

                                                            //dialog dismiss
                                                            dialog.dismiss();
                                                            finish();

                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                                        }
                                                    });
                                                }
                                            }
                                        });

                                    }else // if user exists
                                    {
                                        users.child(phone).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                User loginLocal= dataSnapshot.getValue(User.class);
                                                //copy code LoginActivity
                                                Intent homeintent = new Intent(MainActivity.this, ListaRestaurantesActivity.class);
                                                Common.currentUser = loginLocal;
                                                startActivity(homeintent);

                                                //dialog dismiss
                                                dialog.dismiss();
                                                finish();

                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }

                        @Override
                        public void onError(AccountKitError accountKitError) {
                            Toast.makeText(MainActivity.this, ""+accountKitError.getErrorType().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }
    }
}
