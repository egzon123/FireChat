package com.example.firechat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firestore.v1.WriteResult;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    static final int GOOGLE_SIGN = 123;
    private static final String TAG = "com.example.firechat";
    FirebaseAuth firebaseAuth;
    Button btn_login;
    TextView textView;
    static GoogleSignInClient signInClient;
    ProgressBar progressBar;
    ImageView imageView;
    FirebaseFirestore db;
    static String logedUserName;
    boolean hasUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        btn_login = findViewById(R.id.login);
        textView = findViewById(R.id.text1);
        progressBar = findViewById(R.id.progress_circular);
        imageView = findViewById(R.id.image);
        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions
                .Builder()
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        signInClient = GoogleSignIn.getClient(this, googleSignInOptions);
        btn_login.setOnClickListener(v -> signInGoogle());
    }

    void signInGoogle() {
        progressBar.setVisibility(View.VISIBLE);
        Intent signIntent = signInClient.getSignInIntent();
        startActivityForResult(signIntent, GOOGLE_SIGN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GOOGLE_SIGN) {
            Task<GoogleSignInAccount> task = GoogleSignIn
                    .getSignedInAccountFromIntent(data);

            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);

                if (account != null) {
                    firebaseAuthWithGoogle(account);
                }
            } catch (ApiException e) {
                e.printStackTrace();
            }
        }

    }


    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {

        AuthCredential authCredential = GoogleAuthProvider
                .getCredential(account.getIdToken(), null);

        firebaseAuth.signInWithCredential(authCredential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        progressBar.setVisibility(View.INVISIBLE);
                        Log.d("TAG", "Signin success");
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        userHasUserName(user);
                    } else {
                        progressBar.setVisibility(View.INVISIBLE);
                        Log.w("TAG", "Sigin failure", task.getException());
                        Toast.makeText(this, "SignIn Failed!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void userHasUserName(FirebaseUser firebaseUser) {

        db.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (document.getString("uuid").equals(firebaseUser.getUid())) {
                            hasUserName = true;
                            logedUserName = document.getString("userName");
                            showRoomActivity();
                            return;
                        }

                    }
                    if (hasUserName == false) {
                        request_user_name();
                    }

                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });

    }


    public static String getAuthUserName() {
        return logedUserName;
    }

    public void showRoomActivity() {
        Intent intent = new Intent(this, ChatRooms.class);
        startActivity(intent);
    }

    private void request_user_name() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter name:");

        final EditText input_field = new EditText(this);

        builder.setView(input_field);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                String uuid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                Map<String, Object> docData = new HashMap<>();
                docData.put("userName", input_field.getText().toString());
                docData.put("uuid", uuid);
                db.collection("users").document().set(docData);
                logedUserName = input_field.getText().toString();
                showRoomActivity();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
                request_user_name();
            }
        });

        builder.show();
    }

}
