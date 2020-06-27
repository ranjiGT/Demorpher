package com.ss2020.project.demorpher;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;



public class MainScreen extends AppCompatActivity {

    private static final int CAMERA_PERMISSION_CODE = 111;
    private static final int CAMERA_REQUEST_CODE = 999;

    Button take_photo;
    Button viewPhotos;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_screen);


        //connecting the components
        take_photo = (Button) findViewById(R.id.btn_take_photo);
        viewPhotos = (Button) findViewById(R.id.btn_view_photos);


        viewPhotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainScreen.this, ViewPhotos.class);
                startActivity(i);
            }
        });


        //takes the image from user
        take_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // and returned in the Activity's onRequestPermissionsResult()
                int PERMISSION_ALL = 1;
                String[] PERMISSIONS = {
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        android.Manifest.permission.CAMERA
                };

                if (!hasPermissions(MainScreen.this, PERMISSIONS)) {
                    ActivityCompat.requestPermissions(MainScreen.this, PERMISSIONS, PERMISSION_ALL);
                } else {
                    Intent i = new Intent(MainScreen.this, TakePhoto.class);
                    startActivity(i);
                }

            }
        });


    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Intent i = new Intent(MainScreen.this, TakePhoto.class);
                startActivity(i);
            } else {
                Toast.makeText(this, "All the permission is required", Toast.LENGTH_SHORT).show();
            }
        }

    }

    public void nfcScan(View view) {
        Intent intent = new Intent(this, NfcScan.class);
        startActivity(intent);
    }

    public void demorph(View view) {
        Intent intent = new Intent(this, DemorphPhotos.class);
        startActivity(intent);
    }

    public void matchPhoto(View view) {
        Intent intent = new Intent(this, MatchPhotos.class);
        startActivity(intent);
    }

    public void about(View view) {
        Intent intent = new Intent(this, AboutUs.class);
        startActivity(intent);
    }
}
