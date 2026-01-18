# EasyDarshan Android App - Setup Guide

## Overview
This Android application has been set up with a complete architecture including:
- **Data Models**: All entity classes (Temple, Booking, User, Notification, etc.)
- **API Layer**: Retrofit interfaces with dummy service returning mock data
- **Repository Pattern**: Centralized data access layer
- **ViewModels**: MVVM architecture for all screens
- **Activities**: All UI screens with data binding
- **Adapters**: RecyclerView adapters for lists

## Project Structure

```
app/src/main/java/com/easydarshan/
├── data/
│   ├── api/
│   │   ├── ApiService.java (Retrofit interface)
│   │   └── DummyApiService.java (Mock API implementation)
│   ├── model/
│   │   ├── Temple.java
│   │   ├── Booking.java
│   │   ├── User.java
│   │   ├── Notification.java
│   │   └── ... (other models)
│   └── repository/
│       └── AppRepository.java
├── ui/
│   ├── splash/
│   │   ├── SplashActivity.java
│   │   └── SplashViewModel.java
│   ├── login/
│   │   ├── MobileLoginActivity.java
│   │   └── MobileLoginViewModel.java
│   ├── otp/
│   │   ├── OtpVerificationActivity.java
│   │   └── OtpVerificationViewModel.java
│   ├── home/
│   │   ├── HomeActivity.java
│   │   └── HomeViewModel.java
│   ├── temple/
│   │   ├── TempleDetailsActivity.java
│   │   └── TempleDetailsViewModel.java
│   ├── bookings/
│   │   ├── MyBookingsActivity.java
│   │   └── MyBookingsViewModel.java
│   ├── notifications/
│   │   ├── NotificationsActivity.java
│   │   └── NotificationsViewModel.java
│   ├── profile/
│   │   ├── ProfileActivity.java
│   │   └── ProfileViewModel.java
│   ├── prebooking/
│   │   ├── PreBookingFlowActivity.java
│   │   └── PreBookingViewModel.java
│   └── adapter/
│       ├── TempleAdapter.java
│       ├── BookingAdapter.java
│       └── NotificationAdapter.java
```

## Dependencies Added

The following dependencies have been added to `app/build.gradle`:

- **Lifecycle Components**: ViewModel, LiveData
- **Retrofit**: For API calls
- **Gson**: JSON serialization
- **RecyclerView**: For lists
- **Data Binding**: Enabled in build.gradle

## Features Implemented

### 1. Authentication Flow
- Splash Screen → Mobile Login → OTP Verification → Home
- All screens connected with proper navigation

### 2. Home Screen
- Temple list with search functionality
- Bottom navigation
- Click on temple to view details

### 3. Temple Details
- Full temple information
- Darshan types
- Pre-book or Join Live Queue options

### 4. Bookings
- Tab-based filtering (Upcoming, Active, Completed, Cancelled)
- Booking list with details
- Empty state handling

### 5. Notifications
- Notification list
- Mark all as read functionality
- Empty state handling

### 6. Profile
- User information display
- Menu items for settings
- Logout functionality

### 7. Pre-Booking Flow
- Multi-step booking process
- Date selection → Slot selection → Details → Payment
- Booking creation with API call

## API Integration

### Current Implementation
- **DummyApiService**: Returns mock data with simulated network delays
- All API calls return success responses with dummy data
- Ready to be replaced with real backend integration

### To Connect Real Backend:
1. Update `BASE_URL` in `DummyApiService.java`
2. Replace `DummyApiService` calls with actual Retrofit calls
3. Update API endpoints in `ApiService.java` interface
4. Handle authentication tokens if required

## Running the App

1. **Sync Gradle**: Let Android Studio sync all dependencies
2. **Build Project**: Build → Make Project
3. **Run**: Click Run button or Shift+F10

## Navigation Flow

```
SplashActivity
    ↓
MobileLoginActivity
    ↓
OtpVerificationActivity
    ↓
HomeActivity
    ├──→ TempleDetailsActivity
    │       ├──→ PreBookingFlowActivity → MyBookingsActivity
    │       └──→ LiveQueueFlowActivity (to be implemented)
    ├──→ MyBookingsActivity
    ├──→ NotificationsActivity
    └──→ ProfileActivity
```

## Notes

1. **Data Binding**: All layouts use data binding. Make sure views are properly referenced.
2. **Mock Data**: Currently returns dummy data. Replace `DummyApiService` with real API calls.
3. **Error Handling**: Basic error handling implemented. Enhance as needed.
4. **Loading States**: Loading indicators shown during API calls.
5. **Empty States**: Empty state views for lists when no data available.

## Next Steps

1. **Replace Dummy API**: Connect to real Spring Boot backend
2. **Add Authentication**: Implement token-based authentication
3. **Image Loading**: Add Glide for temple images
4. **Date/Time Formatting**: Improve date display formatting
5. **Live Queue**: Implement LiveQueueFlowActivity
6. **Booking Details**: Create BookingDetailsActivity
7. **Profile Setup**: Create ProfileSetupActivity
8. **Language Selection**: Create LanguageSelectionActivity

## Testing

- All screens are functional with mock data
- Navigation between screens works
- API calls simulate network delays (1-2 seconds)
- Error handling in place

## Troubleshooting

If you encounter issues:
1. Check that all dependencies are synced
2. Verify AndroidManifest.xml has all activities registered
3. Ensure data binding is enabled in build.gradle
4. Check that all layout files exist and are properly named

