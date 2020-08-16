package com.ss2020.project.demorpher;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.appliedrec.mrtdreader.MRTDScanActivity;
import com.appliedrec.mrtdreader.MRTDScanResult;
import com.appliedrec.verid.core.DetectedFace;

import java.io.File;
import java.io.FileOutputStream;

public class ScanResultActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_LIVENESS_DETECTION = 0;
    Bitmap faceBitmap;
    DetectedFace mrtdFace;
    MRTDScanResult scanResult;

    private static class StorageKeys {
        public static final String MRTD_FACE = "mrtdFace";
        public static final String SCAN_RESULT = "scanResult";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if (savedInstanceState != null) {
            mrtdFace = savedInstanceState.getParcelable(StorageKeys.MRTD_FACE);
            scanResult = savedInstanceState.getParcelable(StorageKeys.SCAN_RESULT);
        } else if (intent != null && intent.hasExtra(MRTDScanActivity.EXTRA_MRTD_SCAN_RESULT)) {
            scanResult = intent.getParcelableExtra(MRTDScanActivity.EXTRA_MRTD_SCAN_RESULT);
        }
        faceBitmap = BitmapFactory.decodeFile(scanResult.getFaceImageFilePath());



        invalidateOptionsMenu();
        showScanResult();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mrtdFace != null) {
            outState.putParcelable(StorageKeys.MRTD_FACE, mrtdFace);
        }
        if (scanResult != null) {
            outState.putParcelable(StorageKeys.SCAN_RESULT, scanResult);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.scan_result, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_save_passport_face).setEnabled(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_save_passport_face) {
            File file = new File(getExternalFilesDir(Environment.DIRECTORY_DCIM) + File.separator + "passport_photo.jpeg");
            try {
                FileOutputStream out = new FileOutputStream(file);
                faceBitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
                out.flush();
                out.close();
                String msg = "Saved at " + file.getAbsolutePath();
                Toast.makeText(getBaseContext(), msg, Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showScanResult() {
        setContentView(R.layout.activity_scan_result);
        ((TextView)findViewById(R.id.documentNumber)).setText(scanResult.getDocumentNumber());
        ((TextView)findViewById(R.id.personalNumber)).setText(scanResult.getPersonalNumber());
        ((TextView)findViewById(R.id.documentCode)).setText(scanResult.getDocumentCode());
        ((TextView)findViewById(R.id.issuingState)).setText(scanResult.getIssuingState());
        ((TextView)findViewById(R.id.dateOfExpiry)).setText(scanResult.getDateOfExpiry());
        ((TextView)findViewById(R.id.primaryIdentifier)).setText(scanResult.getPrimaryIdentifier());
        ((TextView)findViewById(R.id.secondaryIdentifiers)).setText(TextUtils.join(", ", scanResult.getSecondaryIdentifiers()));
        ((TextView)findViewById(R.id.nationality)).setText(scanResult.getNationality());
        ((TextView)findViewById(R.id.gender)).setText(scanResult.getGender());
        ((TextView)findViewById(R.id.dateOfBirth)).setText(scanResult.getDateOfBirth());
        if (faceBitmap != null) {
            ((ImageView)findViewById(R.id.imageView)).setImageBitmap(faceBitmap);
        } else {
            findViewById(R.id.imageView).setVisibility(View.GONE);
        }
    }

//    private void captureLiveFace() {
//        if (mrtdFace != null) {
//            addDisposable(getRxVerID().getVerID().subscribe(verID -> {
//                LivenessDetectionSessionSettings sessionSettings = new LivenessDetectionSessionSettings();
//                Intent intent = new VerIDSessionIntent<>(this, verID, sessionSettings);
//                startActivityForResult(intent, REQUEST_CODE_LIVENESS_DETECTION);
//            }, error -> showError(error.getLocalizedMessage())));
//        }
//    }

    private void showError(String description) {
        new AlertDialog.Builder(this)
                .setMessage(description)
                .setPositiveButton(android.R.string.ok, null)
                .create()
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_LIVENESS_DETECTION && resultCode == RESULT_OK) {

        }
    }
}
