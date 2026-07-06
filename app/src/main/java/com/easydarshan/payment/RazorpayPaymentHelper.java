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
    
    public void startPayment(String keyId, String orderId, String amount, String name, String description, String contact) {
        try {
            Checkout checkout = new Checkout();
            if (keyId != null && !keyId.isEmpty()) {
                checkout.setKeyID(keyId);
            } else {
                // Fallback to default test key
                checkout.setKeyID("rzp_test_SAcfURPiizpCJo");
            }

            JSONObject options = new JSONObject();
            options.put("name", name);
            options.put("description", description);
            
            if (orderId != null && !orderId.isEmpty()) {
                options.put("order_id", orderId);
            } else {
                Log.w(TAG, "Starting payment without gatewayOrderId. This might fail if the server expects order-linked payments.");
            }

            options.put("currency", "INR");
            options.put("amount", amount);
            
            // Prefill user details
            JSONObject prefill = new JSONObject();
            if (contact != null && !contact.isEmpty()) {
                prefill.put("contact", contact);
            }
            options.put("prefill", prefill);

            // Theme color (Matching brand blue #2563EB)
            JSONObject theme = new JSONObject();
            theme.put("color", "#2563EB");
            options.put("theme", theme);

            checkout.open(activity, options);
        } catch (Exception e) {
            Log.e(TAG, "Error in starting Razorpay Checkout", e);
            if (callback != null) {
                callback.onPaymentError(-1, e.getMessage());
            }
        }
    }
}
