package execution;

import static protocol.ComunicationProtocol.DELIMITER;

public class Division extends Operation{
    public Division(double left,double right){
        super(left,right);
    }

    @Override
    public Result execute() {
        if (rightOperand == 0){
            return new Result(false,0);
        }
        return new Result(true,leftOperand / rightOperand);
    }

    @Override
    public String toString() {
        return super.toString() + DELIMITER + "/";
    }
}
