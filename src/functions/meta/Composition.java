package functions.meta;

import functions.Function;

public class Composition implements Function {
    private Function f1; // внешняя функция
    private Function f2; // внутренняя функция

    public Composition(Function f1, Function f2) {
        this.f1 = f1;
        this.f2 = f2;
    }

    @Override
    public double getLeftDomainBorder() {
        // Область определения композиции - область определения внутренней функции
        return f2.getLeftDomainBorder();
    }

    @Override
    public double getRightDomainBorder() {
        return f2.getRightDomainBorder();
    }

    @Override
    public double getFunctionValue(double x) {
        // Вычисляем f2(x), затем f1(f2(x))
        double innerValue = f2.getFunctionValue(x);
        if (Double.isNaN(innerValue)) {
            return Double.NaN;
        }
        return f1.getFunctionValue(innerValue);
    }
}
