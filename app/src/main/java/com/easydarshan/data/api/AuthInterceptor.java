package com.easydarshan.data.api;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

import com.easydarshan.data.session.SessionManager;

import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {

    private static final String TAG = "AuthInterceptor";
    private final Context context;

    public AuthInterceptor(Context context) {
        this.context = context.getApplicationContext();
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();

        SessionManager sessionManager = SessionManager.getInstance(context);
        String token = sessionManager.getToken();

        if (token == null || token.isEmpty()) {
            return chain.proceed(originalRequest);
        }

        String userId = extractUserId(token);

        Request.Builder requestBuilder = originalRequest.newBuilder()
                .header("Authorization", "Bearer " + token);

        if (userId != null && !userId.isEmpty()) {
            requestBuilder.header("X-User-Id", userId);
        }

        return chain.proceed(requestBuilder.build());
    }

    /** Extracts the userId claim from the JWT payload without validating the signature. */
    private String extractUserId(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length < 2) return null;
            byte[] decodedBytes = Base64.decode(parts[1], Base64.URL_SAFE | Base64.NO_PADDING);
            String payload = new String(decodedBytes, StandardCharsets.UTF_8);
            JSONObject json = new JSONObject(payload);
            return json.optString("userId", null);
        } catch (Exception e) {
            Log.e(TAG, "Failed to extract userId from JWT", e);
            return null;
        }
    }
}


