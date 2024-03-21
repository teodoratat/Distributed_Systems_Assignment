package ro.tuc.ds2020.dtos;

import ro.tuc.ds2020.entities.User;

import javax.validation.constraints.NotNull;
import java.util.Objects;
import java.util.UUID;


public class DeviceDetailsDTO {

    private UUID id;
    @NotNull
    private String name;
    @NotNull
    private String description;
    @NotNull
    private String address;
    @NotNull
    private int maximumEnergyConsumption;

    private User user;

    public DeviceDetailsDTO(String name, String description, String address, int maximumEnergyConsumption, User user) {
        this.name = name;
        this.description = description;
        this.address = address;
        this.maximumEnergyConsumption = maximumEnergyConsumption;
        this.user = user;
    }
    public DeviceDetailsDTO(){
    }

    public DeviceDetailsDTO(UUID id, String name, String description, String address, int maximumEnergyConsumption, User user) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.address = address;
        this.maximumEnergyConsumption = maximumEnergyConsumption;
        this.user = user;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getMaximumEnergyConsumption() {
        return maximumEnergyConsumption;
    }

    public void setMaximumEnergyConsumption(int maximumEnergyConsumption) {
        this.maximumEnergyConsumption = maximumEnergyConsumption;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeviceDetailsDTO that = (DeviceDetailsDTO) o;
        return  Objects.equals(name, that.name)
                && Objects.equals(description, that.description)
                && Objects.equals(address, that.address)
                && maximumEnergyConsumption == that.maximumEnergyConsumption;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description,address,maximumEnergyConsumption);
    }
}