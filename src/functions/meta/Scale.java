package functions.meta;

import functions.Function;

public class Scale implements Function {
    private Function f;
    private double scaleX;
    private double scaleY;

    public Scale(Function f, double scaleX, double scaleY) {
        this.f = f;
        this.scaleX = scaleX;
        this.scaleY = scaleY;
    }

    @Override
    public double getLeftDomainBorder() {
        // Масштабирование области определения
        if (scaleX > 0) {
            return f.getLeftDomainBorder() / scaleX;
        } else if (scaleX < 0) {
            return f.getRightDomainBorder() / scaleX;
        } else {
            // scaleX = 0 - особая ситуация
            return Double.NaN;
        }
    }

    @Override
    public double getRightDomainBorder() {
        if (scaleX > 0) {
            return f.getRightDomainBorder() / scaleX;
        } else if (scaleX < 0) {
            return f.getLeftDomainBorder() / scaleX;
        } else {
            return Double.NaN;
        }
    }

    @Override
    public double getFunctionValue(double x) {
        // Масштабируем x, вычисляем значение f, затем масштабируем y
        double scaledX = x * scaleX;
        double value = f.getFunctionValue(scaledX);
        if (Double.isNaN(value)) {
            return Double.NaN;
        }
        return value * scaleY;
    }
}
