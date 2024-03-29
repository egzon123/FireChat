package com.example.firechat;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.ViewHolder> {
    private List<String> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private DatabaseReference root;

    public RoomAdapter(Context context, List<String> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View room_item = mInflater.inflate(R.layout.item_room, parent, false);

        return new ViewHolder(room_item);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String room_name = mData.get(position);
        holder.room_name.setText(room_name);
        Query lastQuery = FirebaseDatabase.getInstance().getReference().child(room_name).orderByKey().limitToLast(1);
        lastQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Long time = (Long) child.child("messageTime").getValue();
                    holder.room_last_msg.setText(child.child("userName").getValue() + " : " + child.child("msg").getValue().toString());
                    holder.room_last_time.setText(DateFormat.format("dd/MM/yyyy (HH:mm)", time));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors.
            }
        });


    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView room_name, room_last_time, room_last_msg;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            room_name = itemView.findViewById(R.id.room_name);
            room_last_time = itemView.findViewById(R.id.room_last_time);
            room_last_msg = itemView.findViewById(R.id.room_last_msg);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }

    }

    // convenience method for getting data at click position
    String getItem(int id) {
        return mData.get(id);
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
