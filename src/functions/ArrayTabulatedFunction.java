package functions;

import java.io.Serializable;
import java.util.Arrays;

public class ArrayTabulatedFunction implements TabulatedFunction, Serializable {
    private static final double EPSILON = 1e-10;
    private FunctionPoint[] points;
    private int pointsCount;

    // Конструкторы
    public ArrayTabulatedFunction(FunctionPoint[] points) {
        if (points.length < 2) {
            throw new IllegalArgumentException("Требуется не менее 2 точек");
        }
        // Проверка упорядоченности точек
        for (int i = 1; i < points.length; i++) {
            if (points[i].getX() <= points[i-1].getX()) {
                throw new IllegalArgumentException("Точки должны быть упорядочены по X");
            }
        }
        // Создание копии массива для инкапсуляции
        this.points = Arrays.copyOf(points, points.length);
        this.pointsCount = points.length;
    }

    public ArrayTabulatedFunction(double leftX, double rightX, int pointsCount) {
        if (leftX >= rightX) {
            throw new IllegalArgumentException("Левая граница должна быть меньше правой");
        }
        if (pointsCount < 2) {
            throw new IllegalArgumentException("Количество точек должно быть не менее 2");
        }

        this.pointsCount = pointsCount;
        this.points = new FunctionPoint[pointsCount + 2]; // Запас места

        double step = (rightX - leftX) / (pointsCount - 1);
        for (int i = 0; i < pointsCount; i++) {
            double x = leftX + i * step;
            points[i] = new FunctionPoint(x, 0);
        }
    }

    public ArrayTabulatedFunction(double leftX, double rightX, double[] values) {
        if (leftX >= rightX) {
            throw new IllegalArgumentException("Левая граница должна быть меньше правой");
        }
        if (values.length < 2) {
            throw new IllegalArgumentException("Количество точек должно быть не менее 2");
        }

        this.pointsCount = values.length;
        this.points = new FunctionPoint[pointsCount + 2];

        double step = (rightX - leftX) / (pointsCount - 1);
        for (int i = 0; i < pointsCount; i++) {
            double x = leftX + i * step;
            points[i] = new FunctionPoint(x, values[i]);
        }
    }

    // Методы для работы с функцией
    public double getLeftDomainBorder() {
        return points[0].getX();
    }

    public double getRightDomainBorder() {
        return points[pointsCount - 1].getX();
    }

    public double getFunctionValue(double x) {
        if (x < getLeftDomainBorder() - EPSILON || x > getRightDomainBorder() + EPSILON) {
            return Double.NaN;
        }

        // Поиск интервала, в который попадает x
        int i = 0;
        while (i < pointsCount - 1 && points[i + 1].getX() < x - EPSILON) {
            i++;
        }

        // Если x совпадает с одной из точек (в пределах epsilon)
        if (Math.abs(points[i].getX() - x) < EPSILON) {
            return points[i].getY();
        }
        if (i < pointsCount - 1 && Math.abs(points[i + 1].getX() - x) < EPSILON) {
            return points[i + 1].getY();
        }

        // Линейная интерполяция
        double x1 = points[i].getX();
        double y1 = points[i].getY();
        double x2 = points[i + 1].getX();
        double y2 = points[i + 1].getY();

        return y1 + (y2 - y1) * (x - x1) / (x2 - x1);
    }

    // Методы для работы с точками
    public int getPointsCount() {
        return pointsCount;
    }

    public FunctionPoint getPoint(int index) throws FunctionPointIndexOutOfBoundsException {
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException("Индекс " + index + " вне границ [0, " + (pointsCount-1) + "]");
        }
        return new FunctionPoint(points[index]);
    }

    public void setPoint(int index, FunctionPoint point) throws 
            FunctionPointIndexOutOfBoundsException, InappropriateFunctionPointException {
        
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException("Индекс " + index + " вне границ [0, " + (pointsCount-1) + "]");
        }

        // Проверка корректности новой x-координаты
        if (index > 0 && point.getX() <= points[index - 1].getX() + EPSILON) {
            throw new InappropriateFunctionPointException(
                "X-координата точки (" + point.getX() + ") должна быть больше предыдущей (" + points[index-1].getX() + ")");
        }
        if (index < pointsCount - 1 && point.getX() >= points[index + 1].getX() - EPSILON) {
            throw new InappropriateFunctionPointException(
                "X-координата точки (" + point.getX() + ") должна быть меньше следующей (" + points[index+1].getX() + ")");
        }

        points[index].setX(point.getX());
        points[index].setY(point.getY());
    }

    public double getPointX(int index) throws FunctionPointIndexOutOfBoundsException {
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException("Индекс " + index + " вне границ [0, " + (pointsCount-1) + "]");
        }
        return points[index].getX();
    }

    public void setPointX(int index, double x) throws 
            FunctionPointIndexOutOfBoundsException, InappropriateFunctionPointException {
        
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException("Индекс " + index + " вне границ [0, " + (pointsCount-1) + "]");
        }

        // Проверка корректности новой x-координаты
        if (index > 0 && x <= points[index - 1].getX() + EPSILON) {
            throw new InappropriateFunctionPointException(
                "X-координата (" + x + ") должна быть больше предыдущей (" + points[index-1].getX() + ")");
        }
        if (index < pointsCount - 1 && x >= points[index + 1].getX() - EPSILON) {
            throw new InappropriateFunctionPointException(
                "X-координата (" + x + ") должна быть меньше следующей (" + points[index+1].getX() + ")");
        }

        points[index].setX(x);
    }

    public double getPointY(int index) throws FunctionPointIndexOutOfBoundsException {
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException("Индекс " + index + " вне границ [0, " + (pointsCount-1) + "]");
        }
        return points[index].getY();
    }

    public void setPointY(int index, double y) throws FunctionPointIndexOutOfBoundsException {
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException("Индекс " + index + " вне границ [0, " + (pointsCount-1) + "]");
        }
        points[index].setY(y);
    }

    // Методы для изменения количества точек
    public void deletePoint(int index) throws 
            FunctionPointIndexOutOfBoundsException, IllegalStateException {
        
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException("Индекс " + index + " вне границ [0, " + (pointsCount-1) + "]");
        }
        if (pointsCount <= 2) {
            throw new IllegalStateException("Нельзя удалить точку: функция должна содержать минимум 2 точки");
        }

        // Сдвигаем точки влево
        for (int i = index; i < pointsCount - 1; i++) {
            points[i] = points[i + 1];
        }
        points[pointsCount - 1] = null;
        pointsCount--;
    }

    public void addPoint(FunctionPoint point) throws InappropriateFunctionPointException {
        // Если массив заполнен, увеличиваем его
        if (pointsCount >= points.length) {
            FunctionPoint[] newPoints = new FunctionPoint[points.length * 2];
            System.arraycopy(points, 0, newPoints, 0, pointsCount);
            points = newPoints;
        }

        // Проверяем, нет ли уже точки с такой x-координатой
        for (int i = 0; i < pointsCount; i++) {
            if (Math.abs(points[i].getX() - point.getX()) < EPSILON) {
                throw new InappropriateFunctionPointException(
                    "Точка с x=" + point.getX() + " уже существует");
            }
        }

        // Находим позицию для вставки
        int insertIndex = 0;
        while (insertIndex < pointsCount && points[insertIndex].getX() < point.getX() - EPSILON) {
            insertIndex++;
        }

        // Сдвигаем точки вправо
        for (int i = pointsCount; i > insertIndex; i--) {
            points[i] = points[i - 1];
        }

        // Вставляем новую точку
        points[insertIndex] = new FunctionPoint(point);
        pointsCount++;
    }
}
