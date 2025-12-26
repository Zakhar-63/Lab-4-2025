/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */


/**
 *
 * @author I5-10400F
 */

package functions;

public interface TabulatedFunction extends Function {

    // Методы получения информации о функции
    int getPointsCount();

    // Методы работы с точками
    FunctionPoint getPoint(int index) throws FunctionPointIndexOutOfBoundsException;
    void setPoint(int index, FunctionPoint point) throws 
        FunctionPointIndexOutOfBoundsException, InappropriateFunctionPointException;
    
    double getPointX(int index) throws FunctionPointIndexOutOfBoundsException;
    void setPointX(int index, double x) throws 
        FunctionPointIndexOutOfBoundsException, InappropriateFunctionPointException;
    
    double getPointY(int index) throws FunctionPointIndexOutOfBoundsException;
    void setPointY(int index, double y) throws FunctionPointIndexOutOfBoundsException;
    
    void deletePoint(int index) throws 
        FunctionPointIndexOutOfBoundsException, IllegalStateException;
    
    void addPoint(FunctionPoint point) throws InappropriateFunctionPointException;
}
