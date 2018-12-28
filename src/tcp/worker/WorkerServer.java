package tcp.worker;

import execution.Operation;
import execution.Result;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

import static protocol.ComunicationProtocol.*;
import static protocol.ComunicationProtocol.SERVER_ADDRESS;
import static protocol.ComunicationProtocol.SERVER_PORT;

public class WorkerServer {
    private ServerSocket serverSocket;
    private boolean shouldWork;

    public WorkerServer(int port) throws IOException {
        this.serverSocket = new ServerSocket(port);
        shouldWork = true;
        subscribeToCentralServer();

    }

    private void subscribeToCentralServer() {
        ObjectInputStream input = null;
        try(Socket socket = new Socket(InetAddress.getByName(SERVER_ADDRESS),SERVER_PORT);
            ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream())){

            output.writeInt(REQUEST_SUBSCRIBE);
            output.writeObject(serverSocket.getInetAddress());
            output.writeInt(serverSocket.getLocalPort());
            output.flush();
        } catch (IOException e) {
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

    public void start(){
        ObjectOutputStream output = null;
        while (shouldWork){
            try (Socket clientSocket = serverSocket.accept();
                 ObjectInputStream input = new ObjectInputStream(clientSocket.getInputStream())){

                Operation operation = (Operation)input.readObject();
                Result result = operation.execute();

                output = new ObjectOutputStream(clientSocket.getOutputStream());

                output.writeObject(result);
                output.flush();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
                if (output != null){
                    try {
                        output.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void stop(){
        shouldWork = false;
    }

    public static void main(String[] args) {
        Random rand = new Random();
        int port = Math.abs(rand.nextInt() % 1024) + 2048;
        try {
            WorkerServer server = new WorkerServer(port);
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
