package com.ss2020.project.demorpher;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.widget.ImageView;


import java.io.File;

public class ViewPhotos extends AppCompatActivity {

    ImageView cam_photo;
    ImageView pass_photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_photos);

        pass_photo = (ImageView) findViewById(R.id.view_from_passport);
        cam_photo = (ImageView) findViewById(R.id.view_from_camera);


        Bitmap cam_image = BitmapFactory.decodeFile(getExternalFilesDir(Environment.DIRECTORY_DCIM) + File.separator + "camera_photo.jpeg");
        if(cam_image != null){
            Bitmap small_image2 = Bitmap.createScaledBitmap(cam_image, 300, 300, false);
            cam_photo.setImageBitmap(small_image2);
        }

        Bitmap pass_image = BitmapFactory.decodeFile(getExternalFilesDir(Environment.DIRECTORY_DCIM) + File.separator + "passport_photo.jpeg");
        pass_photo.setImageBitmap(pass_image);
    }
}
