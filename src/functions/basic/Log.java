package functions.basic;

import functions.Function;

public class Log implements Function {
    private double base;

    // Конструктор с параметром основания
    public Log(double base) {
        if (base <= 0) {
            throw new IllegalArgumentException("Основание логарифма должно быть положительным числом");
        }
        if (Math.abs(base - 1.0) < 1e-10) {
            throw new IllegalArgumentException("Основание логарифма не может быть равно 1");
        }
        this.base = base;
    }

    // Конструктор по умолчанию (натуральный логарифм)
    public Log() {
        this(Math.E);
    }

    @Override
    public double getLeftDomainBorder() {
        return 0; // x > 0
    }

    @Override
    public double getRightDomainBorder() {
        return Double.POSITIVE_INFINITY;
    }

    @Override
    public double getFunctionValue(double x) {
        if (x <= 0) {
            return Double.NaN; // логарифм определен только для x > 0
        }
        return Math.log(x) / Math.log(base);
    }

    public double getBase() {
        return base;
    }
}
