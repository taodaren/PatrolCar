package patrolcar.bobi.cn.patrolcar;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import patrolcar.bobi.cn.blelib.BleManager;
import patrolcar.bobi.cn.blelib.callback.BleGattCallback;
import patrolcar.bobi.cn.blelib.callback.BleScanCallback;
import patrolcar.bobi.cn.blelib.data.BleDevice;
import patrolcar.bobi.cn.blelib.exception.BleException;
import patrolcar.bobi.cn.blelib.scan.BleScanRuleConfig;
import patrolcar.bobi.cn.patrolcar.app.BleControl;
import patrolcar.bobi.cn.patrolcar.model.BleDeviceEvent;
import patrolcar.bobi.cn.patrolcar.view.base.BaseActivity;
import patrolcar.bobi.cn.patrolcar.view.fragment.TabControlFragment;
import patrolcar.bobi.cn.patrolcar.view.fragment.TabDeviceFragment;
import patrolcar.bobi.cn.patrolcar.view.fragment.TabMineFragment;

import static patrolcar.bobi.cn.patrolcar.app.AppConstant.BLE_DEVICE_NAME;

public class MainActivity extends BaseActivity implements BottomNavigationBar.OnTabSelectedListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int REQUEST_CODE_OPEN_GPS = 1;
    private static final int REQUEST_CODE_PERMISSION_LOCATION = 2;

    private List<Fragment> mFragments;
    private Fragment mCurrentFragment;

    @Override
    protected int getActivityLayout() {
        return R.layout.activity_main;
    }

    @Override
    public void initView() {
        initConfigBLE();
        checkPermissions();

        initBtnNavBar();
        mFragments = getFragments();
        setDefFragment();
    }

    /** BLE 初始化及全局配置 */
    public void initConfigBLE() {
        // 初始化
        BleManager.getInstance().init(getApplication());
        // 全局配置
        BleManager.getInstance()
                // 配置日志 → 默认打开库中的运行日志
                .enableLog(true)
                // 配置重连 → 设置连接时重连次数和重连间隔（毫秒），默认为 0 次不重连
                .setReConnectCount(1, 5000)
                // 配置分包发送 → 设置分包发送的时候，每一包的数据长度，默认 20 个字节
                .setSplitWriteNum(20)
                // 配置连接超时 → 设置连接超时时间（毫秒），默认 10 秒
                .setConnectOverTime(20000)
                // 配置操作超时 → 设置 readRssi、setMtu、write、read、notify、indicate 的超时时间（毫秒），默认 5 秒
                .setOperateTimeout(5000);
    }

    /** 检查权限 */
    private void checkPermissions() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!bluetoothAdapter.isEnabled()) {
            Toast.makeText(this, getString(R.string.please_open_blue), Toast.LENGTH_LONG).show();
            return;
        }

        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION};
        List<String> permissionDeniedList = new ArrayList<>();
        for (String permission : permissions) {
            int permissionCheck = ContextCompat.checkSelfPermission(this, permission);
            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                onPermissionGranted(permission);
            } else {
                permissionDeniedList.add(permission);
            }
        }
        if (!permissionDeniedList.isEmpty()) {
            String[] deniedPermissions = permissionDeniedList.toArray(new String[permissionDeniedList.size()]);
            ActivityCompat.requestPermissions(this, deniedPermissions, REQUEST_CODE_PERMISSION_LOCATION);
        }
    }

    /** 授予许可 */
    private void onPermissionGranted(String permission) {
        switch (permission) {
            case Manifest.permission.ACCESS_FINE_LOCATION:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !checkGPSIsOpen()) {
                    new AlertDialog.Builder(this)
                            .setTitle(R.string.notifyTitle)
                            .setMessage(R.string.gpsNotifyMsg)
                            .setNegativeButton(R.string.cancel,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                        }
                                    })
                            .setPositiveButton(R.string.setting,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                            startActivityForResult(intent, REQUEST_CODE_OPEN_GPS);
                                        }
                                    })

                            .setCancelable(false)
                            .show();
                } else {
                    setScanRule();
                    startScan();
                }
                break;
        }
    }

    /** 检查 GPS 是否打开 */
    private boolean checkGPSIsOpen() {
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager == null)
            return false;
        return locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER);
    }

    /** 设置扫描规则 */
    private void setScanRule() {
        BleScanRuleConfig scanRuleConfig = new BleScanRuleConfig.Builder()
                .setServiceUuids(null)                             // 只扫描指定的服务的设备，可选
                .setDeviceName(true, BLE_DEVICE_NAME)        // 只扫描指定广播名的设备，可选
                .setDeviceMac(null)                                // 只扫描指定mac的设备，可选
                .setAutoConnect(false)                             // 连接时的autoConnect参数，可选，默认false
                .setScanTimeOut(10000)                             // 扫描超时时间，可选，默认10秒
                .build();
        BleManager.getInstance().initScanRule(scanRuleConfig);
    }

    /** 开始扫描 */
    private void startScan() {
        // 扫描及过滤过程是在工作线程中进行，所以不会影响主线程的UI操作，最终每一个回调结果都会回到主线程
        BleManager.getInstance().scan(new BleScanCallback() {

            /** @param scanResultList 本次扫描时段内所有被扫描且过滤后的设备集合 */
            @Override
            public void onScanFinished(List<BleDevice> scanResultList) {
                // 扫描结束，它会回到主线程，相当于 onScanning 设备之和
                Log.i(TAG, "扫描完成..." + "设备数：" + +scanResultList.size());

                for (int i = 0; i < scanResultList.size(); i++) {
                    Log.i(TAG, "扫描完成设备: " + scanResultList.get(i).getMac());
                }
            }

            /** @param success 本次扫描动作是否开启成功 */
            @Override
            public void onScanStarted(boolean success) {
                // 会回到主线程，由于蓝牙没有打开，上一次扫描没有结束等原因，会造成扫描开启失败
                Log.i(TAG, "扫描是否开启成功: " + success);
            }

            /** @param bleDevice 经过扫描过滤规则过滤后的设备，同一个设备只会出现一次 */
            @Override
            public void onScanning(BleDevice bleDevice) {
                // 扫描到一个符合扫描规则的 BLE 设备（主线程）
                String bleDevName = bleDevice.getName();
                String mac = bleDevice.getMac();
                byte[] scanRecord = bleDevice.getScanRecord();
                int rssi = bleDevice.getRssi();
                Log.i(TAG, "扫描中..."
                        + " 广播名: " + bleDevName
                        + " Mac地址: " + mac
                        + " 广播数据: " + scanRecord.length
                        + " 信号强度: " + rssi
                );

                connByBleDevice(bleDevice);
            }

            /** @param bleDevice 同一个设备会在不同的时间，携带自身不同的状态（比如信号强度等），出现在这个回调方法中，出现次数取决于周围的设备量及外围设备的广播间隔 */
            @Override
            public void onLeScan(BleDevice bleDevice) {
                // 扫描过程中所有被扫描到的结果回调；由于扫描及过滤的过程是在工作线程中的，此方法也处于工作线程中
                Log.d(TAG, "扫描结果回调：" + bleDevice.getName() + "丨" + bleDevice.getMac());
            }
        });
    }

    /** BLE 通过设备对象连接 */
    public void connByBleDevice(final BleDevice bleDevice) {
        BleManager.getInstance().connect(bleDevice, new BleGattCallback() {

            @Override
            public void onStartConnect() {
                Log.i(TAG, "DEV_MAC: " + bleDevice.getMac() + " 开始进行连接！");
            }

            @Override
            public void onConnectFail(BleDevice bleDevice, BleException exception) {
                Log.i(TAG, "DEV_MAC: " + bleDevice.getMac() + " 连接不成功！异常为：" + exception.getDescription());
                try {
                    Thread.sleep(2000);
                    connByBleDevice(bleDevice);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            /** 连接成功并发现服务 */
            @Override
            public void onConnectSuccess(BleDevice bleDevice, BluetoothGatt gatt, int status) {
                Log.i(TAG, "DEV_MAC: " + bleDevice.getMac() + " 连接成功！");

                EventBus.getDefault().post(new BleDeviceEvent(bleDevice,"yes"));

//                // 通过 BluetoothGatt，查找出所有的 Service 和 Characteristic 的 UUID
//                selectUuids(bleDevice, gatt);
            }

            /** 连接断开，特指连接后再断开的情况 */
            @Override
            public void onDisConnected(boolean isActiveDisConnected, BleDevice bleDevice, BluetoothGatt gatt, int status) {
                /* 在这里可以监控设备的连接状态，一旦连接断开，可以根据自身情况考虑对 BleDevice 对象进行重连操作。
                   需要注意的是，断开和重连之间最好间隔一段时间，否则可能会出现长时间连接不上的情况。
                   此外，如果通过调用 disconnect(BleDevice bleDevice) 方法，
                   主动断开蓝牙连接的结果也会在这个方法中回调，此时 isActiveDisConnected 将会是 true */
                Log.i(TAG, "DEV_MAC: " + bleDevice.getMac() + " 连接后再断开: " + isActiveDisConnected);

                EventBus.getDefault().post(new BleDeviceEvent(bleDevice,"dis"));
            }
        });
    }


    /** 设置底部导航 */
    private void initBtnNavBar() {
        BottomNavigationBar mNavBar = findViewById(R.id.bottom_navigation_bar);

        // 设置模块名背景色
        mNavBar.setBarBackgroundColor(R.color.colorPrimary);
        // 设置背景模式
        mNavBar.setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_RIPPLE);
        // 设置Tab点击的模式
        mNavBar.setMode(BottomNavigationBar.MODE_FIXED);

        // 添加 Tab
        mNavBar
                // 设置导航图标及名称
                .addItem(new BottomNavigationItem(R.drawable.tab_device, R.string.text_tab_device)
                        // 导航背景颜色
                        .setActiveColorResource(R.color.colorWhite))
                .addItem(new BottomNavigationItem(R.drawable.tab_control, R.string.text_tab_control)
                        .setActiveColorResource(R.color.colorWhite))
                .addItem(new BottomNavigationItem(R.drawable.tab_mine, R.string.text_tab_mine)
                        .setActiveColorResource(R.color.colorWhite))
                // 默认显示面板
                .setFirstSelectedPosition(0)
                // 初始化
                .initialise();

        // 设置事件监听器
        mNavBar.setTabSelectedListener(this);
    }

    /** 将 Fragment 加入 fragments 里面 */
    private ArrayList<Fragment> getFragments() {
        ArrayList<Fragment> list = new ArrayList<>();
        list.add(TabDeviceFragment.newInstance());
        list.add(TabControlFragment.newInstance());
        list.add(TabMineFragment.newInstance());
        return list;
    }

    /** 设置默认 fragment */
    private void setDefFragment() {
        Fragment defFragment = mFragments.get(0);
        if (!defFragment.isAdded()) {
            addFragment(R.id.main_content, defFragment);
            mCurrentFragment = defFragment;
        }
    }

    /** 添加 Fragment 到 Activity 的布局 */
    protected void addFragment(int containerViewId, Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(containerViewId, fragment);
        fragmentTransaction.commit();
    }

    /** 切换 fragment */
    @SuppressLint("CommitTransaction")
    private void replaceFragment(Fragment fragment) {
        // 添加或者显示 fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (mCurrentFragment == fragment)
            return;
        if (!fragment.isAdded()) {
            // 如果当前 fragment 未被添加，则添加到 Fragment 管理器中
            transaction.hide(mCurrentFragment).add(R.id.main_content, fragment).commit();
        } else {
            // 如果当前 fragment 已添加，则显示 Fragment 管理器中的 fragment
            transaction.hide(mCurrentFragment).show(fragment).commit();
        }
        mCurrentFragment = fragment;
    }

    /** Tab 被选中 */
    @Override
    public void onTabSelected(int position) {
        replaceFragment(mFragments.get(position));
    }

    /** Tab 被取消选中 */
    @Override
    public void onTabUnselected(int position) {
    }

    /** Tab 被重新选中 */
    @Override
    public void onTabReselected(int position) {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BleManager.getInstance().disconnectAllDevice();
        BleManager.getInstance().destroy();
    }
}
