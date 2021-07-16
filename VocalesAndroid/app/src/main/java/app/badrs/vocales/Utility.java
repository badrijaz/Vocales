package app.badrs.vocales;

import android.content.Context;
import android.widget.Toast;

public class Utility {
    public static void toast(Context context, String textToToast) {
        Toast.makeText(context, textToToast, Toast.LENGTH_SHORT).show();
    }
}
