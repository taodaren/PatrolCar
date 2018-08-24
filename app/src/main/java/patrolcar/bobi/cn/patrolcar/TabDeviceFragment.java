package patrolcar.bobi.cn.patrolcar;

import android.view.View;

/**
 * 设备模块
 */

public class TabDeviceFragment extends BaseFragment {

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

    }

    @Override
    public void initData() {

    }

    @Override
    public void initListener() {

    }
}
