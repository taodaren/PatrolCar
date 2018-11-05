package patrolcar.bobi.cn.patrolcar.view.activity;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.os.ParcelUuid;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;

import java.util.ArrayList;
import java.util.List;

import patrolcar.bobi.cn.patrolcar.R;
import patrolcar.bobi.cn.patrolcar.util.BleDevProtocol;
import patrolcar.bobi.cn.patrolcar.util.LogUtil;
import patrolcar.bobi.cn.patrolcar.view.fragment.TabCtrlFragment;
import patrolcar.bobi.cn.patrolcar.view.fragment.TabDistanceFragment;
import patrolcar.bobi.cn.patrolcar.view.fragment.TabStatusFragment;

import static patrolcar.bobi.cn.patrolcar.app.AppConstant.UUID_GATT_CHARACTERISTIC_WRITE;
import static patrolcar.bobi.cn.patrolcar.app.AppConstant.UUID_GATT_SERVICE;

public class MainActivity extends BLEManagerActivity implements BottomNavigationBar.OnTabSelectedListener {
    private static final String TAG = "MainActivity";
    private List<Fragment> mFragments;
    private Fragment mCurrentFragment;

    static private MainActivity AppInstance;

    public static MainActivity getAppCtrl() {
        return AppInstance;
    }

    @Override
    protected int getActivityLayout() {
        return R.layout.activity_main;
    }

    @Override
    public void initView() {
        super.initView();
        AppInstance = this;
        initBtnNavBar();
        mFragments = getFragments();
        setDefFragment();
        scanRefresh();
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
                .addItem(new BottomNavigationItem(R.drawable.tab_status, R.string.txt_tab_device)
                        // 导航背景颜色
                        .setActiveColorResource(R.color.colorWhite))
                .addItem(new BottomNavigationItem(R.drawable.tab_ctrl, R.string.txt_tab_control)
                        .setActiveColorResource(R.color.colorWhite))
                .addItem(new BottomNavigationItem(R.drawable.tab_distance, R.string.txt_distance_dis)
                        .setActiveColorResource(R.color.colorWhite))
//                .addItem(new BottomNavigationItem(R.drawable.tab_video, "VIDEO")
//                        .setActiveColorResource(R.color.colorWhite))
                // 默认显示面板
                .setFirstSelectedPosition(1)
                // 初始化
                .initialise();
        // 设置事件监听器
        mNavBar.setTabSelectedListener(this);
    }

    /** 将 Fragment 加入 fragments 里面 */
    private ArrayList<Fragment> getFragments() {
        ArrayList<Fragment> list = new ArrayList<>();
        list.add(TabStatusFragment.newInstance());
        list.add(TabCtrlFragment.newInstance());
        list.add(TabDistanceFragment.newInstance());
//        list.add(TabVideoFragment.newInstance());
        return list;
    }

    /** 设置默认 fragment */
    private void setDefFragment() {
        Fragment defFragment = mFragments.get(1);
        if (!defFragment.isAdded()) {
            addFragment(defFragment);
            mCurrentFragment = defFragment;
        }
    }

    /** 添加 Fragment 到 Activity 的布局 */
    protected void addFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.main_content, fragment);
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
        // 断开蓝牙
    }

    public void scanRefresh() {
        addScanFilter(UUID_GATT_SERVICE);
        refresh();
    }

    @Override
    void onFoundDevice(BluetoothDevice bleDevice, @Nullable List<ParcelUuid> serviceUuids) {
        super.onFoundDevice(bleDevice, serviceUuids);
        Log.i(TAG, "onFoundDevice serviceUuids: " + serviceUuids);
        String name = bleDevice.getName();
        String mac = bleDevice.getAddress();
        Log.i(TAG, "getName: " + name);

        // 通过设备广播名称，判断是否为配置的设备
        if (name.indexOf(getAllowedConnDevName()) != 0) {
            return;
        }

        connDevice(bleDevice.getAddress());
        LogUtil.d(TAG, "dev mac: " + mac);
    }

    /**
     * 设备就绪
     */
    @Override
    void onDeviceReady(final String mac) {
        LogUtil.i(TAG, "onDeviceReady " + mac);
        if (setSendDefaultChannel(mac, UUID_GATT_CHARACTERISTIC_WRITE)) {
        }
    }

    @Override
    void onDeviceConnect(String mac) {
        LogUtil.i(TAG, "onDeviceConnect " + mac);

    }

    /**
     * 设备断开
     */
    @Override
    void onDeviceDisconnect(String mac) {
        unregisterPeriod(mac + "-status");

    }

    /**
     * 连接设备
     */
    public void connDevice(final String mac) {
        addDeviceByMac(mac);
    }

    public void sendCmd(String mac, byte[] data) {
        send(mac, data, false);
    }

}
