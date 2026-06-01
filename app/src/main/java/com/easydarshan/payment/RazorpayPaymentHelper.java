package com.easydarshan.payment;

import android.app.Activity;
import android.util.Log;

import com.razorpay.Checkout;

import org.json.JSONObject;

public class RazorpayPaymentHelper {

    private static final String TAG = "RazorpayPaymentHelper";
    private static final String RAZORPAY_KEY = "rzp_test_SvcTWhFDWhUkNM";

    private final Activity activity;
    private final PaymentCallback callback;

    public interface PaymentCallback {
        void onPaymentSuccess(String paymentId, String orderId);
        void onPaymentError(int code, String response);
    }

    public RazorpayPaymentHelper(Activity activity, PaymentCallback callback) {
        this.activity = activity;
        this.callback = callback;
        Checkout.preload(activity.getApplicationContext());
    }

    /**
     * Razorpay standard checkout — cards, netbanking, wallets, UPI.
     * Razorpay SDK opens CheckoutActivity. Result via PaymentResultListener on the Activity.
     */
    public void startStandardCheckout(String orderId, String amountInPaise,
                                       String userPhone, String userEmail) {
        try {
            Checkout checkout = new Checkout();
            checkout.setKeyID(RAZORPAY_KEY);

            JSONObject options = buildBaseOptions(orderId, amountInPaise);
            options.put("name", "Easy Darshan");
            options.put("description", "Darshan Booking");

            JSONObject prefill = new JSONObject();
            prefill.put("contact", userPhone);
            prefill.put("email", userEmail);
            options.put("prefill", prefill);

            checkout.open(activity, options);
        } catch (Exception e) {
            Log.e(TAG, "startStandardCheckout failed", e);
            if (callback != null) callback.onPaymentError(-1, e.getMessage());
        }
    }

    private JSONObject buildBaseOptions(String orderId, String amountInPaise) throws Exception {
        JSONObject options = new JSONObject();
        options.put("order_id", orderId);
        options.put("amount", amountInPaise);
        options.put("currency", "INR");
        options.put("send_sms_hash", true);

        JSONObject theme = new JSONObject();
        theme.put("color", "#FF9800");
        options.put("theme", theme);

        JSONObject retry = new JSONObject();
        retry.put("enabled", true);
        retry.put("max_count", 2);
        options.put("retry", retry);

        return options;
    }
}
