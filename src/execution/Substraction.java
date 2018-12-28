package execution;

import static protocol.ComunicationProtocol.DELIMITER;

public class Substraction extends Operation{
    public Substraction(double left,double right){
        super(left,right);
    }

    @Override
    public Result execute() {
        return new Result(true,leftOperand - rightOperand);
    }

    @Override
    public String toString() {
        return super.toString() + DELIMITER + "-";
    }
}
