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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StatusActivity extends AppCompatActivity
{
    private Toolbar mToolbar ;
    private Button saveChangesButton ;
    private EditText statusInput ;
    private DatabaseReference ChangeStatusReference ;
    private FirebaseAuth mAuth ;
    private ProgressDialog loadingBar ;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        mAuth = FirebaseAuth.getInstance();
        String user_Id = mAuth.getCurrentUser().getUid();
        ChangeStatusReference = FirebaseDatabase.getInstance().getReference().child("Users").child(user_Id);

        mToolbar = (Toolbar) findViewById(R.id.status_app_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Change Status");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        saveChangesButton = (Button) findViewById(R.id.save_status_change_button);
        statusInput = (EditText) findViewById(R.id.status_input);
        loadingBar = new ProgressDialog(this);

        String old_status_user = getIntent().getExtras().get("user_status").toString();
        statusInput.setText(old_status_user);

        saveChangesButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String new_status = statusInput.getText().toString();

                ChangeProfileStatus(new_status);
            }
        });
    }

    private void ChangeProfileStatus(String new_status)
    {
        if (TextUtils.isEmpty(new_status))
        {
            Toast.makeText(StatusActivity.this, "Please Write Your Status", Toast.LENGTH_SHORT).show();
        }

        else
        {
            loadingBar.setTitle("Change Proifle Status");
            loadingBar.setMessage("Please Wait While Updating...");
            loadingBar.show();

            ChangeStatusReference.child("user_status").setValue(new_status).addOnCompleteListener(new OnCompleteListener<Void>()
            {
                @Override
                public void onComplete(@NonNull Task<Void> task)
                {
                    if (task.isSuccessful())
                    {
                        loadingBar.dismiss();

                        Intent ayush = new Intent(StatusActivity.this, SettingsActivity.class);
                        startActivity(ayush);

                        Toast.makeText(StatusActivity.this, "Proifle Status Updated Successfully...",
                                Toast.LENGTH_LONG).show();
                    }

                    else
                    {
                        Toast.makeText(StatusActivity.this, "Error Occurred...",
                                Toast.LENGTH_LONG).show();
                    }
                }
            }) ;
        }
    }
}
