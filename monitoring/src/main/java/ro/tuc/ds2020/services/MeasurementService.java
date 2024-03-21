package ro.tuc.ds2020.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ro.tuc.ds2020.dtos.HourlyEnergyConsumptionDTO;
import ro.tuc.ds2020.dtos.builders.HourlyEnergyConsumptionBuilder;
import ro.tuc.ds2020.entities.Device;
import ro.tuc.ds2020.entities.EnergySent;
import ro.tuc.ds2020.entities.HourlyEnergyConsumption;
import ro.tuc.ds2020.repositories.DeviceRepository;
import ro.tuc.ds2020.repositories.EnergySentRepository;
import ro.tuc.ds2020.repositories.HourlyEnergyConsumptionRepository;

import java.time.*;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class MeasurementService {
    private final DeviceRepository deviceRepository;
    private final EnergySentRepository energySentRepository;
    private final HourlyEnergyConsumptionRepository hourlyEnergyConsumptionRepository;

    @Autowired
    public MeasurementService(DeviceRepository deviceRepository, EnergySentRepository energySentRepository, HourlyEnergyConsumptionRepository hourlyEnergyConsumptionRepository) {
        this.deviceRepository = deviceRepository;
        this.energySentRepository = energySentRepository;
        this.hourlyEnergyConsumptionRepository = hourlyEnergyConsumptionRepository;
    }

    public List<HourlyEnergyConsumptionDTO> findHourlyEnergyConsumptions() {
        List<HourlyEnergyConsumption> devices = hourlyEnergyConsumptionRepository.findAll();
        return devices.stream()
                .map(HourlyEnergyConsumptionBuilder::toHourlyEnergyConsumptionDTO)
                .collect(Collectors.toList());
    }

    public void calculateAndStoreHourlyEnergyConsumption(UUID deviceId) {
        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new RuntimeException("Device not found with id: " + deviceId));

        // Get the earliest date for the device
        LocalDateTime earliestDate = energySentRepository.findFirstDateByDeviceOrderByDateAsc(device)
                .orElse(LocalDateTime.now());
        System.out.println("Earliest Date: " + earliestDate);


        LocalDateTime latestDate = energySentRepository.findLastDateByDeviceOrderByDateDesc(device)
                .orElse(LocalDateTime.now());
        System.out.println("Latest Date: " + latestDate);
        // Iterate over each hour within the range of earliestDate to currentDateTime*/
        LocalDateTime startTime = earliestDate;
        while (startTime.isBefore(latestDate)) {
            // Calculate the end time for the current hour
            System.out.println("intru in while");
            LocalDateTime endTime = startTime.plusHours(1);
            System.out.println("prima ora: "+ endTime);
            // Fetch energy sent records for the device and the time interval
            List<EnergySent> energySentList = energySentRepository.findAllByDeviceAndDateBetween(device, startTime, endTime);
            for (EnergySent energySent : energySentList) {
                System.out.println("Date: " + energySent.getDate() + ", Value: " + energySent.getValue());
            }
            // Calculate the total energy consumption for the time interval
            double totalConsumption = energySentList.stream()
                    .mapToDouble(EnergySent::getValue)
                    .sum();

            // Create a new HourlyEnergyConsumption record
            HourlyEnergyConsumption hourlyEnergyConsumption = new HourlyEnergyConsumption();
            hourlyEnergyConsumption.setDevice(device);
            hourlyEnergyConsumption.setTotalConsumption(totalConsumption);
            hourlyEnergyConsumption.setDate(startTime);

            // Save the calculated hourly energy consumption
            hourlyEnergyConsumptionRepository.save(hourlyEnergyConsumption);

            // Move to the next hour
            startTime = endTime;
        }
        System.out.println("is afara");
    }
    public Optional<List<HourlyEnergyConsumption>> findAllByDeviceId(UUID deviceId) {
        return hourlyEnergyConsumptionRepository.findAllByDeviceId(deviceId);
    }
    public Optional<List<HourlyEnergyConsumption>> findAllByDeviceIdAndDate(UUID deviceId, LocalDate date) {
        System.out.println("ziua mea"+ date);
        LocalDateTime startOfDay = LocalDateTime.of(date, LocalTime.MIDNIGHT);
        System.out.println("start of day"+startOfDay);
        LocalDateTime startOfNextDay = startOfDay.plusDays(1);
        System.out.println("start of next day"+startOfNextDay);

        return hourlyEnergyConsumptionRepository.findAllByDeviceIdAndDate(deviceId, startOfDay, startOfNextDay);
    }
    public void calculateAndStoreHourlyEnergyConsumption1(UUID deviceId) {
        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new RuntimeException("Device not found with id: " + deviceId));

        // Get the latest energy sent record for the device
        LocalDateTime latestDate = energySentRepository.findLastDateByDeviceOrderByDateDesc(device)
                .orElse(LocalDateTime.now());
        System.out.println("Latest Date: " + latestDate);

        // Calculate the start time for the current hour
        LocalDateTime startTime = latestDate.minusHours(1);

        // Fetch energy sent records for the device and the time interval
        List<EnergySent> energySentList = energySentRepository.findAllByDeviceAndDateBetween(device, startTime, latestDate);
        for (EnergySent energySent : energySentList) {
            System.out.println("Date: " + energySent.getDate() + ", Value: " + energySent.getValue());
        }

        // Check if the number of energy sent records is a multiple of 6
        if (energySentList.size() % 6 == 0) {
            // Calculate the total energy consumption for the time interval
            double totalConsumption = energySentList.stream()
                    .mapToDouble(EnergySent::getValue)
                    .sum();

            // Create a new HourlyEnergyConsumption record
            HourlyEnergyConsumption hourlyEnergyConsumption = new HourlyEnergyConsumption();
            hourlyEnergyConsumption.setDevice(device);
            hourlyEnergyConsumption.setTotalConsumption(totalConsumption);
            hourlyEnergyConsumption.setDate(startTime);

            // Save the calculated hourly energy consumption
            hourlyEnergyConsumptionRepository.save(hourlyEnergyConsumption);

            System.out.println("Hourly energy consumption calculated and stored for time: "
                    + startTime + ", value: " + totalConsumption);
        } else {
            System.out.println("Number of energy sent records is not a multiple of 6.");
        }
    }
    public void calculateTotalEnergyConsumption(UUID deviceId, LocalDateTime startDate, LocalDateTime endDate) {
        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new RuntimeException("Device not found with id: " + deviceId));

        // Fetch energy sent records for the device and the time interval
        List<EnergySent> energySentList = energySentRepository.findAllByDeviceAndDateBetween(device, startDate, endDate);
        for (EnergySent energySent : energySentList) {
            System.out.println("Date: " + energySent.getDate() + ", Value: " + energySent.getValue());
        }

        // Calculate the total energy consumption for the time interval
        double totalConsumption = energySentList.stream()
                .mapToDouble(EnergySent::getValue)
                .sum();

        // Create a new HourlyEnergyConsumption record
        HourlyEnergyConsumption hourlyEnergyConsumption = new HourlyEnergyConsumption();
        hourlyEnergyConsumption.setDevice(device);
        hourlyEnergyConsumption.setTotalConsumption(totalConsumption);
        hourlyEnergyConsumption.setDate(startDate);

        // Save the calculated hourly energy consumption
        hourlyEnergyConsumptionRepository.save(hourlyEnergyConsumption);

        System.out.println("Total energy consumption calculated and stored for time interval: "
                + startDate + " to " + endDate + ", value: " + totalConsumption);
    }

}
