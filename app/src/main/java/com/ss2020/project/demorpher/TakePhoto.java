package com.ss2020.project.demorpher;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Rational;
import android.util.Size;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraX;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureConfig;
import androidx.camera.core.Preview;
import androidx.camera.core.PreviewConfig;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.exifinterface.media.ExifInterface;
import androidx.lifecycle.LifecycleOwner;

import com.appliedrec.mrtdreader.MRTDScanActivity;
import com.appliedrec.mrtdreader.MRTDScanResult;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static com.ss2020.project.demorpher.NfcScan.exifToDegrees;

public class TakePhoto extends AppCompatActivity {

    private static final int RESULT_LOAD_IMAGE = 1;
    private int REQUEST_CODE_PERMISSIONS = 101;
    private final String[] REQUIRED_PERMISSIONS = new String[]{"android.permission.CAMERA", "android.permission.WRITE_EXTERNAL_STORAGE"};
    TextureView textureView;
    ImageView guidline;
    Button retake;
    Button capture;
    Button switch_cam;
    Button next_match_btn;
    Boolean flipRequired = false;
    SharedPreferences sharedPreferences;
    public  CameraX.LensFacing lensFacing = CameraX.LensFacing.BACK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_photo);


        sharedPreferences = getSharedPreferences("enableDisable", MODE_PRIVATE);

        textureView = (TextureView) findViewById(R.id.textureView);
        guidline = (ImageView) findViewById(R.id.guidline);
        retake = (Button) findViewById(R.id.retake_btn);
        capture = (Button) findViewById(R.id.capture);
        switch_cam = (Button) findViewById(R.id.switch_camera_btn);
        next_match_btn = (Button) findViewById(R.id.next_match_btn);
        next_match_btn.setVisibility(View.GONE);
        retake.setVisibility(View.GONE);




        if (allPermissionsGranted()) {
            startCamera(); //start camera if permission has been granted by user
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }

        retake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCamera();
                guidline.setImageDrawable(getDrawable(R.drawable.face_guidline));

                retake.setVisibility(View.GONE);
                next_match_btn.setVisibility(View.GONE);
                capture.setVisibility(View.VISIBLE);
                switch_cam.setVisibility(View.VISIBLE);

            }
        });

        next_match_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(TakePhoto.this, MatchPhotos.class);
                startActivity(i);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.gallery, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.choose_photo_capture){
            Intent i = new Intent(
                    Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(i, RESULT_LOAD_IMAGE);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {

            Uri selectedImage = data.getData();
            Bitmap bitmap = null;
            try {
                bitmap = getBitmapFromGalleryUri(getApplicationContext(), selectedImage);
            } catch (IOException e) {
                e.printStackTrace();
            }
            FileOutputStream out;
            try {
                out = new FileOutputStream(getExternalFilesDir(Environment.DIRECTORY_DCIM) + File.separator + "camera_photo.jpeg");
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                out.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            CameraX.unbindAll();

            guidline.setImageBitmap(bitmap);

            capture.setVisibility(View.GONE);
            switch_cam.setVisibility(View.GONE);
            retake.setVisibility(View.VISIBLE);
            if(sharedPreferences.contains("hasPassportImage") && sharedPreferences.getBoolean("hasPassportImage", false )){
                next_match_btn.setVisibility(View.VISIBLE);
                sharedPreferences.edit().putBoolean("isMatched", false).apply();

            }
        }
    }

    public Bitmap getBitmapFromGalleryUri(Context mContext, Uri uri)throws IOException {
        int orientation = 0;

        InputStream input = mContext.getContentResolver().openInputStream(uri);
        if (input != null){
            androidx.exifinterface.media.ExifInterface exif = new androidx.exifinterface.media.ExifInterface(input);
            orientation = exif.getAttributeInt(androidx.exifinterface.media.ExifInterface.TAG_ORIENTATION, androidx.exifinterface.media.ExifInterface.ORIENTATION_NORMAL);
            //Log.d("Utils", "rotation value = " + orientation);
            input.close();
        }


        input = mContext.getContentResolver().openInputStream(uri);

        BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
        onlyBoundsOptions.inJustDecodeBounds = true;
        onlyBoundsOptions.inDither = true;//optional
        onlyBoundsOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;//optional
        BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
        try {
            input.close();

        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        if ((onlyBoundsOptions.outWidth == -1) || (onlyBoundsOptions.outHeight == -1)) {
            return null;
        }

        int originalSize = (onlyBoundsOptions.outHeight > onlyBoundsOptions.outWidth) ? onlyBoundsOptions.outHeight : onlyBoundsOptions.outWidth;

        //double ratio = (originalSize > maxSize) ? (originalSize / maxSize) : 1.0;

        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        // bitmapOptions.inSampleSize = getPowerOfTwoForSampleRatio(ratio);
        bitmapOptions.inDither = true; //optional
        bitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;//
        input = mContext.getContentResolver().openInputStream(uri);
        Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
        try {
            input.close();

        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        Matrix matrix = new Matrix();

        //Log.d("Utils", "rotation value = " + orientation);

        int rotationInDegrees = exifToDegrees(orientation);
        //Log.d("Utils", "rotationInDegrees value = " + rotationInDegrees);

        if (orientation != 0) {
            matrix.preRotate(rotationInDegrees);
        }

        int bmpWidth = 0;
        try {
            bmpWidth = bitmap.getWidth();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        Bitmap adjustedBitmap = bitmap;
        if (bmpWidth > 0) {
            adjustedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        }

        return adjustedBitmap;

    }



    private void startCamera() {

        CameraX.unbindAll();

        Rational aspectRatio = new Rational(textureView.getWidth(), textureView.getHeight());
        Size screen = new Size(textureView.getWidth(), textureView.getHeight()); //size of the screen

        // setting resolution and aspect ratio
        PreviewConfig pConfig = new PreviewConfig.Builder().
                setLensFacing(lensFacing).
                setTargetAspectRatio(aspectRatio).
                setTargetResolution(screen).build();
        Preview preview = new Preview(pConfig);

        preview.setOnPreviewOutputUpdateListener(new Preview.OnPreviewOutputUpdateListener() {

            @Override
            public void onUpdated(Preview.PreviewOutput output) {
                ViewGroup parent = (ViewGroup) textureView.getParent();
                parent.removeView(textureView);
                parent.addView(textureView, 0);

                textureView.setSurfaceTexture(output.getSurfaceTexture());
                updateTransform();
            }
        });


        ImageCaptureConfig imageCaptureConfig = new ImageCaptureConfig.Builder()
                .setLensFacing(lensFacing)
                .setCaptureMode(ImageCapture.CaptureMode.MAX_QUALITY) // to get maximum quality
                .setTargetRotation(Surface.ROTATION_0).build();
        final ImageCapture imgCap = new ImageCapture(imageCaptureConfig);

        switch_cam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(lensFacing == CameraX.LensFacing.BACK){
                    lensFacing = CameraX.LensFacing.FRONT;
                    flipRequired = true;
                }else {
                    lensFacing = CameraX.LensFacing.BACK;
                    flipRequired = false;
                }
                startCamera();
            }
        });

        // to add button for capturing image
        capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file = new File(getExternalFilesDir(Environment.DIRECTORY_DCIM) + File.separator + "camera_photo.jpeg");
                imgCap.takePicture(file, new ImageCapture.OnImageSavedListener() {

                    @Override
                    public void onImageSaved(@NonNull File file) {
                        String msg = "Captured at " + file.getAbsolutePath();
                        Toast.makeText(getBaseContext(), msg, Toast.LENGTH_LONG).show();

                        rotateImage(file);

                    }

                    @Override
                    public void onError(@NonNull ImageCapture.UseCaseError useCaseError, @NonNull String message, @Nullable Throwable cause) {
                        String msg = "Capture failed : " + message;
                        Toast.makeText(getBaseContext(), msg, Toast.LENGTH_LONG).show();
                        if (cause != null) {
                            cause.printStackTrace();
                        }
                    }
                });
            }
        });

        //bind to lifecycle:
        CameraX.bindToLifecycle((LifecycleOwner) this, preview, imgCap);
    }


    private void updateTransform() {
        Matrix mx = new Matrix();
        float w = textureView.getMeasuredWidth();
        float h = textureView.getMeasuredHeight();

        float cX = w / 2f;
        float cY = h / 2f;

        int rotationDgr;
        int rotation = (int) textureView.getRotation();

        switch (rotation) {
            case Surface.ROTATION_0:
                rotationDgr = 0;
                break;
            case Surface.ROTATION_90:
                rotationDgr = 90;
                break;
            case Surface.ROTATION_180:
                rotationDgr = 180;
                break;
            case Surface.ROTATION_270:
                rotationDgr = 270;
                break;
            default:
                return;
        }

        mx.postRotate((float) rotationDgr, cX, cY);
        textureView.setTransform(mx);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera();
            } else {
                Toast.makeText(this, "Permissions not granted to use the camera", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private boolean allPermissionsGranted() {

        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return true;
            }
        }
        return true;
    }

    public void rotateImage(File file) {
        try {

            ExifInterface exif = new ExifInterface(file.getPath());
            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);

            int angle = 0;

            if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
                angle = 90;
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
                angle = 180;
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
                angle = 270;
            }

            Matrix mat = new Matrix();
            mat.postRotate(angle);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 2;

            Bitmap bmp = BitmapFactory.decodeStream(new FileInputStream(file),
                    null, options);
            Bitmap bitmap = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(),
                    bmp.getHeight(), mat, true);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            if(flipRequired)
                bitmap = flip(bitmap);
            FileOutputStream out = new FileOutputStream(getExternalFilesDir(Environment.DIRECTORY_DCIM) + File.separator + "camera_photo.jpeg");
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.close();

            guidline.setImageBitmap(bitmap);
            textureView.setTransform(mat);
            CameraX.unbindAll();

            capture.setVisibility(View.GONE);
            switch_cam.setVisibility(View.GONE);
            retake.setVisibility(View.VISIBLE);
            if(sharedPreferences.contains("hasPassportImage") && sharedPreferences.getBoolean("hasPassportImage", false )){
                next_match_btn.setVisibility(View.VISIBLE);
                sharedPreferences.edit().putBoolean("isMatched", false).apply();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (OutOfMemoryError oom) {

            Toast.makeText(getApplicationContext(), "Out of Memory", Toast.LENGTH_SHORT).show();

        }
    }

    public Bitmap flip(Bitmap bitmap){
        Bitmap bOutput;
        Matrix matrix = new Matrix();
        matrix.preScale(-1.0f, 1.0f);
        bOutput = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return bOutput;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        CameraX.unbindAll();
    }
}
