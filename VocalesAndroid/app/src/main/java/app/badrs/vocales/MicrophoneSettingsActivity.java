package app.badrs.vocales;

import androidx.appcompat.app.AppCompatActivity;

import android.media.audiofx.AcousticEchoCanceler;
import android.media.audiofx.NoiseSuppressor;
import android.os.Bundle;
import android.widget.CheckBox;

public class MicrophoneSettingsActivity extends AppCompatActivity {

    /* Audio Configurations */
    private final NoiseSuppressor noiseSuppressor
            = NoiseSuppressor.create(MainActivity.audioRecorder.getAudioSessionId());
    private final AcousticEchoCanceler acousticEchoCanceler
            = AcousticEchoCanceler.create(MainActivity.audioRecorder.getAudioSessionId());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_microphone_settings);

        initializeViews();
    }

    public void initializeViews() {
        /* CheckBox */
        CheckBox noiseSuppressionCheckBox = findViewById(R.id.noiseSuppressionCheckBox);
        CheckBox echoCancellationCheckBox = findViewById(R.id.echoCancellationCheckBox);

        noiseSuppressionCheckBox.setOnCheckedChangeListener((buttonView, isChecked) ->
                noiseSuppressor.setEnabled(isChecked));

        echoCancellationCheckBox.setOnCheckedChangeListener((buttonView, isChecked) ->
                acousticEchoCanceler.setEnabled(isChecked));
    }


}