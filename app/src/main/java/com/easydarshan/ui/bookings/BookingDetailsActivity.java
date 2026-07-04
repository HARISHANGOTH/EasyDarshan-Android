package com.easydarshan.ui.bookings;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.easydarshan.databinding.ActivityBookingDetailsBinding;
import com.easydarshan.data.model.Booking;
import com.easydarshan.ui.livequeue.LiveQueueActivity;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.util.HashMap;
import java.util.Map;

public class BookingDetailsActivity extends AppCompatActivity {
    
    private ActivityBookingDetailsBinding binding;
    private BookingDetailsViewModel viewModel;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBookingDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        String bookingId = getIntent().getStringExtra("booking_id");
        viewModel = new ViewModelProvider(this).get(BookingDetailsViewModel.class);
        
        setupObservers();
        setupListeners();
        
        viewModel.loadBookingDetails(bookingId);
    }
    
    private void setupObservers() {
        viewModel.getBooking().observe(this, booking -> {
            if (booking != null) displayBookingDetails(booking);
        });

        viewModel.getErrorMessage().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
                viewModel.clearErrorMessage();
            }
        });
    }
    
    private void setupListeners() {
        binding.backButton.setOnClickListener(v -> finish());
        
        binding.liveQueueButton.setOnClickListener(v -> {
            Booking booking = viewModel.getBooking().getValue();
            if (booking != null) {
                Intent intent = new Intent(this, LiveQueueActivity.class);
                intent.putExtra("booking_id", booking.getId());
                intent.putExtra("temple_name", booking.getTemple());
                intent.putExtra("booking_date", booking.getDate());
                if (booking.getDevotees() != null) intent.putExtra("devotees", booking.getDevotees());
                startActivity(intent);
            }
        });
    }
    
    private void displayBookingDetails(Booking booking) {
        binding.templeName.setText(booking.getTemple());
        binding.bookingDate.setText(booking.getDate());
        binding.bookingTime.setText(booking.getTime());
        binding.ticketType.setText(booking.getType());
        binding.bookingId.setText(String.format("ID: %s", booking.getId()));
        
        String qrData = booking.getQrCode();
        if (qrData == null || qrData.isEmpty()) qrData = booking.getId();
        
        if (qrData != null) generateQRCode(qrData);

        boolean isLive = (booking.getTime() != null && booking.getTime().toLowerCase().contains("live")) 
                        || "LIVE_QUEUE".equals(booking.getTime());
        binding.liveQueueButton.setVisibility(isLive ? View.VISIBLE : View.GONE);
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
            binding.qrCodeImage.setImageBitmap(bitmap);
        } catch (WriterException e) {
            Toast.makeText(this, "Failed to generate QR code", Toast.LENGTH_SHORT).show();
        }
    }
}
