package udp.worker;

import data.ClientSocket;
import execution.Operation;
import execution.Result;
import protocol.ComunicationProtocol;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Random;

import static protocol.ComunicationProtocol.DELIMITER;
import static protocol.ComunicationProtocol.SERVER_PORT;

public class WorkerServer {
    DatagramSocket socket;
    DatagramPacket packet;

    public WorkerServer(int port) throws SocketException, UnknownHostException {
        InetAddress localHost = InetAddress.getLocalHost();
        this.socket = new DatagramSocket(port, localHost);
    }

    public void start() throws IOException {

        byte[] bytes = (ComunicationProtocol.REQUEST_SUBSCRIBE + DELIMITER
                + socket.getLocalAddress().toString() + DELIMITER
                + socket.getLocalPort()).getBytes();

        packet = new DatagramPacket(bytes,bytes.length,InetAddress.getLocalHost(),SERVER_PORT);

        socket.send(packet);

        while (true) {
            byte[] buffer = new byte[100];
            packet = new DatagramPacket(buffer, buffer.length);

            socket.receive(packet);

            String[] parts = new String(packet.getData()).split(DELIMITER);

            Operation operation = Operation.fromString(parts[0], parts[1], parts[2]);

            ClientSocket clientSocket = ClientSocket.fromString(parts[3], parts[4]);

            Result result = operation.execute();
            byte[] resultBytes = null;
            if (result != null) {
                resultBytes = result.toString().getBytes();
            }else{
                resultBytes = new Result(false,0).toString().getBytes();
            }
            DatagramPacket resultPacket =
                    new DatagramPacket(resultBytes,resultBytes.length,
                            clientSocket.getAddress(),clientSocket.getPort());

            socket.send(resultPacket);
        }
    }

    public static void main(String[] args) throws IOException {
        Random random = new Random();
        int port = Math.abs(random.nextInt() % 1024) + 2048;
        WorkerServer server = new WorkerServer(port);
        server.start();
    }
}
