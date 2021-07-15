package app.badrs.vocales;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity {

    // Views
    private ImageView imageViewStreaming;
    private TextView textViewIsStreaming;
    private Button buttonStream;

    // Configuration

    private final int PORT = 8081;
    private boolean isStreaming = false;

    private Thread streamingThread = null;

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
                streamingThread = null;

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
    }
}