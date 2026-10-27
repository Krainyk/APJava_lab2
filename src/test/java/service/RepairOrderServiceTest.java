package service;

import domain.Car;
import domain.Client;
import domain.Job;
import domain.Mechanic;
import domain.OrderStatus;
import domain.RepairOrder;
import domain.Specialization;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pricing.PriceCalculator;
import repository.RepairOrderRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RepairOrderServiceTest {

    private RepairOrderRepository repository;
    private RepairOrderService service;
    private Car car;

    @BeforeEach
    void setUp() {
        // Фальшивий репозиторій: сервіс тестується окремо від реального сховища
        repository = mock(RepairOrderRepository.class);
        service = new RepairOrderService(repository, new PriceCalculator(new BigDecimal("300")));
        car = new Car("VIN0000000000001", "Audi", "RS6", 2020,
                new Client("Влад", "+380501111111"));
    }

    private Job job() {
        return new Job("Робота", new BigDecimal("500"), new BigDecimal("200"));
    }

    private RepairOrder storedOrder() {
        RepairOrder order = new RepairOrder(car);
        when(repository.findById(order.getId())).thenReturn(Optional.of(order));
        return order;
    }

    @Test
    void shouldCreateAndSaveOrder() {
        // When
        RepairOrder order = service.createOrder(car);
        // Then
        assertEquals(OrderStatus.CREATED, order.getStatus());
        verify(repository).save(order);
    }

    @Test
    void shouldThrowWhenOrderNotFound() {
        // Given
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());
        // Then
        assertThrows(IllegalArgumentException.class, () -> service.getOrder(id));
    }

    @Test
    void shouldAllowDiagnoseThenApproveWithJob() {
        // Given
        RepairOrder order = storedOrder();
        service.addJob(order.getId(), job());
        // When
        service.diagnose(order.getId());
        service.approve(order.getId());
        // Then
        assertEquals(OrderStatus.APPROVED, order.getStatus());
        verify(repository, atLeastOnce()).save(order);
    }

    @Test
    void shouldRejectApproveWithoutJobs() {
        // Given
        RepairOrder order = storedOrder();
        service.diagnose(order.getId());
        // Then
        assertThrows(IllegalStateException.class, () -> service.approve(order.getId()));
        assertEquals(OrderStatus.DIAGNOSTICS, order.getStatus());
    }

    @Test
    void shouldRejectForbiddenTransition() {
        // Given
        RepairOrder order = storedOrder();
        // Then: CREATED -> IN_PROGRESS заборонено
        assertThrows(IllegalStateException.class, () -> service.startWork(order.getId()));
    }

    @Test
    void shouldRejectCompleteWhenJobsNotDone() {
        // Given
        RepairOrder order = storedOrder();
        Job job = job();
        service.addJob(order.getId(), job);
        service.diagnose(order.getId());
        service.approve(order.getId());
        service.assignMechanic(order.getId(), new Mechanic("Петро", Specialization.ENGINE));
        service.startWork(order.getId());
        // Then
        assertThrows(IllegalStateException.class, () -> service.complete(order.getId()));
        // When: виконуємо роботу
        service.completeJob(order.getId(), job);
        service.complete(order.getId());
        assertEquals(OrderStatus.COMPLETED, order.getStatus());
    }

    @Test
    void shouldRejectBusyMechanic() {
        // Given: механік уже веде інше активне замовлення
        Mechanic petro = new Mechanic("Петро", Specialization.ENGINE);
        RepairOrder busy = new RepairOrder(car);
        RepairOrder current = storedOrder();
        current.addJob(job());
        current.diagnose();
        current.approve();
        when(repository.findActiveByMechanic(petro)).thenReturn(List.of(busy));
        // Then
        assertThrows(IllegalStateException.class,
                () -> service.assignMechanic(current.getId(), petro));
        assertNull(current.getMechanic());
    }

    @Test
    void shouldReturnBaseAndFinalPrice() {
        // Given: 500 + 200 = 700 (звичайний клієнт без знижок, мінімум 300)
        RepairOrder order = storedOrder();
        service.addJob(order.getId(), job());
        // Then
        assertEquals(new BigDecimal("700"), service.getBasePrice(order.getId()));
        assertEquals(new BigDecimal("700.00"), service.getFinalPrice(order.getId()));
    }

    @Test
    void shouldNotSaveWhenDomainRejectsOperation() {
        // Given
        RepairOrder order = storedOrder();
        // When
        assertThrows(IllegalStateException.class, () -> service.approve(order.getId()));
        // Then: після помилки збереження не відбулось
        verify(repository, never()).save(any());
    }
}