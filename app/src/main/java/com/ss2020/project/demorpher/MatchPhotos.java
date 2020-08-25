package com.ss2020.project.demorpher;



import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.ss2020.project.demorpher.FaceMatch.FaceMatchUtil;
import com.ss2020.project.demorpher.FaceMatch.MobileFaceNet.MobileFaceNet;
import com.ss2020.project.demorpher.FaceMatch.mtcnn.Box;
import com.ss2020.project.demorpher.FaceMatch.mtcnn.MTCNN;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Vector;


public class MatchPhotos extends AppCompatActivity {

//    private Button addBtn;
    private Button matchBtn;
    private Button testBtn;
    private ImageView tempCamera;
    private ImageView tempPassport;
    private ImageView faceCamera;
    private ImageView facePass;
    private TextView result;
    private CardView detect_pass;
    private CardView detect_cam;
//    private TextureView temp_textureview;
    private TextView threshold;

    //face comparision
    private MobileFaceNet mobileFaceNet;
    private MTCNN mtcnn;
    public static Bitmap bitmap1;
    public static Bitmap bitmap2;
    private Bitmap bitmapCrop1;
    private Bitmap bitmapCrop2;
    SharedPreferences sharedPreferences;
    public Button next_demorph;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match);


        sharedPreferences = getSharedPreferences("enableDisable", MODE_PRIVATE);

        //connecting components
//        addBtn = (Button) findViewById(R.id.button_temp_add_train_image);
        matchBtn = (Button) findViewById(R.id.button_temp_match);
        tempCamera = (ImageView) findViewById(R.id.image_temp_for_testing);
        tempPassport = (ImageView) findViewById(R.id.image_temp_for_training);
        result = (TextView) findViewById(R.id.temp_text_result);
//        temp_textureview = (TextureView) findViewById(R.id.temp_textureView);

        detect_cam = (CardView) findViewById(R.id.cam_card_match);
        detect_pass = (CardView) findViewById(R.id.pass_card_match);

        detect_pass.setVisibility(View.INVISIBLE);
        detect_cam.setVisibility(View.INVISIBLE);


        testBtn = (Button) findViewById(R.id.temp_test_button);
        threshold = (TextView) findViewById(R.id.threshold_text);
        faceCamera = (ImageView) findViewById(R.id.detected_face_cam);
        facePass = (ImageView) findViewById(R.id.detected_face_pass);
        facePass.setVisibility(View.INVISIBLE);
        faceCamera.setVisibility(View.INVISIBLE);


        next_demorph = (Button) findViewById(R.id.next_demorph_button);
        next_demorph.setVisibility(View.GONE);

        String thr = " Threshold : " + MobileFaceNet.THRESHOLD + " %";
        threshold.setText(thr);


        bitmap1 = BitmapFactory.decodeFile(getExternalFilesDir(Environment.DIRECTORY_DCIM) + File.separator + "passport_photo.jpeg");
        bitmap2 = BitmapFactory.decodeFile(getExternalFilesDir(Environment.DIRECTORY_DCIM) + File.separator + "camera_photo.jpeg");

        next_demorph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MatchPhotos.this, DemorphPhotos.class);
                startActivity(intent);
            }
        });
        //Setting up Images
        setImage();

        // face comparision
        try {
            mtcnn = new MTCNN(getAssets());
            mobileFaceNet = new MobileFaceNet(getAssets());
        } catch (IOException e) {
            e.printStackTrace();
        }



        // start face matching

        matchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                faceCompare();
                }

        });

        testBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                faceCrop();
            }
        });
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(MatchPhotos.this, MainScreen.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }

    private void setImage(){
        Bitmap image = BitmapFactory.decodeFile(getExternalFilesDir(Environment.DIRECTORY_DCIM) + File.separator + "camera_photo.jpeg");
        Bitmap image2 = BitmapFactory.decodeFile(getExternalFilesDir(Environment.DIRECTORY_DCIM) + File.separator + "passport_photo.jpeg");

        if(image != null) {
            Bitmap small_image = Bitmap.createScaledBitmap(image, 300, 300, false);
            tempCamera.setImageBitmap(small_image);
        }

        if(image2 != null) {
            Bitmap small_image2 = Bitmap.createScaledBitmap(image2, 300, 300, false);
            tempPassport.setImageBitmap(small_image2);
        }
    }



    /**
     * Face detection and reduction
     */
    private void faceCrop() {
        if (bitmap1 == null || bitmap2 == null) {
            Toast.makeText(this, "Please take two photos", Toast.LENGTH_LONG).show();
            return;
        }

        Bitmap bitmapTemp1 = bitmap1.copy(bitmap1.getConfig(), false);
        Bitmap bitmapTemp2 = bitmap2.copy(bitmap1.getConfig(), false);

        // Face data detected
        Vector<Box> boxes1 = mtcnn.detectFaces(bitmapTemp1, bitmapTemp1.getWidth() / 5); //Only this code detects the face, the following is based on Box to cut out the face in the picture
        Vector<Box> boxes2 = mtcnn.detectFaces(bitmapTemp2, bitmapTemp2.getWidth() / 5); //Only this code detects the face, the following is based on Box to cut out the face in the picture
        if (boxes1.size() == 0 || boxes2.size() == 0) {
            Toast.makeText(getApplicationContext(), "No face detected", Toast.LENGTH_LONG).show();
            return;
        }else
            Toast.makeText(getApplicationContext(), "Face Detected Successfully", Toast.LENGTH_SHORT).show();


        // Because there is only one face in each photo used here, the first value is used to crop the face
        Box box1 = boxes1.get(0);
        Box box2 = boxes2.get(0);
        box1.toSquareShape();
        box2.toSquareShape();
        box1.limitSquare(bitmapTemp1.getWidth(), bitmapTemp1.getHeight());
        box2.limitSquare(bitmapTemp2.getWidth(), bitmapTemp2.getHeight());
        Rect rect1 = box1.transform2Rect();
        Rect rect2 = box2.transform2Rect();

        // Cut face
//        Toast.makeText(getApplicationContext(), bitmapTemp1.getHeight() + " width" + bitmapTemp1.getWidth() + " rect top" + rect1.top + " " +
//                rect1.bottom + " " + rect1.left + " " + rect1.right , Toast.LENGTH_LONG).show();
        bitmapCrop1 = FaceMatchUtil.crop(bitmapTemp1, rect1);
        bitmapCrop2 = FaceMatchUtil.crop(bitmapTemp2, rect2);

        int x = rect1.left - rect1.left/4;
        int x2 = rect2.left - rect2.left/4;

        Bitmap showBitmap1 = Bitmap.createBitmap(bitmapTemp1, x , rect1.top/2, rect1.right - rect1.left/2, rect1.bottom);
        Bitmap showBitmap2 = Bitmap.createBitmap(bitmapTemp2, x2 , rect2.top/2, rect2.right - rect2.left/2 , rect2.bottom);




        Bitmap scBitmap1 = Bitmap.createScaledBitmap(showBitmap1, 413, 531, true);
        Bitmap scBitmap2 = Bitmap.createScaledBitmap(showBitmap2, 413, 531, true);


        File passFile = new File(getExternalFilesDir(Environment.DIRECTORY_DCIM) + File.separator + "passport_face.jpeg");
        try {
            FileOutputStream out = new FileOutputStream(passFile);
            scBitmap1.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        File liveFile = new File(getExternalFilesDir(Environment.DIRECTORY_DCIM) + File.separator + "camera_face.jpeg");
        try {
            FileOutputStream out = new FileOutputStream(liveFile);
            scBitmap2.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }


        faceCamera.setVisibility(View.VISIBLE);
        facePass.setVisibility(View.VISIBLE);
        detect_pass.setVisibility(View.VISIBLE);
        detect_cam.setVisibility(View.VISIBLE);
        faceCamera.setImageBitmap(scBitmap2);
        facePass.setImageBitmap(scBitmap1);
    }


    /**
     * Face comparison
     */
    private void faceCompare() {
        if (bitmapCrop1 == null || bitmapCrop2 == null) {
            Toast.makeText(this, "Please detect faces first", Toast.LENGTH_LONG).show();
            return;
        }

        float same = mobileFaceNet.compare(bitmapCrop1, bitmapCrop2) * 100 ; //Just this useful code, everything else is UI



        String text = String.valueOf(same) + " %";
        if (same > MobileFaceNet.THRESHOLD) {
            result.setTextColor(getResources().getColor(android.R.color.holo_green_light));
            sharedPreferences.edit().putBoolean("isMatched", true).apply();
            next_demorph.setVisibility(View.VISIBLE);
        } else {
            sharedPreferences.edit().putBoolean("isMatched", false).apply();
            result.setTextColor(getResources().getColor(android.R.color.holo_red_light));
        }


        result.setText(text.substring(0,5));
    }



}
