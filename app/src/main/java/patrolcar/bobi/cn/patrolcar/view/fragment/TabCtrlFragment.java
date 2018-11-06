package patrolcar.bobi.cn.patrolcar.view.fragment;

import android.view.View;

import butterknife.OnClick;
import patrolcar.bobi.cn.patrolcar.R;
import patrolcar.bobi.cn.patrolcar.util.BleCmdCtrl;
import patrolcar.bobi.cn.patrolcar.view.base.BaseFragment;

import static patrolcar.bobi.cn.patrolcar.app.AppConstant.TYPE_MOTOR_CTRL_BRAKE_OFF;
import static patrolcar.bobi.cn.patrolcar.app.AppConstant.TYPE_MOTOR_CTRL_BRAKE_ON;
import static patrolcar.bobi.cn.patrolcar.app.AppConstant.TYPE_MOTOR_CTRL_DEV_OFF;
import static patrolcar.bobi.cn.patrolcar.app.AppConstant.TYPE_MOTOR_CTRL_DEV_ON;
import static patrolcar.bobi.cn.patrolcar.app.AppConstant.TYPE_MOTOR_CTRL_DOWN;
import static patrolcar.bobi.cn.patrolcar.app.AppConstant.TYPE_MOTOR_CTRL_LEFT;
import static patrolcar.bobi.cn.patrolcar.app.AppConstant.TYPE_MOTOR_CTRL_MOTOR_OFF;
import static patrolcar.bobi.cn.patrolcar.app.AppConstant.TYPE_MOTOR_CTRL_MOTOR_ON;
import static patrolcar.bobi.cn.patrolcar.app.AppConstant.TYPE_MOTOR_CTRL_RIGHT;
import static patrolcar.bobi.cn.patrolcar.app.AppConstant.TYPE_MOTOR_CTRL_STOP;
import static patrolcar.bobi.cn.patrolcar.app.AppConstant.TYPE_MOTOR_CTRL_UP;

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
    @OnClick({R.id.btn_ctrl_stop, R.id.rl_ctrl_up, R.id.rl_ctrl_down, R.id.rl_ctrl_left, R.id.rl_ctrl_right, R.id.tv_dev_on, R.id.tv_dev_off, R.id.tv_brake_on, R.id.tv_brake_off, R.id.tv_motor_on, R.id.tv_motor_off})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_ctrl_stop:
                BleCmdCtrl.sendCmdMotorCtrl(MAC, TYPE_MOTOR_CTRL_STOP);
                break;
            case R.id.rl_ctrl_up:
                BleCmdCtrl.sendCmdMotorCtrl(MAC, TYPE_MOTOR_CTRL_UP);
                break;
            case R.id.rl_ctrl_down:
                BleCmdCtrl.sendCmdMotorCtrl(MAC, TYPE_MOTOR_CTRL_DOWN);
                break;
            case R.id.rl_ctrl_left:
                BleCmdCtrl.sendCmdMotorCtrl(MAC, TYPE_MOTOR_CTRL_LEFT);
                break;
            case R.id.rl_ctrl_right:
                BleCmdCtrl.sendCmdMotorCtrl(MAC, TYPE_MOTOR_CTRL_RIGHT);
                break;
            case R.id.tv_dev_on:
                BleCmdCtrl.sendCmdMotorCtrl(MAC, TYPE_MOTOR_CTRL_DEV_ON);
                break;
            case R.id.tv_dev_off:
                BleCmdCtrl.sendCmdMotorCtrl(MAC, TYPE_MOTOR_CTRL_DEV_OFF);
                break;
            case R.id.tv_brake_on:
                BleCmdCtrl.sendCmdMotorCtrl(MAC, TYPE_MOTOR_CTRL_BRAKE_ON);
                break;
            case R.id.tv_brake_off:
                BleCmdCtrl.sendCmdMotorCtrl(MAC, TYPE_MOTOR_CTRL_BRAKE_OFF);
                break;
            case R.id.tv_motor_on:
                BleCmdCtrl.sendCmdMotorCtrl(MAC, TYPE_MOTOR_CTRL_MOTOR_ON);
                break;
            case R.id.tv_motor_off:
                BleCmdCtrl.sendCmdMotorCtrl(MAC, TYPE_MOTOR_CTRL_MOTOR_OFF);
                break;
        }
    }

}
