package ro.tuc.ds2020.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import ro.tuc.ds2020.Rabbit.DeviceMessage;
import ro.tuc.ds2020.Rabbit.MQConfig;
import ro.tuc.ds2020.controllers.handlers.exceptions.model.ResourceNotFoundException;
import ro.tuc.ds2020.dtos.DeviceDTO;
import ro.tuc.ds2020.dtos.DeviceDetailsDTO;
import ro.tuc.ds2020.dtos.builders.DeviceBuilder;
import ro.tuc.ds2020.entities.Device;
import ro.tuc.ds2020.repositories.DeviceRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DeviceService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceService.class);

    private final DeviceRepository deviceRepository;
    private final RabbitTemplate rabbitTemplate;
    private final UserService userService;
    @Autowired
    public DeviceService(DeviceRepository deviceRepository, RabbitTemplate rabbitTemplate, UserService userService) {
        this.deviceRepository = deviceRepository;
        this.rabbitTemplate = rabbitTemplate;
        this.userService = userService;
    }

    public List<DeviceDTO> findDevices() {
        List<Device> deviceList = deviceRepository.findAll();
        return deviceList.stream()
                .map(DeviceBuilder::toDeviceDTO)
                .collect(Collectors.toList());
    }
    public List<DeviceDTO> findDevicesWithUsers() {
        List<Device> deviceList = deviceRepository.findAll();
        return deviceList.stream()
                .map(DeviceBuilder::toDeviceDTO2)
                .collect(Collectors.toList());
    }
    public List<DeviceDTO> findAllDevicesWithUsers() {
        List<Device> deviceList = deviceRepository.findAll();
        return deviceList.stream()
                .map(DeviceBuilder::toDeviceDTO3)
                .collect(Collectors.toList());
    }
    public DeviceDetailsDTO findDeviceById(UUID id) {
        Optional<Device> prosumerOptional = deviceRepository.findById(id);
        if (!prosumerOptional.isPresent()) {
            LOGGER.error("Device with id {} was not found in db", id);
            throw new ResourceNotFoundException(Device.class.getSimpleName() + " with id: " + id);
        }
        return DeviceBuilder.toDeviceDetailsDTO(prosumerOptional.get());
    }
    @GetMapping(value = "/devices")
    public ResponseEntity<List<DeviceDetailsDTO>> getUserDevices(String username) {
        List<DeviceDetailsDTO> devices = userService.findDevicesByUsername(username);
        return new ResponseEntity<>(devices, HttpStatus.OK);
    }
    public UUID insert(DeviceDetailsDTO deviceDTO) {
        Device device = DeviceBuilder.toEntity(deviceDTO);
        System.out.println(deviceDTO.getUser().getId());
        System.out.println(deviceDTO.getMaximumEnergyConsumption());
        device = deviceRepository.save(device);
        //DeviceMessage deviceMessage = new DeviceMessage(device.getId(), device.getUser().getId(),device.getMaximumEnergyConsumption(),"insert");
        //sendDeviceMessage(deviceDTO, "insert");
        LOGGER.debug("Device with id {} was inserted in db", device.getId());
        return device.getId();
    }

    public DeviceDetailsDTO update(UUID id, DeviceDetailsDTO deviceDTO) {
        Optional<Device> deviceOptional = deviceRepository.findById(id);
        if (!deviceOptional.isPresent()) {
            LOGGER.error("Device with id {} was not found in the database", id);
            throw new ResourceNotFoundException(Device.class.getSimpleName() + " with id: " + id);
        }

        Device existingDevice = deviceOptional.get();
        Device updatedDevice = DeviceBuilder.toEntity(deviceDTO);

        updatedDevice.setId(existingDevice.getId());

        Device savedDevice = deviceRepository.save(updatedDevice);
//        DeviceMessage deviceMessage = new DeviceMessage(savedDevice.getId(), savedDevice.getUser().getId(),savedDevice.getMaximumEnergyConsumption(),"update");
        //
        // sendDeviceMessage(deviceDTO, "update");
        LOGGER.debug("Device with id {} was updated in the database", id);
        return DeviceBuilder.toDeviceDetailsDTO(savedDevice);
    }

    public void delete(UUID id) {
        Optional<Device> deviceOptional = deviceRepository.findById(id);
        if (!deviceOptional.isPresent()) {
            LOGGER.error("Device with id {} was not found in the database", id);
            throw new ResourceNotFoundException(Device.class.getSimpleName() + " with id: " + id);
        }

        Device savedDevice = deviceOptional.get();
       // DeviceMessage deviceMessage = new DeviceMessage(savedDevice.getId(), savedDevice.getUser().getId(),savedDevice.getMaximumEnergyConsumption(),"delete");
        deviceRepository.deleteById(id);
        //sendDeleteMessage(id);

        LOGGER.debug("Device with id {} was deleted from the database", id);
    }


}