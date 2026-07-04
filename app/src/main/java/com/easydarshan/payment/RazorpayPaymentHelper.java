package com.easydarshan.payment;

import android.app.Activity;
import android.util.Log;
import com.razorpay.Checkout;
import org.json.JSONObject;

public class RazorpayPaymentHelper {
    
    private static final String TAG = "RazorpayPaymentHelper";
    private final Activity activity;
    private PaymentCallback callback;
    
    public interface PaymentCallback {
        void onPaymentSuccess(String paymentId, String orderId);
        void onPaymentError(int code, String response);
    }
    
    public RazorpayPaymentHelper(Activity activity, PaymentCallback callback) {
        this.activity = activity;
        this.callback = callback;
        Checkout.preload(activity.getApplicationContext());
    }
    
    public void startPayment(String orderId, String amount, String name, String description) {
        try {
            Checkout checkout = new Checkout();
            checkout.setKeyID("rzp_test_SAcfURPiizpCJo");

            JSONObject options = new JSONObject();
            options.put("name", name);
            options.put("description", description);
            options.put("order_id", orderId);
            options.put("currency", "INR");
            options.put("amount", amount);
            // No "method" forced — Razorpay shows its full native payment screen
            options.put("theme", new JSONObject().put("color", "#FF6B35"));

            checkout.open(activity, options);
        } catch (Exception e) {
            Log.e(TAG, "Error in starting Razorpay Checkout", e);
            if (callback != null) {
                callback.onPaymentError(-1, e.getMessage());
            }
        }
    }
}
