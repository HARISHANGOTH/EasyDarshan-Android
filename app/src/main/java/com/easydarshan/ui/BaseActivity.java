package com.easydarshan.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.easydarshan.R;
import com.easydarshan.data.session.SessionManager;
import com.easydarshan.ui.bookings.MyBookingsActivity;
import com.easydarshan.ui.home.HomeActivity;
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
            // Set the correct item as selected based on the current activity
            bottomNav.getMenu().findItem(getNavigationMenuItemId()).setChecked(true);
            
            bottomNav.setOnItemSelectedListener(item -> {
                int itemId = item.getItemId();
                
                // If the user clicks the already selected item, do nothing
                if (itemId == getNavigationMenuItemId()) {
                    return true;
                }
                
                Intent intent = null;
                if (itemId == R.id.nav_home) {
                    intent = new Intent(this, HomeActivity.class);
                } else if (itemId == R.id.nav_bookings) {
                    intent = new Intent(this, MyBookingsActivity.class);
                } else if (itemId == R.id.nav_profile) {
                    intent = new Intent(this, ProfileActivity.class);
                }
                
                if (intent != null) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                    // Smooth transition without blinking
                    overridePendingTransition(0, 0);
                    return true;
                }
                
                return false;
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Ensure the correct item is highlighted when returning to the activity
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        if (bottomNav != null) {
            bottomNav.getMenu().findItem(getNavigationMenuItemId()).setChecked(true);
        }
    }

    protected abstract int getNavigationMenuItemId();
}
