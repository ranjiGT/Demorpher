package com.ss2020.project.demorpher;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.exifinterface.media.ExifInterface;

import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.appliedrec.mrtdreader.BACInputFragment;
import com.appliedrec.mrtdreader.BACSpec;
import com.appliedrec.mrtdreader.MRTDScanActivity;
import com.appliedrec.mrtdreader.MRTDScanResult;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import static android.graphics.BitmapFactory.decodeFile;

public class NfcScan extends AppCompatActivity implements BACInputFragment.OnBACInputListener {

    private static final int REQUEST_CODE_MRTD_SCAN = 0;
    private static final String BAC_INPUT_TAG = "bacInput";
    private BACSpec bacSpec;
    private static int RESULT_LOAD_IMAGE = 1;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = getSharedPreferences("enableDisable", MODE_PRIVATE);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            SharedPreferences preferences = getPreferences(MODE_PRIVATE);
            if (preferences.contains(StorageKeys.DOCUMENT_NUMBER) && preferences.contains(StorageKeys.DATE_OF_BIRTH) && preferences.contains(StorageKeys.DATE_OF_EXPIRY)) {
                bacSpec = new BACSpec(preferences.getString(StorageKeys.DOCUMENT_NUMBER, null), new Date(preferences.getLong(StorageKeys.DATE_OF_BIRTH, new Date().getTime())), new Date(preferences.getLong(StorageKeys.DATE_OF_EXPIRY, new Date().getTime())));
                invalidateOptionsMenu();
            }
            getSupportFragmentManager().beginTransaction().add(R.id.container, BACInputFragment.newInstance(bacSpec), BAC_INPUT_TAG).commit();
        } else {
            if (bacSpec == null && savedInstanceState.containsKey(StorageKeys.DOCUMENT_NUMBER) && savedInstanceState.containsKey(StorageKeys.DATE_OF_BIRTH) && savedInstanceState.containsKey(StorageKeys.DATE_OF_EXPIRY)) {
                String docNumber = savedInstanceState.getString(StorageKeys.DOCUMENT_NUMBER);
                long dob = savedInstanceState.getLong(StorageKeys.DATE_OF_BIRTH);
                long doe = savedInstanceState.getLong(StorageKeys.DATE_OF_EXPIRY);
                bacSpec = new BACSpec(docNumber, new Date(dob), new Date(doe));
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (bacSpec != null) {
            outState.putString(StorageKeys.DOCUMENT_NUMBER, bacSpec.getDocumentNumber());
            outState.putLong(StorageKeys.DATE_OF_BIRTH, bacSpec.getDateOfBirth().getTime());
            outState.putLong(StorageKeys.DATE_OF_EXPIRY, bacSpec.getDateOfExpiry().getTime());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_scan).setEnabled(bacSpec != null);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_scan && bacSpec != null) {
            SharedPreferences preferences = getPreferences(MODE_PRIVATE);
            preferences.edit()
                    .putString(StorageKeys.DOCUMENT_NUMBER, bacSpec.getDocumentNumber())
                    .putLong(StorageKeys.DATE_OF_BIRTH, bacSpec.getDateOfBirth().getTime())
                    .putLong(StorageKeys.DATE_OF_EXPIRY, bacSpec.getDateOfExpiry().getTime())
                    .apply();
            Intent intent = new Intent(this, MRTDScanActivity.class);
            intent.putExtra(MRTDScanActivity.EXTRA_BAC_SPEC, bacSpec);
            startActivityForResult(intent, REQUEST_CODE_MRTD_SCAN);
            return true;
        }else if (item.getItemId() == R.id.choose_photo){
            Intent i = new Intent(
                    Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(i, RESULT_LOAD_IMAGE);
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_MRTD_SCAN && resultCode == RESULT_OK && data != null && data.hasExtra(MRTDScanActivity.EXTRA_MRTD_SCAN_RESULT)) {
            MRTDScanResult scanResult = data.getParcelableExtra(MRTDScanActivity.EXTRA_MRTD_SCAN_RESULT);
            Intent intent = new Intent(this, ScanResultActivity.class);
            intent.putExtra(MRTDScanActivity.EXTRA_MRTD_SCAN_RESULT, scanResult);
            startActivity(intent);
        } else if (requestCode == REQUEST_CODE_MRTD_SCAN && resultCode == RESULT_OK && data != null && data.hasExtra(MRTDScanActivity.EXTRA_MRTD_SCAN_ERROR)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.failed_to_read_travel_document);
            String message = data.getStringExtra(MRTDScanActivity.EXTRA_MRTD_SCAN_ERROR);
            if (message != null) {
                builder.setMessage(message);
            }
            builder.setNeutralButton(android.R.string.ok, null);
            builder.create().show();
        } else if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {

            Uri selectedImage = data.getData();
            Bitmap bitmap = null;
            try {
                bitmap = getBitmapFromGalleryUri(getApplicationContext(), selectedImage);
            } catch (IOException e) {
                e.printStackTrace();
            }
            FileOutputStream out;
            try {
                out = new FileOutputStream(getExternalFilesDir(Environment.DIRECTORY_DCIM) + File.separator + "passport_photo.jpeg");
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                out.close();
                sharedPreferences.edit().putBoolean("hasPassportImage", true).apply();
                sharedPreferences.edit().putBoolean("isMatched", false).apply();
                Intent i = new Intent(NfcScan.this, MainScreen.class);
                startActivity(i);
                finish();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBACChanged(BACSpec bacSpec) {
        this.bacSpec = bacSpec;
        invalidateOptionsMenu();
    }

    public Bitmap getBitmapFromGalleryUri(Context mContext, Uri uri)throws IOException {
        int orientation = 0;

        InputStream input = mContext.getContentResolver().openInputStream(uri);
        if (input != null){
            ExifInterface exif = new ExifInterface(input);
            orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
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

    private static int getPowerOfTwoForSampleRatio(double ratio){
        int k = Integer.highestOneBit((int)Math.floor(ratio));
        if(k==0) return 1;
        else return k;
    }

    public static int exifToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) { return 90; }
        else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {  return 180; }
        else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {  return 270; }
        return 0;
    }

    private static class StorageKeys {
        public static final String DOCUMENT_NUMBER = "documentNumber";
        public static final String DATE_OF_BIRTH = "dateOfBirth";
        public static final String DATE_OF_EXPIRY = "dateOfExpiry";
    }
}
