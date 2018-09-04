package patrolcar.bobi.cn.patrolcar.app;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RequestExecutor;
import com.yanzhenjie.permission.SettingService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import patrolcar.bobi.cn.blelib.BleManager;
import patrolcar.bobi.cn.blelib.callback.BleGattCallback;
import patrolcar.bobi.cn.blelib.callback.BleMtuChangedCallback;
import patrolcar.bobi.cn.blelib.callback.BleRssiCallback;
import patrolcar.bobi.cn.blelib.callback.BleScanAndConnectCallback;
import patrolcar.bobi.cn.blelib.callback.BleScanCallback;
import patrolcar.bobi.cn.blelib.data.BleDevice;
import patrolcar.bobi.cn.blelib.exception.BleException;
import patrolcar.bobi.cn.blelib.scan.BleScanRuleConfig;
import patrolcar.bobi.cn.patrolcar.R;
import patrolcar.bobi.cn.blelib.utils.ToastTools;
import patrolcar.bobi.cn.patrolcar.view.base.BaseActivity;

import static patrolcar.bobi.cn.patrolcar.app.AppConstant.BLE_DEVICE_NAME;

public class BleControl extends BaseActivity {
    private static final String TAG = BleControl.class.getSimpleName();
    private static final int REQUEST_ENABLE_BLE = 0x01;
    private List<BleDevice> mBleDevList;

    @Override
    protected int getActivityLayout() {
        return 0;
    }

    @Override
    public void initView() {
        super.initView();
        mBleDevList = new ArrayList<>();
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

    /** 启动蓝牙：判断 BLE 是否支持、打开 */
    public void startBLE() {
        if (BleManager.getInstance().isSupportBle()) {
            // 判断当前设备是否支持 BLE
            if (BleManager.getInstance().isBlueEnable()) {
                Log.i(TAG, "蓝牙已打开!");
                requestPermissions();
            } else {
                // 如果蓝牙未打开，通过系统弹出框的形式引导用户开启 BLE
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(intent, REQUEST_ENABLE_BLE);
            }
        } else {
            ToastTools.showShort(this,"对不起，您的设备不支持蓝牙");
        }
    }

    /** 向用户解释为什么需要申请该权限 */
    private final Rationale mDefRationale = new Rationale() {
        @Override
        public void showRationale(Context context, List<String> permissions, final RequestExecutor executor) {
            List<String> permissionNames = Permission.transformText(context, permissions);
            String message = context.getString(R.string.message_permission_rationale,
                    TextUtils.join("\n", permissionNames));
            showDialogRationale(context, executor, message);
        }
    };

    /** 展示解释对话框 */
    private void showDialogRationale(Context context, final RequestExecutor executor, String message) {
        new AlertDialog.Builder(context)
                .setCancelable(false)
                .setTitle(R.string.dialog_tips)
                .setMessage(message)
                .setPositiveButton(R.string.dialog_open, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        executor.execute();
                    }
                })
                .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        executor.cancel();
                    }
                })
                .show();
    }

    /** 提示用户手动设置权限 */
    private void showSetting(List<String> permissions) {
        List<String> permissionNames = Permission.transformText(this, permissions);
        String message = this.getString(R.string.message_permission_always_failed,
                TextUtils.join("\n", permissionNames));
        final SettingService settingService = AndPermission.permissionSetting(this);
        showDialogSet(message, settingService);
    }

    /** 展示设置对话框 */
    private void showDialogSet(String message, final SettingService settingService) {
        new AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle(R.string.dialog_tips)
                .setMessage(message)
                .setPositiveButton(R.string.dialog_set, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        settingService.execute();
                    }
                })
                .setNegativeButton(R.string.dialog_open_no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        settingService.cancel();
                    }
                })
                .show();
    }

    /** 6.0 及以上机型动态获取位置权限 */
    private void requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 如果 API level 大于等于 23(Android 6.0) 时
            AndPermission.with(this)
                    .permission(Manifest.permission.ACCESS_COARSE_LOCATION)
                    .rationale(mDefRationale)
                    // 被拒绝
                    .onDenied(new Action() {
                        @Override
                        public void onAction(List<String> permissions) {
                            // 如果一直否认权限
                            if (AndPermission.hasAlwaysDeniedPermission(BleControl.this, permissions)) {
                                // 提示用户设置
                                showSetting(permissions);
                            }
                        }
                    })
                    // 授予
                    .onGranted(new Action() {
                        @Override
                        public void onAction(List<String> permissions) {
                            // 开始扫描
                            setScanRule();
                            scanBleDev();
                        }
                    })
                    .start();
        }
    }

    /** 扫描设备 */
    private void scanBleDev() {
        BleManager.getInstance().scan(new BleScanCallback() {

            /** @param scanResultList 本次扫描时段内所有被扫描且过滤后的设备集合 */
            @Override
            public void onScanFinished(List<BleDevice> scanResultList) {
                // 开始扫描：它会回到主线程，相当于 onScanning 设备之和
                Log.i(TAG, "开始扫描..." + "设备数：" + +scanResultList.size());

                for (int i = 0; i < scanResultList.size(); i++) {
                    Log.i(TAG, "扫描完成设备: " + scanResultList.get(i));
                }
            }

            /** @param success 本次扫描动作是否开启成功；由于蓝牙没有打开，上一次扫描没有结束等原因，会造成扫描开启失败 */
            @Override
            public void onScanStarted(boolean success) {
                // 扫描到一个符合扫描规则的 BLE 设备（主线程）
                Log.i(TAG, "扫描是否开启成功: " + success);
            }

            /** @param bleDevice 经过扫描过滤规则过滤后的设备，同一个设备只会出现一次 */
            @Override
            public void onScanning(BleDevice bleDevice) {
                // 扫描结束，列出所有扫描到的符合扫描规则的 BLE 设备（主线程）
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

                // 通过 BluetoothGatt，查找出所有的 Service 和 Characteristic 的 UUID
                selectUuids(bleDevice, gatt);
            }

            /** 连接断开，特指连接后再断开的情况 */
            @Override
            public void onDisConnected(boolean isActiveDisConnected, BleDevice bleDevice, BluetoothGatt gatt, int status) {
                /* 在这里可以监控设备的连接状态，一旦连接断开，可以根据自身情况考虑对 BleDevice 对象进行重连操作。
                   需要注意的是，断开和重连之间最好间隔一段时间，否则可能会出现长时间连接不上的情况。
                   此外，如果通过调用 disconnect(BleDevice bleDevice) 方法，
                   主动断开蓝牙连接的结果也会在这个方法中回调，此时 isActiveDisConnected 将会是 true */
                Log.i(TAG, "DEV_MAC: " + bleDevice.getMac() + " 连接后再断开: " + isActiveDisConnected);
            }
        });
    }

    /** 通过 BluetoothGatt，查找出所有的 Service 和 Characteristic 的 UUID */
    private void selectUuids(BleDevice bleDevice, BluetoothGatt gatt) {
        List<BluetoothGattService> serviceList = gatt.getServices();
        for (BluetoothGattService service : serviceList) {
            UUID uuid_service = service.getUuid();
            Log.i(TAG, "DEV_MAC: " + bleDevice.getMac() + " UUID_服务: " + uuid_service);

            List<BluetoothGattCharacteristic> characteristicList = service.getCharacteristics();
            for (BluetoothGattCharacteristic characteristic : characteristicList) {
                UUID uuid_chara = characteristic.getUuid();
                Log.i(TAG, "DEV_MAC: " + bleDevice.getMac() + " UUID_特征: " + uuid_chara);
            }
        }
    }

    /**
     * BLE 通过 Mac 连接
     *
     * 此方法可以不经过扫描，尝试直接连接周围符合该 Mac 的 BLE 设备。
     * 在很多使用场景，我建议 APP 保存用户惯用设备的 Mac，然后使用该方法进行连接可以大大提高连接效率。
     */
    public void connByMac(String mac) {
        BleManager.getInstance().connect(mac, new BleGattCallback() {
            @Override
            public void onStartConnect() {
                // 开始连接
            }

            @Override
            public void onConnectFail(BleDevice bleDevice, BleException exception) {
                // 连接失败
            }

            @Override
            public void onConnectSuccess(BleDevice bleDevice, BluetoothGatt gatt, int status) {
                // 连接成功，BleDevice 即为所连接的 BLE 设备
            }

            @Override
            public void onDisConnected(boolean isActiveDisConnected, BleDevice device, BluetoothGatt gatt, int status) {
                // 连接中断，isActiveDisConnected 表示是否是主动调用了断开连接方法
            }
        });
    }

    /** 扫描并连接 */
    public void scanAndConn() {
        BleManager.getInstance().scanAndConnect(new BleScanAndConnectCallback() {
            @Override
            public void onScanFinished(BleDevice scanResult) {
                // 开始连接（主线程）
            }

            @Override
            public void onStartConnect() {
                // 扫描结束，结果即为扫描到的第一个符合扫描规则的BLE设备，如果为空表示未搜索到（主线程）
            }

            @Override
            public void onConnectFail(BleDevice bleDevice, BleException exception) {
                // 连接失败（主线程）
            }

            @Override
            public void onConnectSuccess(BleDevice bleDevice, BluetoothGatt gatt, int status) {
                // 连接成功，BleDevice即为所连接的BLE设备（主线程）
            }

            @Override
            public void onDisConnected(boolean isActiveDisConnected, BleDevice device, BluetoothGatt gatt, int status) {
                // 连接断开，isActiveDisConnected是主动断开还是被动断开（主线程）
            }

            @Override
            public void onScanStarted(boolean success) {

            }

            @Override
            public void onScanning(BleDevice bleDevice) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BLE) {
            // 判断蓝牙是否已经打开
            if (BleManager.getInstance().isBlueEnable()) {
                Toast.makeText(this, "蓝牙打开成功", Toast.LENGTH_SHORT).show();
                requestPermissions();
            } else {
                // 蓝牙未打开，通过系统弹出框的形式引导用户开启 BLE
                Toast.makeText(this, "无法使用蓝牙功能", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void addDevice(BleDevice bleDevice) {
        removeDevice(bleDevice);
        mBleDevList.add(bleDevice);
    }

    public void removeDevice(BleDevice bleDevice) {
        for (int i = 0; i < mBleDevList.size(); i++) {
            BleDevice device = mBleDevList.get(i);
            if (bleDevice.getKey().equals(device.getKey())) {
                mBleDevList.remove(i);
            }
        }
    }

    public void clearConnectedDevice() {
        for (int i = 0; i < mBleDevList.size(); i++) {
            BleDevice device = mBleDevList.get(i);
            if (BleManager.getInstance().isConnected(device)) {
                mBleDevList.remove(i);
            }
        }
    }

    public void clearScanDevice() {
        for (int i = 0; i < mBleDevList.size(); i++) {
            BleDevice device = mBleDevList.get(i);
            if (!BleManager.getInstance().isConnected(device)) {
                mBleDevList.remove(i);
            }
        }
    }

    public void clear() {
        clearConnectedDevice();
        clearScanDevice();
    }

    private void showConnectedDevice() {
        List<BleDevice> deviceList = BleManager.getInstance().getAllConnectedDevice();
        for (BleDevice bleDevice : deviceList) {
            Log.i(TAG, "showConnectedDevice: " + bleDevice);
        }
    }

    private void readRssi(BleDevice bleDevice) {
        BleManager.getInstance().readRssi(bleDevice, new BleRssiCallback() {
            @Override
            public void onRssiFailure(BleException exception) {
                Log.i(TAG, "onRssiFailure" + exception.toString());
            }

            @Override
            public void onRssiSuccess(int rssi) {
                Log.i(TAG, "onRssiSuccess: " + rssi);
            }
        });
    }

    private void setMtu(BleDevice bleDevice, int mtu) {
        BleManager.getInstance().setMtu(bleDevice, mtu, new BleMtuChangedCallback() {
            @Override
            public void onSetMTUFailure(BleException exception) {
                Log.i(TAG, "onsetMTUFailure" + exception.toString());
            }

            @Override
            public void onMtuChanged(int mtu) {
                Log.i(TAG, "onMtuChanged: " + mtu);
            }
        });
    }
}
