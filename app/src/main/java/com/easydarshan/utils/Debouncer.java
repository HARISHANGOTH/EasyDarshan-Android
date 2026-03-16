package com.easydarshan.utils;

import android.os.Handler;
import android.os.Looper;

public class Debouncer {
    
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable runnable;
    private long delayMillis;
    
    public Debouncer(long delayMillis) {
        this.delayMillis = delayMillis;
    }
    
    public void debounce(Runnable action) {
        if (runnable != null) {
            handler.removeCallbacks(runnable);
        }
        
        runnable = action;
        handler.postDelayed(runnable, delayMillis);
    }
    
    public void cancel() {
        if (runnable != null) {
            handler.removeCallbacks(runnable);
            runnable = null;
        }
    }
}





