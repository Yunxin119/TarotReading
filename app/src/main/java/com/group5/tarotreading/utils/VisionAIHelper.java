package com.group5.tarotreading.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Base64;
import android.util.Log;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class VisionAIHelper {
    private static final String url = "https://api.openai.com/v1/chat/completions";
    private Context context;
    private String apiKey;

    public VisionAIHelper(Context context, String apiKey) {
        this.context = context;
        this.apiKey = apiKey;
    }

    private String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.NO_WRAP);
    }

    public void identifyTarotCard(Bitmap image, AIResponseListener listener) {
        RequestQueue queue = Volley.newRequestQueue(context);
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("model", "gpt-4-vision-preview");
            JSONArray messages = new JSONArray();
            JSONObject userMessage = new JSONObject();
            userMessage.put("role", "user");

            JSONArray contentArray = new JSONArray();

            // Specific prompt for tarot card identification
            JSONObject textContent = new JSONObject();
            textContent.put("type", "text");
            textContent.put("text", "This is an image of a tarot card. Please identify which tarot card it is. Only return the card name in standard format (e.g., 'The Fool', 'Two of Cups', etc.). If the image doesn't show a clear tarot card, say 'No tarot card detected'.");
            contentArray.put(textContent);

            // Add image
            JSONObject imageContent = new JSONObject();
            imageContent.put("type", "image_url");
            JSONObject imageUrl = new JSONObject();
            imageUrl.put("url", "data:image/jpeg;base64," + bitmapToBase64(image));
            imageContent.put("image_url", imageUrl);
            contentArray.put(imageContent);

            userMessage.put("content", contentArray);
            messages.put(userMessage);
            jsonObject.put("messages", messages);
            jsonObject.put("max_tokens", 50);
            jsonObject.put("temperature", 0.3);

        } catch (Exception e) {
            listener.onFailure("Error preparing request: " + e.getMessage());
            return;
        }

        JsonObjectRequest postRequest = new JsonObjectRequest(JsonObjectRequest.Method.POST, url, jsonObject,
                response -> {
                    try {
                        String cardName = response.getJSONArray("choices")
                                .getJSONObject(0)
                                .getJSONObject("message")
                                .getString("content").trim();

                        if (cardName.equals("No tarot card detected")) {
                            listener.onFailure("No tarot card found in the image. Please try again.");
                        } else {
                            listener.onSuccess(cardName);
                        }
                    } catch (Exception e) {
                        listener.onFailure("Error reading response: " + e.getMessage());
                    }
                },
                error -> {
                    if (error.networkResponse != null) {
                        String responseData = new String(error.networkResponse.data);
                        Log.e("API_ERROR", "Status Code: " + error.networkResponse.statusCode);
                        Log.e("API_ERROR", "Response: " + responseData);

                        switch (error.networkResponse.statusCode) {
                            case 401:
                                listener.onFailure("Authentication error: Invalid API key");
                                break;
                            case 429:
                                listener.onFailure("Rate limit exceeded. Please try again later");
                                break;
                            case 500:
                                listener.onFailure("OpenAI server error. Please try again");
                                break;
                            default:
                                listener.onFailure("API Error: " + error.networkResponse.statusCode);
                        }
                    } else {
                        listener.onFailure("Network error. Please check your connection");
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer " + apiKey);
                return headers;
            }
        };

        postRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() { return 30000; }
            @Override
            public int getCurrentRetryCount() { return 0; }
            @Override
            public void retry(VolleyError error) throws VolleyError { throw error; }
        });

        queue.add(postRequest);
    }

    public interface AIResponseListener {
        void onSuccess(String cardName);
        void onFailure(String errorMessage);
    }
}