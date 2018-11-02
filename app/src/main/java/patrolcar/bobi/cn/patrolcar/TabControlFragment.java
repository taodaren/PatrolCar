package patrolcar.bobi.cn.patrolcar;

import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 遥控模块
 */

public class TabControlFragment extends BaseFragment {

    @BindView(R.id.btn_ctrl_stop)        Button btnStop;
    @BindView(R.id.rl_ctrl_up)           RelativeLayout btnUp;
    @BindView(R.id.rl_ctrl_down)         RelativeLayout btnDown;
    @BindView(R.id.rl_ctrl_left)         RelativeLayout btnLeft;
    @BindView(R.id.rl_ctrl_right)        RelativeLayout btnRight;
    @BindView(R.id.open_device)          TextView btnDevOpen;
    @BindView(R.id.close_device)         TextView btnDevClose;
    @BindView(R.id.brake_start)          TextView btnBrakeStart;
    @BindView(R.id.brake_release)        TextView btnBrakeRelease;

    public static TabControlFragment newInstance() {
        return new TabControlFragment();
    }

    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_tab_control;
    }

    @Override
    public void initToolbar() {
        setToolbar(R.id.main_toolbar, R.string.text_tab_control, View.VISIBLE);
    }

    @Override
    public void initView(View rootView) {

    }

    @Override
    public void initData() {

    }


    @OnClick({R.id.btn_ctrl_stop, R.id.rl_ctrl_up, R.id.rl_ctrl_down, R.id.rl_ctrl_left, R.id.rl_ctrl_right, R.id.open_device, R.id.close_device, R.id.brake_start, R.id.brake_release, R.id.open_motor, R.id.close_motor})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_ctrl_stop:
                ToastTools.showShort(getContext(), "stop");
                break;
            case R.id.rl_ctrl_up:
                ToastTools.showShort(getContext(), "up");
                break;
            case R.id.rl_ctrl_down:
                ToastTools.showShort(getContext(), "down");
                break;
            case R.id.rl_ctrl_left:
                ToastTools.showShort(getContext(), "left");
                break;
            case R.id.rl_ctrl_right:
                ToastTools.showShort(getContext(), "right");
                break;
            case R.id.open_device:
                ToastTools.showShort(getContext(), "open_device");
                break;
            case R.id.close_device:
                ToastTools.showShort(getContext(), "close_device");
                break;
            case R.id.brake_start:
                ToastTools.showShort(getContext(), "start");
                break;
            case R.id.brake_release:
                ToastTools.showShort(getContext(), "release");
                break;
            case R.id.open_motor:
                ToastTools.showShort(getContext(), "open_motor");
                break;
            case R.id.close_motor:
                ToastTools.showShort(getContext(), "close_motor");
                break;
        }
    }

}
