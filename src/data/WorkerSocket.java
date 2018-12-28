package data;

import java.net.InetAddress;

public class WorkerSocket {
    private InetAddress address;
    private int port;

    public WorkerSocket(InetAddress address, int port) {
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
        return address.toString() + ":" + port;
    }
}
