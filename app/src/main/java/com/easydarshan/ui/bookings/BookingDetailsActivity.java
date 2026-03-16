package com.easydarshan.ui.bookings;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.easydarshan.R;
import com.easydarshan.databinding.ActivityBookingDetailsBinding;
import com.easydarshan.data.model.Booking;
import com.easydarshan.data.model.SingleBookingResponse;
import com.easydarshan.data.repository.AppRepository;
import com.easydarshan.utils.ErrorHandler;
import com.easydarshan.utils.NetworkUtils;
import android.graphics.Bitmap;
import android.graphics.Color;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookingDetailsActivity extends AppCompatActivity {
    
    private ActivityBookingDetailsBinding binding;
    private String bookingId;
    private AppRepository repository;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBookingDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        bookingId = getIntent().getStringExtra("booking_id");
        repository = AppRepository.getInstance(getApplication());
        
        setupListeners();
        loadBookingDetails();
    }
    
    private void setupListeners() {
        binding.backButton.setOnClickListener(v -> finish());
    }
    
    private void loadBookingDetails() {
        if (bookingId == null || bookingId.isEmpty()) {
            Toast.makeText(this, "Invalid booking ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        
        if (!NetworkUtils.isNetworkAvailable(this)) {
            Toast.makeText(this, NetworkUtils.getNetworkErrorMessage(this), Toast.LENGTH_SHORT).show();
            return;
        }
        
        repository.getBookingDetails(bookingId, new Callback<SingleBookingResponse>() {
            @Override
            public void onResponse(Call<SingleBookingResponse> call, Response<SingleBookingResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Booking booking = response.body().getBooking();
                    if (booking != null) {
                        displayBookingDetails(booking);
                    } else {
                        Toast.makeText(BookingDetailsActivity.this, "Booking not found", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    String errorMsg = ErrorHandler.getErrorMessage(response.code(), null);
                    Toast.makeText(BookingDetailsActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                }
            }
            
            @Override
            public void onFailure(Call<SingleBookingResponse> call, Throwable t) {
                String errorMsg = ErrorHandler.getErrorMessage(t, BookingDetailsActivity.this);
                Toast.makeText(BookingDetailsActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void displayBookingDetails(Booking booking) {
        if (booking == null) return;
        
        try {
            if (binding.templeName != null && booking.getTemple() != null) {
                binding.templeName.setText(booking.getTemple());
            }
            if (binding.bookingDate != null && booking.getDate() != null) {
                binding.bookingDate.setText(booking.getDate());
            }
            if (binding.bookingTime != null && booking.getTime() != null) {
                binding.bookingTime.setText(booking.getTime());
            }
            if (binding.ticketType != null && booking.getType() != null) {
                binding.ticketType.setText(booking.getType());
            }
            if (binding.bookingId != null && booking.getId() != null) {
                binding.bookingId.setText("ID: " + booking.getId());
            }
            
            // Generate QR Code
            String qrData = booking.getQrCode();
            if (qrData == null || qrData.isEmpty()) {
                qrData = booking.getId(); // Fallback to booking ID if qrCode is empty
            }
            
            if (qrData != null && !qrData.isEmpty()) {
                generateQRCode(qrData);
            } else {
                Toast.makeText(this, "QR code data not available", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error displaying booking details: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    
    private void generateQRCode(String data) {
        try {
            if (binding.qrCodeImage == null) return;
            
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
            
            binding.qrCodeImage.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to generate QR code", Toast.LENGTH_SHORT).show();
        }
    }
}
