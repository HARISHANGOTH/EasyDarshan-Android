package com.easydarshan.data.api;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {
    
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();
        
        // For now, pass through without adding auth token
        // Token can be added here later if needed
        return chain.proceed(originalRequest);
    }
}


