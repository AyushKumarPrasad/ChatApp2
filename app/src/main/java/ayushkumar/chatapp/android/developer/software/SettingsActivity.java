package ayushkumar.chatapp.android.developer.software;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;


public class SettingsActivity extends AppCompatActivity
{
    private CircleImageView settingsDisplayProfileImage ;
    private TextView settingsDisplayname ;
    private TextView settingsDisplaystatus ;
    private Button settingsChnageProfileImageButton ;
    private Button settingsChangeStatusButton ;

    private final static int Gallery_pic = 1 ;

    private StorageReference storeProfileImagestorageRef ;
    private DatabaseReference getUserDataReference ;
    private FirebaseAuth mAuth ;

    private ProgressDialog loadingBar;

//  public static final String DATA_PATH = "Users";

    Bitmap thumb_bitmap = null;

    private StorageReference thumbImageRef ;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mAuth = FirebaseAuth.getInstance();
        String online_user_Id = mAuth.getCurrentUser().getUid();

        getUserDataReference = FirebaseDatabase.getInstance().getReference().child("Users").child(online_user_Id);

        getUserDataReference.keepSynced(true);

        storeProfileImagestorageRef = FirebaseStorage.getInstance().getReference().child("Profile_Images");

        thumbImageRef = FirebaseStorage.getInstance().getReference().child("Thumb_Images");

        settingsDisplayProfileImage = (CircleImageView) findViewById(R.id.settings_profile_image);
        settingsDisplayname = (TextView) findViewById(R.id.settings_username);
        settingsDisplaystatus = (TextView) findViewById(R.id.settings_user_status);
        settingsChnageProfileImageButton = (Button) findViewById(R.id.settings_change_profile_image_button);
        settingsChangeStatusButton = (Button) findViewById(R.id.settings_change_profile_status_button);
        loadingBar = new ProgressDialog(this);

        getUserDataReference.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                String name = dataSnapshot.child("user_name").getValue().toString();
                String status = dataSnapshot.child("user_status").getValue().toString();

                final String image = dataSnapshot.child("user_image").getValue().toString();

                String thumb_image = dataSnapshot.child("user_thumb_image").getValue().toString();

                settingsDisplayname.setText(name);
                settingsDisplaystatus.setText(status);

                if (!image.equals("default_profile"))
                {
              //      Picasso.with(SettingsActivity.this).load(image)
                    // .placeholder(R.drawable.defaultimage).into(settingsDisplayProfileImage);

                    Picasso.with(SettingsActivity.this).load(image).networkPolicy(NetworkPolicy.OFFLINE)
                     .placeholder(R.drawable.defaultimage).into(settingsDisplayProfileImage, new Callback()
                    {
                        @Override
                        public void onSuccess()
                        {

                        }

                        @Override
                        public void onError()
                        {
                            Picasso.with(SettingsActivity.this).load(image)
                             .placeholder(R.drawable.defaultimage).into(settingsDisplayProfileImage);
                        }
                    });


                }

             }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        }) ;

        settingsChnageProfileImageButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, Gallery_pic);
            }
        });

        settingsChangeStatusButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String old_status = settingsDisplaystatus.getText().toString();
                Intent ayush = new Intent(SettingsActivity.this, StatusActivity.class);
                ayush.putExtra("user_status",old_status);
                startActivity(ayush);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Gallery_pic && resultCode == RESULT_OK && data != null)
        {
            Uri ImageUri = data.getData();

            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK)
            {
                loadingBar.setTitle("Updating Profile Image..");
                loadingBar.setMessage("Please Wait...");
                loadingBar.show();

                Uri resultUri = result.getUri();

                File thumb_filePathUri = new File(resultUri.getPath());

                String user_Id = mAuth.getCurrentUser().getUid();

                try {
                    thumb_bitmap = new Compressor(this)
                            .setMaxWidth(200)
                            .setMaxHeight(200)
                            .setQuality(50)
                            .compressToBitmap(thumb_filePathUri);
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                thumb_bitmap.compress(Bitmap.CompressFormat.JPEG,50, byteArrayOutputStream);
                final byte [] thumb_byte = byteArrayOutputStream.toByteArray();

                StorageReference filepath = storeProfileImagestorageRef.child(user_Id + ".jpg");

                final StorageReference thumb_filepath = thumbImageRef.child(user_Id + ".jpg");

                filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>()
                {
                    @Override
                    public void onComplete(@NonNull final Task<UploadTask.TaskSnapshot> task)
                    {
                        if (task.isSuccessful())
                        {
                            Toast.makeText(SettingsActivity.this, "Saving Profile Image" , Toast.LENGTH_LONG).show();

                                final String downaloadUrl = task.getResult().getDownloadUrl().toString();

                                UploadTask uploadTask = thumb_filepath.putBytes(thumb_byte);

                                uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>()
                                {
                                    @Override
                                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumb_task)
                                    {
                                        String thumb_downloadUrl = thumb_task.getResult().getDownloadUrl().toString();

                                        if (task.isSuccessful())
                                        {
                                            Map update_user_data = new HashMap();
                                            update_user_data.put("user_image",downaloadUrl);
                                            update_user_data.put("user_thumb_image", thumb_downloadUrl);

                                            getUserDataReference.updateChildren(update_user_data).
                                                    addOnCompleteListener(new OnCompleteListener<Void>()
                                                    {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task)
                                                        {
                                                            Toast.makeText(SettingsActivity.this,
                                                                    "Profile Image Uploaded Successfully" ,
                                                                    Toast.LENGTH_LONG).show();

                                                            loadingBar.dismiss();
                                                        }
                                                    }) ;
                                        }
                                    }
                                });




                        }

                        else
                        {
                            Toast.makeText(SettingsActivity.this, "Error While Uploading" , Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                        }
                    }
                }) ;
            }

            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE)
            {
                Exception error = result.getError();
            }
        }
    }
}
