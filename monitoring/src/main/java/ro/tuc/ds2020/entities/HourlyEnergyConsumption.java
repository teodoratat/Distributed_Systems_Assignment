package ro.tuc.ds2020.entities;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

/**
 * this is an entity used to calculate every time the new energy of a device
 */

@Entity
@Table(name = "hourly_energy_consumption")
public class HourlyEnergyConsumption implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name = "user_sequence", sequenceName = "user_sequence", allocationSize = 50)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_sequence")
    private int id;

    @Column(name = "totalEnergyConsumption", nullable = false)
    private double totalConsumption;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "deviceId")
    private Device device;
    @Column(name = "time", nullable = false)
    private LocalDateTime date;

    public HourlyEnergyConsumption() {
    }

    public HourlyEnergyConsumption(int id, double totalConsumption, Device device, LocalDateTime date) {
    this.id = id;
    this.date = date;
    this.totalConsumption = totalConsumption;
    this.device = device;
    }

    public HourlyEnergyConsumption(double totalConsumption, Device device, LocalDateTime date) {
    this.totalConsumption = totalConsumption;
    this.device = device;
    this.date = date;
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getTotalConsumption() {
        return totalConsumption;
    }

    public void setTotalConsumption(double totalConsumption) {
        this.totalConsumption = totalConsumption;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }
}
