package patrolcar.bobi.cn.patrolcar.view.fragment;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothGatt;
import android.content.Intent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import butterknife.BindView;
import patrolcar.bobi.cn.blelib.BleManager;
import patrolcar.bobi.cn.blelib.callback.BleGattCallback;
import patrolcar.bobi.cn.blelib.data.BleDevice;
import patrolcar.bobi.cn.blelib.exception.BleException;
import patrolcar.bobi.cn.patrolcar.R;
import patrolcar.bobi.cn.patrolcar.comm.ObserverManager;
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
//        operatingAnim = AnimationUtils.loadAnimation(getContext(), R.anim.rotate);
//        operatingAnim.setInterpolator(new LinearInterpolator());
//        progressDialog = new ProgressDialog(getContext());

//        mDeviceAdapter = new DeviceAdapter(getContext());
//        mDeviceAdapter.setOnDeviceClickListener(new DeviceAdapter.OnDeviceClickListener() {
//            @Override
//            public void onConnect(BleDevice bleDevice) {
//                if (!BleManager.getInstance().isConnected(bleDevice)) {
//                    BleManager.getInstance().stopScan();
////                    connect(bleDevice);
//                }
//            }
//
//            @Override
//            public void onDisConnect(BleDevice bleDevice) {
////                if (BleManager.getInstance().isConnected(bleDevice)) {
////                    BleManager.getInstance().disconnect(bleDevice);
////                }
//            }
//
//            @Override
//            public void onDetail(BleDevice bleDevice) {
////                if (BleManager.getInstance().isConnected(bleDevice)) {
////                    Intent intent = new Intent(getContext(), RemoteControlActivity.class);
////                    intent.putExtra(RemoteControlActivity.KEY_DATA, bleDevice);
////                    startActivity(intent);
////                }
//            }
//        });
//        listDevice.setAdapter(mDeviceAdapter);
    }


}
