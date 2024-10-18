package com.group5.tarotreading;

import android.content.Context;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class OpenAIHelper {

    private static final String url = "https://api.openai.com/v1/chat/completions";
    private Context context;
    private String apiKey;

    public OpenAIHelper(Context context, String apiKey) {
        this.context = context;
        this.apiKey = apiKey;
    }

    public void generateResponse(String prompt, AIResponseListener listener) {
        // Create request queue.
        RequestQueue queue = Volley.newRequestQueue(context);

        // Create JSON object.
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("model", "gpt-4");
            JSONArray messages = new JSONArray();
            JSONObject userMessage = new JSONObject();
            userMessage.put("role", "user");
            userMessage.put("content", prompt);
            messages.put(userMessage);

            // Add messages array to the JSON request
            jsonObject.put("messages", messages);

            // Other parameters
            jsonObject.put("temperature", 0);
            jsonObject.put("max_tokens", 100);
            jsonObject.put("top_p", 1);
            jsonObject.put("frequency_penalty", 0.0);
            jsonObject.put("presence_penalty", 0.0);
        } catch (Exception e) {
            e.printStackTrace();
        }


        // Make JSON object request.
        JsonObjectRequest postRequest = new JsonObjectRequest(JsonObjectRequest.Method.POST, url, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String resultText = response.getJSONArray("choices")
                                    .getJSONObject(0)
                                    .getJSONObject("message")  // Corrected to handle chat completions
                                    .getString("content");

                            // Pass the result to the listener.
                            listener.onSuccess(resultText.trim());
                        } catch (Exception e) {
                            e.printStackTrace();
                            listener.onFailure("Error parsing response: " + e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.networkResponse != null) {
                            try {
                                String response = new String(error.networkResponse.data, "UTF-8");
                                Log.e("TAGAPI", "Response Code: " + error.networkResponse.statusCode + ", Error: " + response);

                                listener.onFailure("Response Code: " + error.networkResponse.statusCode + ", Error: " + response);
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                                listener.onFailure("Encoding Error: " + e.getMessage());
                            }
                        } else {
                            Log.e("TAGAPI", "Network error: " + error.toString());
                            listener.onFailure("Network error. Please check your connection.");
                        }
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json");
                params.put("Authorization", "Bearer " + apiKey);
                return params;
            }
        };

        // Set retry policy.
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
                // Implement retry behavior if needed.
            }
        });

        // Add request to the queue.
        queue.add(postRequest);
    }
    // Listener interface for callback.
    public interface AIResponseListener {
        void onSuccess(String response);
        void onFailure(String errorMessage);
    }
}
