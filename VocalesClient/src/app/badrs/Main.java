package app.badrs;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;

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

    public static void setupConnection() throws SocketException {
        InetSocketAddress address = new InetSocketAddress(UserInterface.addressTextArea.getText(), PORT);
        serverSocket = new DatagramSocket(null);
        serverSocket.connect(address);
    }

    public static void connectToServer() {
        try {
            setupConnection();
        } catch (SocketException ignored) {}

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
