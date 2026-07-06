package com.easydarshan.ui.livequeue;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.easydarshan.R;
import com.easydarshan.data.model.ApiResponse;
import com.easydarshan.data.model.LiveQueuePositionResponse;
import com.easydarshan.data.repository.AppRepository;
import com.easydarshan.databinding.ActivityLiveQueueBinding;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LiveQueueActivity extends AppCompatActivity {

    private ActivityLiveQueueBinding binding;
    private LiveQueueViewModel viewModel;
    private String bookingId;
    private CountDownTimer validityTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLiveQueueBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        bookingId = getIntent().getStringExtra("booking_id");
        String templeName = getIntent().getStringExtra("temple_name");
        String templeLoc = getIntent().getStringExtra("temple_location");
        String templeImage = getIntent().getStringExtra("temple_image");
        String darshanType = getIntent().getStringExtra("darshan_type");

        if (templeName != null) binding.tvTempleName.setText(templeName);
        if (templeLoc != null) binding.tvTempleLoc.setText(templeLoc);
        if (darshanType != null) binding.tvDarshanType.setText(darshanType);
        
        if (templeImage != null) {
            Glide.with(this).load(templeImage).placeholder(R.drawable.img_plan_visit).into(binding.ivTemple);
        }

        binding.tvJoinedAt.setText(new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(new Date()));
        binding.tvJoinedTime.setText(new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(new Date()));

        viewModel = new ViewModelProvider(this).get(LiveQueueViewModel.class);
        
        setupObservers();
        setupListeners();
        startValidityTimer();
        
        if (bookingId != null) {
            viewModel.startPolling(bookingId);
            generateQRCode(bookingId);
        } else {
            generateQRCode("TEST_PASS_12345");
        }
    }

    private void setupObservers() {
        viewModel.getQueueStatus().observe(this, status -> {
            if (status != null) updateUI(status);
        });
    }

    private void setupListeners() {
        binding.btnBack.setOnClickListener(v -> finish());
        
        binding.btnShare.setOnClickListener(v -> {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "My Live Pass for " + binding.tvTempleName.getText() + ". Booking ID: " + bookingId);
            sendIntent.setType("text/plain");
            startActivity(Intent.createChooser(sendIntent, null));
        });

        binding.btnLeaveQueue.setOnClickListener(v -> {
            if (bookingId != null) {
                AppRepository.getInstance(this).cancelBooking(bookingId, new Callback<ApiResponse<Void>>() {
                    @Override
                    public void onResponse(@NonNull Call<ApiResponse<Void>> call, @NonNull Response<ApiResponse<Void>> response) {
                        Toast.makeText(LiveQueueActivity.this, "Left the queue", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    @Override
                    public void onFailure(@NonNull Call<ApiResponse<Void>> call, @NonNull Throwable t) {
                        Toast.makeText(LiveQueueActivity.this, "Failed to leave queue", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                finish();
            }
        });

        binding.btnContactSupport.setOnClickListener(v -> {
            Toast.makeText(this, "Connecting to support...", Toast.LENGTH_SHORT).show();
        });
    }

    private void startValidityTimer() {
        validityTimer = new CountDownTimer(300000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int minutes = (int) (millisUntilFinished / 1000) / 60;
                int seconds = (int) (millisUntilFinished / 1000) % 60;
                binding.tvValidityTimer.setText(String.format(Locale.getDefault(), "%d:%02d minutes", minutes, seconds));
            }

            @Override
            public void onFinish() {
                binding.tvValidityTimer.setText("Expired");
                binding.tvValidityTimer.setTextColor(Color.RED);
            }
        }.start();
    }

    private void updateUI(LiveQueuePositionResponse status) {
        String qId = status.getQueueId();
        binding.tvQueueNumber.setText(qId != null ? qId : "A-" + (1000 + (int)(Math.random()*9000)));
        int position = status.getPosition() != null ? status.getPosition() : 0;
        int minWait = status.getEstimatedWaitMinutes() != null ? status.getEstimatedWaitMinutes() : position * 2;
        int maxWait = minWait + 5;
        binding.tvWaitTime.setText(String.format(Locale.getDefault(), "%d - %d min", minWait, maxWait));
    }

    private void generateQRCode(String data) {
        try {
            QRCodeWriter writer = new QRCodeWriter();
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
            hints.put(EncodeHintType.MARGIN, 1);

            BitMatrix bitMatrix = writer.encode(data, BarcodeFormat.QR_CODE, 512, 512, hints);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
            binding.ivQrCode.setImageBitmap(bitmap);
        } catch (WriterException e) {
            Toast.makeText(this, "Failed to generate QR code", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (validityTimer != null) validityTimer.cancel();
    }
}
