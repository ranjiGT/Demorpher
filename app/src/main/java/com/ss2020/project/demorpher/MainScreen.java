package com.ss2020.project.demorpher;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.File;


public class MainScreen extends AppCompatActivity {

    private static final int CAMERA_PERMISSION_CODE = 111;
    private static final int CAMERA_REQUEST_CODE = 999;

    SharedPreferences sharedPreferences;

    Button take_photo;
    Button viewPhotos;
    Button nfc_scan;
    Button demorph;
    Button match_photos;
    Button about_us;
    ImageView main_screen_image;

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_screen);


        sharedPreferences = getSharedPreferences("enableDisable", MODE_PRIVATE);

        Bitmap cam_image = BitmapFactory.decodeFile(getExternalFilesDir(Environment.DIRECTORY_DCIM) + File.separator + "camera_photo.jpeg");
        Bitmap pass_image = BitmapFactory.decodeFile(getExternalFilesDir(Environment.DIRECTORY_DCIM) + File.separator + "passport_photo.jpeg");




        //connecting the components
        take_photo = (Button) findViewById(R.id.btn_take_photo);
        viewPhotos = (Button) findViewById(R.id.btn_view_photos);
        main_screen_image = (ImageView) findViewById(R.id.main_screen_image);
        nfc_scan = (Button) findViewById(R.id.btn_nfc_scan);
        demorph = (Button) findViewById(R.id.btn_demorph);
        match_photos = (Button) findViewById(R.id.btn_match_photo);
        about_us = (Button) findViewById(R.id.btn_about_us);


        viewPhotos.setClickable(false);



        if(cam_image != null && pass_image != null){
            viewPhotos.setClickable(true);
            viewPhotos.setBackground(getDrawable(R.drawable.button));
            sharedPreferences.edit().putBoolean("hasBothImages", true).apply();
        }else {
            viewPhotos.setClickable(false);
            viewPhotos.setBackground(getDrawable(R.drawable.button_disabled));
            sharedPreferences.edit().putBoolean("hasBothImages", false).apply();
        }

        if(pass_image != null){
            sharedPreferences.edit().putBoolean("hasPassportImage", true).apply();
        }else
            sharedPreferences.edit().putBoolean("hasPassportImage", false).apply();



        Bitmap main_image = BitmapFactory.decodeFile(getExternalFilesDir(Environment.DIRECTORY_DCIM) + File.separator + "passport_photo.jpeg");
        if(main_image != null)
            main_screen_image.setImageBitmap(main_image);
        match_photos.setClickable(false);
        match_photos.setBackground(getDrawable(R.drawable.button_disabled));
        demorph.setBackground(getDrawable(R.drawable.button_disabled));
        demorph.setClickable(false);
        if(sharedPreferences.getBoolean("hasBothImages", false)){
            match_photos.setBackground(getDrawable(R.drawable.button));
            match_photos.setClickable(true);
        }
        if(sharedPreferences.contains("isMatched") && sharedPreferences.getBoolean("isMatched", false)){
            demorph.setBackground(getDrawable(R.drawable.button));
            demorph.setClickable(true);
        }






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

    public void viewPhoto(View view){
        Intent i = new Intent(MainScreen.this, ViewPhotos.class);
        startActivity(i);
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
