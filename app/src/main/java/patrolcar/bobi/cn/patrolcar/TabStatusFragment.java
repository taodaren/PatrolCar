package patrolcar.bobi.cn.patrolcar;

import android.view.View;

/**
 * 状态模块
 */

public class TabStatusFragment extends BaseFragment {

    public static TabStatusFragment newInstance() {
        return new TabStatusFragment();
    }

    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_tab_status;
    }

    @Override
    public void initToolbar() {
        setToolbar(R.id.main_toolbar, R.string.txt_tab_device, View.VISIBLE);
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
