package ro.tuc.ds2020.dtos.builders;

import ro.tuc.ds2020.dtos.DeviceDTO;
import ro.tuc.ds2020.dtos.DeviceDetailsDTO;
import ro.tuc.ds2020.entities.Device;

public class DeviceBuilder {

    private DeviceBuilder() {
    }

    public static DeviceDTO toDeviceDTO(Device device) {
        return new DeviceDTO(device.getId(), device.getName(), device.getAddress());
    }
    public static DeviceDTO toDeviceDTO2(Device device) {
        return new DeviceDTO(device.getId(), device.getName(), device.getAddress(),device.getUser());
    }
    public static DeviceDTO toDeviceDTO3(Device device) {
        return new DeviceDTO(device.getId(), device.getName(), device.getAddress(),device.getUser(),device.getDescription(),device.getMaximumEnergyConsumption());
    }
    public static DeviceDetailsDTO toDeviceDetailsDTO(Device device) {
        return new DeviceDetailsDTO(device.getId(), device.getName(), device.getDescription(), device.getAddress(), device.getMaximumEnergyConsumption(), device.getUser());
    }

    public static Device toEntity(DeviceDetailsDTO deviceDetailsDTO) {
        return new Device( deviceDetailsDTO.getId(), deviceDetailsDTO.getName(), deviceDetailsDTO.getDescription(), deviceDetailsDTO.getAddress(), deviceDetailsDTO.getMaximumEnergyConsumption(), deviceDetailsDTO.getUser());
    }
}