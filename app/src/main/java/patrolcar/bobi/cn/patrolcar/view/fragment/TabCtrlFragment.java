package patrolcar.bobi.cn.patrolcar.view.fragment;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import butterknife.BindView;
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

public class TabCtrlFragment extends BaseFragment implements View.OnTouchListener {
    private static final String TAG = "TabCtrlFragment";
    private static final int MSG_UP = 1;
    private static final int MSG_DOWN = 2;
    private static final int MSG_LEFT = 3;
    private static final int MSG_RIGHT = 4;

    @BindView(R.id.rl_ctrl_up)       RelativeLayout mUp;
    @BindView(R.id.rl_ctrl_down)     RelativeLayout mDown;
    @BindView(R.id.rl_ctrl_left)     RelativeLayout mLeft;
    @BindView(R.id.rl_ctrl_right)    RelativeLayout mRight;

    private static final String MAC = "F7:4D:B8:21:A5:35";

    public static TabCtrlFragment newInstance() {
        return new TabCtrlFragment();
    }

    private int mPara1, mPara2;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_UP:
                    mPara2 = 5;
                    BleCmdCtrl.sendCmdMotorCtrl(MAC, TYPE_MOTOR_CTRL_UP, mPara1, mPara2);
                    mPara1 += mPara2;
                    if (mPara1 >= 100) {
                        mPara1 = 100;
                    }
                    mHandler.sendEmptyMessageDelayed(MSG_UP, 100);
                    break;
                case MSG_DOWN:
                    mPara2 = 5;
                    BleCmdCtrl.sendCmdMotorCtrl(MAC, TYPE_MOTOR_CTRL_DOWN, mPara1, mPara2);
                    mPara1 -= mPara2;
                    if (mPara1 <= -100) {
                        mPara1 = -100;
                    }
                    mHandler.sendEmptyMessageDelayed(MSG_DOWN, 100);
                    break;
                case MSG_LEFT:
                    mPara1 = 20;
                    mPara2 = -20;
                    BleCmdCtrl.sendCmdMotorCtrl(MAC, TYPE_MOTOR_CTRL_LEFT, mPara1, mPara2);
                    mHandler.sendEmptyMessageDelayed(MSG_LEFT, 100);
                    break;
                case MSG_RIGHT:
                    mPara1 = 20;
                    mPara2 = 20;
                    BleCmdCtrl.sendCmdMotorCtrl(MAC, TYPE_MOTOR_CTRL_RIGHT, mPara1, mPara2);
                    mHandler.sendEmptyMessageDelayed(MSG_RIGHT, 100);
                    break;
            }
        }
    };

    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_tab_ctrl;
    }

    @Override
    public void initToolbar() {
        setToolbar(R.id.main_toolbar, R.string.txt_tab_control, View.VISIBLE);
    }

    @Override
    public void initListener() {
        super.initListener();
        getActivity().findViewById(R.id.rl_ctrl_up).setOnTouchListener(this);
        getActivity().findViewById(R.id.rl_ctrl_down).setOnTouchListener(this);
        getActivity().findViewById(R.id.rl_ctrl_left).setOnTouchListener(this);
        getActivity().findViewById(R.id.rl_ctrl_right).setOnTouchListener(this);
    }

    @OnClick({R.id.btn_ctrl_stop, R.id.rl_ctrl_up, R.id.rl_ctrl_down, R.id.rl_ctrl_left, R.id.rl_ctrl_right, R.id.tv_dev_on, R.id.tv_dev_off, R.id.tv_brake_on, R.id.tv_brake_off, R.id.tv_motor_on, R.id.tv_motor_off})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_ctrl_stop:
                BleCmdCtrl.sendCmdMotorCtrl(MAC, TYPE_MOTOR_CTRL_STOP);
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

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:// 按住事件发生后执行代码的区域
                switch (v.getId()) {
                    case R.id.rl_ctrl_up:
                        mHandler.sendEmptyMessage(MSG_UP);
                        break;
                    case R.id.rl_ctrl_down:
                        mHandler.sendEmptyMessage(MSG_DOWN);
                        break;
                    case R.id.rl_ctrl_left:
                        mHandler.sendEmptyMessage(MSG_LEFT);
                        break;
                    case R.id.rl_ctrl_right:
                        mHandler.sendEmptyMessage(MSG_RIGHT);
                        break;
                }
                break;
            case MotionEvent.ACTION_UP:// 松开事件发生后执行代码的区域
                switch (v.getId()) {
                    case R.id.rl_ctrl_up:
                        mHandler.removeMessages(MSG_UP);
                        break;
                    case R.id.rl_ctrl_down:
                        mHandler.removeMessages(MSG_DOWN);
                        break;
                    case R.id.rl_ctrl_left:
                        mHandler.removeMessages(MSG_LEFT);
                        BleCmdCtrl.sendCmdMotorCtrl(MAC, TYPE_MOTOR_CTRL_LEFT, 0, 0);
                        break;
                    case R.id.rl_ctrl_right:
                        mHandler.removeMessages(MSG_RIGHT);
                        BleCmdCtrl.sendCmdMotorCtrl(MAC, TYPE_MOTOR_CTRL_RIGHT, 0, 0);
                        break;
                }
                break;
        }
        return true;
    }

}
