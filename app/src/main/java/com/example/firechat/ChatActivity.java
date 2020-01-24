package com.example.firechat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import android.widget.EditText;
import android.widget.ListView;
import android.widget.Scroller;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import es.dmoral.toasty.Toasty;

public class ChatActivity extends AppCompatActivity {

    private FloatingActionButton fab;
    private EditText input;
    private MessageAdapter  messageAdapter;
    private String nameFromEmail, room_name;
    private DatabaseReference root;
    private String temp_key;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        nameFromEmail = user.getEmail().split("@")[0];
        room_name = getIntent().getExtras().get("room_name").toString();
        setTitle(" Room - " + room_name);

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
                            .setValue(new ChatMessage(input.getText().toString(),
                                    nameFromEmail,
                                    FirebaseAuth.getInstance().getCurrentUser().getUid())
                            );
                    input.setText("");
                }
            }
        });

//        btn_send_msg.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                Map<String, Object> map = new HashMap<String, Object>();
//                temp_key = root.push().getKey();
//                root.updateChildren(map);
//
//                DatabaseReference message_root = root.child(temp_key);
//                Map<String, Object> map2 = new HashMap<String, Object>();
//                map2.put("email", nameFromEmail);
//                map2.put("msg", input_msg.getText().toString());
//
//                message_root.updateChildren(map2);
//            }
//        });

//        root.addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//
//                append_chat_conversation(dataSnapshot);
//            }
//
//            @Override
//            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//
//                append_chat_conversation(dataSnapshot);
//
//            }
//
//            @Override
//            public void onChildRemoved(DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });

    }

    private String chat_msg, chat_user_name;

//    private void append_chat_conversation(DataSnapshot dataSnapshot) {
//
//        Iterator i = dataSnapshot.getChildren().iterator();
//
//        while (i.hasNext()) {
//            chat_user_name = (String) ((DataSnapshot) i.next()).getValue();
//            chat_msg = (String) ((DataSnapshot) i.next()).getValue();
//            chat_conversation.append(chat_user_name + " : " + chat_msg + " \n");
//        }
//
//
//    }

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


            default:
                return super.onOptionsItemSelected(item);
        }
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
        System.out.println("Main"+ "user id: " + loggedInUserName+"  --------------------");

        messageAdapter = new MessageAdapter(this, ChatMessage.class, R.layout.their_message,
                FirebaseDatabase.getInstance().getReference().child(room_name));
        listView.setAdapter(messageAdapter);
    }

    public String getLoggedInUserName() {
        return loggedInUserName;
    }
}
