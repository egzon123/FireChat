package com.example.firechat;

import android.net.Uri;
import android.text.format.DateFormat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import de.hdodenhof.circleimageview.CircleImageView;


import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import  de.hdodenhof.circleimageview.CircleImageView;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class MessageAdapter extends FirebaseListAdapter<ChatMessage> {
    private ChatActivity chatActivity;
    StorageReference storageReference;

    public MessageAdapter(ChatActivity activity, Class<ChatMessage> modelClass, int modelLayout, Query ref) {
        super(activity, modelClass, modelLayout, ref);
        this.chatActivity = activity;
        storageReference = FirebaseStorage.getInstance().getReference().child("profile_images");
    }


    @Override
    protected void populateView(@NonNull View v, @NonNull ChatMessage model, int position) {
        TextView messageUser = (TextView) v.findViewById(R.id.message_user);
        TextView messageText = (TextView) v.findViewById(R.id.message_text);
        CircleImageView imgProfile = (CircleImageView) v.findViewById(R.id.avatar);
        TextView messageTime = (TextView) v.findViewById(R.id.message_time);
        messageUser.setText(model.getUserName());
        messageText.setText(model.getMsg());

        storageReference.child(model.getMessageUserId())
                .getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(imgProfile);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
        messageTime.setText(DateFormat.format("dd/MM/yyyy, HH:mm", model.getMessageTime()));
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        ChatMessage chatMessage = getItem(position);

        if (chatMessage.getMessageUserId().equals(chatActivity.getLoggedInUserName()))
            view = chatActivity.getLayoutInflater().inflate(R.layout.my_message, viewGroup, false);

        else
            view = chatActivity.getLayoutInflater().inflate(R.layout.their_message, viewGroup, false);

        //generating view
        populateView(view, chatMessage, position);

        return view;
    }

    @Override
    public int getViewTypeCount() {
        // return the total number of view types. this value should never change
        // at runtime
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        // return a value between 0 and (getViewTypeCount - 1)
        return position % 2;
    }
}
