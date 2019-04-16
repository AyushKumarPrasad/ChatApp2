package ayushkumar.chatapp.android.developer.software;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class RegisterActivity extends AppCompatActivity
{
    private Toolbar mToolbar ;
    private EditText Rusername, Remail, Rpass ;
    private Button CreateAccount ;
    private ProgressDialog loadingbar ;

    private FirebaseAuth mAuth ;
    private DatabaseReference storeUserDefaultDataReference ;
 //   public static final String DATA_PATH = "Users";
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        mToolbar = (Toolbar) findViewById(R.id.register_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Sign Up");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Rusername = (EditText) findViewById(R.id.register_name);
        Remail = (EditText) findViewById(R.id.register_email);
        Rpass = (EditText) findViewById(R.id.register_password);
        loadingbar = new ProgressDialog(this);

        CreateAccount = (Button) findViewById(R.id.create_account_button);
        CreateAccount.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                    final String name = Rusername.getText().toString();
                    String email = Remail.getText().toString();
                    String password = Rpass.getText().toString();
                    RegisterAccount(name, email, password);
            }
        });
    }

    private void RegisterAccount(final String name, String email, String password)
    {
        if (TextUtils.isEmpty(name))
        {
            Toast.makeText(RegisterActivity.this, "please write your NAME", Toast.LENGTH_LONG).show();
        }

        if (TextUtils.isEmpty(email))
        {
            Toast.makeText(RegisterActivity.this, "please write your EMAIL", Toast.LENGTH_LONG).show();
        }

        if (TextUtils.isEmpty(password))
        {
            Toast.makeText(RegisterActivity.this, "please write your PASSWORD", Toast.LENGTH_LONG).show();
        }

        else
        {
            loadingbar.setTitle("Creating New Account");
            loadingbar.setMessage("Please Wait...");
            loadingbar.show();

            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>()
            {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task)
                {
                    if (task.isSuccessful())
                    {
                        String DeviceToken = FirebaseInstanceId.getInstance().getToken();
                        String current_user_Id = mAuth.getCurrentUser().getUid();

                        storeUserDefaultDataReference = FirebaseDatabase.getInstance().getReference().child("Users").child(current_user_Id);

                        storeUserDefaultDataReference.child("user_name").setValue(name);
                        storeUserDefaultDataReference.child("user_status").setValue("Welcome to Ayush Chat App");
                        storeUserDefaultDataReference.child("user_image").setValue("default_profile");
                        storeUserDefaultDataReference.child("device_token").setValue(DeviceToken);
                        storeUserDefaultDataReference.child("user_thumb_image").setValue("default_image")
                                .addOnCompleteListener(new OnCompleteListener<Void>()
                                {
                            @Override
                            public void onComplete(@NonNull Task<Void> task)
                            {
                                if (task.isSuccessful())
                                {
                                    Intent SiyaRam = new Intent(RegisterActivity.this, MainActivity.class);
                                    SiyaRam.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(SiyaRam);
                                    finish();
                                }
                            }
                        });
                    }
                    else
                    {
                        Toast.makeText(RegisterActivity.this, "Error Occured Try Again..", Toast.LENGTH_SHORT).show();
                    }

                    loadingbar.dismiss();
                }
            }) ;
        }
    }
}
