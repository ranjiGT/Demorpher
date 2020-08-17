package com.ss2020.project.demorpher;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.os.Bundle;
import android.os.Environment;
import android.util.Rational;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraX;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureConfig;
import androidx.camera.core.Preview;
import androidx.camera.core.PreviewConfig;
import androidx.lifecycle.LifecycleOwner;

import com.ss2020.project.demorpher.FaceMatch.FaceMatchUtil;
import com.ss2020.project.demorpher.FaceMatch.MobileFaceNet.MobileFaceNet;
import com.ss2020.project.demorpher.FaceMatch.mtcnn.Box;
import com.ss2020.project.demorpher.FaceMatch.mtcnn.MTCNN;
import com.ss2020.project.demorpher.FaceMatch.mtcnn.Utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Vector;


public class MatchPhotos extends AppCompatActivity {

//    private Button addBtn;
    private Button matchBtn;
    private Button testBtn;
    private ImageView tempCamera;
    private ImageView tempPassport;
    private TextView result;
//    private TextureView temp_textureview;
    private TextView threshold;

    //face comparision
    private MobileFaceNet mobileFaceNet;
    private MTCNN mtcnn;
    public static Bitmap bitmap1;
    public static Bitmap bitmap2;
    private Bitmap bitmapCrop1;
    private Bitmap bitmapCrop2;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match);


        //connecting components
//        addBtn = (Button) findViewById(R.id.button_temp_add_train_image);
        matchBtn = (Button) findViewById(R.id.button_temp_match);
        tempCamera = (ImageView) findViewById(R.id.image_temp_for_testing);
        tempPassport = (ImageView) findViewById(R.id.image_temp_for_training);
        result = (TextView) findViewById(R.id.temp_text_result);
//        temp_textureview = (TextureView) findViewById(R.id.temp_textureView);
        testBtn = (Button) findViewById(R.id.temp_test_button);
        threshold = (TextView) findViewById(R.id.threshold_text);


        String thr = " Threshold : " + MobileFaceNet.THRESHOLD;
        threshold.setText(thr);


        bitmap1 = BitmapFactory.decodeFile(getExternalFilesDir(Environment.DIRECTORY_DCIM) + File.separator + "passport_photo.jpeg");
        bitmap2 = BitmapFactory.decodeFile(getExternalFilesDir(Environment.DIRECTORY_DCIM) + File.separator + "camera_photo.jpeg");


        //Setting up Images
        setImage();

        // face comparision
        try {
            mtcnn = new MTCNN(getAssets());
            mobileFaceNet = new MobileFaceNet(getAssets());
        } catch (IOException e) {
            e.printStackTrace();
        }


        // to take test photo
//        addBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startCamera();
//            }
//        });


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


    //start temporary methods for testing purpose taken from takephotos.java

//    private void startCamera() {
//
//        CameraX.unbindAll();
//
//        Rational aspectRatio = new Rational(temp_textureview.getWidth(), temp_textureview.getHeight());
//        Size screen = new Size(temp_textureview.getWidth(), temp_textureview.getHeight()); //size of the screen
//
//        // setting resolution and aspect ratio
//        PreviewConfig pConfig = new PreviewConfig.Builder().setTargetAspectRatio(aspectRatio).setTargetResolution(screen).build();
//        Preview preview = new Preview(pConfig);
//
//        preview.setOnPreviewOutputUpdateListener(new Preview.OnPreviewOutputUpdateListener() {
//
//            @Override
//            public void onUpdated(Preview.PreviewOutput output) {
//                ViewGroup parent = (ViewGroup) temp_textureview.getParent();
//                parent.removeView(temp_textureview);
//                parent.addView(temp_textureview, 0);
//
//                temp_textureview.setSurfaceTexture(output.getSurfaceTexture());
//                updateTransform();
//            }
//        });
//
//
//        ImageCaptureConfig imageCaptureConfig = new ImageCaptureConfig.Builder().setCaptureMode(ImageCapture.CaptureMode.MAX_QUALITY) // to get maximum quality
//                .setTargetRotation(Surface.ROTATION_0).build();
//        final ImageCapture imgCap = new ImageCapture(imageCaptureConfig);
//
//        // to add button for capturing image
//        findViewById(R.id.click).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                File file = new File(getExternalFilesDir(Environment.DIRECTORY_DCIM) + File.separator + "train_photo.jpeg");
//                imgCap.takePicture(file, new ImageCapture.OnImageSavedListener() {
//
//                    @Override
//                    public void onImageSaved(@NonNull File file) {
//                        String msg = "Captured at " + file.getAbsolutePath();
//                        Toast.makeText(getBaseContext(), msg, Toast.LENGTH_LONG).show();
//
//                        rotateImage(file);
//
//                    }
//
//                    @Override
//                    public void onError(@NonNull ImageCapture.UseCaseError useCaseError, @NonNull String message, @Nullable Throwable cause) {
//                        String msg = "Capture failed : " + message;
//                        Toast.makeText(getBaseContext(), msg, Toast.LENGTH_LONG).show();
//                        if (cause != null) {
//                            cause.printStackTrace();
//                        }
//                    }
//                });
//            }
//        });
//
//        //bind to lifecycle:
//        CameraX.bindToLifecycle((LifecycleOwner) this, preview, imgCap);
//    }


//    private void updateTransform() {
//        Matrix mx = new Matrix();
//        float w = temp_textureview.getMeasuredWidth();
//        float h = temp_textureview.getMeasuredHeight();
//
//        float cX = w / 2f;
//        float cY = h / 2f;
//
//        int rotationDgr;
//        int rotation = (int) temp_textureview.getRotation();
//
//        switch (rotation) {
//            case Surface.ROTATION_0:
//                rotationDgr = 0;
//                break;
//            case Surface.ROTATION_90:
//                rotationDgr = 90;
//                break;
//            case Surface.ROTATION_180:
//                rotationDgr = 180;
//                break;
//            case Surface.ROTATION_270:
//                rotationDgr = 270;
//                break;
//            default:
//                return;
//        }
//
//        mx.postRotate((float) rotationDgr, cX, cY);
//        temp_textureview.setTransform(mx);
//    }


//    public void rotateImage(File file) {
//        try {
//
//            ExifInterface exif = new ExifInterface(file.getPath());
//            int orientation = exif.getAttributeInt(
//                    ExifInterface.TAG_ORIENTATION,
//                    ExifInterface.ORIENTATION_NORMAL);
//
//            int angle = 0;
//
//            if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
//                angle = 90;
//            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
//                angle = 180;
//            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
//                angle = 270;
//            }
//
//            Matrix mat = new Matrix();
//            mat.postRotate(angle);
//            BitmapFactory.Options options = new BitmapFactory.Options();
//            options.inSampleSize = 2;
//
//            Bitmap bmp = BitmapFactory.decodeStream(new FileInputStream(file),
//                    null, options);
//            Bitmap bitmap = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(),
//                    bmp.getHeight(), mat, true);
//            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 100,
//                    outputStream);
//            FileOutputStream out = new FileOutputStream(getExternalFilesDir(Environment.DIRECTORY_DCIM) + File.separator + "train_photo.jpeg");
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
//            out.close();
//
//            setImage();
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (OutOfMemoryError oom) {
//
//            Toast.makeText(getApplicationContext(), "Out of Memory", Toast.LENGTH_SHORT).show();
//
//        }
//    }

    //end temporary methods for testing purpose taken from takephotos.java

    private void setImage(){
        Bitmap image = BitmapFactory.decodeFile(getExternalFilesDir(Environment.DIRECTORY_DCIM) + File.separator + "camera_photo.jpeg");
        Bitmap image2 = BitmapFactory.decodeFile(getExternalFilesDir(Environment.DIRECTORY_DCIM) + File.separator + "passport_photo.jpeg");

        if(image != null) {
            Bitmap small_image = Bitmap.createScaledBitmap(image, 300, 300, false);
            tempCamera.setImageBitmap(small_image);
        }


        tempPassport.setImageBitmap(image2);
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
        result.setText("Face Detected Successfully");
        Vector<Box> boxes2 = mtcnn.detectFaces(bitmapTemp2, bitmapTemp2.getWidth() / 5); //Only this code detects the face, the following is based on Box to cut out the face in the picture
        if (boxes1.size() == 0 || boxes2.size() == 0) {
            Toast.makeText(getApplicationContext(), "No face detected", Toast.LENGTH_LONG).show();
            return;
        }

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
        bitmapCrop1 = FaceMatchUtil.crop(bitmapTemp1, rect1);
        bitmapCrop2 = FaceMatchUtil.crop(bitmapTemp2, rect2);


        tempCamera.setImageBitmap(bitmapCrop2);
        tempPassport.setImageBitmap(bitmapCrop1);
    }


    /**
     * Face comparison
     */
    private void faceCompare() {
        if (bitmapCrop1 == null || bitmapCrop2 == null) {
            Toast.makeText(this, "Please detect faces first", Toast.LENGTH_LONG).show();
            return;
        }

        float same = mobileFaceNet.compare(bitmapCrop1, bitmapCrop2); //Just this useful code, everything else is UI

        String text = "Similarity :" + same;
        if (same > MobileFaceNet.THRESHOLD) {
            text = text + "，" + "True";
            result.setTextColor(getResources().getColor(android.R.color.holo_green_light));
        } else {
            text = text + "，" + "False";
            result.setTextColor(getResources().getColor(android.R.color.holo_red_light));
        }


//        text = text + " Threshold :" + MobileFaceNet.THRESHOLD;
        result.setText(text);
    }

}
