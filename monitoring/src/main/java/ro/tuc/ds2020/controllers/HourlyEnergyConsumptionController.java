package ro.tuc.ds2020.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ro.tuc.ds2020.dtos.EnergySentDTO;
import ro.tuc.ds2020.dtos.HourlyEnergyConsumptionDTO;
import ro.tuc.ds2020.dtos.builders.HourlyEnergyConsumptionBuilder;
import ro.tuc.ds2020.entities.HourlyEnergyConsumption;
import ro.tuc.ds2020.services.MeasurementService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/hourly_energy_consumption")
public class HourlyEnergyConsumptionController {
    private final MeasurementService measurementService;
    @Autowired
    public HourlyEnergyConsumptionController(MeasurementService measurementService){
        this.measurementService = measurementService;
    }
    /*@GetMapping("/{deviceId}")
    public ResponseEntity<List<HourlyEnergyConsumptionDTO>> getAllEnergySents(@PathVariable("deviceId") UUID deviceId) {
        Optional<List<HourlyEnergyConsumption>> hourlyEnergyConsumptionList = measurementService.findAllByDeviceId(deviceId);

        if (hourlyEnergyConsumptionList.isPresent()) {
            List<HourlyEnergyConsumptionDTO> hourlyEnergyConsumptionDTOs = hourlyEnergyConsumptionList.get()
                    .stream()
                    .map(HourlyEnergyConsumptionBuilder::toHourlyEnergyConsumptionDTO)
                    .collect(Collectors.toList());

            return new ResponseEntity<>(hourlyEnergyConsumptionDTOs, HttpStatus.OK);
        } else {
            // Handle the case where no data is found for the given deviceId
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }*/
    @GetMapping("/devicess/{deviceId}/{date}")
    public ResponseEntity<?> getEnergyDataByDeviceIdAndDate(
            @PathVariable UUID deviceId,
            @PathVariable(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        if (date != null) {
            System.out.println("is in controller date not null");
            Optional<List<HourlyEnergyConsumption>> result = measurementService.findAllByDeviceIdAndDate(deviceId, date);
            if (result.isPresent() && !result.get().isEmpty()) {
                List<HourlyEnergyConsumptionDTO> hourlyEnergyConsumptionDTOs = result.get()
                        .stream()
                        .map(HourlyEnergyConsumptionBuilder::toHourlyEnergyConsumptionDTO)
                        .collect(Collectors.toList());
                return new ResponseEntity<>(hourlyEnergyConsumptionDTOs, HttpStatus.OK);
            } else {
                return new ResponseEntity<>("No data found for the given device and date.", HttpStatus.NOT_FOUND);
            }
        } else {
            System.out.println("date is null");
            Optional<List<HourlyEnergyConsumption>> result = measurementService.findAllByDeviceId(deviceId);
            if (result.isPresent() && !result.get().isEmpty()) {
                List<HourlyEnergyConsumptionDTO> hourlyEnergyConsumptionDTOs = result.get()
                        .stream()
                        .map(HourlyEnergyConsumptionBuilder::toHourlyEnergyConsumptionDTO)
                        .collect(Collectors.toList());
                return new ResponseEntity<>(hourlyEnergyConsumptionDTOs, HttpStatus.OK);
            } else {
                return new ResponseEntity<>("No data found for the given device.", HttpStatus.NOT_FOUND);
            }
        }
    }


}
