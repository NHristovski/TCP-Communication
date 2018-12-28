package data;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static protocol.ComunicationProtocol.DELIMITER;

public class ClientSocket {
    private InetAddress address;
    private int port;

    public ClientSocket(InetAddress address, int port) {
        this.address = address;
        this.port = port;
    }

    public InetAddress getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    @Override
    public String toString() {
        return address.toString() + DELIMITER + port;
    }

    public static ClientSocket fromString(String address,String port) throws UnknownHostException {
        return new ClientSocket(InetAddress.getByName(address.replace("/","")),Integer.parseInt(port.trim()));
    }
}
