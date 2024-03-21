package ro.tuc.ds2020.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ro.tuc.ds2020.dtos.DeviceDTO;
import ro.tuc.ds2020.dtos.builders.DeviceBuilder;
import ro.tuc.ds2020.entities.Device;
import ro.tuc.ds2020.repositories.DeviceRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DeviceService {

    private final DeviceRepository deviceRepository;

    @Autowired
    public DeviceService(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    public List<DeviceDTO> findDevices() {
        List<Device> devices = deviceRepository.findAll();
        return devices.stream()
                .map(DeviceBuilder::toDeviceDTO)
                .collect(Collectors.toList());
    }

    public DeviceDTO findDeviceById(UUID deviceId) {
        Optional<Device> deviceOptional = deviceRepository.findById(deviceId);
        return deviceOptional.map(DeviceBuilder::toDeviceDTO).orElse(null);
    }

    public UUID insert(DeviceDTO deviceDTO) {
        Device device = DeviceBuilder.toEntity(deviceDTO);
        deviceRepository.save(device);
        return device.getId();
    }

    public DeviceDTO update(UUID deviceId, DeviceDTO updatedDeviceDTO) {
        Optional<Device> existingDeviceOptional = deviceRepository.findById(deviceId);
        if (existingDeviceOptional.isPresent()) {
            Device existingDevice = existingDeviceOptional.get();
            // Update the properties of the existing device
            existingDevice.setUserId(updatedDeviceDTO.getUserId());
            existingDevice.setMaxHourlyEnergyConsumption(updatedDeviceDTO.getMaxHourlyEnergyConsumption());

            deviceRepository.save(existingDevice);
            return DeviceBuilder.toDeviceDTO(existingDevice);
        }
        return null;
    }

    public void delete(UUID deviceId) {
        deviceRepository.deleteById(deviceId);
    }
    public Device findById(UUID deviceId) {
        Optional<Device> deviceOptional = deviceRepository.findById(deviceId);
        return deviceOptional.orElseThrow(() -> new RuntimeException("Device not found with id: " + deviceId));
    }
}
