package com.easydarshan.data.api;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import com.easydarshan.data.session.SessionManager;
import com.easydarshan.ui.login.MobileLoginActivity;

public class AuthInterceptor implements Interceptor {
    
    private static final String TAG = "AuthInterceptor";
    private final Context context;
    
    public AuthInterceptor(Context context) {
        this.context = context;
    }
    
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();
        Request.Builder requestBuilder = originalRequest.newBuilder();
        
        requestBuilder.header("ngrok-skip-browser-warning", "true");
        
        SessionManager sessionManager = SessionManager.getInstance(context);
        String token = sessionManager.getToken();
        
        if (token != null && !token.isEmpty()) {
            Log.d(TAG, "Adding Authorization header to request: " + originalRequest.url());
            requestBuilder.header("Authorization", "Bearer " + token);
        } else {
            Log.w(TAG, "No token found in SessionManager for request: " + originalRequest.url());
        }
        
        Response response = chain.proceed(requestBuilder.build());

        if (response.code() == 401 || response.code() == 403) {
            Log.w(TAG, "Received " + response.code() + " — clearing session and redirecting to login");
            sessionManager.clearSession();
            Intent intent = new Intent(context, MobileLoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(intent);
        }

        return response;
    }
}
