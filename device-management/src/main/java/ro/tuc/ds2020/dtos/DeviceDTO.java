package ro.tuc.ds2020.dtos;

import ro.tuc.ds2020.entities.User;

import java.util.UUID;

public class DeviceDTO  {
    private UUID id;
    private String name;
    private String address;
private User user;
private String description ;
private int maximumEnergyConsumption;
    public DeviceDTO(UUID id, String name, String address, User user) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.user = user;
    }

    public DeviceDTO(UUID id, String name, String address) {
        this.id = id;
        this.name = name;
        this.address = address;
    }
    public DeviceDTO(){

    }

    public DeviceDTO(UUID id, String name, String address, User user, String description, int maximumEnergyConsumption) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.user = user;
        this.description = description;
        this.maximumEnergyConsumption = maximumEnergyConsumption;
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
}