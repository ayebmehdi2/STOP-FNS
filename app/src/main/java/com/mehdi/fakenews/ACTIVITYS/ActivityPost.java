package com.mehdi.fakenews.ACTIVITYS;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mehdi.fakenews.DATA.Post;
import com.mehdi.fakenews.R;
import com.mehdi.fakenews.databinding.LayoutPostBinding;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;

public class ActivityPost extends AppCompatActivity {

    LayoutPostBinding binding;
    private String pathImage = null;


    FirebaseDatabase database;
    DatabaseReference reference;

    String id;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.layout_post);

        database = FirebaseDatabase.getInstance();
        reference = database.getReference();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        id =preferences.getString("uid","");

        binding.post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (pathImage == null) {
                    Toast.makeText(ActivityPost.this, "You must pick image", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!(binding.title.getText().toString().length() > 0)){
                    Toast.makeText(ActivityPost.this, "You must choose name", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!(binding.desc.getText().toString().length() > 0)){
                    Toast.makeText(ActivityPost.this, "You must describe news", Toast.LENGTH_SHORT).show();
                    return;
                }

                uploadImage(pathImage, binding.title.getText().toString(), binding.desc.getText().toString());


            }
        });

        binding.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
            }
        });



    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                //Display an error
                return;
            }
            if (data.getData() == null) return;
            pathImage = data.getData().toString();
            binding.img.setImageURI(data.getData());
        }
    }


    private void uploadImage(String filePath, String title, String desc) {

        if (filePath == null) return;
        Bitmap bitmap = null;
        try {
            InputStream inputStream = getContentResolver().openInputStream(Uri.parse(filePath));
            bitmap = BitmapFactory.decodeStream(inputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (bitmap == null){
            Toast.makeText(ActivityPost.this, "Failed Try again", Toast.LENGTH_SHORT).show();
            return;
        };
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
        byte[] data = baos.toByteArray();

        final StorageReference ref =  FirebaseStorage.getInstance().getReference().child("images/"+ UUID.randomUUID().toString());
        UploadTask uploadTask = ref.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(ActivityPost.this, exception.getMessage(), Toast.LENGTH_LONG).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        mouve(uri.toString(), title, desc);
                    }
                });

            }
        });


    }

    public void mouve(String uri, String title, String desc){
        reference.child("USERS").child(id).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = dataSnapshot.getValue(String.class);
                if (name == null) name = "Anynoms";

                String postId = id + "-" + new Random().nextInt(9999);

                Post p = new Post(name, id, postId, title, desc, uri, 0, 0);

                reference.child("USERS").child(id).child("MY_POSTS").child(postId).setValue(0);

                reference.child("POSTS").child(postId).setValue(p);

                pathImage = null;

                binding.desc.setText("");

                binding.title.setText("");

                startActivity(new Intent(ActivityPost.this, MainActivity.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
