package ro.tuc.ds2020.dtos.builders;

import ro.tuc.ds2020.dtos.DeviceDTO;
import ro.tuc.ds2020.entities.Device;

import java.util.UUID;

public class DeviceBuilder {

    private DeviceBuilder() {
    }

    public static DeviceDTO toDeviceDTO(Device device) {
        return new DeviceDTO(device.getId(),device.getUserId(), device.getMaxHourlyEnergyConsumption());
    }

    public static Device toEntity(DeviceDTO deviceDetailsDTO) {
        return new Device( deviceDetailsDTO.getId(), deviceDetailsDTO.getUserId(), deviceDetailsDTO.getMaxHourlyEnergyConsumption());
    }
}
