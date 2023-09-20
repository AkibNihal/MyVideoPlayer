package com.example.android.myvideoplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UpdateProfileActivity extends AppCompatActivity {

    TextInputLayout inputName;
    TextInputLayout inputPhone;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        inputName = findViewById(R.id.name_upd);
        inputPhone = findViewById(R.id.phone_upd);
        Button btnUpdate = findViewById(R.id.upd_button);
        DatabaseReference profile = FirebaseDatabase.getInstance().getReference("Users");

        profile.child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ProfileInfo profileInfo = snapshot.getValue(ProfileInfo.class);
                String prevName = profileInfo.getName();
                String prevPhone = profileInfo.getPhoneNum();
                inputName.getEditText().setText(prevName);
                inputPhone.getEditText().setText(prevPhone);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UpdateProfileActivity.this, "Failed to get data.", Toast.LENGTH_SHORT).show();
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = inputName.getEditText().getText().toString();
                String phone = inputPhone.getEditText().getText().toString();

                ProfileInfo profileInfo = new ProfileInfo(name, phone);


                profile.child(firebaseUser.getUid()).setValue(profileInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(UpdateProfileActivity.this, "Update successful", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(UpdateProfileActivity.this, "" + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

    }
}