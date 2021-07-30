package app.badrs.vocales;

import androidx.appcompat.app.AppCompatActivity;

import android.media.audiofx.AcousticEchoCanceler;
import android.media.audiofx.NoiseSuppressor;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.util.HashMap;

public class MicrophoneSettingsActivity extends AppCompatActivity {

    /* Microphone Options */
    HashMap<String, Integer> microphoneOptions = new HashMap<>();

    /* Audio Configurations */
    private NoiseSuppressor noiseSuppressor;
    private AcousticEchoCanceler acousticEchoCanceler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_microphone_settings);

        initializeMicrophoneOptions();
        initializeViews();

        /* RadioGroup */
        RadioGroup microphoneOptionsRadioGroup = findViewById(R.id.microphoneOptionsRadioGroup);
        microphoneOptionsRadioGroup.setOnCheckedChangeListener((radioGroup, checkedId) -> {
            RadioButton microphoneOptionRadioButton = findViewById(checkedId);
            MainActivity.RECORDER_SOURCE = microphoneOptions.get(
                    microphoneOptionRadioButton.getText().toString()
            );

            Utility.toast(MicrophoneSettingsActivity.this, "Microphone changed");
        });
    }

    private void initializeMicrophoneOptions() {
        microphoneOptions.put("DEFAULT", 0);
        microphoneOptions.put("MIC", 1);
        microphoneOptions.put("VOICE_UPLINK", 2);
        microphoneOptions.put("VOICE_DOWNLINK", 3);
        microphoneOptions.put("VOICE_CALL", 4);
        microphoneOptions.put("CAMCORDER", 5);
        microphoneOptions.put("VOICE_RECOGNITION", 6);
        microphoneOptions.put("VOICE_COMMUNICATION", 7);
        microphoneOptions.put("VOICE_SUBMIX", 8);
        microphoneOptions.put("UNPROCESSED", 9);
        microphoneOptions.put("VOICE_PERFORMANCE", 10);
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