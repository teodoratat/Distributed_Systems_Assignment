package ro.tuc.ds2020.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ro.tuc.ds2020.dtos.EnergySentDTO;
import ro.tuc.ds2020.services.EnergySentService;

import java.util.List;

@RestController
@RequestMapping("/energy-sent")
public class EnergySentController {

    private final EnergySentService energySentService;

    @Autowired
    public EnergySentController(EnergySentService energySentService) {
        this.energySentService = energySentService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<EnergySentDTO>> getAllEnergySents() {
        List<EnergySentDTO> energySents = energySentService.findAll();
        return new ResponseEntity<>(energySents, HttpStatus.OK);
    }

    // Add other endpoints as needed for CRUD operations
}
