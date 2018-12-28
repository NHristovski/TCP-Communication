package execution;

import java.io.Serializable;

import static protocol.ComunicationProtocol.DELIMITER;

public class Result implements Serializable {
    boolean successful;
    double result;

    public Result(boolean successful, double result) {
        this.successful = successful;
        this.result = result;
    }

    public boolean isSuccessful() {
        return successful;
    }

    public double getResult() {
        return result;
    }

    @Override
    public String toString() {
        return successful + DELIMITER + result;
    }

    public static Result fromString(String result){
        String[] parts = result.split(DELIMITER);
        return new Result(Boolean.parseBoolean(parts[0]),Double.parseDouble(parts[1]));
    }
}
