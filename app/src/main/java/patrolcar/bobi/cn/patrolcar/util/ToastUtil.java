package patrolcar.bobi.cn.patrolcar.util;

import android.view.Gravity;
import android.widget.Toast;

import patrolcar.bobi.cn.patrolcar.app.MyApplication;

public class ToastUtil {

    private static Toast mToast;

    public static void showShort(CharSequence text) {
        mToast = Toast.makeText(MyApplication.getContext(), text, Toast.LENGTH_SHORT);
        mToast.setGravity(Gravity.CENTER, 0, 0);
        mToast.show();
    }

    public static void showLong(CharSequence text) {
        mToast = Toast.makeText(MyApplication.getContext(), text, Toast.LENGTH_LONG);
        mToast.setGravity(Gravity.CENTER, 0, 0);
        mToast.show();
    }

}
