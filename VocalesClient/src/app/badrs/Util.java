package app.badrs;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Mixer;
import java.util.Scanner;

public class Util {

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

    public static void log(String stringToLog) {
        System.out.println(stringToLog);
    }

    public static String getInput(String prompt) {
        Scanner scanner = new Scanner(System.in);
        System.out.println(prompt);

        return scanner.nextLine();
    }
}
