package ro.tuc.ds2020.controllers;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ro.tuc.ds2020.Rabbit.DeviceMessage;
import ro.tuc.ds2020.Rabbit.MQConfig;
import ro.tuc.ds2020.dtos.DeviceDTO;
import ro.tuc.ds2020.dtos.DeviceDetailsDTO;
import ro.tuc.ds2020.services.DeviceService;
import com.google.gson.*;
import ro.tuc.ds2020.services.UserService;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin
@RequestMapping(value = "/device")
public class DeviceController {

    private final DeviceService deviceService;
    private final UserService userService;
    private final RabbitTemplate rabbitTemplate;
    @Autowired
    public DeviceController(DeviceService deviceService, UserService userService, RabbitTemplate rabbitTemplate) {
        this.deviceService = deviceService;
        this.userService = userService;
        this.rabbitTemplate = rabbitTemplate;
    }

    @GetMapping()
    public ResponseEntity<List<DeviceDTO>> getDevices() {
        List<DeviceDTO> dtos = deviceService.findDevices();
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }
    @GetMapping("/all")
    public ResponseEntity<List<DeviceDTO>> getDevices2() {
        List<DeviceDTO> dtos = deviceService.findDevicesWithUsers();
        return new ResponseEntity<>(dtos, HttpStatus.OK);}

    @GetMapping("/alll")
    public ResponseEntity<List<DeviceDTO>> getDevices3() {
        List<DeviceDTO> dtos = deviceService.findAllDevicesWithUsers();
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }
    @PostMapping("/insert")
    public ResponseEntity<UUID> insertDevice(@Valid @RequestBody DeviceDetailsDTO deviceDTO) {
        UUID deviceID = deviceService.insert(deviceDTO);
        System.out.println(deviceDTO.getId());
        deviceDTO.setId(deviceID);
        System.out.println(deviceID);
        //sendDeviceMessagebun(deviceDTO,"insert");
       // sendDeviceMessage1(deviceID,deviceDTO.getUser().getId(), deviceDTO.getMaximumEnergyConsumption(),"insert");
        return new ResponseEntity<>(deviceID, HttpStatus.CREATED);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<DeviceDetailsDTO> getDevice(@PathVariable("id") UUID deviceId) {
        DeviceDetailsDTO dto = deviceService.findDeviceById(deviceId);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }
    @GetMapping(value = "/devices")
    public ResponseEntity<List<DeviceDetailsDTO>> getUserDevices(String username) {

        List<DeviceDetailsDTO> devices = userService.findDevicesByUsername(username);
        return new ResponseEntity<>(devices, HttpStatus.OK);
    }
    @PutMapping(value = "/update/{id}")
    public ResponseEntity<DeviceDetailsDTO> updateDevice(
            @PathVariable("id") UUID deviceId,
            @Valid @RequestBody DeviceDetailsDTO deviceDetailsDTO
    ) {
        DeviceDetailsDTO updatedDevice = deviceService.update(deviceId, deviceDetailsDTO);
        //sendDeviceMessagebun(updatedDevice, "update");
        return new ResponseEntity<>(updatedDevice, HttpStatus.OK);
    }

    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity<String> deleteDevice(@PathVariable("id") UUID deviceId) {
        DeviceDetailsDTO deviceDTO = deviceService.findDeviceById(deviceId);
        //sendDeviceMessagebun(deviceDTO, "delete");
        deviceService.delete(deviceId);
        String responseMessage = "Device with ID " + deviceId + " has been successfully deleted.";
        return new ResponseEntity<>(responseMessage, HttpStatus.OK);
    }

    private void sendDeviceMessage(DeviceDTO deviceDetailsDTO, String action) {
        UUID deviceId = deviceDetailsDTO.getId();
        System.out.println(deviceId);
        UUID userId = deviceDetailsDTO.getUser().getId();
        int maxHourlyEnergy = deviceDetailsDTO.getMaximumEnergyConsumption();
        System.out.println(maxHourlyEnergy);
        // Include the operation ("insert", "update", etc.)
        DeviceMessage deviceMessage = new DeviceMessage(deviceId.toString(), userId.toString(), maxHourlyEnergy, action);
        System.out.println(deviceMessage.getDeviceId()) ;
        System.out.println(deviceMessage.getUserId());
        System.out.println(deviceMessage.getMaxHourlyEnergy());
        rabbitTemplate.convertAndSend(MQConfig.QUEUE_DEVICE, deviceMessage);
    }
    private void sendDeviceMessagebun(DeviceDetailsDTO deviceDetailsDTO, String action) {
        UUID deviceId = deviceDetailsDTO.getId();
        UUID userId = deviceDetailsDTO.getUser().getId();
        int maxHourlyEnergy = deviceDetailsDTO.getMaximumEnergyConsumption();

        // Convert UUIDs to String
        String deviceIdString = deviceId.toString();
        String userIdString = userId.toString();

        // Include the operation ("insert", "update", etc.)
        DeviceMessage deviceMessage = new DeviceMessage(deviceIdString, userIdString, maxHourlyEnergy, action);

        System.out.println("din mesaj: "+ deviceMessage.getDeviceId()) ;
        System.out.println("din mesaj maxH: "+ deviceMessage.getMaxHourlyEnergy());
        // Convert the DeviceMessage to JSON
        Gson gson = new Gson();
        String jsonMessage = gson.toJson(deviceMessage);

        // Send the JSON message to RabbitMQ
        rabbitTemplate.convertAndSend(MQConfig.QUEUE_DEVICE, jsonMessage);
    }


   /* private void sendDeviceMessage1(UUID deviceId, UUID userId, int maxHourlyEnergy, String action) {
        DeviceMessage deviceMessage = new DeviceMessage(deviceId, userId, maxHourlyEnergy, action);
        rabbitTemplate.convertAndSend(MQConfig.QUEUE_DEVICE, deviceMessage);
    }
*/
    private void sendDeleteMessage(String deviceId) {
        // Send a message to RabbitMQ indicating deletion
        DeviceMessage deviceMessage = new DeviceMessage(deviceId, null, 0, "delete");
        rabbitTemplate.convertAndSend(MQConfig.QUEUE_DEVICE, deviceMessage);
    }
}
