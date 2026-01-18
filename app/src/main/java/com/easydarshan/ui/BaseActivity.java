package com.easydarshan.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.easydarshan.R;
import com.easydarshan.data.session.SessionManager;
import com.easydarshan.ui.bookings.MyBookingsActivity;
import com.easydarshan.ui.home.HomeActivity;
import com.easydarshan.ui.notifications.NotificationsActivity;
import com.easydarshan.ui.profile.ProfileActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize SessionManager with context
        SessionManager.getInstance(this);
    }

    protected void setupBottomNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        if (bottomNav != null) {
            bottomNav.setSelectedItemId(getNavigationMenuItemId());
            bottomNav.setOnNavigationItemSelectedListener(item -> {
                int itemId = item.getItemId();
                if (itemId == getNavigationMenuItemId()) {
                    return false;
                }
                
                Intent intent = null;
                if (itemId == R.id.nav_home) {
                    intent = new Intent(this, HomeActivity.class);
                } else if (itemId == R.id.nav_bookings) {
                    intent = new Intent(this, MyBookingsActivity.class);
                } else if (itemId == R.id.nav_notifications) {
                    intent = new Intent(this, NotificationsActivity.class);
                } else if (itemId == R.id.nav_profile) {
                    intent = new Intent(this, ProfileActivity.class);
                }
                
                if (intent != null) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                }
                
                return true;
            });
        }
    }

    protected abstract int getNavigationMenuItemId();
}
