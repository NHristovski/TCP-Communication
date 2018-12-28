package execution;

import protocol.ComunicationProtocol;

import java.io.Serializable;

public abstract class Operation implements Serializable {
    double leftOperand;
    double rightOperand;

    public Operation(double leftOperand, double rightOperand) {
        this.leftOperand = leftOperand;
        this.rightOperand = rightOperand;
    }

    public abstract Result execute();

    @Override
    public String toString() {
        return leftOperand + ComunicationProtocol.DELIMITER + rightOperand;
    }

    public static Operation fromString(String left,String right,String operand){
        return OperationFactory.getOperation(operand.charAt(0),Double.parseDouble(left),Double.parseDouble(right));
    }
}
