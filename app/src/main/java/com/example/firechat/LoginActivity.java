package com.example.firechat;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserInfo;
import com.squareup.picasso.Picasso;

public class LoginActivity extends AppCompatActivity {


    static final int GOOGLE_SIGN =123;
    FirebaseAuth firebaseAuth;
    Button btn_login;
    TextView textView;
   static GoogleSignInClient signInClient;
    ProgressBar progressBar;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        btn_login = findViewById(R.id.login);
        textView = findViewById(R.id.text1);
        progressBar = findViewById(R.id.progress_circular);
        imageView = findViewById(R.id.image);
        firebaseAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions
                .Builder()
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        signInClient = GoogleSignIn.getClient(this,googleSignInOptions);
        btn_login.setOnClickListener(v ->signInGoogle());
    }

    void signInGoogle(){
        progressBar.setVisibility(View.VISIBLE);
        Intent signIntent = signInClient.getSignInIntent();
        startActivityForResult(signIntent,GOOGLE_SIGN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GOOGLE_SIGN){
            Task<GoogleSignInAccount> task = GoogleSignIn
                        .getSignedInAccountFromIntent(data);

            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);

                if(account != null){
                    firebaseAuthWithGoogle(account);
                }
            }catch (ApiException e){
                e.printStackTrace();
            }
        }

    }


    private void firebaseAuthWithGoogle(GoogleSignInAccount account){
        Log.d("TAG","firsebaseAuthWithGoogle:"+ account.getId());

        AuthCredential authCredential = GoogleAuthProvider
                .getCredential(account.getIdToken(),null);

        firebaseAuth.signInWithCredential(authCredential)
                .addOnCompleteListener(this, task->{
                    if(task.isSuccessful()){
                        progressBar.setVisibility(View.INVISIBLE);
                        Log.d("TAG","Signin success");
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        Intent intent = new Intent(this,ChatRooms.class);
                        intent.putExtra("email",user.getEmail());

                        startActivity(intent);
//                        System.out.println("---- FirebaseUser displayName ---->> :"+user.getDisplayName());
//                        updateUI(user,account.getDisplayName());
                    }else{
                        progressBar.setVisibility(View.INVISIBLE);
                        Log.w("TAG","Sigin failure",task.getException());
                        Toast.makeText(this, "SignIn Failed!", Toast.LENGTH_SHORT).show();
                        updateUI(null,null);
                    }
                });
    }

    private void updateUI(FirebaseUser user,String displayName) {
        if(user != null){
            String email = user.getEmail();
            String photo = String.valueOf(user.getPhotoUrl());


            textView.append("Info : \n");
            textView.append(displayName+" \n");
            textView.append(email);

            Picasso.get().load(photo).into(imageView);
            btn_login.setVisibility(View.INVISIBLE);


        }else{
            textView.setText("FireChat");
//            Picasso.get().load(R.drawable.ic_block_note).into(imageView);
            imageView.setImageResource(R.drawable.fire_chat_logo);
            btn_login.setVisibility(View.VISIBLE);

        }
    }
}
