package udp.client;

import execution.Result;
import protocol.ComunicationProtocol;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

import static protocol.ComunicationProtocol.DELIMITER;
import static protocol.ComunicationProtocol.SERVER_PORT;

public class Client {
    public static void main(String[] args) throws IOException {
        Scanner input = new Scanner(System.in);

        System.out.println("Input number:");
        double d1 = Double.parseDouble(input.nextLine());
        System.out.println("Input number:");
        double d2 = Double.parseDouble(input.nextLine());

        System.out.println("Input operand:");
        char operand = input.nextLine().charAt(0);

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(ComunicationProtocol.REQUEST_EXECUTE)
                .append(DELIMITER)
                .append(d1)
                .append(DELIMITER)
                .append(d2)
                .append(DELIMITER)
                .append(operand);

        byte[] requestBuffer = stringBuilder.toString().getBytes();

        DatagramPacket packet =
                new DatagramPacket(requestBuffer,requestBuffer.length
                        ,InetAddress.getLocalHost(),SERVER_PORT);

        DatagramSocket socket = new DatagramSocket();
        socket.send(packet);

        byte[] buff = new byte[100];
        DatagramPacket packetToRecieve = new DatagramPacket(buff,buff.length);

        socket.receive(packetToRecieve);

        String resultString = new String(packetToRecieve.getData());

        Result result = Result.fromString(resultString);

        if(result.isSuccessful()){
            System.out.println("Answer " + result.getResult());
        }else{
            System.out.println("Fail");
        }

    }
}
