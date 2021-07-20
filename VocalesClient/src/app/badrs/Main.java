package app.badrs;

import java.io.IOException;
import java.net.*;

public class Main {

    /* Socket and Packet */
    private static final int PORT = 55286;
    private static final int BUFFER_SIZE = 10000;
    private static DatagramSocket serverSocket;
    private static DatagramPacket receivedPacket;

    public static Thread serverThread;
    public static boolean isThreadRunning = false;

    public static void main(String[] args) throws Exception {

        /* GUI */
        new UserInterface();
    }

    public static void setupConnection() {
        InetSocketAddress address = new InetSocketAddress(UserInterface.addressTextArea.getText(), PORT);
        try {
            serverSocket = new DatagramSocket(null);
        } catch (SocketException ignored) {} // Ignoring this because initially we're binding to a null port

        try {
            serverSocket.connect(address);
        } catch (SocketException | IllegalArgumentException socketException) {
            Util.log("Cannot connect");
        }
    }

    public static void connectToServer() {
        setupConnection();

        serverThread = new Thread(() -> {
            while (serverThread == Thread.currentThread() && !serverThread.isInterrupted()) {
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
        Util.log("Starting server...");
        serverThread.start();
    }

    public static void disconnectFromServer() {
        Util.log("Disconnecting...");
        serverThread.interrupt();
    }
}
