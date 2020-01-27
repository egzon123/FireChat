package com.example.firechat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Scroller;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

public class ChatActivity extends AppCompatActivity {

    private FloatingActionButton fab;
    private EditText input;
    private MessageAdapter messageAdapter;
    private String nameFromEmail, room_name;
    StorageReference storageReference;
    private DatabaseReference root;
    private String temp_key;
    private ListView listView;
    Uri filePath;
    ImageView imgProfilePic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        nameFromEmail = user.getEmail().split("@")[0];
        room_name = getIntent().getExtras().get("room_name").toString();
        setTitle(" Room - " + room_name);
        storageReference = FirebaseStorage.getInstance().getReference().child("profile_images");
        fab = findViewById(R.id.fab);
        input = findViewById(R.id.input);
        input.setScroller(new Scroller(this));
        input.setMaxLines(5);
        input.setVerticalScrollBarEnabled(true);
        input.setMovementMethod(new ScrollingMovementMethod());
        listView = findViewById(R.id.list_chat);
        root = FirebaseDatabase.getInstance().getReference().child(room_name);
        showAllOldMessages();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (input.getText().toString().trim().equals("")) {
                    Toasty.warning(ChatActivity.this, "Please enter some texts!", Toast.LENGTH_SHORT).show();
                } else {
                    FirebaseDatabase.getInstance()
                            .getReference().child(room_name)
                            .push()
                            .setValue(new ChatMessage(input.getText().toString().trim(),
                                    nameFromEmail,
                                    FirebaseAuth.getInstance().getCurrentUser().getUid(), LoginActivity.getAuthUserName())
                            );
                    input.setText("");
                }
            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menucontext, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sign_out:
                logOut();
                break;
            case R.id.upload_profile_img:
                grabImage();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return false;
    }

    public void logOut() {
        FirebaseAuth.getInstance().signOut();
        LoginActivity.signInClient.signOut().addOnCompleteListener(this, task -> {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        });
        finish();
    }


    private String loggedInUserName = "";

    private void showAllOldMessages() {
        loggedInUserName = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Log.d("Main", "user id: " + loggedInUserName);
        System.out.println("Main" + "user id: " + loggedInUserName + "  --------------------");

        messageAdapter = new MessageAdapter(this, ChatMessage.class, R.layout.their_message,
                FirebaseDatabase.getInstance().getReference().child(room_name));
        listView.setAdapter(messageAdapter);
    }

    public String getLoggedInUserName() {
        return loggedInUserName;
    }


    public void grabImage() {
        /*
        Build a dialogView for user to set profile image
         */
        AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.profile_image, null);
        builder.setView(dialogView);

        final AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();

        Button btnClose = dialog.findViewById(R.id.btnClose);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        imgProfilePic = dialog.findViewById(R.id.imgProfilePic);
        imgProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Use android-image-cropper library to grab and crop image
                CropImage.activity()
                        .setFixAspectRatio(true)
                        .start(ChatActivity.this);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                filePath = result.getUri();
                uploadImage();
                imgProfilePic.setImageURI(filePath);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    public void uploadImage() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading...");
        progressDialog.show();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final StorageReference ref = FirebaseStorage.getInstance().getReference().child("profile_images").child(userId);
        ref.putFile(filePath)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        taskSnapshot.getMetadata().getReference().getDownloadUrl()
                                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String url = uri.toString();
                                        Map<String, String> imageLocation = new HashMap<>();
                                        imageLocation.put("a6_imageUrl", url);
                                        storageReference.putFile(uri);
//                                        database.collection("users").document(userId).set(imageLocation);
                                        progressDialog.dismiss();
                                        Toast.makeText(getApplicationContext(), "Image saved", Toast.LENGTH_SHORT).show();
                                        imgProfilePic.setImageURI(filePath);
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                .getTotalByteCount());
                        progressDialog.setMessage((int) progress + "%" + " completed");
                    }
                })
        ;
    }
}
