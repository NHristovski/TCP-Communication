package execution;

import static protocol.ComunicationProtocol.DELIMITER;

public class NullOperation extends Operation {
    public NullOperation(double leftOperand, double rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    public Result execute() {
        System.out.println("Unknow operator.");
        return null;
    }

    @Override
    public String toString() {
        return super.toString() + DELIMITER + '\0';
    }
}
