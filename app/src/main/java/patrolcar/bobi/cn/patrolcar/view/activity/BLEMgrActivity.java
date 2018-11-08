package patrolcar.bobi.cn.patrolcar.view.activity;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.ParcelUuid;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import android.util.Pair;

import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.SettingService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import patrolcar.bobi.cn.patrolcar.R;
import patrolcar.bobi.cn.patrolcar.app.MyLifecycleHandler;
import patrolcar.bobi.cn.patrolcar.util.BleDevProtocol;
import patrolcar.bobi.cn.patrolcar.util.LogUtil;
import patrolcar.bobi.cn.patrolcar.util.ToastUtil;
import patrolcar.bobi.cn.patrolcar.util.Util;
import patrolcar.bobi.cn.patrolcar.view.base.BaseActivity;


/**
 * 管理多个BLE设备的发现、连接、通讯
 */

public class BLEMgrActivity extends BaseActivity {
    private static final String TAG = BLEMgrActivity.class.getSimpleName();
    private static final String BLE_DEV_NAME = "EENENG-CAR-01";
    public static final ParcelUuid UUID_GATT_SERVICE = ParcelUuid.fromString("6e400001-b5a3-f393-e0a9-e50e24dcca9e");
    public static final UUID UUID_GATT_CHARACTERISTIC_WRITE = UUID.fromString("6e400002-b5a3-f393-e0a9-e50e24dcca9e");

    private static final int MAX_BLUETOOTH_SEND_PKG_LEN = 18;        // 蓝牙发送包的最大长度
    private static final int REQUEST_ENABLE_BT = 38192;              // 请求启用蓝牙
    private static final int REFRESHING_PERIOD = 60 * 1000;          // 刷新周期
    private static final int SCANNING_TIME = 8 * 1000;               // 扫描时间
    private boolean mIsShutdown;                                     // 是否关机
    private boolean mIsUserDenied;                                   // 用户是否拒绝
    private boolean mIsScanning;                                     // 是否在扫描
    private boolean mIsDoNextRefresh;                                // 是否在下次刷新
    private long mNextRefreshingTime;                                // 下一次刷新时间
    private List<ScanFilter> mScanFilterList;                        // 扫描过滤器
    private ScanSettings mScanSettings;                              // BLE 扫描设置
    private BluetoothAdapter mBleAdapter;                            // 本地设备蓝牙适配器
    private BluetoothLeScanner mBleLeScanner;                        // 提供了为 BLE 设备执行扫描相关操作的方法
    private Handler mHandler;
    private Map<String, DeviceManager> mDevMgrSet;                   // 已经连接到的设备
    private List<String> mAllowConnDevListMAC;                       // 允许连接的设备集合
    private String mAllowConnDevName;                                // 允许连接的设备名称

    // 运行周期
    private final Map<String, Pair<Runnable, Integer>> mPeriodRunMap = new ArrayMap<>();


    /** 配置当前 APP 处理的蓝牙设备名称 */
    private void setAllowedConnDevName(String name) {
        this.mAllowConnDevName = name;
    }

    public String getAllowedConnDevName() {
        return mAllowConnDevName;
    }

    /** 设置允许连接设备管理(通过 MAC) */
    public void setAllowConnDevListMAC(List<String> newMacs) {
        mAllowConnDevListMAC = newMacs;
        removeConnectedMoreDevice();
    }

    public void clearAllowConnDevListMAC() {
        mAllowConnDevListMAC.clear();
    }

    public void addAllowConnDevListMAC(String newMac) {
        mAllowConnDevListMAC.add(newMac);
    }

    /** 更新允许连接的设备 MAC 地址列表后，删除已经连接的不在列表中多余的设备 */
    public void removeConnectedMoreDevice() {
        // 判断已经连接的数据
        for (DeviceManager mgr : mDevMgrSet.values()) {
            // 如果已连接的设备不在 AllowedConnectDevicesMAC 中，断开设备连接
            if (!mAllowConnDevListMAC.contains(mgr.mac)) {
                if (mgr.gatt != null && mgr.isConnected) {
                    // 断开连接
                    mgr.gatt.disconnect();
                }
                mDevMgrSet.remove(mgr.mac);
            }
        }
    }

    /**
     * 设备管理类
     */
    private static class DeviceManager {
        final String mac;
        final BluetoothDevice bleDevice;
        BluetoothGatt gatt;
        List<BluetoothGattCharacteristic> characteristicList;
        BluetoothGattCharacteristic writeCharacteristic;
        boolean isConnected;          // 是否连接
        boolean isDiscovering;        // 是否发现

        DeviceManager(BluetoothDevice dev) {
            mac = dev.getAddress();
            bleDevice = dev;
        }

        boolean setWriteChannel(UUID uuid) {
            if (characteristicList != null) {
                for (BluetoothGattCharacteristic c : characteristicList) {
                    if (c.getUuid().equals(uuid)) {
                        writeCharacteristic = c;
                        return true;
                    }
                }
            }
            return false;
        }

        BluetoothGattCharacteristic getWriteChannel() {
            return writeCharacteristic;
        }
    }

    @Override
    protected int getActivityLayout() {
        return 0;
    }

    @Override
    public void initView() {
        super.initView();
        setAllowedConnDevName(BLE_DEV_NAME);
        mDevMgrSet = new ArrayMap<>();
        mAllowConnDevListMAC = new ArrayList<>();

        // 添加校验新的状态变化监听
        MyLifecycleHandler.addListener(mOnForegroundStateChangeListener);

        mHandler = new Handler();
        BluetoothManager bleMgr = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBleAdapter = (bleMgr == null) ? null : bleMgr.getAdapter();
    }

    // 新的状态变化监听
    private final MyLifecycleHandler.OnForegroundStateChangeListener mOnForegroundStateChangeListener =
            new MyLifecycleHandler.OnForegroundStateChangeListener() {
                @Override
                public void onStateChanged(boolean foreground) {
                    LogUtil.i(TAG, "OnForegroundStateChangeListener " + foreground);
                    if (foreground) {
                        for (Pair<Runnable, Integer> s : mPeriodRunMap.values()) {
                            mHandler.postDelayed(s.first, s.second);
                        }
                        mHandler.post(mPollRun);
                    } else {
                        mHandler.removeCallbacks(mPollRun);
                        for (Pair<Runnable, Integer> s : mPeriodRunMap.values()) {
                            mHandler.removeCallbacks(s.first);
                        }
                    }
                }
            };

    @Override
    protected void onPause() {
        super.onPause();
        /*
        mHandler.removeCallbacks(mPollRun);
        for(Pair<Runnable, Integer> s : mPeriodRunMap.values()){
            mHandler.removeCallbacks(s.first);
        }
        */
        LogUtil.i(TAG, "onPause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        for(Pair<Runnable, Integer> s : mPeriodRunMap.values()){
            mHandler.postDelayed(s.first, s.second);
        }
        mHandler.post(mPollRun);
    }

    private final Runnable mPollRun = new Runnable() {
        @Override
        public void run() {
            poll();
            if (!mIsShutdown) {
                mHandler.postDelayed(mPollRun, 100);

                if (mIsDoNextRefresh && (System.currentTimeMillis() - mNextRefreshingTime) > 0) {
                    mIsDoNextRefresh = false;
                    startScan();
                }
            }
        }
    };

    void poll() {
        peekADeviceToDiscovering();
    }

    private void peekADeviceToDiscovering() {
        if (mIsShutdown) {
            return;
        }
        for (DeviceManager mgr : mDevMgrSet.values()) {
            if (mgr.isConnected && mgr.characteristicList == null && !mgr.isDiscovering) {
                LogUtil.i(TAG, "discovering services " + mgr.mac);
                mgr.isDiscovering = mgr.gatt.discoverServices();
                return;
            }
        }
    }

    private void startScan() {
        if (mBleAdapter != null) {
            if (!mBleAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            } else {
                bleEnabled();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT) {
            if (mBleAdapter.isEnabled()) {
                bleEnabled();
            } else {
                mIsUserDenied = true;
                userDenied();
            }
        }
    }

    /** 用户拒绝 */
    private void userDenied() {
        ToastUtil.showShort("无法使用蓝牙功能");
    }

    // 给用户一个说法
    private final Rationale mDefRationale = (context, permissions, executor) -> {
        List<String> permissionNames = Permission.transformText(context, permissions);
        String message = context.getString(R.string.message_permission_rationale, TextUtils.join("\n", permissionNames));

        new AlertDialog.Builder(context)
                .setCancelable(false)
                .setTitle(R.string.tip)
                .setMessage(message)
                .setPositiveButton(R.string.resume, (dialog, which) -> executor.execute())
                .setNegativeButton(R.string.cancel, (dialog, which) -> executor.cancel())
                .show();
    };

    /** 蓝牙启用 */
    private void bleEnabled() {
        Log.i(TAG, "bleEnabled: ");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            AndPermission.with(this)
                    .permission(Manifest.permission.ACCESS_COARSE_LOCATION)
                    .rationale(mDefRationale)
                    .onDenied(permissions -> {
                        if (AndPermission.hasAlwaysDeniedPermission(BLEMgrActivity.this, permissions)) {
                            // 如果用户一直否认许可，提示用户自行设置
                            showSetting(permissions);
                        }
                    })
                    .onGranted(permissions -> startLeScanNoBug())
                    .start();
        } else {
            startLeScanNoBug();
        }
    }

    /** 显示用户指导设置 */
    private void showSetting(final List<String> permissions) {
        List<String> permissionNames = Permission.transformText(this, permissions);
        String message = this.getString(R.string.message_permission_always_failed, TextUtils.join("\n", permissionNames));

        final SettingService settingService = AndPermission.permissionSetting(this);
        new AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle(R.string.tip)
                .setMessage(message)
                .setPositiveButton(R.string.set, (dialog, which) -> settingService.execute())
                .setNegativeButton(R.string.no, (dialog, which) -> settingService.cancel())
                .show();
    }

    /** 如果在 bleEnabled 中直接调用 startLeScan， 将出现 android.os.DeadObjectException */
    private void startLeScanNoBug() {
        mHandler.post(() -> startLeScan());
    }

    @SuppressWarnings("SameParameterValue")
    void addScanFilter(ParcelUuid uuid) {
        LogUtil.i(TAG, "addScanFilter: ");
        ScanFilter filter = new ScanFilter.Builder()
                .setServiceUuid(uuid)
                .build();
        if (mScanFilterList == null) {
            mScanFilterList = new LinkedList<>();
        }
        mScanFilterList.add(filter);
    }

    private void startLeScan() {
        LogUtil.v(TAG, "startLeScan");
        mBleLeScanner = (mBleAdapter == null) ? null : mBleAdapter.getBluetoothLeScanner();
        if (mBleLeScanner != null) {
            mIsScanning = true;
            if (mScanFilterList == null) {
                mBleLeScanner.startScan(mScanCallback);
            } else {
                if (mScanSettings == null) {
                    mScanSettings = new ScanSettings.Builder()
                            .setReportDelay(0)
                            .setScanMode(ScanSettings.SCAN_MODE_BALANCED)
                            .build();
                }
                mBleLeScanner.startScan(mScanFilterList, mScanSettings, mScanCallback);
            }
            //mHandler.postDelayed(mStopScanRun, SCANNING_TIME);
        }
    }

    private final Runnable mStopScanRun = new Runnable() {
        @Override
        public void run() {
            mIsScanning = false;
            mBleLeScanner.flushPendingScanResults(mScanCallback);
            mBleLeScanner.stopScan(mScanCallback);
            stopScan();
            if (!mIsUserDenied) {
                mIsDoNextRefresh = true;
                mNextRefreshingTime = System.currentTimeMillis() + REFRESHING_PERIOD;
            }
        }
    };

    void stopScan() {
    }

    void refresh() {
        if (mIsScanning) {
            return;
        }
        mIsDoNextRefresh = true;
        mNextRefreshingTime = System.currentTimeMillis();
    }

    private final ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            String mac = result.getDevice().getAddress();
            LogUtil.d(TAG, "onScanResult " + callbackType + " " + mac + " | " + result.getDevice().getName());
            if (callbackType == ScanSettings.CALLBACK_TYPE_ALL_MATCHES) {
                // 如果回调类型全部匹配
                if (!mDevMgrSet.containsKey(mac)) {
                    ScanRecord record = result.getScanRecord();
                    onFoundDevice(result.getDevice(), (record == null) ? null : record.getServiceUuids());
                }
            }
        }

        @Override// 批量扫描结果
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
            LogUtil.i(TAG, "onBatchScanResults " + results.size());
            for (ScanResult result : results) {
                String mac = result.getDevice().getAddress();
                LogUtil.i(TAG, "onScanResult " + " " + mac + " | " + result.getDevice().getName());
                if (!mDevMgrSet.containsKey(mac)) {
                    ScanRecord record = result.getScanRecord();
                    onFoundDevice(result.getDevice(), (record == null) ? null : record.getServiceUuids());
                }
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            LogUtil.i(TAG, "onScanFailed " + errorCode);
        }
    };

    void onFoundDevice(BluetoothDevice bleDevice, @Nullable List<ParcelUuid> serviceUuids) {
        String name = bleDevice.getName();
        String mac = bleDevice.getAddress();
        LogUtil.d(TAG, "onFoundDevice " + mac + " | " + name);

        if (serviceUuids != null) {
            for (ParcelUuid uuid : serviceUuids) {
                LogUtil.d(TAG, "serviceUuid " + uuid.toString());
            }
        }
    }

    private void addDeviceByObject(BluetoothDevice bleDevice) {
        LogUtil.d(TAG, "addDeviceByObject:");
        final DeviceManager mgr;
        String mac = bleDevice.getAddress();
        if (!mDevMgrSet.containsKey(mac)) {
            mgr = new DeviceManager(bleDevice);
            mDevMgrSet.put(mac, mgr);
        } else {
            mgr = mDevMgrSet.get(mac);
        }

        LogUtil.w(TAG, "addDeviceByObject mDevMgrSet size " + mDevMgrSet.size());
        if (mgr.gatt == null && !mIsShutdown) {
            mgr.gatt = mgr.bleDevice.connectGatt(this, true, mBleGattCallback);
        }
    }

    void addDeviceByMac(String mac) {
        mac = mac.toUpperCase();
        LogUtil.i(TAG, "Add dev by " + mac);
        if (BluetoothAdapter.checkBluetoothAddress(mac)) {
            addDeviceByObject(mBleAdapter.getRemoteDevice(mac));
        } else {
            throw new IllegalArgumentException("invalid Bluetooth address : " + mac);
        }
    }

    private void removeDeviceByObject(String mac) {
        final DeviceManager mgr;
        //if (mDevMgrSet.containsKey(mac))
        {
            mgr = mDevMgrSet.get(mac);
            try {
                //mgr.gatt.disconnect();
                //Thread.sleep(10);
                mgr.gatt.close();
                MainActivity.getAppCtrl().scanRefresh();
                onDeviceDisconnect(mac);
            } catch (Exception e) {
                e.printStackTrace();
            }
            mDevMgrSet.remove(mac);
        }
    }

    void removeDeviceByMac(String mac) {
        //mac = mac.toUpperCase();
        LogUtil.i(TAG, "Add dev by " + mac);
        removeDeviceByObject(mac);
//        if (BluetoothAdapter.checkBluetoothAddress(mac)) {
//            removeDeviceByObject(mBleAdapter.getRemoteDevice(mac));
//        } else {
//            throw new IllegalArgumentException("invalid Bluetooth address : " + mac);
//        }
    }

    /** 获取匹配的设备管理器 */
    private DeviceManager getMatchedDevMgr(BluetoothGatt gatt) {
        String mac = gatt.getDevice().getAddress();
        return mDevMgrSet.get(mac);
    }

    /**
     * GATT 操作类
     */
    private static class GattOperation {
        final static int OP_CHARACTERISTIC_WRITE = 1;
        final static int OP_DESCRIPTOR_WRITE = 2;
        BluetoothGattCharacteristic characteristic;
        BluetoothGattDescriptor descriptor;
        BluetoothGatt gatt;
        byte[] data;
        int operation;

        @SuppressWarnings("SameParameterValue")
        static GattOperation newWriteDescriptor(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, byte[] data) {
            GattOperation opGatt = new GattOperation();
            opGatt.characteristic = null;
            opGatt.descriptor = descriptor;
            opGatt.data = data;
            opGatt.operation = OP_DESCRIPTOR_WRITE;
            opGatt.gatt = gatt;
            return opGatt;
        }

        static GattOperation newWriteCharacteristic(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, byte[] data) {
            GattOperation opGatt = new GattOperation();
            opGatt.characteristic = characteristic;
            opGatt.descriptor = null;
            opGatt.data = data;
            opGatt.operation = OP_CHARACTERISTIC_WRITE;
            opGatt.gatt = gatt;
            return opGatt;
        }

        boolean run() {
            switch (operation) {
                case OP_CHARACTERISTIC_WRITE:
                    LogUtil.i(TAG, "write characteristic : " + characteristic.getUuid() + " : " + Util.hex(data, data.length));
                    characteristic.setValue(data);
                    return gatt.writeCharacteristic(characteristic);
                case GattOperation.OP_DESCRIPTOR_WRITE:
                    LogUtil.i(TAG, "write descriptor " + descriptor.getUuid());
                    descriptor.setValue(data);
                    return gatt.writeDescriptor(descriptor);
            }
            return false;
        }
    }

    private final LinkedList<GattOperation> mGattOperations = new LinkedList<>();
    private GattOperation mCurrentGattOperation;
    private final Object mGattOperationLock = new Object();

    private void addOpGatt(GattOperation op) {
        GattOperation toRun = null;
        synchronized (mGattOperationLock) {
            mGattOperations.addLast(op);
            if (mCurrentGattOperation == null) {
                mCurrentGattOperation = mGattOperations.pollFirst();
                toRun = mCurrentGattOperation;
            }
        }
        if (toRun != null) {
            if (!toRun.run()) {
                removeOpGatt();
            }
        }
    }

    private void removeCurrentGattOperation(BluetoothGatt gatt) {
        synchronized (mGattOperationLock) {
            if (mCurrentGattOperation != null && gatt.equals(mCurrentGattOperation.gatt)) {
                removeOpGatt();
            }
        }
    }

    private void removeOpGatt() {
        while (true) {
            GattOperation toRun;
            synchronized (mGattOperationLock) {
                mCurrentGattOperation = mGattOperations.pollFirst();
                toRun = mCurrentGattOperation;
            }
            if (toRun != null) {
                if (toRun.run()) {
                    break;
                }
            } else {
                break;
            }
        }
    }

    private final BluetoothGattCallback mBleGattCallback = new BluetoothGattCallback() {
        @Override// 连接状态变化
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            final DeviceManager mgr = getMatchedDevMgr(gatt);
            if (mgr != null) {
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    LogUtil.i(TAG, "connected " + mgr.mac);
                    mgr.isConnected = true;
                    runOnUiThread(() -> onDeviceConnect(mgr.mac));
                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    LogUtil.i(TAG, "disconnected " + mgr.mac);
                    mgr.isConnected = false;
                    mgr.isDiscovering = false;
                    mgr.characteristicList = null;
                    runOnUiThread(() -> onDeviceDisconnect(mgr.mac));
                }
            }
        }

        @Override// 发现的服务
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            final DeviceManager mgr = getMatchedDevMgr(gatt);
            if (mgr != null) {
                mgr.isDiscovering = false;
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    LogUtil.i(TAG, "onServicesDiscovered success for " + mgr.mac);
                    mgr.characteristicList = new LinkedList<>();
                    for (BluetoothGattService service : gatt.getServices()) {
                        mgr.characteristicList.addAll(service.getCharacteristics());
                    }
                    for (BluetoothGattCharacteristic ch : mgr.characteristicList) {
                        if ((ch.getProperties() & BluetoothGattCharacteristic.PROPERTY_NOTIFY) != 0) {
                            gatt.setCharacteristicNotification(ch, true);
                            List<BluetoothGattDescriptor> descriptorList = ch.getDescriptors();
                            if (descriptorList != null) {
                                for (BluetoothGattDescriptor descriptor : descriptorList) {
                                    addOpGatt(GattOperation.newWriteDescriptor(gatt, descriptor, BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE));
                                }
                            }
                        }
                    }
                    runOnUiThread(() -> onDeviceReady(mgr.mac));
                }
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            removeCurrentGattOperation(gatt);
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            removeCurrentGattOperation(gatt);
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            onReceive(gatt.getDevice().getAddress(), characteristic.getValue());
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorRead(gatt, descriptor, status);
            removeCurrentGattOperation(gatt);
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
            LogUtil.i(TAG, "onDescriptorWrite");
            removeCurrentGattOperation(gatt);
        }
    };

    void onReceive(String mac, byte[] data) {
        LogUtil.d(TAG, "recv from " + mac + " : " + Util.hex(data, data.length));
    }

    void onDeviceReady(String mac) {
    }

    void onDeviceConnect(String mac) {
    }

    void onDeviceDisconnect(String mac) {
    }

    @SuppressWarnings("SameParameterValue")
    boolean setSendDefaultChannel(String mac, UUID uuid) {
        DeviceManager mgr = mDevMgrSet.get(mac);
        return (mgr != null) && mgr.setWriteChannel(uuid);
    }

    boolean send(String mac, byte[] data, boolean encrypt) {
        LogUtil.i(TAG, "send: " + Util.hex(data, data.length));
        if (mIsShutdown) {
            return false;
        }
        if (encrypt) {
            data = BleDevProtocol.wrappedPackage(data);
        }
        DeviceManager mgr = mDevMgrSet.get(mac);
        if (mgr != null && mgr.isConnected && mgr.gatt != null) {
            BluetoothGattCharacteristic ch = mgr.getWriteChannel();
            if (ch != null) {
                if (data.length > MAX_BLUETOOTH_SEND_PKG_LEN) {
                    for (int i = 0; i < data.length; i += MAX_BLUETOOTH_SEND_PKG_LEN) {
                        int len = Math.min(data.length - i, MAX_BLUETOOTH_SEND_PKG_LEN);
                        addOpGatt(GattOperation.newWriteCharacteristic(mgr.gatt, ch, Arrays.copyOfRange(data, i, i + len)));
                    }
                } else {
                    addOpGatt(GattOperation.newWriteCharacteristic(mgr.gatt, ch, data));
                }
                return true;
            }
        }
        return false;
    }

    /** 断开所有 BLE 设备 */
    private void disconnectAll() {
        mIsShutdown = true;
        synchronized (mGattOperationLock) {
            mGattOperations.clear();
        }
        for (DeviceManager mgr : mDevMgrSet.values()) {
            if (mgr.gatt != null && mgr.isConnected) {
                mgr.gatt.disconnect();
            }
        }
    }

    @Override
    protected void onDestroy() {
        MyLifecycleHandler.removeListener(mOnForegroundStateChangeListener);

        for (Pair<Runnable, Integer> s : mPeriodRunMap.values()) {
            mHandler.removeCallbacks(s.first);
        }
        mPeriodRunMap.clear();

        if (mIsScanning) {
            mHandler.removeCallbacks(mStopScanRun);
            mStopScanRun.run();
        }
        disconnectAll();
        super.onDestroy();
    }

    private class WrappedRunnable implements Runnable {
        private final Runnable runnable;
        private final Handler handler;
        private final int delay;

        WrappedRunnable(Runnable runnable, Handler handler, int delay) {
            this.runnable = runnable;
            this.handler = handler;
            this.delay = delay;
        }

        @Override
        public void run() {
            runnable.run();
            handler.postDelayed(this, delay);
        }
    }

    @SuppressWarnings("SameParameterValue")
    void registerPeriod(@NonNull String tag, @NonNull Runnable runnable, int period) {
        if (mPeriodRunMap.containsKey(tag)) {
            Runnable old = mPeriodRunMap.get(tag).first;
            mHandler.removeCallbacks(old);
        }
        Runnable wrappedRunnable = new WrappedRunnable(runnable, mHandler, period);
        mPeriodRunMap.put(tag, new Pair<>(wrappedRunnable, period));
        mHandler.postDelayed(wrappedRunnable, period);
    }

    void unregisterPeriod(@NonNull String tag) {
        if (mPeriodRunMap.containsKey(tag)) {
            Runnable old = mPeriodRunMap.get(tag).first;
            mHandler.removeCallbacks(old);
            mPeriodRunMap.remove(tag);
        }
    }
}
