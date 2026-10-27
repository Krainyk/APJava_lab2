package domain;

public class Car {
    private final String vinCode;
    private final String brandAuto;
    private final String modelAuto;
    private final int yearAuto;
    private final Client owner;

    public Car(String vinCode, String brandAuto, String modelAuto, int yearAuto, Client owner) {
        if (vinCode == null || vinCode.isBlank()) {
            throw new IllegalArgumentException("VIN-код не може бути пустим");
        }

        if (brandAuto == null || brandAuto.isBlank()) {
            throw new IllegalArgumentException("Бренд автомобіля не може бути пустим");
        }

        if (modelAuto == null || modelAuto.isBlank()) {
            throw new IllegalArgumentException("Модель автомобіля не може бути пустим");
        }

        if (yearAuto < 1886 || yearAuto > 2026) {
            throw new IllegalArgumentException("Рік випуску авто не може бути меншим від 1886 року або більшим від 2026 року");
        }

        if (owner == null) {
            throw new IllegalArgumentException("Автомобіль повинен мати власника");
        }

        this.vinCode = vinCode;
        this.brandAuto = brandAuto;
        this.modelAuto = modelAuto;
        this.yearAuto = yearAuto;
        this.owner = owner;
    }

    public String getVinCode() {
        return vinCode;
    }

    public Client getOwner() {
        return owner;
    }

    @Override
    public String toString() {
        return brandAuto + " " + modelAuto + " (" + yearAuto + "), VIN: " + vinCode + " " + owner;
    }
}
