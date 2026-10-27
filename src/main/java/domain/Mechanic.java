package domain;

public class Mechanic {
    private  final String name;
    private final Specialization specialization;

    public Mechanic(String name, Specialization specialization) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Ім'я механіка не може бути порожнім");
        }
        if (specialization == null) {
            throw new IllegalArgumentException("Спеціалізація механіка не може бути порожньою");
        }

        this.name = name;
        this.specialization = specialization;
    }

    @Override
    public String toString() {
        return name + " (" + specialization + ")";
    }
}
