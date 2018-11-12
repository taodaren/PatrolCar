package patrolcar.bobi.cn.patrolcar.view.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothDevice;
import android.os.ParcelUuid;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ListView;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import patrolcar.bobi.cn.patrolcar.R;
import patrolcar.bobi.cn.patrolcar.util.BleDevProtocol;
import patrolcar.bobi.cn.patrolcar.util.LogUtil;
import patrolcar.bobi.cn.patrolcar.view.fragment.TabCtrlFragment;
import patrolcar.bobi.cn.patrolcar.view.fragment.TabDistanceFragment;
import patrolcar.bobi.cn.patrolcar.view.fragment.TabStatusFragment;

import static android.app.AlertDialog.THEME_HOLO_LIGHT;

public class MainActivity extends BLEMgrActivity implements BottomNavigationBar.OnTabSelectedListener {
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
        showSelectMacDialog();
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

    public void scanRefresh() {
        addScanFilter(UUID_GATT_SERVICE);
        refresh();
    }

    @Override
    void onFoundDevice(BluetoothDevice bleDevice, @Nullable List<ParcelUuid> serviceUuids) {
        super.onFoundDevice(bleDevice, serviceUuids);
        // 通过设备广播名称，判断是否为配置的设备
        if (bleDevice.getName().indexOf(getAllowedConnDevName()) != 0) {
            return;
        }
        if (mAdapterByFoundMac != null) {
            if (!mListByFoundMac.contains(bleDevice.getAddress())) {
                mListByFoundMac.add(bleDevice.getAddress());
                mAdapterByFoundMac.notifyDataSetChanged();
            }
        }
    }

    BleDevProtocol protocol = new BleDevProtocol();

    @Override
    void onReceive(String mac, byte[] data) {
        super.onReceive(mac, data);
        protocol.bleReceive(data);
    }

    @Override
    void onDeviceReady(final String mac) {
        LogUtil.i(TAG, "onDeviceReady " + mac);
        setSendDefaultChannel(mac, UUID_GATT_CHARACTERISTIC_WRITE);
    }

//    @Override
//    void onDeviceConnect(String mac) {
//        LogUtil.i(TAG, "onDeviceConnect " + mac);
//    }
//
//    @Override
//    void onDeviceDisconnect(String mac) {
//        unregisterPeriod(mac + "-status");
//    }

    public void sendCmd(String mac, byte[] data) {
//        Log.w("TabCtrlFragment", "sendCmd: ");
        send(mac, data, false);
    }

    private final List<String> mListByFoundMac = new LinkedList<>();
    private ArrayAdapter<String> mAdapterByFoundMac;

    private void showSelectMacDialog() {
        View inflate = View.inflate(this, R.layout.layout_dev_list, null);
        ListView lv = inflate.findViewById(R.id.lv_dev_list);
        mAdapterByFoundMac = new ArrayAdapter<String>(this, R.layout.layout_dev_address, R.id.ctv_dev_address, mListByFoundMac) {
            @NonNull
            @Override
            public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View getView = super.getView(position, convertView, parent);
                CheckedTextView ctv = getView.findViewById(R.id.ctv_dev_address);
                ctv.setOnClickListener(view -> {
                    CheckedTextView ct = (CheckedTextView) view;
                    ct.toggle();
                    lv.setItemChecked(position, ct.isChecked());
                });
                return getView;
            }
        };
        lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        lv.setAdapter(mAdapterByFoundMac);

        Dialog selectDialog = new AlertDialog.Builder(MainActivity.this, THEME_HOLO_LIGHT)
                .setTitle("选择设备")
                .setView(inflate)
                .setCancelable(false)
                .setPositiveButton("确定", (dialog, which) -> {
                    SparseBooleanArray selected_array = lv.getCheckedItemPositions();
                    for (int i = 0; i < mListByFoundMac.size(); i++) {
                        if (selected_array.get(i)) {
                            addDeviceByMac(mListByFoundMac.get(i));
                            setMac(mListByFoundMac.get(i));
                        }
                    }
                    mAdapterByFoundMac = null;
                })
                .create();
        selectDialog.show();
    }

    public void connectCar(String mac) {
        addDeviceByMac(mac);
    }

    public void disconnectCar(String mac) {
        removeDeviceByMac(mac);
    }

    private String mMac;

    public String getMac() {
        return mMac;
    }

    public void setMac(String mac) {
        this.mMac = mac;
    }

}
