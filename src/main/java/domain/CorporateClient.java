package domain;

import java.math.BigDecimal;

public class CorporateClient extends Client {
    private final String companyName;
    private final BigDecimal partsDiscountPercent;
    private final BigDecimal laborDiscountPercent;

    public CorporateClient(String name, String phone, String companyName,
                           BigDecimal partsDiscountPercent, BigDecimal laborDiscountPercent) {
        super(name, phone);

        if (companyName == null || companyName.isBlank()) {
            throw new IllegalArgumentException("Назва компанії не може бути порожньою");
        }
        if (partsDiscountPercent == null
                || partsDiscountPercent.compareTo(BigDecimal.ZERO) < 0
                || partsDiscountPercent.compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new IllegalArgumentException("Знижка на запчастини має бути від 0 до 100 відсотків");
        }
        if (laborDiscountPercent == null
                || laborDiscountPercent.compareTo(BigDecimal.ZERO) < 0
                || laborDiscountPercent.compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new IllegalArgumentException("Знижка на роботу має бути від 0 до 100 відсотків");
        }

        this.companyName = companyName;
        this.partsDiscountPercent = partsDiscountPercent;
        this.laborDiscountPercent = laborDiscountPercent;
    }

    @Override
    public BigDecimal getPartsDiscountPercent() {
        return partsDiscountPercent;
    }

    @Override
    public BigDecimal getLaborDiscountPercent() {
        return laborDiscountPercent;
    }

    @Override
    public String toString() {
        return super.toString() + ", компанія: " + companyName;
    }
}