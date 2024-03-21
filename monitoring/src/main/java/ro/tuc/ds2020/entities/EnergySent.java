package ro.tuc.ds2020.entities;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;
/**
 * this is the measurement sent every time to a device - the consumption
 */

@Entity
@Table(name = "energy_sent")
public class EnergySent {
    @Id
    @SequenceGenerator(name = "user_sequence", sequenceName = "user_sequence", allocationSize = 50)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_sequence")
    private int id;

    @Column(name = "time", nullable = false)
    private LocalDateTime date;

    @Column(name = "value", nullable = false)
    private double value;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "deviceId")
    private Device device;

    public EnergySent(int id, LocalDateTime date, double value, Device device) {
        this.id = id;
        this.date = date;
        this.value = value;
        this.device = device;
    }
    public EnergySent(){

    }



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    public UUID getDeviceId() {
        return device.getId();
    }
}