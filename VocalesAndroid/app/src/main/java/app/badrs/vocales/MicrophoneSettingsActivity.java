package app.badrs.vocales;

import androidx.appcompat.app.AppCompatActivity;

import android.media.audiofx.AcousticEchoCanceler;
import android.media.audiofx.NoiseSuppressor;
import android.os.Bundle;
import android.widget.CheckBox;

public class MicrophoneSettingsActivity extends AppCompatActivity {

    /* Audio Configurations */
    private NoiseSuppressor noiseSuppressor;
    private AcousticEchoCanceler acousticEchoCanceler;

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

        noiseSuppressionCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!NoiseSuppressor.isAvailable()) {
                Utility.toast(
                        MicrophoneSettingsActivity.this,
                        "Noise Suppressor not available"
                );
                return;
            }

            noiseSuppressor = (NoiseSuppressor) NoiseSuppressor.create(
                    MainActivity.audioRecorder.getAudioSessionId()
            );
            noiseSuppressor.setEnabled(isChecked);
        });


        echoCancellationCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            acousticEchoCanceler = (AcousticEchoCanceler) AcousticEchoCanceler.create(
                    MainActivity.audioRecorder.getAudioSessionId()
            );
            acousticEchoCanceler.setEnabled(isChecked);
        });
    }


}