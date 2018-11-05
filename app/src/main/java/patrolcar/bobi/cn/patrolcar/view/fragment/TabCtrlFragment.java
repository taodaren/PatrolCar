package patrolcar.bobi.cn.patrolcar.view.fragment;

import android.view.View;

import butterknife.OnClick;
import patrolcar.bobi.cn.patrolcar.R;
import patrolcar.bobi.cn.patrolcar.util.BleCmdCtrl;
import patrolcar.bobi.cn.patrolcar.util.ToastUtil;
import patrolcar.bobi.cn.patrolcar.view.activity.MainActivity;
import patrolcar.bobi.cn.patrolcar.view.base.BaseFragment;

/**
 * 遥控模块
 */

public class TabCtrlFragment extends BaseFragment {

    public static TabCtrlFragment newInstance() {
        return new TabCtrlFragment();
    }

    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_tab_ctrl;
    }

    @Override
    public void initToolbar() {
        setToolbar(R.id.main_toolbar, R.string.txt_tab_control, View.VISIBLE);
    }

    private static final String MAC = "F7:4D:B8:21:A5:35";
    @OnClick({R.id.btn_ctrl_stop, R.id.rl_ctrl_up, R.id.rl_ctrl_down, R.id.rl_ctrl_left, R.id.rl_ctrl_right, R.id.open_device, R.id.close_device, R.id.brake_start, R.id.brake_release, R.id.open_motor, R.id.close_motor})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_ctrl_stop:
                BleCmdCtrl.sendCmdStop(MAC);
                ToastUtil.showShort("stop");
                break;
            case R.id.rl_ctrl_up:
                BleCmdCtrl.sendCmdUp(MAC);
                ToastUtil.showShort("up");
                break;
            case R.id.rl_ctrl_down:
                BleCmdCtrl.sendCmdDown(MAC);
                ToastUtil.showShort("down");
                break;
            case R.id.rl_ctrl_left:
                BleCmdCtrl.sendCmdLeft(MAC);
                ToastUtil.showShort("left");
                break;
            case R.id.rl_ctrl_right:
                BleCmdCtrl.sendCmdRight(MAC);
                ToastUtil.showShort("right");
                break;
            case R.id.open_device:
                BleCmdCtrl.sendCmdOpenDevice(MAC);
                ToastUtil.showShort("open_device");
                break;
            case R.id.close_device:
                BleCmdCtrl.sendCmdCloseDevice(MAC);
                ToastUtil.showShort("close_device");
                break;
            case R.id.brake_start:
                BleCmdCtrl.sendCmdBrakeStart(MAC);
                ToastUtil.showShort("brake_start");
                break;
            case R.id.brake_release:
                BleCmdCtrl.sendCmdBrakeRelease(MAC);
                ToastUtil.showShort("brake_release");
                break;
            case R.id.open_motor:
                BleCmdCtrl.sendCmdOpenMotor(MAC);
                ToastUtil.showShort("open_motor");
                break;
            case R.id.close_motor:
                BleCmdCtrl.sendCmdCloseMotor(MAC);
                ToastUtil.showShort("close_motor");
                break;
        }
    }

}
