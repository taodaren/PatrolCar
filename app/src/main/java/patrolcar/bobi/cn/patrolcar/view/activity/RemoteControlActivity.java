package patrolcar.bobi.cn.patrolcar.view.activity;

import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.OnClick;
import patrolcar.bobi.cn.blelib.data.BleDevice;
import patrolcar.bobi.cn.patrolcar.R;
import patrolcar.bobi.cn.blelib.utils.ToastTools;
import patrolcar.bobi.cn.patrolcar.comm.Observer;
import patrolcar.bobi.cn.patrolcar.comm.ObserverManager;
import patrolcar.bobi.cn.patrolcar.view.base.BaseActivity;

public class RemoteControlActivity extends BaseActivity implements Observer {
    private static final String TAG = RemoteControlActivity.class.getSimpleName();
    public static final String KEY_DATA = "key_data";

    @BindView(R.id.btn_ctrl_stop)        Button btnStop;
    @BindView(R.id.rl_ctrl_up)           RelativeLayout btnUp;
    @BindView(R.id.rl_ctrl_down)         RelativeLayout btnDown;
    @BindView(R.id.rl_ctrl_left)         RelativeLayout btnLeft;
    @BindView(R.id.rl_ctrl_right)        RelativeLayout btnRight;
    @BindView(R.id.open_device)          TextView btnDevOpen;
    @BindView(R.id.close_device)         TextView btnDevClose;
    @BindView(R.id.brake_start)          TextView btnBrakeStart;
    @BindView(R.id.brake_release)        TextView btnBrakeRelease;

    private BleDevice mBleDevice;

    @Override
    protected int getActivityLayout() {
        return R.layout.activity_remote_control;
    }

    @Override
    public void initView() {
        ObserverManager.getInstance().addObserver(this);
        mBleDevice = getIntent().getParcelableExtra(KEY_DATA);
        if (mBleDevice == null){ finish();}
        setToolbar(mBleDevice.getMac(), View.VISIBLE, null, View.GONE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ObserverManager.getInstance().deleteObserver(this);
    }

    @Override
    public void disConnected(BleDevice bleDevice) {
        if (bleDevice != null && mBleDevice != null && bleDevice.getKey().equals(mBleDevice.getKey())) {
            finish();
        }
    }

    @OnClick({R.id.btn_ctrl_stop, R.id.rl_ctrl_up, R.id.rl_ctrl_down, R.id.rl_ctrl_left, R.id.rl_ctrl_right, R.id.open_device, R.id.close_device, R.id.brake_start, R.id.brake_release})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_ctrl_stop:
                ToastTools.showShort(this, "stop");
                break;
            case R.id.rl_ctrl_up:
                ToastTools.showShort(this, "up");
                break;
            case R.id.rl_ctrl_down:
                ToastTools.showShort(this, "down");
                break;
            case R.id.rl_ctrl_left:
                ToastTools.showShort(this, "left");
                break;
            case R.id.rl_ctrl_right:
                ToastTools.showShort(this, "right");
                break;
            case R.id.open_device:
                ToastTools.showShort(this, "open");
                break;
            case R.id.close_device:
                ToastTools.showShort(this, "close");
                break;
            case R.id.brake_start:
                ToastTools.showShort(this, "start");
                break;
            case R.id.brake_release:
                ToastTools.showShort(this, "release");
                break;
        }
    }
}
