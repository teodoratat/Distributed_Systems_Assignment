package ro.tuc.ds2020.dtos.builders;

import ro.tuc.ds2020.dtos.HourlyEnergyConsumptionDTO;
import ro.tuc.ds2020.entities.HourlyEnergyConsumption;
import ro.tuc.ds2020.entities.Device;

import java.time.LocalDateTime;

public class HourlyEnergyConsumptionBuilder {

    private HourlyEnergyConsumptionBuilder() {
    }

    public static HourlyEnergyConsumptionDTO toHourlyEnergyConsumptionDTO(HourlyEnergyConsumption hourlyEnergyConsumption) {
        return new HourlyEnergyConsumptionDTO(hourlyEnergyConsumption.getId(),
                hourlyEnergyConsumption.getTotalConsumption(), hourlyEnergyConsumption.getDate());
    }

    public static HourlyEnergyConsumption toEntity(HourlyEnergyConsumptionDTO hourlyEnergyConsumptionDTO, Device device) {
        return new HourlyEnergyConsumption(hourlyEnergyConsumptionDTO.getId(),
                hourlyEnergyConsumptionDTO.getTotalConsumption(), device, hourlyEnergyConsumptionDTO.getDate());
    }

    public static HourlyEnergyConsumption toEntity(Device device, double totalConsumption, LocalDateTime date) {
        return new HourlyEnergyConsumption(totalConsumption, device, date);
    }
}
