package com.example.android.myvideoplayer;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class AllowAccessActivity extends AppCompatActivity {

    int STORAGE_PERMISSION = 1;
    int REQUEST_PERMISSION_SETTING = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_allow_access);

        /* only ask for permission for first time */
        SharedPreferences preferences = getSharedPreferences("AllowAccess", MODE_PRIVATE); // get application level preference
        String check = preferences.getString("isAllowed", "");  // get data from saved preference
        if (check.equals("Allowed")) {
            // already allowed, move to MainActivity
            startActivity(new Intent(AllowAccessActivity.this, MainActivity.class));
            finish();
        }

        Button allow_button = findViewById(R.id.allowButton);

        allow_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    /* permission already given save shared preference and go to MainActivity */
                    SharedPreferences.Editor editor =  preferences.edit();
                    editor.putString("isAllowed", "Allowed");
                    editor.apply();
                    startActivity(new Intent(AllowAccessActivity.this, MainActivity.class));
                    finish();
                } else {
                    /* permission is not given. Request for permission */
                    ActivityCompat.requestPermissions(AllowAccessActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION);
                }
            }
        });
    }

    /**
     * process the permission
     * this method is invoked by system after user responds to the system permission dialogue
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION) {
            for (int i = 0; i < permissions.length; i++) { // check all the permissions
                String permission = permissions[i];
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    boolean ifShowRationale = shouldShowRequestPermissionRationale(permission); // if false "DENY" is clicked
                    if (!ifShowRationale) {
                        // user clicked "never ask again"
                        /* show alert dialogue to user */
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setTitle("App Permission").setMessage("For playing video the app must get access to storage\n\n" +
                                        "Please follow the below steps\n\n" +
                                        "Open settings from below\n" +
                                        "Click on Permission\n" +
                                        "Allow access for storage")
                                .setPositiveButton("Open Settings", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                                        intent.setData(uri);
                                        startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
                                    }
                                }).create().show();


                    } else {
                        /* again ask for permission */
                        ActivityCompat.requestPermissions(AllowAccessActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION);
                    }
                } else {
                    /* permission given. Save shared preference as "Allowed" and Go to MainActivity */
                    SharedPreferences preferences = getSharedPreferences("AllowAccess", MODE_PRIVATE);
                    SharedPreferences.Editor editor =  preferences.edit();
                    editor.putString("isAllowed", "Allowed");
                    editor.apply();
                    startActivity(new Intent(AllowAccessActivity.this, MainActivity.class));
                    finish();
                }
            }
        }
    }

    /**
     * Avoid coming back to AllowAccess activity when user comes back from setting after allowing access
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onResume() {
        super.onResume();
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            startActivity(new Intent(AllowAccessActivity.this, MainActivity.class));
            finish();
        }
    }
}