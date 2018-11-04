package patrolcar.bobi.cn.patrolcar.view.fragment;

import android.view.View;

import butterknife.OnClick;
import patrolcar.bobi.cn.patrolcar.R;
import patrolcar.bobi.cn.patrolcar.util.ToastUtil;
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

    @OnClick({R.id.btn_ctrl_stop, R.id.rl_ctrl_up, R.id.rl_ctrl_down, R.id.rl_ctrl_left, R.id.rl_ctrl_right, R.id.open_device, R.id.close_device, R.id.brake_start, R.id.brake_release, R.id.open_motor, R.id.close_motor})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_ctrl_stop:
                ToastUtil.showShort("stop");
                break;
            case R.id.rl_ctrl_up:
                ToastUtil.showShort("up");
                break;
            case R.id.rl_ctrl_down:
                ToastUtil.showShort("down");
                break;
            case R.id.rl_ctrl_left:
                ToastUtil.showShort("left");
                break;
            case R.id.rl_ctrl_right:
                ToastUtil.showShort("right");
                break;
            case R.id.open_device:
                ToastUtil.showShort("open_device");
                break;
            case R.id.close_device:
                ToastUtil.showShort("close_device");
                break;
            case R.id.brake_start:
                ToastUtil.showShort("brake_start");
                break;
            case R.id.brake_release:
                ToastUtil.showShort("brake_release");
                break;
            case R.id.open_motor:
                ToastUtil.showShort("open_motor");
                break;
            case R.id.close_motor:
                ToastUtil.showShort("close_motor");
                break;
        }
    }

}
