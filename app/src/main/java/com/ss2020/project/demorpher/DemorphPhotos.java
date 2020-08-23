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

import com.google.gson.JsonObject;
import com.ss2020.project.demorpher.FaceMatch.MobileFaceNet.MobileFaceNet;
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


        Bitmap image = BitmapFactory.decodeFile(getExternalFilesDir(Environment.DIRECTORY_DCIM) + File.separator + "camera_photo.jpeg");
        Bitmap image2 = BitmapFactory.decodeFile(getExternalFilesDir(Environment.DIRECTORY_DCIM) + File.separator + "passport_photo.jpeg");
        demorphedImage.setVisibility(View.INVISIBLE);
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


//            String base64Image = encodedCamImage;
//            byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
//            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
//            cam.setImageBitmap(decodedByte);
//
//            String base64Image2 = encodedPassImage;
//            byte[] decodedString2 = Base64.decode(base64Image2, Base64.DEFAULT);
//            Bitmap decodedByte2 = BitmapFactory.decodeByteArray(decodedString2, 0, decodedString2.length);
//            pass.setImageBitmap(decodedByte2);


//
//            System.out.println("IIIIIIIIIIIIIIIIIIIIIIIIIIIII");
//            System.out.println("Camera " + encodedCamImage.length());
//            System.out.println("Passport " + encodedPassImage.length());


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





//
//            //json string for volley
////            String data = "{\"clientQueryId\": \"OvGU analyse query\",\"imageData\": {\"documentImage\": \"" + encodedPassImage +"\" , \"liveImage\": \"" + encodedCamImage + "\"}}";
//
//            try {
//                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(getApplicationContext().openFileOutput("demorph.txt", Context.MODE_PRIVATE));
//                outputStreamWriter.write(jsonObject.toString());
//            outputStreamWriter.close();
//        }
//            catch (IOException e) {
//            Log.e("Exception", "File write failed: " + e.toString());
//        }

//            String ret = "";
//
//            try {
//                InputStream inputStream = getApplicationContext().openFileInput("demorph.txt");
//
//                if ( inputStream != null ) {
//                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
//                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
//                    String receiveString = "";
//                    StringBuilder stringBuilder = new StringBuilder();
//
//                    while ( (receiveString = bufferedReader.readLine()) != null ) {
//                        stringBuilder.append("\n").append(receiveString);
//                    }
//
//                    inputStream.close();
//                    ret = stringBuilder.toString();
//                }
//            }
//            catch (FileNotFoundException e) {
//                Log.e("login activity", "File not found: " + e.toString());
//            } catch (IOException e) {
//                Log.e("login activity", "Can not read file: " + e.toString());
//            }
//
//            String testt = jsonObject.toString();
//
//            testt.replace("\"", "'");
//            JSONObject jsonObject2 = null;
//            try {
//                jsonObject2 = new JSONObject(ret);
//            }catch (JSONException err){
//                Log.d("Error", err.toString());
//            }
//
//            System.out.println("OOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO");
//            System.out.println(ret);
//            System.out.println("OOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO");
//            //System.out.println(jsonObject2.toString());
//
//            String string = jsonObject.toString();
//
//            String json = new Gson().toJson(string);

            demorph(jsonObject);
//            try {
//                demprph3(data);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
            //demorph2(data, encodedCamImage, encodedPassImage);
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

//        result.setText(jsonObject.getAsString());
//
//        Gson gson = new GsonBuilder().setPrettyPrinting().create();
//        JsonParser jp = new JsonParser();
//        JsonElement je = jp.parse(String.valueOf(jsonObject));
//        String prettyJsonString = gson.toJson(je);
//        System.out.println("/\n/\n/\n/\n/\n\n\n\n\n   over here");
//        System.out.println(prettyJsonString);
//        System.out.println("/\n/\n/\n/\n/\n\n\n\n\n   over here");



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
                        demorphedImage.setVisibility(View.VISIBLE);
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

//// second method
//    public void demorph2(String data, String liveimg, String pass){
//        final String saveData = data;
//        String URL = "https://ananas.cs.uni-magdeburg.de/api/anomalydetector/demorphing/analyse";
//
//
//
//
//
//        //JSONObject jsonObj = new JSONObject(params);
//
//        requestQueue = Volley.newRequestQueue(getApplicationContext());
//        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new com.android.volley.Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                Toast.makeText(getApplicationContext(), "Got smthng", Toast.LENGTH_SHORT).show();
//
//                JSONObject object = null;
//                try {
//                    object = new JSONObject(response);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                Toast.makeText(getApplicationContext(), object.toString(), Toast.LENGTH_LONG).show();
//            }
//        }, new com.android.volley.Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
//                error.printStackTrace();
//            }
//        })
//        {
//            @Override
//            public String getBodyContentType() {
//            return "application/json";
//        }
//
//            @Override
//            public byte[] getBody() throws AuthFailureError {
//                // POST parameters
//                Map<String, String> params = new HashMap<String, String>();
//                params.put("clientQueryId", "test");
//
//                Map<String, String> params2 = new HashMap<String, String>();
//                params2.put("documentImage", pass);
//                params2.put("liveImage", liveimg);
//
//                params.put("imageData", params2.toString());
//                return new JSONObject(params).toString().getBytes();
//
////            try {
////                return data == null ? null : data.getBytes("utf-8");
////            } catch (UnsupportedEncodingException uee) {
////                return null;
////            }
//        }
//
//            @Override
//            protected com.android.volley.Response<String> parseNetworkResponse(NetworkResponse response) {
//            String responseString = "";
//            if (response != null) {
//                responseString = String.valueOf(response.statusCode);
//                // can get more details such as response.headers
//            }
//            return com.android.volley.Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
//        }
//        };
//
//        requestQueue.add(stringRequest);
//
//
//
//    }
//
//
//    public void demprph3(String data) throws IOException {
//        try {
//            URL url = new URL("https://ananas.cs.uni-magdeburg.de/api/anomalydetector/demorphing/analyse/");
//            HttpURLConnection client = (HttpURLConnection) url.openConnection();
//            client.setRequestMethod("POST");
//            client.setRequestProperty("key", data);
//            client.setDoOutput(true);
//
//
//            OutputStream outputPost = new BufferedOutputStream(client.getOutputStream());
//            //writeStream(outputPost);
//            outputPost.flush();
//            outputPost.close();
//        }
//        catch (IOException e){
//            e.printStackTrace();
//        };
////
////        HttpClient httpClient = new DefaultHttpClient();
////        HttpPost httpPost = new HttpPost("https://ananas.cs.uni-magdeburg.de/api/anomalydetector/demorphing/analyse/");
////        httpPost.setHeader("content-type", "application/json");
////
////        StringEntity entity = new StringEntity(data.toString(), HTTP.UTF_8);
////        httpPost.setEntity(entity);
////
////        HttpResponse response = httpClient.execute(httpPost);
//    }



}
