package patrolcar.bobi.cn.patrolcar.view.fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import patrolcar.bobi.cn.patrolcar.R;
import patrolcar.bobi.cn.patrolcar.model.DealWithPkgEvent;
import patrolcar.bobi.cn.patrolcar.model.StatusBean;
import patrolcar.bobi.cn.patrolcar.util.LogUtil;
import patrolcar.bobi.cn.patrolcar.util.Util;
import patrolcar.bobi.cn.patrolcar.view.adapter.TabStatusAdapter;
import patrolcar.bobi.cn.patrolcar.view.base.BaseFragment;

import static patrolcar.bobi.cn.patrolcar.app.AppConstant.CCM_STATE_ACCL_XH;
import static patrolcar.bobi.cn.patrolcar.app.AppConstant.CCM_STATE_ACCL_XL;
import static patrolcar.bobi.cn.patrolcar.app.AppConstant.CCM_STATE_ACCL_YH;
import static patrolcar.bobi.cn.patrolcar.app.AppConstant.CCM_STATE_ACCL_YL;
import static patrolcar.bobi.cn.patrolcar.app.AppConstant.CCM_STATE_ACCL_ZH;
import static patrolcar.bobi.cn.patrolcar.app.AppConstant.CCM_STATE_ACCL_ZL;
import static patrolcar.bobi.cn.patrolcar.app.AppConstant.CCM_STATE_ANGLE_XH;
import static patrolcar.bobi.cn.patrolcar.app.AppConstant.CCM_STATE_ANGLE_XL;
import static patrolcar.bobi.cn.patrolcar.app.AppConstant.CCM_STATE_ANGLE_YH;
import static patrolcar.bobi.cn.patrolcar.app.AppConstant.CCM_STATE_ANGLE_YL;
import static patrolcar.bobi.cn.patrolcar.app.AppConstant.CCM_STATE_ANGLE_ZH;
import static patrolcar.bobi.cn.patrolcar.app.AppConstant.CCM_STATE_ANGLE_ZL;
import static patrolcar.bobi.cn.patrolcar.app.AppConstant.CCM_STATE_BATVOL_40V;
import static patrolcar.bobi.cn.patrolcar.app.AppConstant.CCM_STATE_BURST_STOP;
import static patrolcar.bobi.cn.patrolcar.app.AppConstant.CCM_STATE_DATA_CNT;
import static patrolcar.bobi.cn.patrolcar.app.AppConstant.CCM_STATE_GPS_ALH;
import static patrolcar.bobi.cn.patrolcar.app.AppConstant.CCM_STATE_GPS_ALL;
import static patrolcar.bobi.cn.patrolcar.app.AppConstant.CCM_STATE_GPS_HDOP;
import static patrolcar.bobi.cn.patrolcar.app.AppConstant.CCM_STATE_GPS_LAT0;
import static patrolcar.bobi.cn.patrolcar.app.AppConstant.CCM_STATE_GPS_LAT1;
import static patrolcar.bobi.cn.patrolcar.app.AppConstant.CCM_STATE_GPS_LAT2;
import static patrolcar.bobi.cn.patrolcar.app.AppConstant.CCM_STATE_GPS_LAT3;
import static patrolcar.bobi.cn.patrolcar.app.AppConstant.CCM_STATE_GPS_LON0;
import static patrolcar.bobi.cn.patrolcar.app.AppConstant.CCM_STATE_GPS_LON1;
import static patrolcar.bobi.cn.patrolcar.app.AppConstant.CCM_STATE_GPS_LON2;
import static patrolcar.bobi.cn.patrolcar.app.AppConstant.CCM_STATE_GPS_LON3;
import static patrolcar.bobi.cn.patrolcar.app.AppConstant.CCM_STATE_HUMI;
import static patrolcar.bobi.cn.patrolcar.app.AppConstant.CCM_STATE_MAGN_XH;
import static patrolcar.bobi.cn.patrolcar.app.AppConstant.CCM_STATE_MAGN_XL;
import static patrolcar.bobi.cn.patrolcar.app.AppConstant.CCM_STATE_MAGN_YH;
import static patrolcar.bobi.cn.patrolcar.app.AppConstant.CCM_STATE_MAGN_YL;
import static patrolcar.bobi.cn.patrolcar.app.AppConstant.CCM_STATE_MAGN_ZH;
import static patrolcar.bobi.cn.patrolcar.app.AppConstant.CCM_STATE_MAGN_ZL;
import static patrolcar.bobi.cn.patrolcar.app.AppConstant.CCM_STATE_TEMP;
import static patrolcar.bobi.cn.patrolcar.app.AppConstant.CCM_STATE_TURN_DIR;
import static patrolcar.bobi.cn.patrolcar.app.AppConstant.CCM_STATE_WHEEL_LBH;
import static patrolcar.bobi.cn.patrolcar.app.AppConstant.CCM_STATE_WHEEL_LBL;
import static patrolcar.bobi.cn.patrolcar.app.AppConstant.CCM_STATE_WHEEL_LFH;
import static patrolcar.bobi.cn.patrolcar.app.AppConstant.CCM_STATE_WHEEL_LFL;
import static patrolcar.bobi.cn.patrolcar.app.AppConstant.CCM_STATE_WHEEL_RBH;
import static patrolcar.bobi.cn.patrolcar.app.AppConstant.CCM_STATE_WHEEL_RBL;
import static patrolcar.bobi.cn.patrolcar.app.AppConstant.CCM_STATE_WHEEL_RFH;
import static patrolcar.bobi.cn.patrolcar.app.AppConstant.CCM_STATE_WHEEL_RFL;
import static patrolcar.bobi.cn.patrolcar.app.AppConstant.STATUS_NOTHING;

/**
 * 状态模块
 */

public class TabStatusFragment extends BaseFragment {
    private static final String TAG = "TabStatusFragment";

    @BindView(R.id.rv_robot_status)    RecyclerView rvRobotStatus;

    private String mAccX, mAccY, mAccZ;
    private String mDirectionX, mDirectionY, mDirectionZ;
    private String mMagneticX, mMagneticY, mMagneticZ;
    private String mTemperature, mHumidity;
    private String mGPSLongitude, mGPSLatitude, mGPSHigh, mGPSHdop;
    private String mTurnDirection;
    private String mMotorSpeed1, mMotorSpeed2, mMotorSpeed3, mMotorSpeed4;
    private String mBatteryVoltageValue, mStopDetection;

    private List<StatusBean> mList = new ArrayList<>();

    public static TabStatusFragment newInstance() {
        return new TabStatusFragment();
    }

    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_tab_status;
    }

    @Override
    public void initView(View rootView) {
        initStatus();
        refreshData();
    }

    @Override
    public void onEventDealWithPkg(DealWithPkgEvent event) {
        super.onEventDealWithPkg(event);
        Log.i(TAG, "onEventDealWithPkg");
        byte[] pkg = event.getPkgInfo();
        Log.i(TAG, "event pkg len" + pkg.length);
        if (pkg[0] == (byte) 0xa5
                && pkg[1] == (CCM_STATE_DATA_CNT + 5)
                && pkg[2] == (byte) 0x06) {
            byte[] recvData = Util.bytesGetSub(pkg, 3, CCM_STATE_DATA_CNT);
            LogUtil.w(TAG, "" + recvData.length);

            mDirectionX = String.valueOf(Util.byteTowToS16(recvData[CCM_STATE_ANGLE_XH], recvData[CCM_STATE_ANGLE_XL]));
            mDirectionY = String.valueOf(Util.byteTowToS16(recvData[CCM_STATE_ANGLE_YH], recvData[CCM_STATE_ANGLE_YL]));
            mDirectionZ = String.valueOf(Util.byteTowToS16(recvData[CCM_STATE_ANGLE_ZH], recvData[CCM_STATE_ANGLE_ZL]));
            LogUtil.i(TAG, "mDirectionX " + mDirectionX + " mDirectionY " + mDirectionY + " mDirectionZ " + mDirectionZ);

            mAccX = String.valueOf(Util.byteTowToS16(recvData[CCM_STATE_ACCL_XH], recvData[CCM_STATE_ACCL_XL]));
            mAccY = String.valueOf(Util.byteTowToS16(recvData[CCM_STATE_ACCL_YH], recvData[CCM_STATE_ACCL_YL]));
            mAccZ = String.valueOf(Util.byteTowToS16(recvData[CCM_STATE_ACCL_ZH], recvData[CCM_STATE_ACCL_ZL]));
            LogUtil.i(TAG, "mAccX " + mAccX + " mAccY " + mAccY + " mAccZ " + mAccZ);

            mMagneticX = String.valueOf(Util.byteTowToS16(recvData[CCM_STATE_MAGN_XH], recvData[CCM_STATE_MAGN_XL]));
            mMagneticY = String.valueOf(Util.byteTowToS16(recvData[CCM_STATE_MAGN_YH], recvData[CCM_STATE_MAGN_YL]));
            mMagneticZ = String.valueOf(Util.byteTowToS16(recvData[CCM_STATE_MAGN_ZH], recvData[CCM_STATE_MAGN_ZL]));
            LogUtil.i(TAG, "mMagneticX " + mMagneticX + " mMagneticY " + mMagneticY + " mMagneticZ " + mMagneticZ);

            mGPSLongitude = String.valueOf(Util.bytesToFloat(recvData[CCM_STATE_GPS_LON3], recvData[CCM_STATE_GPS_LON2], recvData[CCM_STATE_GPS_LON1], recvData[CCM_STATE_GPS_LON0]));
            mGPSLatitude = String.valueOf(Util.bytesToFloat(recvData[CCM_STATE_GPS_LAT3], recvData[CCM_STATE_GPS_LAT2], recvData[CCM_STATE_GPS_LAT1], recvData[CCM_STATE_GPS_LAT0]));
            mGPSHigh = String.valueOf(Util.byteTowToU16(recvData[CCM_STATE_GPS_ALH], recvData[CCM_STATE_GPS_ALL]));
            mGPSHdop = String.valueOf((recvData[CCM_STATE_GPS_HDOP]) & 0XFFFF);
            LogUtil.i(TAG, "mGPSLongitude " + mGPSLongitude + " mGPSLatitude " + mGPSLatitude + " mGPSHigh " + mGPSHigh + " mGPSHdop " + mGPSHdop);

            mTemperature = String.valueOf((recvData[CCM_STATE_TEMP]) & 0XFFFF);
            mHumidity = String.valueOf((recvData[CCM_STATE_HUMI]) & 0XFFFF);
            mTurnDirection = String.valueOf(recvData[CCM_STATE_TURN_DIR]);
            LogUtil.i(TAG, "mTemperature " + mTemperature + " mHumidity " + mHumidity + " mTurnDirection " + mTurnDirection);

            mMotorSpeed1 = String.valueOf((recvData[CCM_STATE_WHEEL_LFH] * 0X0100 | recvData[CCM_STATE_WHEEL_LFL]) & 0XFFFF);
            mMotorSpeed2 = String.valueOf((recvData[CCM_STATE_WHEEL_RFH] * 0X0100 | recvData[CCM_STATE_WHEEL_RFL]) & 0XFFFF);
            mMotorSpeed3 = String.valueOf((recvData[CCM_STATE_WHEEL_LBH] * 0X0100 | recvData[CCM_STATE_WHEEL_LBL]) & 0XFFFF);
            mMotorSpeed4 = String.valueOf((recvData[CCM_STATE_WHEEL_RBH] * 0X0100 | recvData[CCM_STATE_WHEEL_RBL]) & 0XFFFF);
            LogUtil.i(TAG, "mMotorSpeed1 " + mMotorSpeed1 + " mMotorSpeed2 " + mMotorSpeed2 + " mMotorSpeed3 " + mMotorSpeed3 + " mMotorSpeed4 " + mMotorSpeed4);

            mBatteryVoltageValue = String.valueOf((recvData[CCM_STATE_BATVOL_40V] * 0.1f) + 40.0f);
            mStopDetection = String.valueOf((recvData[CCM_STATE_BURST_STOP]) & 0XFF);
            LogUtil.i(TAG, "mBatteryVoltageValue " + mBatteryVoltageValue + " mStopDetection " + mStopDetection);

            refreshData();
        }
    }

    private void refreshData() {
        mList.clear();
        updateStatus();
        initRecyclerView();
    }

    private void initRecyclerView() {
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        rvRobotStatus.setLayoutManager(manager);
        TabStatusAdapter adapter = new TabStatusAdapter(mList);
        rvRobotStatus.setAdapter(adapter);
    }

    public void updateStatus() {
        mList.add(new StatusBean("加速度X", mAccX, "m/s2"));
        mList.add(new StatusBean("加速度Y", mAccY, "m/s2"));
        mList.add(new StatusBean("加速度Z", mAccZ, "m/s2"));
        mList.add(new StatusBean("方位X", mDirectionX, "°"));
        mList.add(new StatusBean("方位Y", mDirectionY, "°"));
        mList.add(new StatusBean("方位Z", mDirectionZ, "°"));
        mList.add(new StatusBean("磁场X", mMagneticX, "°"));
        mList.add(new StatusBean("磁场Y", mMagneticY, "°"));
        mList.add(new StatusBean("磁场Z", mMagneticZ, "°"));
        mList.add(new StatusBean("电路板温度", mTemperature, "℃"));
        mList.add(new StatusBean("电路板湿度", mHumidity, "％"));
        mList.add(new StatusBean("GPS经度", mGPSLongitude, "°"));
        mList.add(new StatusBean("GPS纬度", mGPSLatitude, "°"));
        mList.add(new StatusBean("GPS高度", mGPSHigh, "m"));
        mList.add(new StatusBean("GPS精度", mGPSHdop, "cep"));
        mList.add(new StatusBean("转向方向", mTurnDirection, "°"));
        mList.add(new StatusBean("电机速度1", mMotorSpeed1, "个"));
        mList.add(new StatusBean("电机速度2", mMotorSpeed2, "个"));
        mList.add(new StatusBean("电机速度3", mMotorSpeed3, "个"));
        mList.add(new StatusBean("电机速度4", mMotorSpeed4, "个"));
        mList.add(new StatusBean("电池电压值", mBatteryVoltageValue, "V"));
        mList.add(new StatusBean("急停检测", mStopDetection, ""));
    }

    private void initStatus() {
        mAccX = STATUS_NOTHING;
        mAccY = STATUS_NOTHING;
        mAccZ = STATUS_NOTHING;
        mDirectionX = STATUS_NOTHING;
        mDirectionY = STATUS_NOTHING;
        mDirectionZ = STATUS_NOTHING;
        mMagneticX = STATUS_NOTHING;
        mMagneticY = STATUS_NOTHING;
        mMagneticZ = STATUS_NOTHING;
        mTemperature = STATUS_NOTHING;
        mHumidity = STATUS_NOTHING;
        mGPSLongitude = STATUS_NOTHING;
        mGPSLatitude = STATUS_NOTHING;
        mGPSHigh = STATUS_NOTHING;
        mGPSHdop = STATUS_NOTHING;
        mTurnDirection = STATUS_NOTHING;
        mMotorSpeed1 = STATUS_NOTHING;
        mMotorSpeed2 = STATUS_NOTHING;
        mMotorSpeed3 = STATUS_NOTHING;
        mMotorSpeed4 = STATUS_NOTHING;
        mBatteryVoltageValue = STATUS_NOTHING;
        mStopDetection = STATUS_NOTHING;
    }

}
