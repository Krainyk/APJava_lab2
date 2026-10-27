package repository;

import domain.Car;
import domain.Client;
import domain.Job;
import domain.Mechanic;
import domain.RepairOrder;
import domain.Specialization;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryRepairOrderRepositoryTest {

    private InMemoryRepairOrderRepository repository;
    private Car car;

    @BeforeEach
    void setUp() {
        // Новий репозиторій перед кожним тестом: тести не залежать один від одного
        repository = new InMemoryRepairOrderRepository();
        car = new Car("VIN0000000000001", "Audi", "RS6", 2020,
                new Client("Влад", "+380501111111"));
    }

    @Test
    void shouldSaveAndFindOrderById() {
        // Given
        RepairOrder order = new RepairOrder(car);
        // When
        repository.save(order);
        Optional<RepairOrder> found = repository.findById(order.getId());
        // Then
        assertTrue(found.isPresent());
        assertSame(order, found.get());
    }

    @Test
    void shouldReturnEmptyWhenOrderMissing() {
        // When
        Optional<RepairOrder> found = repository.findById(UUID.randomUUID());
        // Then
        assertTrue(found.isEmpty());
    }

    @Test
    void shouldReturnEmptyWhenIdIsNull() {
        assertTrue(repository.findById(null).isEmpty());
    }

    @Test
    void shouldUpdateOrderOnRepeatedSave() {
        // Given
        RepairOrder order = new RepairOrder(car);
        repository.save(order);
        // When: змінюємо стан і зберігаємо ще раз
        order.diagnose();
        repository.save(order);
        // Then
        assertEquals(domain.OrderStatus.DIAGNOSTICS,
                repository.findById(order.getId()).orElseThrow().getStatus());
    }

    @Test
    void shouldRejectNullOrder() {
        assertThrows(IllegalArgumentException.class, () -> repository.save(null));
    }

    @Test
    void shouldFindOnlyActiveOrdersOfMechanic() {
        // Given: одне активне й одне скасоване замовлення того самого механіка
        Mechanic petro = new Mechanic("Петро", Specialization.ENGINE);
        RepairOrder active = approvedOrderWith(petro);
        RepairOrder cancelled = approvedOrderWith(petro);
        cancelled.cancel();
        repository.save(active);
        repository.save(cancelled);
        // When
        List<RepairOrder> result = repository.findActiveByMechanic(petro);
        // Then
        assertEquals(1, result.size());
        assertSame(active, result.get(0));
    }

    @Test
    void shouldReturnEmptyListForNullMechanic() {
        assertTrue(repository.findActiveByMechanic(null).isEmpty());
    }

    private RepairOrder approvedOrderWith(Mechanic mechanic) {
        RepairOrder order = new RepairOrder(car);
        order.addJob(new Job("Робота", new BigDecimal("100"), new BigDecimal("100")));
        order.diagnose();
        order.approve();
        order.assignMechanic(mechanic);
        return order;
    }
}