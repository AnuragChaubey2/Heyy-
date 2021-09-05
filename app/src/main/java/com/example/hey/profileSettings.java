package com.example.hey;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.hey.MODULE.User;
import com.example.hey.databinding.ActivityProfileSettingsBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class profileSettings extends AppCompatActivity {
    ActivityProfileSettingsBinding binding;
    FirebaseDatabase database;
    FirebaseAuth auth;
    FirebaseStorage storage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileSettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().hide();
        storage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();


        binding.leftArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(profileSettings.this, MainActivity.class);
                startActivity(intent);
            }
        });

        binding.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = binding.etStatus.getText().toString();
                //    String about=binding.etAbout.getText().toString();
                HashMap<String, Object> obj = new HashMap<>();
                obj.put("name", username);
                database.getReference().child("users").child(FirebaseAuth.getInstance().getUid()).updateChildren(obj);

            }
        });
        //Ecpreiment  //clicking on plus profile pic is not updating
        database.getReference().child("users").child(FirebaseAuth.getInstance().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User users = snapshot.getValue(User.class);
                Glide.with(profileSettings.this).load(users.getProfileImage())
                        .placeholder(R.drawable.avatar)
                        .into(binding.profieImage);
                binding.etStatus.setText(users.getName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //yhaa tak

//        binding.add.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent=new Intent();
//                intent.setAction(Intent.ACTION_GET_CONTENT);
//                intent.setType("image/*");
//                startActivityForResult(intent,33);
//            }
//        });
//    }


//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if(data.getData()!=null){
//            Uri sFile=data.getData();
//            String imgeUrl=sFile.toString();
//            binding.profieImage.setImageURI(sFile);
//            HashMap<String,Object> upimg=new HashMap<>();
//            upimg.put("profileImage",imgeUrl);
//            database.getReference().child("users").child(FirebaseAuth.getInstance().getUid());
//           // final StorageReference reference=storage.getReference().child("profile pic").child(FirebaseAuth.getInstance().getUid());
//            final StorageReference reference=storage.getReference().child("Profiles").child(FirebaseAuth.getInstance().getUid());
//            reference.putFile(sFile).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                @Override
//                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//
//                }
//            });
    }
}

