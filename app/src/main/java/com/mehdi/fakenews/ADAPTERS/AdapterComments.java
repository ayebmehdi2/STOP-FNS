package com.mehdi.fakenews.ADAPTERS;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mehdi.fakenews.DATA.Comment;
import com.mehdi.fakenews.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class AdapterComments extends RecyclerView.Adapter<AdapterComments.holder> {

    private ArrayList<String> data = null;
    public void swapAdapter(ArrayList<String> gifts){
        if (data == gifts) return;
        data = gifts;
        this.notifyDataSetChanged();
        }

    public interface CommentClick{
    void profile(String commentId);
    void likes(String commentId);
    void replays(String commentId);
    }

    private final CommentClick c;

    private String ty;
    public AdapterComments(CommentClick cc, String type){
        c = cc;
        ty = type;
    }

    private DatabaseReference reference;
    private Context context;
    private String id;

    @NonNull
    @Override
    public holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        reference = database.getReference();
        context = parent.getContext();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        id =preferences.getString("uid","");
        if (ty.equals("COMMENTS")){
            return new holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false));
        }else if (ty.equals("REPLAYS")){
            return new holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_replay, parent, false));
        }
        return null;
    }


    @Override
    public void onBindViewHolder(@NonNull holder holder, int position) {
        String id = data.get(position);
        if (id == null) return;
        reference.child(ty).child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Comment comment = dataSnapshot.getValue(Comment.class);
                if (comment == null) return;


                String date = "";
                long millS = System.currentTimeMillis() - Long.valueOf(comment.getTime());
                long min = TimeUnit.MILLISECONDS.toMinutes(millS);
                if (min < 60){
                    date = min + " min";
                }else if ((min > 60) && (min < 1440)){
                    long hour = TimeUnit.MILLISECONDS.toHours(millS);
                    date = hour + " hour";
                }else {
                    @SuppressLint("SimpleDateFormat")
                    DateFormat simple = new SimpleDateFormat("dd MMM yyyy HH:mm");
                    Date result = new Date(millS);
                    date = simple.format(result);
                }

                holder.name.setText(comment.getName());
                Glide.with(context).load(comment.getPic()).into(holder.img);
                holder.time.setText(date);
                holder.comment.setText(comment.getComment());


                if (ty.equals("REPLAYS")) return;


                reference.child("COMMENTS").child(id).child("REPLAYS").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getChildrenCount() == 0){
                            holder.replays.setText("Replay");
                        }else {
                            String lk = dataSnapshot.getChildrenCount() + " Replay";
                            holder.replays.setText(lk);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    @Override
    public int getItemCount() {
        if (data == null){
            return 0;
        }
        return data.size();
    }

    class holder extends RecyclerView.ViewHolder {

    ImageView img;
    TextView name, time, comment;
    TextView  replays;


    public holder(@NonNull View itemView) {
        super(itemView);

        img = itemView.findViewById(R.id.img_person);
        name = itemView.findViewById(R.id.name_person);
        time = itemView.findViewById(R.id.time);
        comment = itemView.findViewById(R.id.comment);
        replays = itemView.findViewById(R.id.replays);
        img.setOnClickListener(v -> c.profile(data.get(getAdapterPosition())));

        if (ty.equals("REPLAYS")) return;

        replays.setOnClickListener(v -> c.replays(data.get(getAdapterPosition())));

    }

}

}
