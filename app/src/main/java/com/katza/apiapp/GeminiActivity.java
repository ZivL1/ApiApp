package com.katza.apiapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class GeminiActivity extends AppCompatActivity {
    EditText etQuestion;
    Button btnSend;

    TextView tvAnswer;
    private static final String TAG = "GeminiDebug";
    private static final String MODEL_NAME = "gemini-4-hydrogen";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_gemini);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        etQuestion = findViewById(R.id.etQuestion);
        btnSend = findViewById(R.id.btnSend);
        tvAnswer = findViewById(R.id.tvAnswer);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String question =
                        etQuestion.getText().toString().trim();

                if (question.isEmpty()) {
                    etQuestion.setError("יש להזין שאלה");
                    return;
                }

                tvAnswer.setText("ממתין לתשובה...");

                askGemini(question);

            }
        });


    }

    private void askGemini(String question) {
        //Old: OkHttpClient client = new OkHttpClient();
// מודל AI עשוי להזדקק לזמן ארוך יותר ליצירת תשובה.
// לכן נגדיל את זמן ההמתנה לקבלת נתונים מהשרת.
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build();


        // יצירת אובייקט שמכיל את השאלה
        GeminiRequest geminiRequest =
                new GeminiRequest(question);

        // המרת האובייקט ל-JSON
        Gson gson = new Gson();

        String json = gson.toJson(geminiRequest);

        // הגדרת סוג התוכן
        MediaType jsonType =
                MediaType.get("application/json; charset=utf-8");

        // יצירת גוף הבקשה
        RequestBody body =
                RequestBody.create(json, jsonType);

        // כתובת השירות
        String url =
                "https://generativelanguage.googleapis.com/v1beta/models/"
                        + MODEL_NAME + ":generateContent";

        // בניית בקשת POST
        Request request = new Request.Builder()
                .url(url)
                .addHeader(
                        "x-goog-api-key",
                        BuildConfig.AI_API_KEY
                )
                .post(body)
                .build();

        // שליחת הבקשה
        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {

                Log.e(TAG, "Network request failed", e);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvAnswer.setText("שגיאת תקשורת ברשת");
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response)
                    throws IOException {

                try (Response res = response) {

                    if (res.isSuccessful() && res.body() != null) {

                        // קבלת תשובת JSON
                        String jsonResponse =
                                res.body().string();

                        Log.d(TAG, "JSON Response: " + jsonResponse);

                        // המרת JSON לאובייקט Java
                        Gson gson = new Gson();

                        GeminiResponse geminiResponse =
                                gson.fromJson(
                                        jsonResponse,
                                        GeminiResponse.class
                                );

                        // חילוץ הטקסט מתוך התשובה
                        final String answer =
                                geminiResponse != null
                                        ? geminiResponse.getAnswer()
                                        : "לא התקבלה תשובה";

                        // הצגת התשובה בממשק
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tvAnswer.setText(answer);
                            }
                        });

                    } else {

                        final int statusCode = res.code();
                        final String errorBody = res.body() != null
                                ? res.body().string()
                                : "לא התקבל פירוט שגיאה מהשרת";

                        Log.e(TAG, "Server error: " + statusCode);
                        Log.e(TAG, "Error body: " + errorBody);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tvAnswer.setText(
                                        "שגיאה מהשרת: " + statusCode +
                                                "\n" + errorBody
                                );
                            }
                        });

                    }
                }
            }
        });
    }

}
