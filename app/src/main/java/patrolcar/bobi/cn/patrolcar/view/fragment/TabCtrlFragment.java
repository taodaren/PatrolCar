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

/**
 * 遥控模块
 */

public class TabCtrlFragment extends BaseFragment implements View.OnTouchListener {
    private static final String TAG = "TabCtrlFragment";
    private static final String MAC = "F7:4D:B8:21:A5:35";
    private static final int HZ        = 100;
    private static final int MSG_UP    = 1;
    private static final int MSG_DOWN  = 2;
    private static final int MSG_LEFT  = 3;
    private static final int MSG_RIGHT = 4;

    private int mPwrSwitch, mMotorSwitch, mBrakeSignal, mDriveMotor, mAcc, mTurnMotorTime, mTurnMotorSpeed;

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

    @Override
    public void initListener() {
        super.initListener();
        getActivity().findViewById(R.id.rl_ctrl_up).setOnTouchListener(this);
        getActivity().findViewById(R.id.rl_ctrl_down).setOnTouchListener(this);
        getActivity().findViewById(R.id.rl_ctrl_left).setOnTouchListener(this);
        getActivity().findViewById(R.id.rl_ctrl_right).setOnTouchListener(this);
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_UP:
                    mAcc = 5;
                    cmdMotorCtrl();
                    mDriveMotor += mAcc;
                    if (mDriveMotor >= 100) {
                        mDriveMotor = 100;
                    }
                    mHandler.sendEmptyMessageDelayed(MSG_UP, HZ);
                    break;
                case MSG_DOWN:
                    mAcc = 5;
                    cmdMotorCtrl();
                    mDriveMotor -= mAcc;
                    if (mDriveMotor <= -100) {
                        mDriveMotor = -100;
                    }
                    mHandler.sendEmptyMessageDelayed(MSG_DOWN, HZ);
                    break;
                case MSG_LEFT:
                    mTurnMotorTime = 20;
                    mTurnMotorSpeed = -20;
                    cmdMotorCtrl();
                    mHandler.sendEmptyMessageDelayed(MSG_LEFT, HZ);
                    break;
                case MSG_RIGHT:
                    mTurnMotorTime = 20;
                    mTurnMotorSpeed = 20;
                    cmdMotorCtrl();
                    mHandler.sendEmptyMessageDelayed(MSG_RIGHT, HZ);
                    break;
            }
        }
    };

    @OnClick({R.id.btn_ctrl_stop, R.id.rl_ctrl_up, R.id.rl_ctrl_down, R.id.rl_ctrl_left, R.id.rl_ctrl_right, R.id.tv_dev_on, R.id.tv_dev_off, R.id.tv_brake_on, R.id.tv_brake_off, R.id.tv_motor_on, R.id.tv_motor_off})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_ctrl_stop:
                clearZero();
                cmdMotorCtrl();
                break;
            case R.id.tv_dev_on:
                clearZero();
                mPwrSwitch = 0X66;
                cmdMotorCtrl();
                break;
            case R.id.tv_dev_off:
                clearZero();
                mPwrSwitch = 0X77;
                cmdMotorCtrl();
                break;
            case R.id.tv_brake_on:
                clearZero();
                mBrakeSignal = 1;
                cmdMotorCtrl();
                break;
            case R.id.tv_brake_off:
                clearZero();
                mBrakeSignal = 2;
                cmdMotorCtrl();
                break;
            case R.id.tv_motor_on:
                clearZero();
                mMotorSwitch = 0X88;
                cmdMotorCtrl();
                break;
            case R.id.tv_motor_off:
                clearZero();
                mMotorSwitch = 0X55;
                cmdMotorCtrl();
                break;
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 按住事件发生后执行代码的区域
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
            case MotionEvent.ACTION_UP:
                // 松开事件发生后执行代码的区域
                switch (v.getId()) {
                    case R.id.rl_ctrl_up:
                        mDriveMotor = 0;
                        mHandler.removeMessages(MSG_UP);
                        break;
                    case R.id.rl_ctrl_down:
                        mDriveMotor = 0;
                        mHandler.removeMessages(MSG_DOWN);
                        break;
                    case R.id.rl_ctrl_left:
                        mHandler.removeMessages(MSG_LEFT);
                        cmdMotorCtrl();
                        break;
                    case R.id.rl_ctrl_right:
                        mHandler.removeMessages(MSG_RIGHT);
                        cmdMotorCtrl();
                        break;
                }
                break;
        }
        return true;
    }

    /** 电机控制命令 */
    private void cmdMotorCtrl() {
        BleCmdCtrl.sendCmdMotorCtrl(MAC, mPwrSwitch, mMotorSwitch, mBrakeSignal, mDriveMotor, mAcc, mTurnMotorTime, mTurnMotorSpeed);
    }

    /** 所有数据清零 */
    private void clearZero() {
        mPwrSwitch = 0;
        mMotorSwitch = 0;
        mBrakeSignal = 0;
        mDriveMotor = 0;
        mAcc = 0;
        mTurnMotorTime = 0;
        mTurnMotorSpeed = 0;
    }

}
