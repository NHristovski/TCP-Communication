package execution;

public class OperationFactory {
    public static Operation getOperation(char operator,double leftOperand,double rigthOperand){
        switch (operator){
            case '+' : return new Addition(leftOperand,rigthOperand);
            case '-' : return new Substraction(leftOperand,rigthOperand);
            case '*' : return new Multiplication(leftOperand,rigthOperand);
            case '/' : return new Division(leftOperand,rigthOperand);
            default: return new NullOperation(leftOperand,rigthOperand);
        }
    }
}
