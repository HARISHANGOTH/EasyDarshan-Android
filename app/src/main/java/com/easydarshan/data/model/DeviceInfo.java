package com.easydarshan.data.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeviceInfo {
    public String deviceId;
    public String deviceType;
    public String deviceModel;
    public String manufacturer;
    public String osVersion;
    public String pushToken;
}
