package ro.tuc.ds2020.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ro.tuc.ds2020.entities.Device;
import ro.tuc.ds2020.entities.EnergySent;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EnergySentRepository extends JpaRepository<EnergySent, Integer> {

    //List<EnergySent> findByDeviceIdAndDateBetween(UUID deviceID, LocalDateTime startDate, LocalDateTime endDate);
    List<EnergySent> findAllByDevice(Device device);
    @Query("SELECT MIN(e.date) FROM EnergySent e WHERE e.device = :device ORDER BY e.date ASC")
    Optional<LocalDateTime> findFirstDateByDeviceOrderByDateAsc(@Param("device") Device device);
    @Query("SELECT MAX(e.date) FROM EnergySent e WHERE e.device = :device")
    Optional<LocalDateTime> findLastDateByDeviceOrderByDateDesc(@Param("device") Device device);
    List<EnergySent> findAllByDeviceAndDateBetween(Device device, LocalDateTime startTime, LocalDateTime endTime);

}
