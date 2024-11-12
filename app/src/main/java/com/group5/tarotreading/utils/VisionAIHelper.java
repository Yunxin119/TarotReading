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
import java.io.UnsupportedEncodingException;
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
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    public void analyzeCardLocations(Bitmap image, String spreadType, AIResponseListener listener) {
        RequestQueue queue = Volley.newRequestQueue(context);
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("model", "gpt-4-vision-preview");
            JSONArray messages = new JSONArray();
            JSONObject userMessage = new JSONObject();
            userMessage.put("role", "user");

            JSONArray contentArray = new JSONArray();

            // Add the system prompt for card detection
            JSONObject textContent = new JSONObject();
            textContent.put("type", "text");

            // Create spread-specific prompt
            String prompt = createSpreadPrompt(spreadType);
            textContent.put("text", prompt);
            contentArray.put(textContent);

            // Add image content
            if (image != null) {
                JSONObject imageContent = new JSONObject();
                imageContent.put("type", "image_url");

                JSONObject imageUrl = new JSONObject();
                String base64Image = bitmapToBase64(image);
                imageUrl.put("url", "data:image/jpeg;base64," + base64Image);

                imageContent.put("image_url", imageUrl);
                contentArray.put(imageContent);
            }

            userMessage.put("content", contentArray);
            messages.put(userMessage);
            jsonObject.put("messages", messages);

            // Increase max tokens for detailed analysis
            jsonObject.put("max_tokens", 500);
            jsonObject.put("temperature", 0.5);

        } catch (Exception e) {
            e.printStackTrace();
            listener.onFailure("Error creating request: " + e.getMessage());
            return;
        }

        // Create the request with same error handling as before...
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
        queue.add(postRequest);
    }

    private String createSpreadPrompt(String spreadType) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Analyze this image of a tarot card spread. ");

        switch (spreadType) {
            case "lovercross":
                prompt.append("This is a Lover's Cross spread with 5 cards. ")
                        .append("Identify the cards in these positions: ")
                        .append("1. Center (current situation) ")
                        .append("2. Above (thoughts/influences) ")
                        .append("3. Below (foundation/emotions) ")
                        .append("4. Left (past) ")
                        .append("5. Right (future). ")
                        .append("For each card, describe its position in the image and whether it's upright or reversed.");
                break;

            case "hexgram":
                prompt.append("This is a Hexagram spread with 6 cards arranged in a hexagon. ")
                        .append("Identify the cards in clockwise order starting from the top, ")
                        .append("noting their positions and orientations. ")
                        .append("Specify if any cards appear to be misplaced from the traditional hexagram pattern.");
                break;

            case "horseshoe":
                prompt.append("This is a Horseshoe spread with 5 cards in a curved line. ")
                        .append("Starting from the left, identify each card's: ")
                        .append("1. Position in the spread ")
                        .append("2. Whether it's upright or reversed ")
                        .append("3. Relative position to other cards ");
                break;

            case "timeflow":
                prompt.append("This is a 3-card Time Flow spread. ")
                        .append("Identify the cards from left to right as Past, Present, and Future. ")
                        .append("For each card, specify its exact position and orientation.");
                break;

            default:
                prompt.append("Identify all visible tarot cards in the image. ")
                        .append("For each card, describe: ")
                        .append("1. Its location in the image ")
                        .append("2. Whether it's upright or reversed ")
                        .append("3. Its relative position to other cards");
        }

        prompt.append(" Also note if any cards are partially obscured or unclear.");
        return prompt.toString();
    }

    public interface AIResponseListener {
        void onSuccess(String response);
        void onFailure(String errorMessage);
    }
}
