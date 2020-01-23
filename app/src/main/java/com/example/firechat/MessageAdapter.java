package com.example.firechat;

import android.app.Activity;
import android.text.format.DateFormat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.firebase.ui.database.FirebaseListAdapter;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

public class MessageAdapter  extends FirebaseListAdapter<ChatMessage> {
    private ChatActivity chatActivity;

    public MessageAdapter(ChatActivity activity, Class<ChatMessage> modelClass, int modelLayout, Query ref) {
        super(activity, modelClass, modelLayout, ref);
        this.chatActivity = activity;
    }


    @Override
    protected void populateView(@NonNull View v, @NonNull ChatMessage model, int position) {
        TextView messageText = (TextView) v.findViewById(R.id.message_text);
        TextView messageUser = (TextView) v.findViewById(R.id.message_user);
//        TextView messageTime = (TextView) v.findViewById(R.id.message_time);
        System.out.println("====>> Inside populateView "+model.getMsg()+" === "+model.getMessageUser());
        messageText.setText(model.getMsg());
        messageUser.setText(model.getMessageUser());

        // Format the date before showing it
//        messageTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)", model.getMessageTime()));
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        ChatMessage chatMessage = getItem(position);
        System.out.println(chatMessage+" ------>>>>>>>");
        if (chatMessage.getMessageUserId().equals(chatActivity.getLoggedInUserName()))
            view = chatActivity.getLayoutInflater().inflate(R.layout.item_out_message, viewGroup, false);
        else
            view = chatActivity.getLayoutInflater().inflate(R.layout.item_in_message, viewGroup, false);

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
