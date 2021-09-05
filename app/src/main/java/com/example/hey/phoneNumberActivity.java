package com.example.hey;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.hey.databinding.ActivityPhoneNumberBinding;
import com.google.firebase.auth.FirebaseAuth;

public class phoneNumberActivity extends AppCompatActivity {
    ActivityPhoneNumberBinding binding;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPhoneNumberBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();

        if(auth.getCurrentUser()!= null){
            Intent intent  = new Intent(phoneNumberActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }


        getSupportActionBar().hide();
        binding.phonebox.requestFocus();

        binding.pflBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(phoneNumberActivity.this,OTPactivity.class);
                intent.putExtra("phoneNumber",binding.phonebox.getText().toString());
                startActivity(intent);
//                finish();
            }
        });
    }
}