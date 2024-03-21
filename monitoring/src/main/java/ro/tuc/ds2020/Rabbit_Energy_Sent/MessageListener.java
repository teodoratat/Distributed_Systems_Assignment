package ro.tuc.ds2020.Rabbit_Energy_Sent;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import ro.tuc.ds2020.dtos.TextMessage;
import ro.tuc.ds2020.entities.Device;
import ro.tuc.ds2020.entities.EnergySent;
import ro.tuc.ds2020.entities.HourlyEnergyConsumption;
import ro.tuc.ds2020.repositories.DeviceRepository;
import ro.tuc.ds2020.repositories.EnergySentRepository;
import ro.tuc.ds2020.services.MeasurementService;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class MessageListener {

    private final EnergySentRepository energySentService;
    private final DeviceRepository deviceRepository;
    private final MeasurementService measurementService;
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public MessageListener(EnergySentRepository energySentService, DeviceRepository deviceRepository, MeasurementService measurementService,SimpMessagingTemplate messagingTemplate) {
        this.energySentService = energySentService;
        this.deviceRepository = deviceRepository;
        this.measurementService = measurementService;
        this.messagingTemplate = messagingTemplate;
    }

    private final Object lock = new Object();

    @RabbitListener(queues = MQConfig.QUEUE)
    public void listen(CustomMessage message) {
        System.out.println("Received message: " + message);

        // Extract relevant information from the message and create an EnergySent object
        UUID deviceId = UUID.fromString(message.getDevice_id()); // Assuming device_id is a string representation of UUID
        System.out.println("Device id: " + deviceId);
        double measurementValue = message.getMeasurement_value();

        String timestampString = message.getTimestamp();
        LocalDateTime timestamp = LocalDateTime.parse(timestampString, DateTimeFormatter.ISO_DATE_TIME);
        long millisecondsSinceEpoch = timestamp.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        Device device = deviceRepository.findById(deviceId).orElse(null);
        System.out.println("Device: " + device);
        EnergySent energySent = new EnergySent();
        energySent.setDate(timestamp);
        energySent.setValue(measurementValue);
        energySent.setDevice(device);

        // Save the EnergySent object using the service
        energySentService.save(energySent);

        // Use synchronized block to ensure exclusive access to the list
        synchronized (lock) {
            List<EnergySent> energySentList = energySentService.findAllByDevice(device);
            if (energySentList.size() % 6 == 0) {
                LocalDateTime endDate = energySent.getDate();
                LocalDateTime startDate = endDate.minusHours(1);
                measurementService.calculateTotalEnergyConsumption(deviceId, startDate, endDate);

                Optional<List<HourlyEnergyConsumption>> hourlyEnergyConsumptionList = measurementService.findAllByDeviceId(deviceId);
                hourlyEnergyConsumptionList.ifPresent(hourlyEnergyConsumptions -> {
                    HourlyEnergyConsumption lastHourlyEnergyConsumption = hourlyEnergyConsumptions.get(hourlyEnergyConsumptions.size() - 1);

                    double threshold = device.getMaxHourlyEnergyConsumption();
                    if (lastHourlyEnergyConsumption.getTotalConsumption() > threshold) {
                        // Send WebSocket message
                        sendWebSocketMessage("Warning: Device with id: "+ device.getId()+ " This device exceeds maximum hourly energy consumption. Value: "
                                + lastHourlyEnergyConsumption.getTotalConsumption()
                                + ", Threshold: " + threshold
                                + ", Time: " + lastHourlyEnergyConsumption.getDate());
                    } else {
                        System.out.println("Hourly energy consumption is within limits.");
                    }
                });
            }
        }
    }

    // Helper method to send WebSocket message
    private void sendWebSocketMessage(String message) {
        TextMessage textMessage = new TextMessage();
        textMessage.setMessage(message);
        messagingTemplate.convertAndSend("/topic/message", textMessage);
    }
}
