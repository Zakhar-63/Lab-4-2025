package functions;

import java.util.StringTokenizer;
import java.io.*;

public class TabulatedFunctions {
    // Приватный конструктор
    private TabulatedFunctions() {
        throw new AssertionError("Не удается создать экземпляр служебного класса");
    }

    public static TabulatedFunction tabulate(Function function, double leftX, double rightX, int pointsCount) {
        if (leftX < function.getLeftDomainBorder() || rightX > function.getRightDomainBorder()) {
            throw new IllegalArgumentException("Выход за границы определения функции");
        }
        if (leftX >= rightX) {
            throw new IllegalArgumentException("Левая граница должна быть меньше правой");
        }
        if (pointsCount < 2) {
            throw new IllegalArgumentException("Количество точек должно быть не меньше 2");
        }

        // Создаем табулированную функцию (используем ArrayTabulatedFunction по умолчанию)
        TabulatedFunction tabulatedFunc = new ArrayTabulatedFunction(leftX, rightX, pointsCount);

        // Заполняем значения функции
        double step = (rightX - leftX) / (pointsCount - 1);
        for (int i = 0; i < pointsCount; i++) {
            double x = leftX + i * step;
            double y = function.getFunctionValue(x);
            tabulatedFunc.setPointY(i, y);
        }

        return tabulatedFunc;
    }

    public static void outputTabulatedFunction(TabulatedFunction function, OutputStream out) throws IOException {
        DataOutputStream dataOut = new DataOutputStream(out);

        // Записываем количество точек
        dataOut.writeInt(function.getPointsCount());

        // Записываем координаты точек
        for (int i = 0; i < function.getPointsCount(); i++) {
            dataOut.writeDouble(function.getPointX(i));
            dataOut.writeDouble(function.getPointY(i));
        }

        // Не закрываем поток! Пусть это делает вызывающий код
        dataOut.flush();
    }

    public static TabulatedFunction inputTabulatedFunction(InputStream in) throws IOException {
        DataInputStream dataIn = new DataInputStream(in);

        // Читаем количество точек
        int pointsCount = dataIn.readInt();

        // Читаем точки
        FunctionPoint[] points = new FunctionPoint[pointsCount];
        for (int i = 0; i < pointsCount; i++) {
            double x = dataIn.readDouble();
            double y = dataIn.readDouble();
            points[i] = new FunctionPoint(x, y);
        }

        // Создаем табулированную функцию (используем ArrayTabulatedFunction по умолчанию)
        return new ArrayTabulatedFunction(points);
    }

    public static void writeTabulatedFunction(TabulatedFunction function, Writer out) throws IOException {
        PrintWriter writer = new PrintWriter(out);

        // Записываем количество точек
        writer.print(function.getPointsCount());
        writer.print(' ');

        // Записываем координаты точек
        for (int i = 0; i < function.getPointsCount(); i++) {
            writer.print(function.getPointX(i));
            writer.print(' ');
            writer.print(function.getPointY(i));
            if (i < function.getPointsCount() - 1) {
                writer.print(' ');
            }
        }

        // Не закрываем поток! Пусть это делает вызывающий код
        writer.flush();
    }

    public static TabulatedFunction readTabulatedFunction(Reader in) throws IOException {
        StreamTokenizer tokenizer = new StreamTokenizer(in);

        // Читаем количество точек
        tokenizer.nextToken();
        int pointsCount = (int) tokenizer.nval;

        // Читаем точки
        FunctionPoint[] points = new FunctionPoint[pointsCount];
        for (int i = 0; i < pointsCount; i++) {
            tokenizer.nextToken();
            double x = tokenizer.nval;

            tokenizer.nextToken();
            double y = tokenizer.nval;

            points[i] = new FunctionPoint(x, y);
        }

        // Создаем табулированную функцию (используем ArrayTabulatedFunction по умолчанию)
        return new ArrayTabulatedFunction(points);
    }
}
