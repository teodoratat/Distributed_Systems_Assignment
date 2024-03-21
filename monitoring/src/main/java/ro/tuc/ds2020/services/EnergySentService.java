package ro.tuc.ds2020.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ro.tuc.ds2020.dtos.EnergySentDTO;
import ro.tuc.ds2020.dtos.builders.EnergySentBuilder;
import ro.tuc.ds2020.entities.Device;
import ro.tuc.ds2020.entities.EnergySent;
import ro.tuc.ds2020.repositories.DeviceRepository;
import ro.tuc.ds2020.repositories.EnergySentRepository;
import javax.persistence.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class EnergySentService {

    private final EnergySentRepository energySentRepository;
    private final DeviceRepository deviceRepository;
    @Autowired
    public EnergySentService(EnergySentRepository energySentRepository, DeviceRepository deviceRepository) {
        this.energySentRepository = energySentRepository;
        this.deviceRepository = deviceRepository;
    }

    public List<EnergySentDTO> findAll() {
        List<EnergySent> energySents = energySentRepository.findAll();
        return energySents.stream()
                .map(EnergySentBuilder::toEnergySentDTO)
                .collect(Collectors.toList());
    }

    public Optional<EnergySentDTO> findById(int id) {
        Optional<EnergySent> energySentOptional = energySentRepository.findById(id);
        return energySentOptional.map(EnergySentBuilder::toEnergySentDTO);
    }

    public int save(EnergySentDTO energySentDTO) {
        EnergySent energySent = EnergySentBuilder.toEntity(energySentDTO);
        EnergySent savedEnergySent = energySentRepository.save(energySent);
        return savedEnergySent.getId();
    }

    public void update(int id, EnergySentDTO energySentDTO) {
        Optional<EnergySent> existingEnergySentOptional = energySentRepository.findById(id);
        if (existingEnergySentOptional.isPresent()) {
            EnergySent existingEnergySent = existingEnergySentOptional.get();
            existingEnergySent.setDate(energySentDTO.getDate());
            existingEnergySent.setValue(energySentDTO.getValue());
            // Update other fields as needed
            energySentRepository.save(existingEnergySent);
        }
    }

    public List<EnergySent> findAllByDevice(Device device){
        return energySentRepository.findAllByDevice(device);
    }
    public void delete(int id) {
        energySentRepository.deleteById(id);
    }

    /*public LocalDateTime findEarliestDateByDeviceId(UUID deviceId) {
        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new RuntimeException("Device not found with id: " + deviceId));

        return energySentRepository.findFirstDateByDeviceOrderByDateAsc(device);
    }*/

}
