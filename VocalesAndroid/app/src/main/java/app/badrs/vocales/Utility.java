package app.badrs.vocales;

import android.app.Activity;
import android.content.Context;
import android.widget.Button;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class Utility {
    public static void toast(Context context, String textToToast) {
        Toast.makeText(context, textToToast, Toast.LENGTH_SHORT).show();
    }

    public static void temporarilyDisableButtonAccess(Activity activity, Button button, int delay) {
        button.setEnabled(false);

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                activity.runOnUiThread(() -> button.setEnabled(true));
            }
        }, delay);
    }
}
