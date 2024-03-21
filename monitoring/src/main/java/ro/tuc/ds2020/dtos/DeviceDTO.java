package ro.tuc.ds2020.dtos;

import java.util.UUID;

public class DeviceDTO {

    private UUID id;
    private UUID userId;
    private int maxHourlyEnergyConsumption;

    public DeviceDTO() {
    }

    public DeviceDTO(UUID id, UUID userId, int maxHourlyEnergyConsumption) {
        this.id = id;
        this.userId = userId;
        this.maxHourlyEnergyConsumption = maxHourlyEnergyConsumption;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public int getMaxHourlyEnergyConsumption() {
        return maxHourlyEnergyConsumption;
    }

    public void setMaxHourlyEnergyConsumption(int maxHourlyEnergyConsumption) {
        this.maxHourlyEnergyConsumption = maxHourlyEnergyConsumption;
    }
}
