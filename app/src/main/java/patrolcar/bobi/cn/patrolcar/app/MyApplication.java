package patrolcar.bobi.cn.patrolcar.app;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;

/**
 * Application 基类
 */

public class MyApplication extends Application {
    // 以下属性应用于整个应用程序，合理利用资源，减少资源浪费
    @SuppressLint("StaticFieldLeak")
    private static Context mContext;          // 上下文
    private static Thread mMainThread;        // 主线程
    private static long mMainThreadId;        // 主线程 id
    private static Looper mMainLooper;        // 循环队列
    private static Handler mHandler;          // 主线程 Handler

    @Override
    public void onCreate() {
        super.onCreate();
        // 对全局属性赋值
        mContext = getApplicationContext();
        mMainThread = Thread.currentThread();
        mMainThreadId = android.os.Process.myTid();
        mHandler = new Handler();
    }

    public static Context getContext() {
        return mContext;
    }

    public static void setContext(Context context) {
        MyApplication.mContext = context;
    }

    public static Thread getmMainThread() {
        return mMainThread;
    }

    public static void setmMainThread(Thread mainThread) {
        MyApplication.mMainThread = mainThread;
    }

    public static long getmMainThreadId() {
        return mMainThreadId;
    }

    public static void setmMainThreadId(long mainThreadId) {
        MyApplication.mMainThreadId = mainThreadId;
    }

    public static Looper getmMainLooper() {
        return mMainLooper;
    }

    public static void setmMainLooper(Looper mainLooper) {
        MyApplication.mMainLooper = mainLooper;
    }

    public static Handler getmHandler() {
        return mHandler;
    }

    public static void setmHandler(Handler handler) {
        MyApplication.mHandler = handler;
    }

    /** 重启当前应用 */
    public static void restart() {
        Intent intent = mContext.getPackageManager().getLaunchIntentForPackage(mContext.getPackageName());
        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }
        mContext.startActivity(intent);
    }
}
