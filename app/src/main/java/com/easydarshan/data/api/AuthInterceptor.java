package com.easydarshan.data.api;

import android.content.Context;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import com.easydarshan.data.session.SessionManager;

public class AuthInterceptor implements Interceptor {
    
    private Context context;
    
    public AuthInterceptor(Context context) {
        this.context = context;
    }
    
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();
        
        // Get token from SessionManager
        SessionManager sessionManager = SessionManager.getInstance(context);
        String token = sessionManager.getToken();
        
        // Add Authorization header if token exists
        if (token != null && !token.isEmpty()) {
            Request.Builder requestBuilder = originalRequest.newBuilder()
                    .header("Authorization", "Bearer " + token);
            return chain.proceed(requestBuilder.build());
        }
        
        return chain.proceed(originalRequest);
    }
}


