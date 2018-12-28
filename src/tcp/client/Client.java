package tcp.client;


import execution.Result;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

import static protocol.ComunicationProtocol.*;

public class Client {
    private static final char DEFAULT_OPEEAND = '+';

    public void start(){
        ObjectInputStream input = null;
        try(Socket socket = new Socket(InetAddress.getByName(SERVER_ADDRESS),SERVER_PORT);
            ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
            Scanner in = new Scanner(System.in)){

            double leftOperand = Double.parseDouble(in.nextLine());
            double rigthOperand = Double.parseDouble(in.nextLine());
            String operandString = in.nextLine();

            char operand = DEFAULT_OPEEAND;
            if (operandString.length() > 0){
                operand = operandString.charAt(0);
            }

            output.writeInt(REQUEST_EXECUTE);

            output.writeDouble(leftOperand);
            output.writeDouble(rigthOperand);
            output.writeChar(operand);

            output.flush();

            input = new ObjectInputStream(socket.getInputStream());
            Result result = (Result)input.readObject();

            if (result == null || !result.isSuccessful()){
                System.out.println("The execution failed");
                return;
            }

            System.out.println("Result is: " + result.getResult());


        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (input != null){
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


    }

    public static void main(String[] args) {
        Client client = new Client();
        client.start();
    }
}
