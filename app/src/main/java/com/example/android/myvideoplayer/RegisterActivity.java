package com.example.android.myvideoplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    TextView accountAlready;
    TextInputLayout inputEmail, inputPassword, inputConfirmPassword, inputName, inputPhone;
    Button btnRegister;
    ProgressDialog progressDialog;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        accountAlready = findViewById(R.id.already_account);
        inputEmail = findViewById(R.id.email_reg);
        inputPassword = findViewById(R.id.password_reg);
        inputConfirmPassword = findViewById(R.id.confirm_reg);
        inputName = findViewById(R.id.name_reg);
        inputPhone = findViewById(R.id.phone_reg);
        btnRegister = findViewById(R.id.register_button);
        progressDialog = new ProgressDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();


        accountAlready.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performAuthentication();
            }

        });
    }

    private void performAuthentication() {
        String email = inputEmail.getEditText().getText().toString();
        String pass = inputPassword.getEditText().getText().toString();
        String confirm = inputConfirmPassword.getEditText().getText().toString();
        String name = inputName.getEditText().getText().toString();
        String phone = inputPhone.getEditText().getText().toString();

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            inputEmail.setError("Enter Correct Email");
            inputEmail.requestFocus();
        } else if (pass.length() < 6) {
            inputEmail.setError("Password length must be atleast 6 characters");
            inputEmail.requestFocus();
        } else if (!pass.matches(confirm)) {
            inputEmail.setError("Password not matches");
        } else {
            progressDialog.setMessage("Please Wait...");
            progressDialog.setTitle("Registration");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            firebaseAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        ProfileInfo profileInfo = new ProfileInfo(name, phone);

                        DatabaseReference profile = FirebaseDatabase.getInstance().getReference("Users");
                        profile.child(firebaseUser.getUid()).setValue(profileInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    firebaseUser.sendEmailVerification();
                                    progressDialog.dismiss();
                                    Toast.makeText(RegisterActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(RegisterActivity.this, AllowAccessActivity.class));

                                } else {
                                    progressDialog.dismiss();
                                    Toast.makeText(RegisterActivity.this, "" + task.getException(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    }
                }
            });
        }
    }
}
