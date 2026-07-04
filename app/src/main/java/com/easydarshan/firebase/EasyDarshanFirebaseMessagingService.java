package com.easydarshan.firebase;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.easydarshan.R;
import com.easydarshan.data.api.RetrofitClient;
import com.easydarshan.data.model.ApiResponse;
import com.easydarshan.data.model.DeviceRegistrationRequest;
import com.easydarshan.data.session.SessionManager;
import com.easydarshan.ui.livequeue.LiveQueueActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EasyDarshanFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "FCMService";
    private static final String CHANNEL_ID = "easydarshan_bookings";
    private static final int NOTIFICATION_ID = 1001;

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        String type = remoteMessage.getData().get("type");
        if ("BOOKING_CONFIRMED".equals(type)) {
            String bookingId = remoteMessage.getData().get("booking_id");
            String templeName = remoteMessage.getData().get("temple_name");

            String title = remoteMessage.getNotification() != null
                    ? remoteMessage.getNotification().getTitle()
                    : "You're in the Live Queue! \uD83D\uDE4F";
            String body = remoteMessage.getNotification() != null
                    ? remoteMessage.getNotification().getBody()
                    : "Your booking is confirmed. Tap to open your Live Queue.";

            showBookingNotification(title, body, bookingId, templeName);
        }
    }

    @Override
    public void onNewToken(@NonNull String token) {
        Log.d(TAG, "FCM token refreshed: " + token);
        SessionManager session = SessionManager.getInstance(this);
        if (session.isLoggedIn()) {
            registerTokenWithBackend(token);
        }
    }

    private void showBookingNotification(String title, String body, String bookingId, String templeName) {
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID, "Booking Notifications", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Notifications for booking confirmations and live queue updates");
            manager.createNotificationChannel(channel);
        }

        Intent intent = new Intent(this, LiveQueueActivity.class);
        intent.putExtra("booking_id", bookingId);
        intent.putExtra("temple_name", templeName != null ? templeName : "");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.app_logo);

        NotificationCompat.Builder notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setLargeIcon(largeIcon)
                .setColor(ContextCompat.getColor(getApplicationContext(), R.color.primary))
                .setContentTitle(title)
                .setContentText(body)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(body))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        manager.notify(NOTIFICATION_ID, notification.build());
    }

    private void registerTokenWithBackend(String token) {
        RetrofitClient.getInstance(this)
                .getApiService()
                .registerDevice(new DeviceRegistrationRequest(token, "ANDROID"))
                .enqueue(new Callback<ApiResponse<Void>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                        Log.d(TAG, "FCM token registered with backend");
                    }
                    @Override
                    public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                        Log.w(TAG, "Failed to register FCM token: " + t.getMessage());
                    }
                });
    }
}
