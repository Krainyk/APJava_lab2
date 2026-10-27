package pricing;

import domain.Client;
import domain.Job;
import domain.RepairOrder;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class PriceCalculator {
    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);

    private final BigDecimal minimumPrice;

    public PriceCalculator(BigDecimal minimumPrice) {
        // Мінімальна ціна має бути коректною, щоб розрахунок не допускав від'ємних сум.
        if (minimumPrice == null || minimumPrice.signum() < 0) {
            throw new IllegalArgumentException("Мінімальна ціна не може бути null або від'ємною");
        }
        this.minimumPrice = minimumPrice;
    }

    public BigDecimal calculateFinalPrice(RepairOrder order) {
        // Без замовлення неможливо визначити власника та склад ціни.
        if (order == null) {
            throw new IllegalArgumentException("Замовлення не може бути null");
        }

        // Порожнє замовлення не повинно оплачуватися за мінімальною ціною.
        if (order.getJobs().isEmpty()) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }

        Client owner = order.getCar().getOwner();
        BigDecimal partsDiscount = owner.getPartsDiscountPercent();
        BigDecimal laborDiscount = owner.getLaborDiscountPercent();

        BigDecimal total = BigDecimal.ZERO;
        for (Job job : order.getJobs()) {
            total = total
                    .add(applyDiscount(job.getPartsPrice(), partsDiscount))
                    .add(applyDiscount(job.getJobPrice(), laborDiscount));
        }

        // Два знаки після коми відповідають грошовому формату з чесним округленням.
        total = total.setScale(2, RoundingMode.HALF_UP);

        // Мінімальна вартість покриває базові витрати сервісу на оформлення замовлення
        if (total.compareTo(minimumPrice) < 0) {
            return minimumPrice.setScale(2, RoundingMode.HALF_UP);
        }
        return total;
    }

    private BigDecimal applyDiscount(BigDecimal price, BigDecimal percent) {
        return price.multiply(HUNDRED.subtract(percent))
                .divide(HUNDRED, 2, RoundingMode.HALF_UP);
    }
}
