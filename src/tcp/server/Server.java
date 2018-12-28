package tcp.server;

import data.WorkerSocket;
import execution.Operation;
import execution.OperationFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static protocol.ComunicationProtocol.*;

public class Server {
    public static final int MAX_THREADS = 100;
    public static final int MAX_BACKLOG = 100;
    private ServerSocket serverSocket;
    private List<WorkerSocket> avaliableWorkers;
    private ExecutorService threadPool = Executors.newFixedThreadPool(MAX_THREADS);
    private boolean shouldWork;

    public Server() throws IOException {
        this.serverSocket = new ServerSocket(SERVER_PORT, MAX_BACKLOG, InetAddress.getByName(SERVER_ADDRESS));
        avaliableWorkers = new ArrayList<>();
        shouldWork = true;
    }

    public synchronized WorkerSocket getAvaliableWorker(){
        if (avaliableWorkers.isEmpty()){
            return null;
        }
        return avaliableWorkers.remove(0);
    }

    public synchronized void addAvaliableWorker(WorkerSocket workerSocket){
        this.avaliableWorkers.add(workerSocket);
    }

    public void start() {
        while (shouldWork) {
            try {
                Socket clientSocket = serverSocket.accept();

                ObjectInputStream input = new ObjectInputStream(clientSocket.getInputStream());
                int request = input.readInt();

                if (request == REQUEST_SUBSCRIBE) {
                    handleSubscribe(input);
                } else if (request == REQUEST_EXECUTE) {

                    double leftOperand = input.readDouble();
                    double rightOperand = input.readDouble();
                    char operand = input.readChar();

                    Operation operation = OperationFactory.getOperation(operand, leftOperand, rightOperand);

                    WorkerThread thread = new WorkerThread(clientSocket, operation, this);
                    threadPool.submit(thread);
                }else{
                    System.out.println("No such request.");
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        cleanUp();
    }

    public void stop(){
        this.shouldWork = false;
    }

    private void handleSubscribe(ObjectInputStream input) {
        try {
            InetAddress address = (InetAddress)input.readObject();
            int port = input.readInt();

            WorkerSocket workerSocket = new WorkerSocket(address,port);
            addAvaliableWorker(workerSocket);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }finally {
            if (input != null){
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void cleanUp() {
        avaliableWorkers.clear();

        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        threadPool.shutdown();
    }

    public static void main(String[] args) {
        try {
            Server server = new Server();
            server.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
