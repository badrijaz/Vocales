package app.badrs;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

public class Main {

    /* Socket and Packet */
    private static String IP_ADDRESS = "192.168.10.4";
    private static final int PORT = 55286;
    private static InetSocketAddress address = new InetSocketAddress(IP_ADDRESS, PORT);
    private static final int BUFFER_SIZE = 10000;
    private static DatagramSocket serverSocket;
    private static DatagramPacket receivedPacket;

    private static Thread serverThread;

    public static void main(String[] args) throws Exception {



	    /* Initialize and connect to Android */
        serverSocket = new DatagramSocket(null);
        serverSocket.connect(address);

        /* Thread */
        serverThread = new Thread(() -> {
            while (serverThread == Thread.currentThread()) {
                // Ensure the client (this) is alive
                try {
                    serverSocket.send(new DatagramPacket(new byte[0], 0));

                    // Receive packet from the Android server
                    byte[] receivedData = new byte[BUFFER_SIZE];
                    receivedPacket = new DatagramPacket(receivedData, receivedData.length);
                    serverSocket.receive(receivedPacket);

                    Util.log("Received packet " + receivedPacket.getLength());
                    // Play audio to mixer
                    Util.playAudioFromBytes(receivedPacket.getData());

                } catch (IOException error) {
                    error.printStackTrace();
                }
            }
        });

        /* GUI */
        new UserInterface(serverThread);
    }

    public static void connectToServer() {
        Util.log("Starting server...");
        serverThread.start();
    }

    public static void disconnectFromServer() {
        Util.log("Disconnecting...");
        serverThread.stop();
    }
}
