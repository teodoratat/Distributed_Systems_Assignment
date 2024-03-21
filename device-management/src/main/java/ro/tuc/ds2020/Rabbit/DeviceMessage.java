package ro.tuc.ds2020.Rabbit;

import java.util.UUID;

public class DeviceMessage {

    private String deviceId;
    private String userId;
    private int maxHourlyEnergy;
    private String action;

    public DeviceMessage() {
    }

    public DeviceMessage(String deviceId, String userId, int maxHourlyEnergy, String action) {
        this.deviceId = deviceId;
        this.userId = userId;
        this.maxHourlyEnergy = maxHourlyEnergy;
        this.action = action;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getMaxHourlyEnergy() {
        return maxHourlyEnergy;
    }

    public void setMaxHourlyEnergy(int maxHourlyEnergy) {
        this.maxHourlyEnergy = maxHourlyEnergy;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    @Override
    public String toString() {
        return "DeviceMessage{" +
                "deviceId=" + deviceId +
                ", userId=" + userId +
                ", maxHourlyEnergy=" + maxHourlyEnergy +
                ", action='" + action + '\'' +
                '}';
    }
}
