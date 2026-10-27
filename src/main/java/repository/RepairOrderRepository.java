package repository;

import domain.Mechanic;
import domain.RepairOrder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RepairOrderRepository {
    void save(RepairOrder order);
    Optional<RepairOrder> findById(UUID id);
    List<RepairOrder> findActiveByMechanic(Mechanic mechanic);
}