package ro.tuc.ds2020.Rabbit_Devices;



import java.io.Serializable;
import java.util.UUID;



public class DeviceMessage implements Serializable {

    private String deviceId; // Match with the producer
    private String userId;
    private int maxHourlyEnergy; // Match with the producer
    private String action;

   public DeviceMessage(String deviceId, String userId, int maxHourlyEnergy, String action) {
        this.deviceId = deviceId;
        this.userId = userId;
        this.maxHourlyEnergy = maxHourlyEnergy;
        this.action = action;
    }
    public DeviceMessage(String deviceId, String userId, String action) {
        this.deviceId = deviceId;
        this.userId = userId;
        this.action = action;
    }
    public DeviceMessage(){

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
}
