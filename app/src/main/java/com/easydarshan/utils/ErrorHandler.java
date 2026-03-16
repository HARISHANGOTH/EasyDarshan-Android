package com.easydarshan.utils;

import android.content.Context;
import android.util.Log;

import com.easydarshan.R;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import retrofit2.HttpException;

public class ErrorHandler {
    
    private static final String TAG = "ErrorHandler";
    
    public static String getErrorMessage(Throwable throwable, Context context) {
        if (throwable == null) {
            return "An unexpected error occurred";
        }
        
        // Network errors
        if (throwable instanceof UnknownHostException || throwable instanceof IOException) {
            if (!NetworkUtils.isNetworkAvailable(context)) {
                return NetworkUtils.getNetworkErrorMessage(context);
            }
            return "Unable to connect to server. Please try again.";
        }
        
        if (throwable instanceof SocketTimeoutException) {
            return "Request timeout. Please check your connection and try again.";
        }
        
        // HTTP errors
        if (throwable instanceof HttpException) {
            HttpException httpException = (HttpException) throwable;
            int code = httpException.code();
            
            switch (code) {
                case 400:
                    return "Invalid request. Please check your input.";
                case 401:
                    return "Session expired. Please login again.";
                case 403:
                    return "Access denied. You don't have permission.";
                case 404:
                    return "Resource not found.";
                case 408:
                    return "Request timeout. Please try again.";
                case 429:
                    return "Too many requests. Please try again later.";
                case 500:
                case 502:
                case 503:
                    return "Server error. Please try again later.";
                default:
                    return "Error " + code + ". Please try again.";
            }
        }
        
        // Log unknown errors
        Log.e(TAG, "Unknown error", throwable);
        return "An unexpected error occurred. Please try again.";
    }
    
    public static String getErrorMessage(int httpCode, String message) {
        if (message != null && !message.isEmpty()) {
            return message;
        }
        
        switch (httpCode) {
            case 400:
                return "Invalid request";
            case 401:
                return "Unauthorized. Please login again";
            case 403:
                return "Access denied";
            case 404:
                return "Resource not found";
            case 500:
                return "Server error";
            default:
                return "Error occurred";
        }
    }
}





