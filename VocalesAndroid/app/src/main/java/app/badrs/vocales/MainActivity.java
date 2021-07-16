package app.badrs.vocales;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.Html;
import android.text.format.Formatter;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    // Views
    private ImageView imageViewStreaming;
    private TextView textViewIsStreaming;
    private Button buttonStream;

    // Configuration
    private Thread streamingThread;
    private final int PORT = 55286;
    private boolean isStreaming = false;
    private final byte[] buffer = new byte[256];

    // UDP Socket
    private DatagramSocket udpServerSocket;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get imageViewStreaming
        imageViewStreaming = findViewById(R.id.imageViewStreaming);

        // Get textViewIsStreaming
        textViewIsStreaming = findViewById(R.id.textViewIsStreaming);

        // Get buttonStream and set onClickListener
        buttonStream = findViewById(R.id.buttonStream);
        buttonStream.setOnClickListener(view -> {
            if (isStreaming) {
                imageViewStreaming.setVisibility(View.INVISIBLE);

                textViewIsStreaming.setText(R.string.text_is_streaming);

                buttonStream.setText(R.string.text_stream);
                buttonStream.setBackgroundColor(getResources().getColor(R.color.purple_500));

                // Stop the thread for socket streaming
                isStreaming = false;
                stopStreaming();

            } else {
                imageViewStreaming.setVisibility(View.VISIBLE);

                String streamingText = "Streaming to port <b>" + PORT + "</b>..";
                textViewIsStreaming.setText(Html.fromHtml(streamingText));
                textViewIsStreaming.setTypeface(textViewIsStreaming.getTypeface(), Typeface.BOLD);

                buttonStream.setText("Stop stream");
                buttonStream.setBackgroundColor(getResources().getColor(R.color.teal_700));

                // Start actual socket streaming
                isStreaming = true;
                startStreaming();
            }
        });
    }

    private void startStreaming() {

        // This disables the button access for 2 seconds
        buttonStream.setEnabled(false);

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                MainActivity.this.runOnUiThread(() -> buttonStream.setEnabled(true));
            }
        }, 2000);

        // Start UDP socket server

        streamingThread = new Thread(() -> {
            try {
                // We pass null in the UDP Socket constructor so that we can bind
                // IPv4 on the socket later
                udpServerSocket =  new DatagramSocket(null);

                // Get the IPv4 of the device
                Context mainActivityContext = MainActivity.this;
                WifiManager wifiManager
                        = (WifiManager) mainActivityContext.getSystemService(WIFI_SERVICE);
                String localHostAddress
                        = Formatter.formatIpAddress(wifiManager.getConnectionInfo().getIpAddress());
                InetSocketAddress address
                        = new InetSocketAddress(localHostAddress, PORT);

                // Bind that ip address
                udpServerSocket.bind(address);

                runOnUiThread(() ->
                    Toast.makeText(
                        this,
                        udpServerSocket.getLocalSocketAddress().toString(),
                        Toast.LENGTH_SHORT
                ).show());

                while (streamingThread == Thread.currentThread()) {
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    udpServerSocket.receive(packet);

                    packet = new DatagramPacket(
                            buffer,
                            buffer.length,
                            packet.getAddress(),
                            packet.getPort()
                    );

                    DatagramPacket finalPacket = packet; // runOnUIThread requires packet to be
                    runOnUiThread(() -> {                // final
                        Toast.makeText(
                                this,
                                new String(finalPacket.getData(), 0, finalPacket.getLength()),
                                Toast.LENGTH_SHORT
                        ).show();
                    });
                }
            } catch (IOException error) {
                error.printStackTrace();
            }
        });
        streamingThread.start();
    }

    private void stopStreaming() {
        streamingThread = null;
        udpServerSocket.close();
        Toast.makeText(this, "Closed socket", Toast.LENGTH_SHORT).show();
    }
}