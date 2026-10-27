package app;

import domain.Car;
import domain.Client;
import domain.CorporateClient;
import domain.Job;
import domain.Mechanic;
import domain.OrderStatus;
import domain.RepairOrder;
import domain.Specialization;
import pricing.PriceCalculator;
import repository.InMemoryRepairOrderRepository;
import repository.RepairOrderRepository;
import service.RepairOrderService;

import java.math.BigDecimal;
import java.util.UUID;

public class CarServiceDemo {
    public static void main(String[] args) {
        RepairOrderRepository repository = new InMemoryRepairOrderRepository();
        PriceCalculator calculator = new PriceCalculator(new BigDecimal("300"));
        RepairOrderService service = new RepairOrderService(repository, calculator);

        Client ordinaryClient = new Client("Іван Петренко", "+380501112233");
        Car ordinaryCar = new Car("VIN-ORDINARY-001", "Toyota", "Corolla", 2020, ordinaryClient);
        Mechanic petro = new Mechanic("Петро", Specialization.ENGINE);

        System.out.println("=== 1. Створення замовлення ===");
        RepairOrder ordinaryOrder = service.createOrder(ordinaryCar);
        RepairOrder loadedOrder = service.getOrder(ordinaryOrder.getId());
        System.out.println("Замовлення: " + loadedOrder.getId());
        printStep("Отримано замовлення", loadedOrder);

        System.out.println("=== 2. Повний успішний цикл ===");
        Job engineJob = newJob("Заміна деталей двигуна", "500", "200");
        Job diagnosticsJob = newJob("Діагностика електрики", "1000", "300");
        service.addJob(ordinaryOrder.getId(), engineJob);
        printStep("Додано першу роботу", ordinaryOrder);
        service.addJob(ordinaryOrder.getId(), diagnosticsJob);
        printStep("Додано другу роботу", ordinaryOrder);
        service.diagnose(ordinaryOrder.getId());
        printStep("Виконано діагностику", ordinaryOrder);
        service.approve(ordinaryOrder.getId());
        printStep("Замовлення схвалено", ordinaryOrder);
        service.assignMechanic(ordinaryOrder.getId(), petro);
        printStep("Призначено механіка Петра", ordinaryOrder);
        service.startWork(ordinaryOrder.getId());
        printStep("Розпочато роботу", ordinaryOrder);
        service.completeJob(ordinaryOrder.getId(), engineJob);
        printStep("Завершено першу роботу", ordinaryOrder);
        service.completeJob(ordinaryOrder.getId(), diagnosticsJob);
        printStep("Завершено другу роботу", ordinaryOrder);
        service.complete(ordinaryOrder.getId());
        printStep("Замовлення завершено", ordinaryOrder);

        System.out.println("=== 3. Ціна для звичайного клієнта ===");
        System.out.println("Базова ціна: " + service.getBasePrice(ordinaryOrder.getId()));
        System.out.println("Фінальна ціна: " + service.getFinalPrice(ordinaryOrder.getId()));

        System.out.println("=== 4. Ціна для корпоративного клієнта ===");
        CorporateClient corporateClient = new CorporateClient(
                "Олена Коваль", "+380671234567", "ТОВ Авто-Сервіс",
                new BigDecimal("10"), new BigDecimal("20"));
        Car corporateCar = new Car("VIN-CORPORATE-002", "Ford", "Focus", 2021, corporateClient);
        RepairOrder corporateOrder = service.createOrder(corporateCar);
        printStep("Створено корпоративне замовлення", corporateOrder);
        Job corporateJob = newJob("Корпоративний ремонт", "1000", "500");
        service.addJob(corporateOrder.getId(), corporateJob);
        printStep("Додано корпоративну роботу", corporateOrder);
        System.out.println("Базова ціна: " + service.getBasePrice(corporateOrder.getId()));
        System.out.println("Фінальна ціна: " + service.getFinalPrice(corporateOrder.getId()));

        System.out.println("=== 5. Мінімальна ціна ===");
        Client minimumPriceClient = new Client("Марія Бондар", "+380931234567");
        Car minimumPriceCar = new Car(
                "VIN-MINIMUM-003", "Honda", "Civic", 2019, minimumPriceClient);
        RepairOrder minimumPriceOrder = service.createOrder(minimumPriceCar);
        printStep("Створено замовлення з мінімальною ціною", minimumPriceOrder);
        service.addJob(minimumPriceOrder.getId(), newJob("Дешевий ремонт", "100", "50"));
        printStep("Додано дешеву роботу", minimumPriceOrder);
        System.out.println("Базова ціна: " + service.getBasePrice(minimumPriceOrder.getId()));
        System.out.println("Фінальна ціна: " + service.getFinalPrice(minimumPriceOrder.getId()));

        System.out.println("=== 6. Схвалення порожнього замовлення ===");
        Client emptyOrderClient = new Client("Сергій Мельник", "+380631234567");
        RepairOrder emptyOrder = service.createOrder(
                new Car("VIN-EMPTY-004", "Mazda", "3", 2018, emptyOrderClient));
        service.diagnose(emptyOrder.getId());
        printStep("Виконано діагностику порожнього замовлення", emptyOrder);
        try {
            service.approve(emptyOrder.getId());
        } catch (IllegalStateException | IllegalArgumentException e) {
            System.out.println("Помилка: " + e.getMessage());
        }

        System.out.println("=== 7. Початок роботи без механіка ===");
        Client noMechanicClient = new Client("Андрій Шевченко", "+380661234567");
        RepairOrder noMechanicOrder = service.createOrder(
                new Car("VIN-NO-MECHANIC-005", "Kia", "Ceed", 2022, noMechanicClient));
        service.addJob(noMechanicOrder.getId(), newJob("Ремонт підвіски", "400", "250"));
        printStep("Додано роботу", noMechanicOrder);
        service.diagnose(noMechanicOrder.getId());
        printStep("Виконано діагностику", noMechanicOrder);
        service.approve(noMechanicOrder.getId());
        printStep("Замовлення схвалено", noMechanicOrder);
        try {
            service.startWork(noMechanicOrder.getId());
        } catch (IllegalStateException | IllegalArgumentException e) {
            System.out.println("Помилка: " + e.getMessage());
        }

        System.out.println("=== 8. Призначення зайнятого механіка ===");
        Client busyMechanicClient = new Client("Наталія Іваненко", "+380681234567");
        RepairOrder busyMechanicOrder = service.createOrder(
                new Car("VIN-BUSY-006", "Skoda", "Octavia", 2020, busyMechanicClient));
        printStep("Створено замовлення для Петра", busyMechanicOrder);
        service.addJob(busyMechanicOrder.getId(), newJob("Ремонт двигуна", "700", "450"));
        printStep("Додано роботу", busyMechanicOrder);
        service.diagnose(busyMechanicOrder.getId());
        printStep("Виконано діагностику", busyMechanicOrder);
        service.approve(busyMechanicOrder.getId());
        printStep("Замовлення схвалено", busyMechanicOrder);
        service.assignMechanic(busyMechanicOrder.getId(), petro);
        printStep("Петра призначено на активне замовлення", busyMechanicOrder);
        service.startWork(busyMechanicOrder.getId());
        printStep("Петро зайнятий роботою", busyMechanicOrder);

        Client anotherClient = new Client("Віктор Лисенко", "+380691234567");
        RepairOrder anotherOrder = service.createOrder(
                new Car("VIN-ANOTHER-007", "Renault", "Megane", 2017, anotherClient));
        printStep("Створено інше замовлення", anotherOrder);
        service.addJob(anotherOrder.getId(), newJob("Ремонт двигуна", "600", "350"));
        printStep("Додано роботу", anotherOrder);
        service.diagnose(anotherOrder.getId());
        printStep("Виконано діагностику", anotherOrder);
        service.approve(anotherOrder.getId());
        printStep("Підготовлено інше схвалене замовлення", anotherOrder);
        try {
            service.assignMechanic(anotherOrder.getId(), petro);
        } catch (IllegalStateException | IllegalArgumentException e) {
            System.out.println("Помилка: " + e.getMessage());
        }

        System.out.println("=== 9. Зміна завершеного замовлення ===");
        try {
            service.addJob(ordinaryOrder.getId(), newJob("Заборонена робота", "50", "50"));
        } catch (IllegalStateException | IllegalArgumentException e) {
            System.out.println("Помилка: " + e.getMessage());
        }

        System.out.println("=== 10. Заборонений перехід зі CREATED ===");
        Client createdClient = new Client("Юлія Ткаченко", "+380631111222");
        RepairOrder createdOrder = service.createOrder(
                new Car("VIN-CREATED-008", "Nissan", "Qashqai", 2023, createdClient));
        printStep("Створено замовлення", createdOrder);
        try {
            service.startWork(createdOrder.getId());
        } catch (IllegalStateException | IllegalArgumentException e) {
            System.out.println("Помилка: " + e.getMessage());
        }

        System.out.println("=== 11. Пошук неіснуючого замовлення ===");
        try {
            service.getOrder(UUID.randomUUID());
        } catch (IllegalStateException | IllegalArgumentException e) {
            System.out.println("Помилка: " + e.getMessage());
        }
    }

    private static Job newJob(String description, String partsPrice, String jobPrice) {
        return new Job(description, new BigDecimal(partsPrice), new BigDecimal(jobPrice));
    }

    private static void printStep(String message, RepairOrder order) {
        OrderStatus status = order.getStatus();
        System.out.println(message + ": " + status);
    }
}
