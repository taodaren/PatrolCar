package patrolcar.bobi.cn.patrolcar;

import android.view.View;

/**
 * 距离模块
 */

public class TabDistanceFragment extends BaseFragment {

    public static TabDistanceFragment newInstance() {
        return new TabDistanceFragment();
    }

    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_tab_distance;
    }

    @Override
    public void initToolbar() {
        setToolbar(R.id.main_toolbar, R.string.txt_tab_mine, View.VISIBLE);
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
