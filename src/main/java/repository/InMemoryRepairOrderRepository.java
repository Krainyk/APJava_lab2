package repository;

import domain.Mechanic;
import domain.RepairOrder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class InMemoryRepairOrderRepository implements RepairOrderRepository {
    private final Map<UUID, RepairOrder> storage = new HashMap<>();

    @Override
    public void save(RepairOrder order) {
        if (order == null) {
            throw new IllegalArgumentException("Замовлення на ремонт не може бути null");
        }

        // Один метод створює і оновлює замовлення за його ідентифікатором.
        storage.put(order.getId(), order);
    }

    @Override
    public Optional<RepairOrder> findById(UUID id) {
        // Optional явно показує відсутність замовлення замість null.
        return id == null ? Optional.empty() : Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<RepairOrder> findActiveByMechanic(Mechanic mechanic) {
        List<RepairOrder> result = new ArrayList<>();
        if (mechanic == null) {
            return result;
        }

        for (RepairOrder order : storage.values()) {
            if (order.isActive() && Objects.equals(order.getMechanic(), mechanic)) {
                result.add(order);
            }
        }
        return result;
    }
}
