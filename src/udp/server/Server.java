package udp.server;

import data.ClientSocket;
import data.WorkerSocket;
import execution.Operation;
import execution.OperationFactory;
import execution.Result;
import protocol.ComunicationProtocol;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static protocol.ComunicationProtocol.DELIMITER;
import static protocol.ComunicationProtocol.REQUEST_EXECUTE;
import static protocol.ComunicationProtocol.REQUEST_SUBSCRIBE;
import static protocol.ComunicationProtocol.SERVER_PORT;

public class Server {
    private DatagramSocket socket;
    private DatagramPacket packet;

    private List<WorkerSocket> avaliableServers;

    public Server() throws UnknownHostException, SocketException {
        this.socket = new DatagramSocket(SERVER_PORT,InetAddress.getLocalHost());
        avaliableServers = new ArrayList<>();
    }

    public synchronized void addAvaliableServer(WorkerSocket workerSocket){
        avaliableServers.add(workerSocket);
    }

    public synchronized WorkerSocket getAvaliableServer(){
        if (avaliableServers.isEmpty()){
            return null;
        }
        return avaliableServers.remove(0);
    }

    public void start(){
        while(true) {
            byte[] buffer = new byte[100];
            packet = new DatagramPacket(buffer,buffer.length);
            try {
                socket.receive(packet);
                String request = new String(packet.getData());

                String[] parts = request.split(DELIMITER);
                int requestCode = Integer.parseInt(parts[0].trim());

                if (requestCode == REQUEST_SUBSCRIBE){

                    InetAddress address = InetAddress.getByName(parts[1].replace("/",""));
                    int port = Integer.parseInt(parts[2].trim());
                    WorkerSocket workerSocket =
                            new WorkerSocket(address,port);

                    addAvaliableServer(workerSocket);
                }else if (requestCode == REQUEST_EXECUTE){

                    WorkerSocket worker = getAvaliableServer();

                    if (worker == null){
                        Result result = new Result(false,0);
                        byte[] resultBuffer = result.toString().getBytes();
                        DatagramPacket packetResult = new DatagramPacket(resultBuffer,resultBuffer.length);
                        socket.send(packetResult);
                    }

                    double leftOperand = Double.parseDouble(parts[1]);
                    double rightOperand = Double.parseDouble(parts[2]);
                    char operator = parts[3].charAt(0);

                    Operation operation = OperationFactory
                            .getOperation(operator, leftOperand, rightOperand);

                    ClientSocket clientSocket = new ClientSocket(packet.getAddress(),packet.getPort());

                    byte[] workerRequestBuffer =
                            (operation.toString()
                                    + DELIMITER
                                    + clientSocket.toString()).getBytes();

                    DatagramPacket packetRequest =
                            new DatagramPacket(workerRequestBuffer,workerRequestBuffer.length);

                    packetRequest.setAddress(worker.getAddress());
                    packetRequest.setPort(worker.getPort());

                    socket.send(packetRequest);

                    addAvaliableServer(worker);
                }else{
                    System.out.println("Wrong command");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws SocketException, UnknownHostException {
        Server server = new Server();
        server.start();
    }
}
