# Автосервіс: лабораторна №2

Консольний застосунок: управління замовленнями автосервісу.
Доменна модель, прикладний сервіс, репозиторій в пам'яті, розрахунок ціни зі знижками та мінімальною вартістю.

## Вимоги
- Java 27
- Maven 3.9+

## Запуск тестів
    mvn test

## Запуск демо
    mvn compile exec:java

Або в IntelliJ: відкрити `src/main/java/app/CarServiceDemo.java` і натиснути Run.

## Структура
| Пакет | Призначення |
| `domain` | модель: RepairOrder, Job, Car, Client, Mechanic, статуси |
| `pricing` | PriceCalculator: знижки й мінімальна вартість |
| `repository` | інтерфейс RepairOrderRepository та реалізація в пам'яті |
| `service` | RepairOrderService: сценарії замовлень |
| `app` | CarServiceDemo: точка входу |

## Ключові рішення
- Сервіс залежить від інтерфейсу `RepairOrderRepository`, реалізація передається в конструктор.
- Відсутнє замовлення: `findById` повертає `Optional.empty()`, сервіс кидає `IllegalArgumentException`.
- `RepairOrder` рахує лише базову суму, комерційну ціну рахує `PriceCalculator`.
- Гроші на `BigDecimal`, округлення `HALF_UP` до 2 знаків.
