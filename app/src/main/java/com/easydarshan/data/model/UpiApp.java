package com.easydarshan.data.model;

import android.graphics.drawable.Drawable;

public class UpiApp {
    private String name;
    private String packageName;
    private Drawable icon;

    public UpiApp(String name, String packageName, Drawable icon) {
        this.name = name;
        this.packageName = packageName;
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public String getPackageName() {
        return packageName;
    }

    public Drawable getIcon() {
        return icon;
    }
}
