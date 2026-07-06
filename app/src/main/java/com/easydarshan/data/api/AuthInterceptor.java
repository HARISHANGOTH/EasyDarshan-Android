package com.easydarshan.data.api;

import android.content.Context;
import android.content.Intent;
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

    public static final String ACTION_UNAUTHORIZED = "com.easydarshan.ACTION_UNAUTHORIZED";
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
            Log.d(TAG, "No token found in SessionManager for request: " + originalRequest.url());
            return chain.proceed(originalRequest);
        }

        if (isTokenExpired(token)) {
            Log.w(TAG, "Token is expired, clearing session: " + originalRequest.url());
            sessionManager.clearSession();
            broadcastUnauthorized();
            return chain.proceed(originalRequest);
        }

        Log.d(TAG, "Adding Authorization header to request: " + originalRequest.url());
        String userId = extractUserId(token);
        Request.Builder requestBuilder = originalRequest.newBuilder()
                .header("Authorization", "Bearer " + token);
        if (userId != null && !userId.isEmpty()) {
            requestBuilder.header("X-User-Id", userId);
        }

        Response response = chain.proceed(requestBuilder.build());
        if (response.code() == 401) {
            sessionManager.clearSession();
            broadcastUnauthorized();
        }
        return response;
    }

    private void broadcastUnauthorized() {
        context.sendBroadcast(new Intent(ACTION_UNAUTHORIZED));
    }

    private boolean isTokenExpired(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length < 2) return false; // Not a JWT, don't assume expired
            byte[] decodedBytes = Base64.decode(parts[1], Base64.URL_SAFE | Base64.NO_PADDING);
            JSONObject json = new JSONObject(new String(decodedBytes, StandardCharsets.UTF_8));
            long exp = json.optLong("exp", 0);
            if (exp == 0) return false; // No expiry claim, don't assume expired
            return System.currentTimeMillis() / 1000 >= exp;
        } catch (Exception e) {
            Log.e(TAG, "Failed to check token expiry", e);
            return false; // Error in parsing, don't clear session automatically
        }
    }

    private String extractUserId(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length < 2) return null;
            byte[] decodedBytes = Base64.decode(parts[1], Base64.URL_SAFE | Base64.NO_PADDING);
            JSONObject json = new JSONObject(new String(decodedBytes, StandardCharsets.UTF_8));
            return json.optString("userId", null);
        } catch (Exception e) {
            Log.e(TAG, "Failed to extract userId from JWT", e);
            return null;
        }
    }
}


