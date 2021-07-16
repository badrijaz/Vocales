package app.badrs.vocales;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.text.format.Formatter;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import java.net.InetSocketAddress;
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

    public static InetSocketAddress getLocalAddress(Activity activity, int port) {
        WifiManager wifiManager = (WifiManager) activity
                                .getApplicationContext()
                                .getSystemService(Context.WIFI_SERVICE);
        String localHostAddress
                = Formatter.formatIpAddress(wifiManager.getConnectionInfo().getIpAddress());

        return new InetSocketAddress(localHostAddress, port);
    }

}
