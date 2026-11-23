package com.katza.apiapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import java.io.IOException;


import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "DogApiDebug", TAGR = "RapidAPIDebug";
    private ImageView ivImage;
    private TextView tvDisplay, tvRating;
    Button btnToGemini;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        ivImage = findViewById(R.id.ivImage);
        tvDisplay = findViewById(R.id.tvDisplay);
        tvRating = findViewById(R.id.tvRating);
        btnToGemini = findViewById(R.id.btnToGemini);
        btnToGemini.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,GeminiActivity.class));
            }
        });
        
        fetchDogImage();
        fetchMovieRating();

    }

    private void fetchMovieRating() {
        OkHttpClient client = new OkHttpClient();
        Request request  =new Request.Builder().url("https://imdb236.p.rapidapi.com/api/imdb/tt30472557/rating").addHeader("x-rapidapi-key", BuildConfig.IMDB_API_KEY).addHeader("x-rapidapi-host", "imdb236.p.rapidapi.com").build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e(TAGR,"Network failure", e);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "שגיאת רשת", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                try(Response res = response){
                    if(res.isSuccessful()&&res.body()!=null){
                        String jsonResponse = res.body().string();
                        Log.d(TAGR, "JSON Response: " + jsonResponse);
                        Gson gson = new Gson();
                        MovieRatingResponse movie = gson.fromJson(jsonResponse, MovieRatingResponse.class);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tvRating.setText("Average rating: " + movie.getAverageRating());
                            }
                        });

                    }else{
                        final int statusCode = res.code();
                        Log.e(TAGR, "server error: " + statusCode);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(MainActivity.this, "שגיאת שרת", Toast.LENGTH_SHORT).show();
                                }
                            });
                    }
                }
            }
        });
    }

    private void fetchDogImage() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url("https://dog.ceo/api/breeds/image/random").build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e(TAG, "network request failed: " + e.getMessage());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "שגיאת תקשורת ברשת", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                try(Response res = response){
                    if(res.isSuccessful() && res.body()!=null){
                        String jsonResponse = res.body().string();
                        Log.d(TAG, "JSON Response: " + jsonResponse);
                        Gson gson = new Gson();
                        DogResponse dog = gson.fromJson(jsonResponse, DogResponse.class);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(dog !=null && "success".equals(dog.getStatus()) && dog.getMessage()!=null){
                                    Glide.with(MainActivity.this).load(dog.getMessage()).into(ivImage);
                                }
                            }
                        });
                    }else{
                        final int statuscode = res.code();
                        Log.e(TAG, "server error code: " + statuscode);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, "שגיאה מהשרת: " + statuscode, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                }
            }
        });
    }


}