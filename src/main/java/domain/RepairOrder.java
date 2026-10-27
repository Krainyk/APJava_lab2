package domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RepairOrder {
    private final Car car;
    private final List<Job> jobs = new ArrayList<>();
    private Mechanic mechanic;
    private OrderStatus status = OrderStatus.CREATED;
    private final UUID id = UUID.randomUUID();

    public RepairOrder(Car car) {
        if (car == null) {
            throw new IllegalArgumentException("Замовлення має належати автомобілю");
        }
        this.car = car;
    }

    public void addJob(Job job) {
        if  (job == null) {
            throw new IllegalArgumentException("Робота не може бути null");
        }

        if (status == OrderStatus.COMPLETED ||  status == OrderStatus.CANCELLED) {
            throw new IllegalStateException("Завершене або скасоване замовлення, не можна змінювати");
        }
        jobs.add(job);
    }

    public BigDecimal calculateBaseTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (Job job : jobs) {
            total = total.add(job.totalCost());
        }
        return total;
    }

    public void diagnose() {
        if (status != OrderStatus.CREATED) {
            throw new IllegalStateException("Діагностику можна почати лише для щойно створеного замовлення");
        }
        status = OrderStatus.DIAGNOSTICS;
    }

    public void approve() {
        if (status != OrderStatus.DIAGNOSTICS) {
            throw new IllegalStateException("Схвалити можна лише замовлення після діагностики");
        }

        if (jobs.isEmpty()) {
            throw new IllegalStateException("У списку робіт має бути хоча б одна робота");
        }
        status = OrderStatus.APPROVED;
    }

    public void assignMechanic(Mechanic mechanic) {
        if (mechanic == null) {
            throw new IllegalArgumentException("Механіка не можна призначити якщо його нема");
        }

        if (status != OrderStatus.APPROVED) {
            throw new IllegalStateException("Щоб призначити механіка статус замовлення має бути підтвердженим");
        }
        this.mechanic = mechanic;
    }

    public void startWork() {
        if (status != OrderStatus.APPROVED) {
            throw new IllegalStateException("Почати роботу можна лише для підтвердженого замовлення");
        }

        if (mechanic == null) {
            throw new IllegalStateException("Щоб розпочати роботу, потрібно призначити механіка");
        }
        status = OrderStatus.IN_PROGRESS;
    }

    public void completeJob(Job job) {
        if (job == null) {
            throw new IllegalArgumentException("Робота не може бути null");
        }

        if (status != OrderStatus.IN_PROGRESS) {
            throw new IllegalStateException("Виконувати роботи можна лише для замовлення в роботі");
        }

        if (!jobs.contains(job)) {
            throw new IllegalStateException("Ця робота не належить цьому замовленню");
        }
        job.markCompleted();
    }

    public void complete() {
        if (status != OrderStatus.IN_PROGRESS) {
            throw new IllegalStateException("Статус має бути в прогресі");
        }

        for (Job job : jobs) {
            if (!job.isCompleted()) {
                throw new IllegalStateException("Не всі роботи завершились");
            }
        }
        status = OrderStatus.COMPLETED;
    }

    public void cancel() {
        if (status == OrderStatus.COMPLETED || status == OrderStatus.CANCELLED) {
            throw new IllegalStateException("Завершене або скасоване замовлення не можна скасувати");
        }
        status = OrderStatus.CANCELLED;
    }


    public OrderStatus getStatus() {
        return status;
    }

    public Mechanic getMechanic() {
        return mechanic;
    }

    public boolean isActive() {
        return status != OrderStatus.COMPLETED && status != OrderStatus.CANCELLED;
    }

    public Car getCar() {
        return car;
    }

    public List<Job> getJobs() {
        return List.copyOf(jobs);
    }

    public UUID getId() {
        return id;
    }
}
