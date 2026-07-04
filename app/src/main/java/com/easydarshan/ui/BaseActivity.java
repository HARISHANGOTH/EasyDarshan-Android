package com.easydarshan.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.easydarshan.R;
import com.easydarshan.data.session.SessionManager;
import com.easydarshan.ui.bookings.MyBookingsActivity;
import com.easydarshan.ui.home.HomeActivity;
import com.easydarshan.ui.livequeue.LiveQueueActivity;
import com.easydarshan.ui.login.MobileLoginActivity;
import com.easydarshan.ui.profile.ProfileActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        SessionManager.getInstance(this);
    }

    /**
     * Call this whenever a 401 Unauthorized response is received.
     * Clears the session and redirects the user to the login screen.
     */
    protected void handleUnauthorized() {
        SessionManager.getInstance(this).clearSession();
        Toast.makeText(this, "Session expired. Please login again.", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this, MobileLoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    protected void setupBottomNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        if (bottomNav != null) {
            // Apply insets to the container so it floats above navigation bar
            View bottomNavContainer = findViewById(R.id.bottomNavContainer);
            if (bottomNavContainer != null) {
                ViewCompat.setOnApplyWindowInsetsListener(bottomNavContainer, (v, insets) -> {
                    Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                    v.setTranslationY(-systemBars.bottom);
                    return insets;
                });
            }

            bottomNav.getMenu().findItem(getNavigationMenuItemId()).setChecked(true);

            bottomNav.setOnItemSelectedListener(item -> {
                int itemId = item.getItemId();

                if (itemId == getNavigationMenuItemId()) {
                    return true;
                }

                Intent intent = null;
                if (itemId == R.id.nav_home) {
                    intent = new Intent(this, HomeActivity.class);
                } else if (itemId == R.id.nav_queue) {
                    intent = new Intent(this, LiveQueueActivity.class);
                } else if (itemId == R.id.nav_bookings) {
                    intent = new Intent(this, MyBookingsActivity.class);
                } else if (itemId == R.id.nav_profile) {
                    intent = new Intent(this, ProfileActivity.class);
                }

                if (intent != null) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
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
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        if (bottomNav != null) {
            bottomNav.getMenu().findItem(getNavigationMenuItemId()).setChecked(true);
        }
    }

    protected abstract int getNavigationMenuItemId();
}
