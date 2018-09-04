package patrolcar.bobi.cn.patrolcar.view.fragment;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothGatt;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ListView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import patrolcar.bobi.cn.blelib.BleManager;
import patrolcar.bobi.cn.blelib.callback.BleGattCallback;
import patrolcar.bobi.cn.blelib.data.BleDevice;
import patrolcar.bobi.cn.blelib.exception.BleException;
import patrolcar.bobi.cn.patrolcar.R;
import patrolcar.bobi.cn.patrolcar.app.BleControl;
import patrolcar.bobi.cn.patrolcar.comm.ObserverManager;
import patrolcar.bobi.cn.patrolcar.model.BleDeviceEvent;
import patrolcar.bobi.cn.patrolcar.view.activity.RemoteControlActivity;
import patrolcar.bobi.cn.patrolcar.view.adapter.DeviceAdapter;
import patrolcar.bobi.cn.patrolcar.view.base.BaseFragment;

/**
 * 设备模块
 */

public class TabDeviceFragment extends BaseFragment {
    private static final String TAG = TabDeviceFragment.class.getSimpleName();

    @BindView(R.id.list_device)           ListView listDevice;

    private Animation operatingAnim;
    private DeviceAdapter mDeviceAdapter;
    private ProgressDialog progressDialog;
    private List<BleDevice> mConnDevList;

    public static TabDeviceFragment newInstance() {
        return new TabDeviceFragment();
    }

    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_tab_device;
    }

    @Override
    public void initToolbar() {
        setToolbar(R.id.main_toolbar, R.string.text_tab_device, View.VISIBLE);
    }

    @Override
    public void initView(View rootView) {
        EventBus.getDefault().register(this);

        Log.i(TAG, "initView: ");
        mConnDevList = BleManager.getInstance().getAllConnectedDevice();

        for (int i=0;i<mConnDevList.size();i++) {
            Log.i(TAG, "frag dev: " + mConnDevList.get(i) + " " + mConnDevList.get(i).getMac());
        }

//        operatingAnim = AnimationUtils.loadAnimation(getContext(), R.anim.rotate);
//        operatingAnim.setInterpolator(new LinearInterpolator());
//        progressDialog = new ProgressDialog(getContext());
//
        mDeviceAdapter = new DeviceAdapter(getContext());
//        mDeviceAdapter.setOnDeviceClickListener(new DeviceAdapter.OnDeviceClickListener() {
//            @Override
//            public void onConnect(BleDevice bleDevice) {
//            }
//
//            @Override
//            public void onDisConnect(BleDevice bleDevice) {
//            }
//
//            @Override
//            public void onDetail(BleDevice bleDevice) {
//            }
//        });
        listDevice.setAdapter(mDeviceAdapter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventDelDevice(BleDeviceEvent event) {
//        mDeviceAdapter.notifyDataSetChanged();

        Log.i(TAG, "onEventDelDevice");
        mConnDevList = BleManager.getInstance().getAllConnectedDevice();

        for (int i=0;i<mConnDevList.size();i++) {
            Log.i(TAG, "frag dev: " + mConnDevList.get(i).getMac() + " " + mConnDevList.get(i).getTimestampNanos());
        }
    }
}
