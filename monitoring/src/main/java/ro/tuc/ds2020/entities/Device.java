package ro.tuc.ds2020.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;
/**
 * this is an entity used to notify the client: if the maxHourlyEnergy is > than the threshold, then the user
 * is notified
 */
@Entity
@Table(name = "devices")
public class Device {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(name = "user_id",columnDefinition = "BINARY(16)")
    private UUID userId;

    @Column(name = "max_hourly_energy_consumption")
    private int maxHourlyEnergyConsumption;

    public int getMaxHourlyEnergyConsumption() {
        return maxHourlyEnergyConsumption;
    }

    public void setMaxHourlyEnergyConsumption(int maxHourlyEnergyConsumption) {
        this.maxHourlyEnergyConsumption = maxHourlyEnergyConsumption;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Device(UUID id, UUID userId, int maxHourlyEnergyConsumption) {
        this.id = id;
        this.userId = userId;
        this.maxHourlyEnergyConsumption = maxHourlyEnergyConsumption;
    }

    public Device() {
    }
}
