package com.example.hey;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hey.ADAPTER.TopStatusAdapter;
import com.example.hey.ADAPTER.UsersAdapter;
import com.example.hey.MODULE.Status;
import com.example.hey.MODULE.User;
import com.example.hey.MODULE.UserStatus;
import com.example.hey.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    FirebaseDatabase database;
    ArrayList<User> users;
    UsersAdapter usersAdapter;
    TopStatusAdapter statusAdapter;     //changed on 18-6-21 for status
    ArrayList<UserStatus> userStatuses;     //changed on 18-6-21 for status
    ProgressDialog dialog;

    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        database = FirebaseDatabase.getInstance();

        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String token) {
                HashMap<String,Object> map = new HashMap<>();
                map.put("token",token);
                database.getReference()
                        .child("users")
                        .child(FirebaseAuth.getInstance().getUid())
                        .updateChildren(map);

            }
        });

        dialog = new ProgressDialog(this);
        dialog.setMessage("Uploading Image..");
        dialog.setCancelable(false);

        getSupportActionBar().hide();


        users = new ArrayList<>();
        userStatuses = new ArrayList<>();       //changed on 18-6-21 for status


        //changed on 18-6-21 for status

        database.getReference().child("users").child(FirebaseAuth.getInstance().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        user = snapshot.getValue(User.class);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        // end of code for status

        usersAdapter = new UsersAdapter(this, users);
        statusAdapter = new TopStatusAdapter(this,userStatuses);
        //changed on 18-6-21 for status
//        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);  //changed on 18-6-21 for status
        layoutManager.setOrientation(RecyclerView.HORIZONTAL);      //changed on 18-6-21 for status
        binding.StatusList.setLayoutManager(layoutManager); //changed on 18-6-21 for status
        binding.StatusList.setAdapter(statusAdapter);      //changed on 18-6-21 for status

        binding.recyclerView.setAdapter(usersAdapter);

        binding.recyclerView.showShimmerAdapter();
        binding.StatusList.showShimmerAdapter();


        database.getReference().child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                users.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    User user = snapshot1.getValue(User.class);
                    if(!user.getUid().equals(FirebaseAuth.getInstance().getUid()))      // to hide the self chat option
                        users.add(user);
                }
                binding.recyclerView.hideShimmerAdapter();

                usersAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });


        //changed on 18-6-21 for status
        database.getReference().child("stories").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    userStatuses.clear();

                    for(DataSnapshot storySnapshot : snapshot.getChildren()){
                        UserStatus status  = new UserStatus();
                        status.setName(storySnapshot.child("name").getValue(String.class));
                        status.setProfileImage(storySnapshot.child("profileImage").getValue(String.class));
                        status.setLastUpdated(storySnapshot.child("lastUpdated").getValue(Long.class));

                        ArrayList<Status> statuses = new ArrayList<>();

                        for(DataSnapshot statusSnapshot : storySnapshot.child("statuses").getChildren()){
                            Status sampleStatus  = statusSnapshot.getValue(Status.class);
                            statuses.add(sampleStatus);

                        }
                        status.setStatuses(statuses);

                        userStatuses.add(status);

                    }
                    binding.StatusList.hideShimmerAdapter();
                    statusAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




        binding.nav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){
                    case R.id.status:
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(intent,75);
                        break;
                    case R.id.group:
                        startActivity(new Intent(com.example.hey.MainActivity.this, GroupChatActivity.class));
                        break;
                    case R.id.Setting:
                        startActivity(new Intent(com.example.hey.MainActivity.this, com.example.hey.profileSettings.class));
                        break;

                }
                return false;
            }
        });
        // end of code for status

    }

    //changed on 18-6-21 for status

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data!=null){
            if(data.getData() !=null){
                dialog.show();
                FirebaseStorage storage = FirebaseStorage.getInstance();
                Date date = new Date();
                StorageReference reference = storage.getReference().child("status").child(date.getTime()+"");


                reference.putFile(data.getData()).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                        if (task.isSuccessful()){
                            reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    UserStatus userStatus  = new UserStatus();
                                    userStatus.setName(user.getName());
                                    userStatus.setProfileImage(user.getProfileImage());
                                    userStatus.setLastUpdated(date.getTime());

                                    HashMap<String, Object> obj = new HashMap<>();
                                    obj.put("name", userStatus.getName());
                                    obj.put("profileImage",userStatus.getProfileImage());
                                    obj.put("lastUpdated",userStatus.getLastUpdated());

                                    String imageUrl = uri.toString();
                                    Status status   = new Status(imageUrl,userStatus.getLastUpdated());

                                    database.getReference()
                                            .child("stories")
                                            .child(FirebaseAuth.getInstance().getUid())
                                            .updateChildren(obj);

                                    database.getReference().child("stories")
                                            .child(FirebaseAuth.getInstance().getUid())
                                            .child("statuses")
                                            .push()
                                            .setValue(status);


                                    dialog.dismiss();

                                }
                            });
                        }

                    }
                });

            }
        }
    }
    // end of code for status


    @Override
    protected void onResume() {
        super.onResume();
        String currentId = FirebaseAuth.getInstance().getUid();
        database.getReference().child("presence").child(currentId).setValue("Online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        String currentId = FirebaseAuth.getInstance().getUid();
        database.getReference().child("presence").child(currentId).setValue("Offline");
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
//                case R.id.group:
//                startActivity(new Intent(MainActivity.this, GroupChatActivity.class));
//                    break;

            case R.id.search:

                Toast.makeText(this, "Search clicked.", Toast.LENGTH_SHORT).show();
                break;
//                case R.id.settings:
//                    Toast.makeText(this, "Settings Clicked.", Toast.LENGTH_SHORT).show();
//                    break;
        }
        return super.onOptionsItemSelected(item);
    }

//        @Override
//        public boolean onCreateOptionsMenu(Menu menu){
//            getMenuInflater().inflate(R.menu.topmenu, menu);
//            return super.onCreateOptionsMenu(menu);
//        }



}
