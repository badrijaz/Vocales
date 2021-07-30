package app.badrs;

import javax.sound.sampled.*;

public class Util {

    /* Audio Configuration */
    private static final int SAMPLE_RATE = 44100;
    private static final short PCM_ENCODING = 16;
    private static final short CHANNEL_IN = 1;
    private static float volumeValue = 6.0f;

    private static AudioFormat format;
    private static DataLine.Info dataLineInfo;
    private static SourceDataLine sourceDataLine;
    private static FloatControl volume;

    private static final String PLAYBACK_MIXER = "Port CABLE Input";

    // Get the VB-Cable Input playback device
    public static Mixer.Info getPlaybackMixer() {
        for (Mixer.Info mixer : AudioSystem.getMixerInfo()) {
            if (mixer.getName().startsWith(PLAYBACK_MIXER)) {
                return mixer;
            }
        }

        // Return the default playback device if not found
        return AudioSystem.getMixerInfo()[0];
    }

    public static void playAudioFromBytes(byte[] serverBytes) {
        try {
            format = new AudioFormat(SAMPLE_RATE, PCM_ENCODING, CHANNEL_IN, true, false);
            dataLineInfo = new DataLine.Info(SourceDataLine.class, format);
            sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
            sourceDataLine.open(format);

            volume = (FloatControl) sourceDataLine.getControl(FloatControl.Type.MASTER_GAIN);
            volume.setValue(volumeValue);

            sourceDataLine.start();
            sourceDataLine.write(serverBytes, 0, serverBytes.length);
        } catch (LineUnavailableException lineUnavailableException) {
            lineUnavailableException.printStackTrace();
        }
    }

    public static void log(String stringToLog) {
        System.out.println(stringToLog);
    }

}
