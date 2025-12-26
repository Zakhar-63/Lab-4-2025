package functions;

import java.io.*;
import java.util.Arrays;

public class LinkedListTabulatedFunction implements TabulatedFunction, Externalizable {
    private static final double EPSILON = 1e-10;



    // Внутренний класс для узла списка
    private static class FunctionNode implements Serializable {
        private FunctionPoint point;
        private FunctionNode prev;
        private FunctionNode next;
        
        public FunctionNode(FunctionPoint point) {
            this.point = point;
        }
        
        // Геттеры и сеттеры
        public FunctionPoint getPoint() { return point; }
        public void setPoint(FunctionPoint point) { this.point = point; }
        public FunctionNode getPrev() { return prev; }
        public void setPrev(FunctionNode prev) { this.prev = prev; }
        public FunctionNode getNext() { return next; }
        public void setNext(FunctionNode next) { this.next = next; }
    }
    
    private FunctionNode head; // голова списка (не хранит данных)
    private int size; // количество элементов
    private FunctionNode lastAccessedNode; // для оптимизации доступа
    private int lastAccessedIndex;

    // Реализация Externalizable
    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(size);

        // Записываем все точки
        FunctionNode current = head.getNext();
        for (int i = 0; i < size; i++) {
            out.writeDouble(current.getPoint().getX());
            out.writeDouble(current.getPoint().getY());
            current = current.getNext();
        }
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        int newSize = in.readInt();

        // Восстанавливаем список
        head = new FunctionNode(null);
        head.setNext(head);
        head.setPrev(head);
        size = 0;

        for (int i = 0; i < newSize; i++) {
            double x = in.readDouble();
            double y = in.readDouble();
            addNodeToTail().setPoint(new FunctionPoint(x, y));
        }
    }

    // Конструкторы

    // Конструктор по умолчанию
    public LinkedListTabulatedFunction() {
        // Инициализация пустого циклического списка с головой
        head = new FunctionNode(null);
        head.setNext(head);
        head.setPrev(head);
        size = 0;
        lastAccessedNode = null;
        lastAccessedIndex = -1;
    }

    public LinkedListTabulatedFunction(FunctionPoint[] points) {
        if (points.length < 2) {
            throw new IllegalArgumentException("Требуется не менее 2 точек");
        }

        // Проверка упорядоченности точек
        for (int i = 1; i < points.length; i++) {
            if (points[i].getX() <= points[i-1].getX() + EPSILON) {
                throw new IllegalArgumentException("Точки должны быть упорядочены по X");
            }
        }

        // Инициализация циклического списка с головой
        head = new FunctionNode(null);
        head.setNext(head);
        head.setPrev(head);
        size = 0;
        lastAccessedNode = null;
        lastAccessedIndex = -1;

        // Добавление точек из массива
        for (FunctionPoint point : points) {
            addNodeToTail().setPoint(new FunctionPoint(point));
        }
    }

    public LinkedListTabulatedFunction(double leftX, double rightX, int pointsCount) {
        if (leftX >= rightX) {
            throw new IllegalArgumentException("Левая граница должна быть меньше правой");
        }
        if (pointsCount < 2) {
            throw new IllegalArgumentException("Количество точек должно быть не менее 2");
        }
        
        // Инициализация циклического списка с головой
        head = new FunctionNode(null);
        head.setNext(head);
        head.setPrev(head);
        size = 0;
        lastAccessedNode = null;
        lastAccessedIndex = -1;
        
        // Добавление точек
        double step = (rightX - leftX) / (pointsCount - 1);
        for (int i = 0; i < pointsCount; i++) {
            double x = leftX + i * step;
            addNodeToTail().setPoint(new FunctionPoint(x, 0));
        }
    }
    
    public LinkedListTabulatedFunction(double leftX, double rightX, double[] values) {
        if (leftX >= rightX) {
            throw new IllegalArgumentException("Левая граница должна быть меньше правой");
        }
        if (values.length < 2) {
            throw new IllegalArgumentException("Количество точек должно быть не менее 2");
        }
        
        // Инициализация циклического списка с головой
        head = new FunctionNode(null);
        head.setNext(head);
        head.setPrev(head);
        size = 0;
        lastAccessedNode = null;
        lastAccessedIndex = -1;
        
        // Добавление точек
        double step = (rightX - leftX) / (values.length - 1);
        for (int i = 0; i < values.length; i++) {
            double x = leftX + i * step;
            addNodeToTail().setPoint(new FunctionPoint(x, values[i]));
        }
    }
    
    // Вспомогательные методы для работы со списком
    private FunctionNode getNodeByIndex(int index) {
        if (index < 0 || index >= size) {
            throw new FunctionPointIndexOutOfBoundsException("Индекс " + index + " вне границ [0, " + (size-1) + "]");
        }
        
        // Оптимизация: начинаем с последнего доступного узла
        FunctionNode current;
        if (lastAccessedNode != null && lastAccessedIndex >= 0) {
            int distance = Math.abs(index - lastAccessedIndex);
            if (distance < index && distance < size - index) {
                current = lastAccessedNode;
                int steps = distance;
                if (index > lastAccessedIndex) {
                    // Двигаемся вперед
                    for (int i = 0; i < steps; i++) {
                        current = current.getNext();
                    }
                } else {
                    // Двигаемся назад
                    for (int i = 0; i < steps; i++) {
                        current = current.getPrev();
                    }
                }
            } else {
                // Начинаем с головы
                current = head.getNext();
                for (int i = 0; i < index; i++) {
                    current = current.getNext();
                }
            }
        } else {
            // Начинаем с головы
            current = head.getNext();
            for (int i = 0; i < index; i++) {
                current = current.getNext();
            }
        }
        
        lastAccessedNode = current;
        lastAccessedIndex = index;
        return current;
    }
    
    private FunctionNode addNodeToTail() {
        FunctionNode newNode = new FunctionNode(null);
        FunctionNode tail = head.getPrev();
        
        // Вставляем новый узел перед головой (в конец циклического списка)
        tail.setNext(newNode);
        newNode.setPrev(tail);
        newNode.setNext(head);
        head.setPrev(newNode);
        
        size++;
        return newNode;
    }
    
    private FunctionNode addNodeByIndex(int index) {
        if (index < 0 || index > size) {
            throw new FunctionPointIndexOutOfBoundsException("Индекс " + index + " вне границ [0, " + size + "]");
        }
        
        FunctionNode newNode = new FunctionNode(null);
        FunctionNode current;
        
        if (index == size) {
            // Вставка в конец
            current = head;
        } else {
            current = getNodeByIndex(index);
        }
        
        // Вставляем перед current
        FunctionNode prevNode = current.getPrev();
        prevNode.setNext(newNode);
        newNode.setPrev(prevNode);
        newNode.setNext(current);
        current.setPrev(newNode);
        
        size++;
        lastAccessedNode = newNode;
        lastAccessedIndex = index;
        return newNode;
    }
    
    private FunctionNode deleteNodeByIndex(int index) {
        if (index < 0 || index >= size) {
            throw new FunctionPointIndexOutOfBoundsException("Индекс " + index + " вне границ [0, " + (size-1) + "]");
        }
        if (size < 3) {
            throw new IllegalStateException("Нельзя удалить точку: функция должна содержать минимум 2 точки");
        }
        
        FunctionNode nodeToDelete = getNodeByIndex(index);
        
        // Исключаем узел из списка
        nodeToDelete.getPrev().setNext(nodeToDelete.getNext());
        nodeToDelete.getNext().setPrev(nodeToDelete.getPrev());
        
        size--;
        lastAccessedNode = null;
        lastAccessedIndex = -1;
        
        return nodeToDelete;
    }
    
    // Методы интерфейса TabulatedFunction
    public double getLeftDomainBorder() {
        if (size == 0) return Double.NaN;
        return head.getNext().getPoint().getX();
    }
    
    public double getRightDomainBorder() {
        if (size == 0) return Double.NaN;
        return head.getPrev().getPoint().getX();
    }
    
    public double getFunctionValue(double x) {
        if (size == 0) return Double.NaN;
        if (x < getLeftDomainBorder() - EPSILON || x > getRightDomainBorder() + EPSILON) {
            return Double.NaN;
        }
        
        // Поиск узла, следующего за точкой с x-координатой, большей или равной x
        FunctionNode current = head.getNext();
        for (int i = 0; i < size; i++) {
            if (current.getPoint().getX() >= x - EPSILON) {
                // Если x совпадает с текущей точкой
                if (Math.abs(current.getPoint().getX() - x) < EPSILON) {
                    return current.getPoint().getY();
                }
                
                // Если это первая точка
                if (i == 0) {
                    return current.getPoint().getY();
                }
                
                // Интерполяция между предыдущей и текущей точкой
                FunctionNode prevNode = current.getPrev();
                double x1 = prevNode.getPoint().getX();
                double y1 = prevNode.getPoint().getY();
                double x2 = current.getPoint().getX();
                double y2 = current.getPoint().getY();
                
                return y1 + (y2 - y1) * (x - x1) / (x2 - x1);
            }
            current = current.getNext();
        }
        
        // Если x больше всех точек, возвращаем значение последней точки
        return head.getPrev().getPoint().getY();
    }
    
    public int getPointsCount() {
        return size;
    }
    
    public FunctionPoint getPoint(int index) throws FunctionPointIndexOutOfBoundsException {
        return new FunctionPoint(getNodeByIndex(index).getPoint());
    }
    
    public void setPoint(int index, FunctionPoint point) throws 
            FunctionPointIndexOutOfBoundsException, InappropriateFunctionPointException {
        
        FunctionNode node = getNodeByIndex(index);
        
        // Проверка корректности новой x-координаты
        if (index > 0 && point.getX() <= getNodeByIndex(index-1).getPoint().getX() + EPSILON) {
            throw new InappropriateFunctionPointException(
                "X-координата точки (" + point.getX() + ") должна быть больше предыдущей");
        }
        if (index < size - 1 && point.getX() >= getNodeByIndex(index+1).getPoint().getX() - EPSILON) {
            throw new InappropriateFunctionPointException(
                "X-координата точки (" + point.getX() + ") должна быть меньше следующей");
        }
        
        node.setPoint(new FunctionPoint(point));
    }
    
    public double getPointX(int index) throws FunctionPointIndexOutOfBoundsException {
        return getNodeByIndex(index).getPoint().getX();
    }
    
    public void setPointX(int index, double x) throws 
            FunctionPointIndexOutOfBoundsException, InappropriateFunctionPointException {
        
        FunctionNode node = getNodeByIndex(index);
        FunctionPoint oldPoint = node.getPoint();
        
        // Проверка корректности новой x-координаты
        if (index > 0 && x <= getNodeByIndex(index-1).getPoint().getX() + EPSILON) {
            throw new InappropriateFunctionPointException(
                "X-координата (" + x + ") должна быть больше предыдущей");
        }
        if (index < size - 1 && x >= getNodeByIndex(index+1).getPoint().getX() - EPSILON) {
            throw new InappropriateFunctionPointException(
                "X-координата (" + x + ") должна быть меньше следующей");
        }
        
        node.setPoint(new FunctionPoint(x, oldPoint.getY()));
    }
    
    public double getPointY(int index) throws FunctionPointIndexOutOfBoundsException {
        return getNodeByIndex(index).getPoint().getY();
    }
    
    public void setPointY(int index, double y) throws FunctionPointIndexOutOfBoundsException {
        FunctionNode node = getNodeByIndex(index);
        FunctionPoint oldPoint = node.getPoint();
        node.setPoint(new FunctionPoint(oldPoint.getX(), y));
    }
    
    public void deletePoint(int index) throws 
            FunctionPointIndexOutOfBoundsException, IllegalStateException {
        
        deleteNodeByIndex(index);
    }
    
    public void addPoint(FunctionPoint point) throws InappropriateFunctionPointException {
        // Проверяем, нет ли уже точки с такой x-координатой
        FunctionNode current = head.getNext();
        for (int i = 0; i < size; i++) {
            if (Math.abs(current.getPoint().getX() - point.getX()) < EPSILON) {
                throw new InappropriateFunctionPointException(
                    "Точка с x=" + point.getX() + " уже существует");
            }
            current = current.getNext();
        }
        
        // Находим позицию для вставки
        current = head.getNext();
        int insertIndex = 0;
        while (insertIndex < size && current.getPoint().getX() < point.getX() - EPSILON) {
            current = current.getNext();
            insertIndex++;
        }
        
        // Вставляем новую точку
        FunctionNode newNode = addNodeByIndex(insertIndex);
        newNode.setPoint(new FunctionPoint(point));
    }
}
