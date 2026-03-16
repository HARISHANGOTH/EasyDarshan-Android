package com.easydarshan.utils;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.atomic.AtomicInteger;

public class RetryHandler {
    
    private static final int MAX_RETRIES = 3;
    private static final long RETRY_DELAY_MS = 2000; // 2 seconds
    
    public interface RetryCallback {
        void onRetry();
        void onMaxRetriesReached();
    }
    
    public static void retryWithBackoff(RetryCallback callback, AtomicInteger retryCount) {
        if (retryCount.get() >= MAX_RETRIES) {
            callback.onMaxRetriesReached();
            return;
        }
        
        retryCount.incrementAndGet();
        long delay = RETRY_DELAY_MS * retryCount.get();
        
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            callback.onRetry();
        }, delay);
    }
    
    public static boolean shouldRetry(Throwable throwable) {
        if (throwable instanceof java.net.SocketTimeoutException) {
            return true;
        }
        if (throwable instanceof java.io.IOException) {
            return true;
        }
        if (throwable instanceof retrofit2.HttpException) {
            retrofit2.HttpException httpException = (retrofit2.HttpException) throwable;
            int code = httpException.code();
            // Retry on server errors and timeouts
            return code >= 500 || code == 408 || code == 429;
        }
        return false;
    }
}





