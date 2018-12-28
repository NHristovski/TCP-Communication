package tcp.server;

import data.WorkerSocket;
import execution.Operation;
import execution.Result;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class WorkerThread implements Runnable {
    private Socket clientSocket;
    private Operation operation;
    private Server server;

    public WorkerThread(Socket socket, Operation operation, Server server) {
        this.clientSocket = socket;
        this.operation = operation;
        this.server = server;
    }

    @Override
    public void run()  {
        WorkerSocket avaliableWorker = server.getAvaliableWorker();
        if (avaliableWorker == null){
            anounceFailure();
            return;
        }

        ObjectInputStream inputStream = null;
        Result result = null;

        try(Socket socket = new Socket(avaliableWorker.getAddress(),avaliableWorker.getPort());
            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream())) {

            outputStream.writeObject(operation);
            outputStream.flush();

            inputStream = new ObjectInputStream(socket.getInputStream());
            result = (Result)inputStream.readObject();

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null){
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        sendResultToClient(result);

        server.addAvaliableWorker(avaliableWorker);
    }

    private void sendResultToClient(Result result) {
        try(ObjectOutputStream outputToClient = new ObjectOutputStream(clientSocket.getOutputStream())){
            outputToClient.writeObject(result);
            outputToClient.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (clientSocket != null){
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void anounceFailure() {
        sendResultToClient(new Result(false,0));
    }
}
