package ro.tuc.ds2020.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ro.tuc.ds2020.dtos.DeviceDTO;
import ro.tuc.ds2020.entities.HourlyEnergyConsumption;
import ro.tuc.ds2020.services.DeviceService;
import ro.tuc.ds2020.services.MeasurementService;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin
@RequestMapping("/device")
public class DeviceController {

    private final DeviceService deviceService;
    private final MeasurementService measurementService;
    @Autowired
    public DeviceController(DeviceService deviceService,
    MeasurementService measurementService) {
        this.deviceService = deviceService;
        this.measurementService = measurementService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<DeviceDTO>> getAllDevices() {
        List<DeviceDTO> devices = deviceService.findDevices();
        return new ResponseEntity<>(devices, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DeviceDTO> getDeviceById(@PathVariable("id") UUID deviceId) {
        DeviceDTO device = deviceService.findDeviceById(deviceId);
        return new ResponseEntity<>(device, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<UUID> insertDevice(@Valid @RequestBody DeviceDTO deviceDTO) {
        UUID deviceId = deviceService.insert(deviceDTO);
        return new ResponseEntity<>(deviceId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DeviceDTO> updateDevice(
            @PathVariable("id") UUID deviceId,
            @Valid @RequestBody DeviceDTO deviceDetailsDTO
    ) {
        DeviceDTO updatedDevice = deviceService.update(deviceId, deviceDetailsDTO);
        return new ResponseEntity<>(updatedDevice, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteDevice(@PathVariable("id") UUID deviceId) {
        deviceService.delete(deviceId);
        String responseMessage = "Device with ID " + deviceId + " has been successfully deleted.";
        return new ResponseEntity<>(responseMessage, HttpStatus.OK);
    }
    @PostMapping("/calculate-hourly-energy/{deviceId}")
    public ResponseEntity<String> calculateHourlyEnergy(@PathVariable UUID deviceId) {
        measurementService.calculateAndStoreHourlyEnergyConsumption(deviceId);
        return ResponseEntity.ok("Calculation initiated for device: " + deviceId);
    }
}
