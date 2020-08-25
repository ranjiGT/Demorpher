package com.ss2020.project.demorpher;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.gson.JsonObject;
import com.ss2020.project.demorpher.demorphing.ImageData;
import com.ss2020.project.demorpher.demorphing.UserRequest;
import com.ss2020.project.demorpher.demorphing.UserResponse;
import com.ss2020.project.demorpher.demorphing.UserService;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class DemorphPhotos extends AppCompatActivity {

    ImageView pass;
    ImageView cam;
    TextView result;
    CardView demorphed_card;
    //RequestQueue requestQueue;
    TextView score;
    ImageView demorphedImage;
    TextView demorphedText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demorph);

        cam = (ImageView) findViewById(R.id.demorph_cam_image);
        pass = (ImageView) findViewById(R.id.demorph_pass_img);
        result = (TextView) findViewById(R.id.demorph_result);
        score = (TextView) findViewById(R.id.demorph_score_txt);
        demorphedImage = (ImageView) findViewById(R.id.demorphed_img);
        demorphedText = (TextView) findViewById(R.id.demorphed_txt);
        demorphed_card = (CardView) findViewById(R.id.demorphed_card);


        Bitmap image = BitmapFactory.decodeFile(getExternalFilesDir(Environment.DIRECTORY_DCIM) + File.separator + "camera_face.jpeg");
        Bitmap image2 = BitmapFactory.decodeFile(getExternalFilesDir(Environment.DIRECTORY_DCIM) + File.separator + "passport_face.jpeg");
        demorphed_card.setVisibility(View.INVISIBLE);
        demorphedText.setVisibility(View.INVISIBLE);
        result.setText("-");
        score.setText("-");

        if(image != null && image2 != null) {

            cam.setImageBitmap(image);
            pass.setImageBitmap(image2);

            // base 64 conversion
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 50, baos);
            byte[] camImage = baos.toByteArray();

            ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
            image2.compress(Bitmap.CompressFormat.JPEG, 100, baos2);
            byte[] passImage = baos2.toByteArray();

            String encodedCamImage = Base64.encodeToString(camImage, Base64.DEFAULT);
            String encodedPassImage = Base64.encodeToString(passImage, Base64.DEFAULT);



            //JSON object for retrospect

            encodedCamImage = encodedCamImage.replace("\n", "");
            encodedPassImage =  encodedPassImage.replace("\n", "");
            UserRequest userRequest = new UserRequest();
            ImageData imageData = new ImageData();
            imageData.setLiveImage(encodedCamImage);
            imageData.setDocumentImage(encodedPassImage);
            userRequest.setClientQueryId("OVGU Demorpher test");
            userRequest.setImageData(imageData);

            JsonObject jsonObjectImage = new JsonObject();
            jsonObjectImage.addProperty("documentImage", encodedPassImage);
            jsonObjectImage.addProperty("liveImage", encodedCamImage);

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("clientQueryId", "OvGU Demorpher test request");
            jsonObject.add("imageData", jsonObjectImage);

            demorph(jsonObject);

        }
    }




    public void demorph(JsonObject jsonObject){

        ProgressDialog progressDialog = new ProgressDialog(DemorphPhotos.this);
        progressDialog.setMessage("Waiting for response");
        progressDialog.setCancelable(false);
        progressDialog.show();

        String url = "https://ananas.cs.uni-magdeburg.de/";
        String url2 = "https://141.44.30.147:5001/";
        String url3 = "https://ananas.cs.uni-magdeburg.de/api/anomalydetector/demorphing_v2/analyse";


        // to increase timeout
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .build();


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();


        UserService userService = retrofit.create(UserService.class);
        Call<UserResponse> call = userService.postData(jsonObject);



        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                progressDialog.dismiss();

                Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_LONG).show();
//                Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_LONG).show();


//                result.setText("Score : " + response.body().getScore() + " Evaluation :" + response.body().getEvaluation());
                if(response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), " response successful", Toast.LENGTH_LONG).show();

                    if (response.body().getResultData().getEvaluation().equals("NoMorph")) {
                        result.setTextColor(getResources().getColor(android.R.color.holo_green_light));
                        score.setTextColor(getResources().getColor(android.R.color.holo_green_light));
                    } else {
                        result.setTextColor(getResources().getColor(android.R.color.holo_red_light));
                        score.setTextColor(getResources().getColor(android.R.color.holo_red_light));
                    }

                    assert response.body() != null;
                    result.setText(response.body().getResultData().getEvaluation());
                    String scoreRecieved = response.body().getResultData().getScore().toString();
                    score.setText(scoreRecieved.substring(0,5));

                    String base64Image = response.body().getResultData().getAccompliceFace();
                    if(!base64Image.isEmpty()) {
                        byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
                        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                        demorphedText.setVisibility(View.VISIBLE);
                        demorphed_card.setVisibility(View.VISIBLE);
                        demorphedImage.setImageBitmap(decodedByte);
                    }
                }
            }
            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                t.printStackTrace();
            }
        });
    }



}
