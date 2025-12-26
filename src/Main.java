import functions.*;
import functions.basic.*;
import functions.meta.*;
import java.io.*;

public class Main {
    public static void main(String[] args) {
        try {
            System.out.println("\n--- Задание 8: Тестирование ---");
            testAssignment8();

            System.out.println("\n--- Задание 9: Сериализация ---");
            testSerialization();

        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void testAssignment8() throws Exception {
        // Создаем объекты Sin и Cos
        System.out.println("\n1. Объекты Sin и Cos созданы");
        Sin sinFunc = new Sin();
        Cos cosFunc = new Cos();

        // Выводим значения на отрезке от 0 до π с шагом 0.1
        System.out.println("\n2. Значения Sin и Cos от 0 до π с шагом 0.1:");
        System.out.println("   x\t\tSin(x)\t\tCos(x)");
        System.out.println("   ------------------------------------");
        for (double x = 0; x <= Math.PI + 1e-10; x += 0.1) {
            System.out.printf("   %.4f\t\t%.4f\t\t%.4f%n",
                    x, sinFunc.getFunctionValue(x), cosFunc.getFunctionValue(x));
        }

        // Создаем табулированные аналоги
        System.out.println("\n3. Табулированные аналоги (10 точек) созданы");
        TabulatedFunction tabSin = TabulatedFunctions.tabulate(sinFunc, 0, Math.PI, 10);
        TabulatedFunction tabCos = TabulatedFunctions.tabulate(cosFunc, 0, Math.PI, 10);

        // Сравниваем значения
        System.out.println("\n4. Сравнение аналитических и табулированных значений:");
        System.out.println("   x\t\tSin(x)\t\tTabSin(x)\tCos(x)\t\tTabCos(x)");
        System.out.println("   ----------------------------------------------------------------");
        for (double x = 0; x <= Math.PI + 1e-10; x += 0.1) {
            double sinVal = sinFunc.getFunctionValue(x);
            double cosVal = cosFunc.getFunctionValue(x);
            double tabSinVal = tabSin.getFunctionValue(x);
            double tabCosVal = tabCos.getFunctionValue(x);

            System.out.printf("   %.4f\t\t%.4f\t\t%.4f\t\t%.4f\t\t%.4f%n",
                    x, sinVal, tabSinVal, cosVal, tabCosVal);
        }

        // Создаем сумму квадратов
        System.out.println("\n5. Сумма квадратов синуса и косинуса:");
        Function sinSquared = Functions.power(tabSin, 2);
        Function cosSquared = Functions.power(tabCos, 2);
        Function sumSquares = Functions.sum(sinSquared, cosSquared);

        System.out.println("   x\t\tSin²(x)+Cos²(x)");
        System.out.println("   ------------------------");
        for (double x = 0; x <= Math.PI + 1e-10; x += 0.1) {
            System.out.printf("   %.4f\t\t%.4f%n", x, sumSquares.getFunctionValue(x));
        }

        // Тестирование с разным количеством точек
        System.out.println("\n6. Исследование зависимости от количества точек:");
        for (int points = 5; points <= 20; points += 5) {
            TabulatedFunction tabSinN = TabulatedFunctions.tabulate(sinFunc, 0, Math.PI, points);
            TabulatedFunction tabCosN = TabulatedFunctions.tabulate(cosFunc, 0, Math.PI, points);
            Function sumSquaresN = Functions.sum(
                    Functions.power(tabSinN, 2),
                    Functions.power(tabCosN, 2)
            );

            double error = 0;
            int count = 0;
            for (double x = 0; x <= Math.PI; x += 0.1) {
                double exact = 1.0; // sin²(x) + cos²(x) = 1
                double approx = sumSquaresN.getFunctionValue(x);
                error += Math.abs(exact - approx);
                count++;
            }
            System.out.printf("   %d точек: средняя ошибка = %.6f%n", points, error / count);
        }

        // Тестирование записи/чтения в файлы
        System.out.println("\n7. Тестирование записи/чтения файлов:");

        // Тест с экспонентой (бинарный формат)
        System.out.println("   а) Экспонента (бинарный формат):");
        Exp expFunc = new Exp();
        TabulatedFunction tabExp = TabulatedFunctions.tabulate(expFunc, 0, 10, 11);

        try (FileOutputStream fos = new FileOutputStream("exp_function.bin")) {
            TabulatedFunctions.outputTabulatedFunction(tabExp, fos);
        }

        TabulatedFunction loadedExp;
        try (FileInputStream fis = new FileInputStream("exp_function.bin")) {
            loadedExp = TabulatedFunctions.inputTabulatedFunction(fis);
        }

        System.out.println("   x\t\tИсходная\tЗагруженная");
        System.out.println("   ---------------------------------");
        for (double x = 0; x <= 10; x += 1.0) {
            double orig = tabExp.getFunctionValue(x);
            double loaded = loadedExp.getFunctionValue(x);
            System.out.printf("   %.1f\t\t%.4f\t\t%.4f%n", x, orig, loaded);
        }

        // Тест с логарифмом (текстовый формат)
        System.out.println("\n   б) Логарифм (текстовый формат):");
        Log logFunc = new Log(Math.E);
        TabulatedFunction tabLog = TabulatedFunctions.tabulate(logFunc, 0.1, 10, 11);

        try (FileWriter fw = new FileWriter("log_function.txt")) {
            TabulatedFunctions.writeTabulatedFunction(tabLog, fw);
        }

        TabulatedFunction loadedLog;
        try (FileReader fr = new FileReader("log_function.txt")) {
            loadedLog = TabulatedFunctions.readTabulatedFunction(fr);
        }

        System.out.println("   x\t\tИсходная\tЗагруженная");
        System.out.println("   ---------------------------------");
        for (double x = 0.1; x <= 10; x += 1.0) {
            double orig = tabLog.getFunctionValue(x);
            double loaded = loadedLog.getFunctionValue(x);
            System.out.printf("   %.1f\t\t%.4f\t\t%.4f%n", x, orig, loaded);
        }

        System.out.println("\n   в) Полученные файлы:");
        System.out.println("      - exp_function.bin (бинарный, компактный, нечитаемый)");
        System.out.println("      - log_function.txt (текстовый, читаемый, больше размер)");
    }

    private static void testSerialization() throws Exception {
        System.out.println("\n1. Тестирование сериализации:");

        // Создаем композицию логарифма от экспоненты
        Exp expFunc = new Exp();
        Log logFunc = new Log(Math.E);
        Function composition = Functions.composition(logFunc, expFunc);

        // Табулируем композицию
        TabulatedFunction tabulated = TabulatedFunctions.tabulate(composition, 0, 10, 11);

        // Сериализация с использованием Serializable
        System.out.println("   а) Serializable:");
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream("function_serializable.bin"))) {
            oos.writeObject(tabulated);
        }

        TabulatedFunction deserialized;
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream("function_serializable.bin"))) {
            deserialized = (TabulatedFunction) ois.readObject();
        }

        // Сравниваем значения
        System.out.println("   x\t\tИсходная\tДесериализованная");
        System.out.println("   ----------------------------------------");
        for (double x = 0; x <= 10; x += 1.0) {
            double orig = tabulated.getFunctionValue(x);
            double deser = deserialized.getFunctionValue(x);
            System.out.printf("   %.1f\t\t%.4f\t\t%.4f%n", x, orig, deser);
        }

        // Сериализация с использованием Externalizable (для LinkedListTabulatedFunction)
        System.out.println("\n   б) Externalizable (LinkedListTabulatedFunction):");
        TabulatedFunction linkedListFunc = new LinkedListTabulatedFunction(0, 10, 11);
        for (int i = 0; i < linkedListFunc.getPointsCount(); i++) {
            double x = linkedListFunc.getPointX(i);
            linkedListFunc.setPointY(i, Math.log(Math.exp(x))); // ln(e^x) = x
        }

        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream("function_externalizable.bin"))) {
            oos.writeObject(linkedListFunc);
        }

        TabulatedFunction deserializedExternal;
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream("function_externalizable.bin"))) {
            deserializedExternal = (TabulatedFunction) ois.readObject();
        }

        System.out.println("   x\t\tИсходная\tДесериализованная");
        System.out.println("   ----------------------------------------");

        for (double x = 0; x <= 10; x += 1.0) {
            double orig = linkedListFunc.getFunctionValue(x);
            double deser = deserializedExternal.getFunctionValue(x);
            boolean match = Math.abs(orig - deser) < 1e-10;
            System.out.printf("   %.1f\t\t%.4f\t\t%.4f%n",
                    x, orig, deser);
        }

        System.out.println("\n   в) Сравнение способов сериализации:");
        System.out.println("      Serializable: проще в реализации, автоматическая сериализация всех полей");
        System.out.println("      Externalizable: больше контроля, можно оптимизировать процесс сериализации");
        System.out.println("      Размеры файлов отличаются приблизительно в 2 раза в пользу externalizable.");
    }

    // Вспомогательный метод для вывода точек функции
    private static void printFunctionPoints(TabulatedFunction function, String name) {
        System.out.println("   " + name + " (" + function.getPointsCount() + " точек):");
        for (int i = 0; i < function.getPointsCount(); i++) {
            try {
                System.out.printf("      [%d] (%.4f; %.4f)%n",
                        i, function.getPointX(i), function.getPointY(i));
            } catch (Exception e) {
                System.out.println("      Ошибка: " + e.getMessage());
            }
        }
    }
}
