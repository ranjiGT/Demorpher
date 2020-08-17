package com.ss2020.project.demorpher.FaceMatch.mtcnn;

import android.graphics.Bitmap;
import android.graphics.Matrix;

/**
 * Face alignment correction
 */
public class Align {

    /**
     * Affine transformation
     * @param bitmap original image
     * @param landmark landmark
     * @return transformed image
     */
    public static Bitmap warpAffine(Bitmap bitmap, float[] landmark) {
        float x = (landmark[0] + landmark[1] + landmark[2]) / 3;
        float y = (landmark[5] + landmark[6] + landmark[7]) / 3;
        float dy = landmark[6] - landmark[5];
        float dx = landmark[1] - landmark[0];
        float degrees = (float) Math.toDegrees(Math.atan2(dy, dx));
        Matrix matrix = new Matrix();
        matrix.setRotate(-degrees, x, y);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }
}
