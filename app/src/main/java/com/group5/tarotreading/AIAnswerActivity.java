package com.group5.tarotreading;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import com.group5.tarotreading.BuildConfig;

public class AIAnswerActivity extends AppCompatActivity {

    TextView responseTV;
    TextView questionTV;
    TextInputEditText queryEdt;

    String url = "https://api.openai.com/v1/completions";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_ai_answer);

            responseTV = findViewById(R.id.idTVResponse);
            questionTV = findViewById(R.id.idTVQuestion);
            queryEdt = findViewById(R.id.idEdtQuery);

            String question = getIntent().getStringExtra("question");
            if (question != null && !question.isEmpty()) {
                queryEdt.setText(question);
                questionTV.setText(question);

                getResponse(question);
            }

            queryEdt.setOnEditorActionListener((v, actionId, event) -> {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    // setting response tv.
                    responseTV.setText("Please wait..");
                    // validating text
                    if (queryEdt.getText().toString().length() > 0) {
                        // calling getResponse to get the response.
                        getResponse(queryEdt.getText().toString());
                    } else {
                        Toast.makeText(this, "Please enter your query..", Toast.LENGTH_SHORT).show();
                    }
                    return true;
                }
                return false;
            });

        } catch (Exception e) {
            System.out.println(e.getStackTrace());
        }
    }

    private void getResponse(String query) {
        questionTV.setText(query);
        queryEdt.setText("");

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

        String customPrompt = "You are a tarot master. Answer the user's questions in the style of a tarot reading. Use a mystical and insightful tone, referencing tarot cards as if you've drawn them for the user. Provide guidance as if interpreting the cards.\nUser's Question: " + query;

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("model", "gpt-3.5-turbo-instruct");
            jsonObject.put("prompt", customPrompt);
            jsonObject.put("temperature", 0);
            jsonObject.put("max_tokens", 150);
            jsonObject.put("top_p", 1);
            jsonObject.put("frequency_penalty", 0.5);
            jsonObject.put("presence_penalty", 0.6);
        } catch (Exception e) {
            e.printStackTrace();
        }

        JsonObjectRequest postRequest = new JsonObjectRequest(JsonObjectRequest.Method.POST, url, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String responseMsg = response.getJSONArray("choices")
                                    .getJSONObject(0)
                                    .getString("text");
                            responseTV.setText(responseMsg);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        try {
                            String response = new String(error.networkResponse.data, "UTF-8");
                        } catch (UnsupportedEncodingException e) {
                            throw new RuntimeException(e);
                        }
                        Log.e("TAGAPI", "Error is: " + error.getMessage() + "\n" + error);
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json");
                params.put("Authorization", "Bearer " + BuildConfig.OPENAI_API_KEY);
                return params;
            }
        };

        postRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {
                // Implement retry behavior.
            }
        });

        queue.add(postRequest);
    }
}
