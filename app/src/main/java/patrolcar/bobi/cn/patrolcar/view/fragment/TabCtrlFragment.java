package patrolcar.bobi.cn.patrolcar.view.fragment;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.OnClick;
import patrolcar.bobi.cn.patrolcar.R;
import patrolcar.bobi.cn.patrolcar.model.DealWithPkgEvent;
import patrolcar.bobi.cn.patrolcar.util.BleCmdCtrl;
import patrolcar.bobi.cn.patrolcar.util.SelfDialog;
import patrolcar.bobi.cn.patrolcar.util.SelfDialogBase;
import patrolcar.bobi.cn.patrolcar.util.ToastUtil;
import patrolcar.bobi.cn.patrolcar.view.activity.MainActivity;
import patrolcar.bobi.cn.patrolcar.view.base.BaseFragment;

/**
 * 遥控模块
 */

public class TabCtrlFragment extends BaseFragment implements View.OnTouchListener {
    private static final String TAG = "TabCtrlFragment";
    private static final String CONN_NO = "未连接";
    private static final String CONN_DIS = "已断开";
    private static final String CONN_OK = "已连接";
    private static final int MSG_CONN_STATUS     = 1010;
    private static final int DEFAULT_ACC         = 3;
    private static final int HZ_10               = 100;
    private static final int HZ_1                = 1000;
    private static final int MSG_UP              = 1;
    private static final int MSG_DOWN            = 2;
    private static final int MSG_LEFT            = 3;
    private static final int MSG_RIGHT           = 4;
    private static final int MSG_LOOSEN_DRIVE    = 5;

    @BindView(R.id.tv_auto_cruise)    TextView    tvAutoCruise;
    @BindView(R.id.tv_robot_ctrl)     TextView    tvRobotCtrl;
    @BindView(R.id.tv_brake_on)       TextView    tvBrakeOn;
    @BindView(R.id.tv_brake_off)      TextView    tvBrakeOff;
    @BindView(R.id.tv_set_acc)        TextView    tvSetAcc;
    @BindView(R.id.tv_conn_status)    TextView    tvConnStatus;

    private SelfDialog mDialogAcc;
    private SelfDialogBase mDialogDisconnect;
    private String mConnStatus;
    private boolean mIsBrake, mIsAuto;
    private int mPwrSwitch, mMotorSwitch, mBrakeSignal, mDriveMotor, mAcc, mTurnTime, mTurnVelocity;

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
    public void initView(View rootView) {
        mConnStatus = CONN_NO;
        mHandler.sendEmptyMessage(MSG_CONN_STATUS);
        // 默认刹车状态
        mIsBrake = true;
        switchSelect(tvBrakeOn, tvBrakeOff);
        // 默认自动巡航
        mIsAuto = true;
        switchSelect(tvAutoCruise, tvRobotCtrl);
        setAcc(DEFAULT_ACC);
    }

    @Override
    public void initListener() {
        super.initListener();
        getActivity().findViewById(R.id.rl_ctrl_up).setOnTouchListener(this);
        getActivity().findViewById(R.id.rl_ctrl_down).setOnTouchListener(this);
        getActivity().findViewById(R.id.rl_ctrl_left).setOnTouchListener(this);
        getActivity().findViewById(R.id.rl_ctrl_right).setOnTouchListener(this);
    }

    @Override
    public void onEventDealWithPkg(DealWithPkgEvent event) {
        super.onEventDealWithPkg(event);
        Log.i(TAG, "onEventDealWithPkg: " + event);
        if (event != null) {
            mConnStatus = CONN_OK;
        }
    }


    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_CONN_STATUS:
                    switch (mConnStatus) {
                        case CONN_NO:
                            // 未连接
                            connShowStatus(R.string.txt_status_conn_no, R.color.line);
                            break;
                        case CONN_DIS:
                            // 已断开
                            if (!tvConnStatus.getText().toString().equals(getContext().getResources().getString(R.string.txt_status_conn_ing))) {
                                connShowStatus(R.string.txt_status_unconn, R.color.colorUnConn);
                            }
                            break;
                        case CONN_OK:
                            // 已连接
                            connShowStatus(R.string.txt_status_conn, R.color.colorConn);
                            break;
                    }
                    mHandler.sendEmptyMessageDelayed(MSG_CONN_STATUS, HZ_1);
                    break;
                case MSG_UP:
                    if (!mIsAuto) {
                        if (!mIsBrake) {
                            if (mDriveMotor < 0) {
                                clearZeroByMT(true);
                            } else {
                                clearZeroByMT(false);
                            }
                            cmdMotorCtrl();
                            mDriveMotor += mAcc;
                            if (mDriveMotor >= 100) {
                                mDriveMotor = 100;
                            } else if (mDriveMotor <= 15) {
                                mDriveMotor = 15;
                            }
                            mHandler.sendEmptyMessageDelayed(MSG_UP, HZ_10);
                        } else {
                            looseningBrakePrompt();
                        }
                    } else {
                        modeSelectPrompt();
                    }
                    break;
                case MSG_DOWN:
                    if (!mIsAuto) {
                        if (!mIsBrake) {
                            if (mDriveMotor > 0) {
                                clearZeroByMT(true);
                            } else {
                                clearZeroByMT(false);
                            }
                            cmdMotorCtrl();
                            mDriveMotor -= mAcc;
                            if (mDriveMotor <= -100) {
                                mDriveMotor = -100;
                            } else if (mDriveMotor >= -15) {
                                mDriveMotor = -15;
                            }
                            mHandler.sendEmptyMessageDelayed(MSG_DOWN, HZ_10);
                        } else {
                            looseningBrakePrompt();
                        }
                    } else {
                        modeSelectPrompt();
                    }
                    break;
                case MSG_LEFT:
                    if (!mIsAuto) {
                        if (!mIsBrake) {
                            mTurnTime = 20;
                            mTurnVelocity = -20;
                            cmdMotorCtrl();
                            mHandler.sendEmptyMessageDelayed(MSG_LEFT, HZ_10);
                        } else {
                            looseningBrakePrompt();
                        }
                    } else {
                        modeSelectPrompt();
                    }
                    break;
                case MSG_RIGHT:
                    if (!mIsAuto) {
                        if (!mIsBrake) {
                            mTurnTime = 20;
                            mTurnVelocity = 20;
                            cmdMotorCtrl();
                            mHandler.sendEmptyMessageDelayed(MSG_RIGHT, HZ_10);
                        } else {
                            looseningBrakePrompt();
                        }
                    } else {
                        modeSelectPrompt();
                    }
                    break;
                case MSG_LOOSEN_DRIVE:
                    clearZeroByMT(false);
                    cmdMotorCtrl();
                    mHandler.sendEmptyMessageDelayed(MSG_LOOSEN_DRIVE, HZ_1);
                    break;
            }
        }
    };

    @OnClick({R.id.tv_conn_status, R.id.tv_set_acc, R.id.btn_ctrl_stop, R.id.rl_ctrl_up, R.id.rl_ctrl_down, R.id.rl_ctrl_left, R.id.rl_ctrl_right, R.id.tv_dev_on, R.id.tv_dev_off, R.id.tv_brake_on, R.id.tv_brake_off, R.id.tv_motor_on, R.id.tv_motor_off, R.id.tv_robot_ctrl, R.id.tv_auto_cruise})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_conn_status:
                if (tvConnStatus.getText().toString().equals("已连接")) {
                    showDialogByDisconnect();
                } else if (tvConnStatus.getText().toString().equals("已断开")) {
                    MainActivity.getAppCtrl().connectCar(MainActivity.getAppCtrl().getMac());
                    connShowStatus(R.string.txt_status_conn_ing, R.color.line);
                }
                break;
            case R.id.btn_ctrl_stop:
                if (!mIsAuto) {
                    if (!mIsBrake) {
                        mHandler.removeMessages(MSG_UP);
                        mHandler.removeMessages(MSG_DOWN);
                        mHandler.removeMessages(MSG_LOOSEN_DRIVE);
                        clearZeroByMT(true);
                        cmdMotorCtrl();
                    } else {
                        looseningBrakePrompt();
                    }
                } else {
                    modeSelectPrompt();
                }
                break;
            case R.id.tv_dev_on:
                clearZeroByMT(true);
                mPwrSwitch = 0X66;
                cmdMotorCtrl();
                break;
            case R.id.tv_dev_off:
                clearZeroByMT(true);
                mPwrSwitch = 0X77;
                cmdMotorCtrl();
                break;
            case R.id.tv_brake_on:
                if (!mIsAuto) {
                    mIsBrake = true;
                    switchSelect(tvBrakeOn, tvBrakeOff);
                    clearZeroByMT(true);
                    mBrakeSignal = 1;
                    cmdMotorCtrl();
                } else {
                    modeSelectPrompt();
                }
                break;
            case R.id.tv_brake_off:
                if (!mIsAuto) {
                    mIsBrake = false;
                    switchSelect(tvBrakeOff, tvBrakeOn);
                    clearZeroByMT(true);
                    mBrakeSignal = 2;
                    cmdMotorCtrl();
                } else {
                    modeSelectPrompt();
                }
                break;
            case R.id.tv_motor_on:
                clearZeroByMT(true);
                mMotorSwitch = 0X88;
                cmdMotorCtrl();
                break;
            case R.id.tv_motor_off:
                clearZeroByMT(true);
                mMotorSwitch = 0X55;
                cmdMotorCtrl();
                break;
            case R.id.tv_robot_ctrl:
                mIsAuto = false;
                switchSelect(tvRobotCtrl, tvAutoCruise);
                cmdAppToPc(0x01, 0);
                break;
            case R.id.tv_auto_cruise:
                mIsAuto = true;
                switchSelect(tvAutoCruise, tvRobotCtrl);
                cmdAppToPc(0x02, 0);
                break;
            case R.id.tv_set_acc:
                showDialogByAcc();
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
                        mHandler.removeMessages(MSG_UP);
                        mHandler.removeMessages(MSG_DOWN);
                        clearZeroByMT(false);
                        mHandler.sendEmptyMessage(MSG_LOOSEN_DRIVE);
                        break;
                    case R.id.rl_ctrl_down:
                        mHandler.removeMessages(MSG_UP);
                        mHandler.removeMessages(MSG_DOWN);
                        clearZeroByMT(false);
                        mHandler.sendEmptyMessage(MSG_LOOSEN_DRIVE);
                        break;
                    case R.id.rl_ctrl_left:
                        mHandler.removeMessages(MSG_LEFT);
                        clearZeroByMT(false);
                        cmdMotorCtrl();
                        break;
                    case R.id.rl_ctrl_right:
                        mHandler.removeMessages(MSG_RIGHT);
                        clearZeroByMT(false);
                        cmdMotorCtrl();
                        break;
                }
                break;
        }
        return true;
    }

    /** 电机控制命令 */
    private void cmdMotorCtrl() {
        Log.i(TAG, "acc " + mAcc + " DriveMotor " + mDriveMotor);
        BleCmdCtrl.sendCmdMotorCtrl(MainActivity.getAppCtrl().getMac(),
                mPwrSwitch, mMotorSwitch, mBrakeSignal, mDriveMotor, mAcc, mTurnTime, mTurnVelocity);
    }

    /** APP 发送命令给工控机 */
    private void cmdAppToPc(int cmd, int autoCruType) {
        BleCmdCtrl.sendCmdAppToPC(MainActivity.getAppCtrl().getMac(), cmd, autoCruType);
    }

    /**
     * 所有数据清零
     *
     * @param isClearDriveMotor 驱动电机是否清零
     */
    private void clearZeroByMT(boolean isClearDriveMotor) {
        if (isClearDriveMotor) {
            mDriveMotor = 0;
        }
        mPwrSwitch = 0;
        mMotorSwitch = 0;
        mBrakeSignal = 0;
        mTurnTime = 0;
        mTurnVelocity = 0;
    }

    private void switchSelect(TextView tvSelect, TextView tvUnSelect) {
        tvSelect.setBackground(getActivity().getDrawable(R.drawable.shape_bg_select));
        tvSelect.setTextColor(getActivity().getResources().getColor(R.color.colorPrimary));
        tvUnSelect.setBackground(getActivity().getDrawable(R.drawable.shape_bg_unselect));
        tvUnSelect.setTextColor(getActivity().getResources().getColor(R.color.colorWhite));
    }

    private void connShowStatus(int txt, int color) {
        tvConnStatus.setText(getContext().getResources().getString(txt));
        tvConnStatus.setTextColor(getContext().getResources().getColor(color));
    }

    /** 设置 ACC Dialog */
    private void showDialogByAcc() {
        mDialogAcc = new SelfDialog(getContext());
        mDialogAcc.setTitle("设置驱动电机加速度");
        mDialogAcc.setMessage("加速度取值范围1~10");
        mDialogAcc.setYesOnclickListener("确定", () -> {
            if (!(mDialogAcc.getEditTextStr().equals(""))) {
                try {
                    final int niAcc = Integer.parseInt(mDialogAcc.getEditTextStr());
                    if (!(niAcc >= 0 && niAcc <= 10)) {
                        // 如果输入的加速度不在 1~10 之间，提示用户
                        ToastUtil.showShort("设置值超出范围，请重新设置");
                        mDialogAcc.dismiss();
                    } else {
                        // 更新 DMX 地址
                        setAcc(niAcc);
                        mDialogAcc.dismiss();
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    // 还有不按规矩出牌的？有！
                    ToastUtil.showShort("请设置正确的加速度");
                    mDialogAcc.dismiss();
                }
            } else {
                ToastUtil.showShort("加速度未更新");
                mDialogAcc.dismiss();
            }
        });
        mDialogAcc.setNoOnclickListener("取消", () -> mDialogAcc.dismiss());
        mDialogAcc.show();
    }

    /** 断开汽车连接 */
    private void showDialogByDisconnect() {
        mDialogDisconnect = new SelfDialogBase(getContext());
        mDialogDisconnect.setTitle("确定要断开蓝牙连接？");
        mDialogDisconnect.setYesOnclickListener("确定", () -> {
            MainActivity.getAppCtrl().disconnectCar(MainActivity.getAppCtrl().getMac());
            mConnStatus = CONN_DIS;
            mDialogDisconnect.dismiss();
        });
        mDialogDisconnect.setNoOnclickListener("取消", () -> mDialogDisconnect.dismiss());
        mDialogDisconnect.show();
    }

    @SuppressLint("SetTextI18n")
    private void setAcc(int acc) {
        mAcc = acc;
        tvSetAcc.setText(getActivity().getResources().getString(R.string.txt_edit_acc) + acc);
    }

    private void modeSelectPrompt() {
        ToastUtil.showShort("非机器人遥控模式无法操作，请切换机器人模式");
    }

    private void looseningBrakePrompt() {
        ToastUtil.showShort("请松开刹车");
    }

}
