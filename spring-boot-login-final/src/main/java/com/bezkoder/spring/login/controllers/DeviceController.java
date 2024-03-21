package com.bezkoder.spring.login.controllers;

import com.bezkoder.spring.login.dtos.DeviceDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@CrossOrigin(origins="http://localhost:3000", maxAge = 3600, allowCredentials="true")
@RestController
@RequestMapping("/api/device")
public class DeviceController {
    @Value("${device.management.base.url}")
    private String deviceManagementBaseUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    @GetMapping(value = "/alll")
    public ResponseEntity<List<DeviceDTO>> getDevices() {
        ResponseEntity<DeviceDTO[]> response = restTemplate.getForEntity(deviceManagementBaseUrl+"/alll", DeviceDTO[].class);
        List<DeviceDTO> devices = Arrays.asList(response.getBody());
        return new ResponseEntity<>(devices, response.getStatusCode());
    }

    @GetMapping(value = "/user/all")
    public ResponseEntity<List<DeviceDTO>> getDevicesByUserId() {
        ResponseEntity<DeviceDTO[]> response = restTemplate.getForEntity(deviceManagementBaseUrl + "/user/all", DeviceDTO[].class);
        List<DeviceDTO> devices = Arrays.asList(response.getBody());
        return new ResponseEntity<>(devices, response.getStatusCode());
    }


    @GetMapping(value = "/devices")
    public ResponseEntity<List<DeviceDTO>> getDevicesByUsername(@RequestParam("username") String username) {
        String url = deviceManagementBaseUrl + "/devices?username=" + username;
        ResponseEntity<DeviceDTO[]> response = restTemplate.getForEntity(url, DeviceDTO[].class);
        List<DeviceDTO> devices = Arrays.asList(response.getBody());
        return new ResponseEntity<>(devices, response.getStatusCode());
    }

    @PostMapping(value = "/insert")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<UUID> insertDevice(@RequestBody DeviceDTO deviceDTO) {
        ResponseEntity<UUID> response = restTemplate.postForEntity(deviceManagementBaseUrl + "/insert", deviceDTO, UUID.class);
        return new ResponseEntity<>(response.getBody(), response.getStatusCode());
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<DeviceDTO> getDevice(@PathVariable("id") UUID deviceID) {
        String url = deviceManagementBaseUrl + "/" + deviceID;
        ResponseEntity<DeviceDTO> response = restTemplate.getForEntity(url, DeviceDTO.class);
        return new ResponseEntity<>(response.getBody(), response.getStatusCode());
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasAuthority('admin')")
    public void updateDevice(@PathVariable UUID id, @RequestBody DeviceDTO updatedDeviceDTO) {
        String url = deviceManagementBaseUrl + "/update/" + id;
        restTemplate.put(url, updatedDeviceDTO);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<Void> deleteDevice(@PathVariable UUID id) {
        String url = deviceManagementBaseUrl + "/delete/" + id;
        System.out.println("\n\n\nURL  " +url);
        restTemplate.delete(url);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
