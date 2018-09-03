package patrolcar.bobi.cn.patrolcar.util;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

public class ToastTools {

    private static Toast mToast;

    public static void showShort(Context context, CharSequence text) {
        mToast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        mToast.setGravity(Gravity.CENTER, 0, 0);
        mToast.show();
    }

    public static void showLong(Context context, CharSequence text) {
        mToast = Toast.makeText(context, text, Toast.LENGTH_LONG);
        mToast.setGravity(Gravity.CENTER, 0, 0);
        mToast.show();
    }
}
