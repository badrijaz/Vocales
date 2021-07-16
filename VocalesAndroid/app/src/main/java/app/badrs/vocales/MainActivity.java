package app.badrs.vocales;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.media.audiofx.AcousticEchoCanceler;
import android.media.audiofx.NoiseSuppressor;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

public class MainActivity extends AppCompatActivity {

    // Views
    private ImageView imageViewStreaming;
    private TextView textViewIsStreaming;
    private Button buttonStream;

    // Configuration
    private Thread streamingThread;
    private final int PORT = 55286;
    private boolean isStreaming = false;

    // UDP Socket
    private final byte[] buffer = new byte[256];
    private DatagramSocket udpServerSocket;

    // Audio
    AudioRecord audioRecorder;

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
                verifyRecordAudioPermissionsThenStream();
            }
        });
    }

    private void startStreaming() {

        // This disables the button access for 2 seconds
        Utility.temporarilyDisableButtonAccess(this, buttonStream, 2000);

        // Start UDP socket server
        streamingThread = new Thread(() -> {
            try {
                // We pass null in the UDP Socket constructor so that we can bind
                // IPv4 on the socket later
                udpServerSocket = new DatagramSocket(null);

                // Get the IPv4 of the device
                InetSocketAddress address = Utility.getLocalAddress(MainActivity.this, PORT);

                // Bind that ip address
                udpServerSocket.bind(address);

                runOnUiThread(() ->
                    Utility.toast(this, udpServerSocket.getLocalSocketAddress().toString())
                );

                // https://stackoverflow.com/a/51718306/15323175
                int bufferSize = AudioRecord.getMinBufferSize(
                        44100,
                        AudioFormat.CHANNEL_IN_MONO,
                        AudioFormat.ENCODING_PCM_16BIT
                );

                // Initialize AudioRecord for getting audio from device
                audioRecorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
                                                            44100,
                                                            AudioFormat.CHANNEL_IN_MONO,
                                                            AudioFormat.ENCODING_PCM_16BIT,
                                                            bufferSize);
                byte[] temporaryBuffer = new byte[bufferSize];

                final int API = Build.VERSION.SDK_INT;
                if (API > 16) {
                    if (NoiseSuppressor.isAvailable()) {
                        NoiseSuppressor.create(audioRecorder.getAudioSessionId()).setEnabled(true);
                    }
                    if (AcousticEchoCanceler.isAvailable()) {
                        AcousticEchoCanceler.create(
                                audioRecorder.getAudioSessionId()
                        ).setEnabled(true);
                    }
                }

                audioRecorder.startRecording();

                // Worker thread loop
                while (streamingThread == Thread.currentThread()) {
                    // Read audioRecorder data and wrap it in a packet for UDP socket to send
                    int bufferRead = audioRecorder.read(temporaryBuffer, 0, bufferSize);

                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    udpServerSocket.receive(packet); // this make sure the connection is established

                    packet = new DatagramPacket(temporaryBuffer, bufferRead,
                                                packet.getAddress(), packet.getPort());
                    try {
                        udpServerSocket.send(packet);

                        DatagramPacket finalPacket = packet; // packet needs to be final to run on
                                                             // uiThread
                        runOnUiThread(() -> textViewIsStreaming.setText(new String(
                                finalPacket.getData(), 0, finalPacket.getLength()
                        )));
                    } catch (java.net.SocketException ignored) { }
                }
            } catch (IOException error) {
                error.printStackTrace();
            }
        });
        streamingThread.start();
    }

    private void stopStreaming() {
        streamingThread = null;
        audioRecorder.stop(); // stopping audio after setting thread to null will avoid crashing app

        udpServerSocket.close();
        Utility.toast(this, "Closed socket");
        Utility.temporarilyDisableButtonAccess(this, buttonStream, 2000);
    }

    private void verifyRecordAudioPermissionsThenStream() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.RECORD_AUDIO
            }, 1);
        } else {
            startStreaming();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startStreaming();
            } else {
                Utility.toast(this, "No permission to record audio");
            }
        }
    }
}