package pricing;

import domain.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class PriceCalculatorTest {

    private RepairOrder orderWith(Client owner, String parts, String labor) {
        Car car = new Car("VIN" + System.nanoTime(), "Audi", "RS6", 2020, owner);
        RepairOrder order = new RepairOrder(car);
        if (parts != null) {
            order.addJob(new Job("Робота", new BigDecimal(parts), new BigDecimal(labor)));
        }
        return order;
    }

    @Test
    void shouldReturnZeroForEmptyOrder() {
        // Given
        PriceCalculator calc = new PriceCalculator(new BigDecimal("300"));
        RepairOrder order = orderWith(new Client("Влад", "+380501111111"), null, null);
        // When
        BigDecimal result = calc.calculateFinalPrice(order);
        // Then
        assertEquals(new BigDecimal("0.00"), result);
    }

    @Test
    void shouldApplySeparateDiscountsForCorporateClient() {
        // Given
        Client corp = new CorporateClient("Олег", "+380502222222", "ТОВ",
                new BigDecimal("10"), new BigDecimal("20"));
        PriceCalculator calc = new PriceCalculator(new BigDecimal("300"));
        // When
        BigDecimal result = calc.calculateFinalPrice(orderWith(corp, "1000", "500"));
        // Then
        assertEquals(new BigDecimal("1300.00"), result);
    }

    @ParameterizedTest
    @CsvSource({
            "100, 50, 300.00",    // нижче мінімуму
            "200, 100, 300.00",   // рівно мінімум
            "200, 101, 301.00",   // трохи вище
            "1000, 500, 1500.00"  // звичайний випадок
    })
    void shouldApplyMinimumPrice(String parts, String labor, String expected) {
        // Given
        PriceCalculator calc = new PriceCalculator(new BigDecimal("300"));
        RepairOrder order = orderWith(new Client("Влад", "+380501111111"), parts, labor);
        // When
        BigDecimal result = calc.calculateFinalPrice(order);
        // Then
        assertEquals(new BigDecimal(expected), result);
    }

    @Test
    void shouldRejectNegativeMinimumPrice() {
        assertThrows(IllegalArgumentException.class,
                () -> new PriceCalculator(new BigDecimal("-1")));
    }

    @Test
    void shouldRejectNullOrder() {
        PriceCalculator calc = new PriceCalculator(BigDecimal.ZERO);
        assertThrows(IllegalArgumentException.class, () -> calc.calculateFinalPrice(null));
    }
}