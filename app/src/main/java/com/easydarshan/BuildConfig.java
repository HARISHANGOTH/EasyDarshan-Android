package com.easydarshan;

/**
 * Manual BuildConfig to satisfy libraries (e.g. Razorpay) that use
 * reflection to read <applicationId>.BuildConfig at runtime.
 *
 * NOTE: Keep the values in sync with app/build.gradle (versionName, versionCode, etc.).
 */
public final class BuildConfig {
    public static final boolean DEBUG = true;
    public static final String APPLICATION_ID = "com.easydarshan";
    public static final String BUILD_TYPE = "debug";
    public static final int VERSION_CODE = 1;
    public static final String VERSION_NAME = "1.0";

    private BuildConfig() {
        // no instances
    }
}



