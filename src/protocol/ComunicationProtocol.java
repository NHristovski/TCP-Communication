package protocol;

public interface ComunicationProtocol {
    int REQUEST_SUBSCRIBE = 1;
    int REQUEST_EXECUTE = 2;

    String SERVER_ADDRESS = "0.0.0.0";
    int SERVER_PORT = 1234;

    String DELIMITER = "&";
}
