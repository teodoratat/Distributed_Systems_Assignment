package ro.tuc.ds2020.Rabbit_Devices;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.google.gson.Gson;
import ro.tuc.ds2020.dtos.DeviceDTO;
import ro.tuc.ds2020.entities.Device;
import ro.tuc.ds2020.services.DeviceService;

import java.util.UUID;

@Component
public class DeviceListener {

    @Autowired
    private DeviceService deviceService;
    @RabbitListener(queues = MQConfigDevice.QUEUE_DEVICE)
    public void processDeviceMessage(String jsonMessage) {
        // Manually deserialize the JSON string
        Gson gson = new Gson();
        DeviceMessage deviceMessage = gson.fromJson(jsonMessage, DeviceMessage.class);

        UUID id = UUID.fromString(deviceMessage.getDeviceId());
        UUID userId = UUID.fromString(deviceMessage.getUserId());
        int maxHourlyEnergy = deviceMessage.getMaxHourlyEnergy();
        System.out.println("Received device message: " + deviceMessage);
        Device device = new Device(id,userId, maxHourlyEnergy);
        System.out.println(device.getId()+ " "+ device.getUserId()+ " "+ device.getMaxHourlyEnergyConsumption());

        switch (deviceMessage.getAction()) {
            case "insert":
                insertDevice(deviceMessage);
                break;
            case "update":
                updateDevice(deviceMessage);
                break;
            case "delete":
                deleteDevice(deviceMessage);
                break;
            default:
                System.out.println("Unknown action: " + deviceMessage.getAction());
        }
    }

    private void insertDevice(DeviceMessage deviceMessage) {
        // Implement logic to insert the device
        // You can use deviceService.insert() or your preferred method
        UUID id = UUID.fromString(deviceMessage.getDeviceId());
        UUID userId = UUID.fromString(deviceMessage.getUserId());
        int maxHourlyEnergy = deviceMessage.getMaxHourlyEnergy();
        deviceService.insert(new DeviceDTO(id, userId, maxHourlyEnergy));
    }

    private void updateDevice(DeviceMessage deviceMessage) {
        UUID id = UUID.fromString(deviceMessage.getDeviceId());
        UUID userId = UUID.fromString(deviceMessage.getUserId());
        int maxHourlyEnergy = deviceMessage.getMaxHourlyEnergy();
        deviceService.update(id, new DeviceDTO(id, userId, maxHourlyEnergy));
    }

    private void deleteDevice(DeviceMessage deviceMessage) {
        // Implement logic to delete the device
        // You can use deviceService.delete() or your preferred method
        deviceService.delete(UUID.fromString(deviceMessage.getDeviceId()));
    }
}