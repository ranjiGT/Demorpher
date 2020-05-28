package com.ss2020.project.demorpher;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.widget.ImageView;




import java.io.File;

public class ViewPhotos extends AppCompatActivity {

    ImageView photos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_photos);

        photos = (ImageView) findViewById(R.id.photos);

        //temporary
        Bitmap image = BitmapFactory.decodeFile(getExternalFilesDir(Environment.DIRECTORY_DCIM) + File.separator  + "photo.jpeg");
        photos.setImageBitmap(image);
    }
}
