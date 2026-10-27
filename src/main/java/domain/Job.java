package domain;

import java.math.BigDecimal;

public class Job {
    private final String description;
    private final BigDecimal partsPrice;
    private final BigDecimal jobPrice;
    private boolean completed;

    public Job(String description, BigDecimal partsPrice, BigDecimal jobPrice) {
        if (description == null || description.isEmpty()) {
            throw new IllegalArgumentException("Опис не може бути порожнім");
        }

        if (partsPrice == null || partsPrice.signum() < 0) {
            throw new IllegalArgumentException("Ціна запчастин не може бути від'ємною");
        }

        if (jobPrice == null || jobPrice.signum() < 0) {
            throw new IllegalArgumentException("Ціна роботи не може бути від'ємною");
        }
        this.description = description;
        this.partsPrice = partsPrice;
        this.jobPrice = jobPrice;
    }

    public void markCompleted() {
        completed = true;
    }

    public boolean isCompleted() {
        return completed;
    }

    public BigDecimal getPartsPrice() {
        return partsPrice;
    }

    public BigDecimal getJobPrice() {
        return jobPrice;
    }

    public BigDecimal totalCost() {
        return partsPrice.add(jobPrice);
    }

    @Override
    public String toString() {
        return description + " (" + totalCost() + ", " + (completed ? "виконано" : "не виконано") + ")";
    }
}
