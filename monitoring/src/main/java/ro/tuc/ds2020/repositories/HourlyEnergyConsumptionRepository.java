package ro.tuc.ds2020.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ro.tuc.ds2020.entities.EnergySent;
import ro.tuc.ds2020.entities.HourlyEnergyConsumption;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface HourlyEnergyConsumptionRepository extends JpaRepository<HourlyEnergyConsumption, Integer> {
    @Query("SELECT h FROM HourlyEnergyConsumption h " +
            "WHERE h.device.id = :deviceId " +
            "AND h.date >= :startOfDay " +
            "AND h.date < :startOfNextDay")
    Optional<List<HourlyEnergyConsumption>> findAllByDeviceIdAndDate(
            @Param("deviceId") UUID deviceId,
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("startOfNextDay") LocalDateTime startOfNextDay
    );

    Optional<List<HourlyEnergyConsumption>> findAllByDeviceId(UUID deviceId);
}