package service;

import domain.Car;
import domain.Job;
import domain.Mechanic;
import domain.RepairOrder;
import pricing.PriceCalculator;
import repository.RepairOrderRepository;

import java.math.BigDecimal;
import java.util.UUID;

public class RepairOrderService {
    // Інтерфейс репозиторію зменшує зв'язність сервісу з конкретним сховищем.
    private final RepairOrderRepository repository;
    private final PriceCalculator calculator;

    public RepairOrderService(RepairOrderRepository repository, PriceCalculator calculator) {
        if (repository == null) {
            throw new IllegalArgumentException("Репозиторій не може бути null");
        }
        if (calculator == null) {
            throw new IllegalArgumentException("Калькулятор цін не може бути null");
        }
        this.repository = repository;
        this.calculator = calculator;
    }

    public RepairOrder createOrder(Car car) {
        RepairOrder order = new RepairOrder(car);
        repository.save(order);
        return order;
    }

    public RepairOrder getOrder(UUID id) {
        return findOrder(id);
    }

    public void addJob(UUID orderId, Job job) {
        RepairOrder order = findOrder(orderId);
        order.addJob(job);
        saveChanged(order);
    }

    public void diagnose(UUID orderId) {
        RepairOrder order = findOrder(orderId);
        order.diagnose();
        saveChanged(order);
    }

    public void approve(UUID orderId) {
        RepairOrder order = findOrder(orderId);
        order.approve();
        saveChanged(order);
    }

    public void assignMechanic(UUID orderId, Mechanic mechanic) {
        RepairOrder order = findOrder(orderId);
        if (mechanic != null) {
            for (RepairOrder activeOrder : repository.findActiveByMechanic(mechanic)) {
                if (!activeOrder.getId().equals(order.getId())) {
                    // Лише сервіс бачить інші замовлення, тому перевірка потрібна тут.
                    throw new IllegalStateException("Механік вже зайнятий в іншому активному замовленні");
                }
            }
        }
        order.assignMechanic(mechanic);
        saveChanged(order);
    }

    public void startWork(UUID orderId) {
        RepairOrder order = findOrder(orderId);
        order.startWork();
        saveChanged(order);
    }

    public void completeJob(UUID orderId, Job job) {
        RepairOrder order = findOrder(orderId);
        order.completeJob(job);
        saveChanged(order);
    }

    public void complete(UUID orderId) {
        RepairOrder order = findOrder(orderId);
        order.complete();
        saveChanged(order);
    }

    public void cancel(UUID orderId) {
        RepairOrder order = findOrder(orderId);
        order.cancel();
        saveChanged(order);
    }

    public BigDecimal getBasePrice(UUID orderId) {
        return findOrder(orderId).calculateBaseTotal();
    }

    public BigDecimal getFinalPrice(UUID orderId) {
        return calculator.calculateFinalPrice(findOrder(orderId));
    }

    private RepairOrder findOrder(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Замовлення не знайдено: " + id));
    }

    private void saveChanged(RepairOrder order) {
        // Зберігаємо після кожної зміни, щоб репозиторій мав актуальний стан.
        repository.save(order);
    }
}
