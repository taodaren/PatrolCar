package patrolcar.bobi.cn.patrolcar;

import android.view.View;

/**
 * 我的模块
 */

public class TabMineFragment extends BaseFragment {

    public static TabMineFragment newInstance() {
        return new TabMineFragment();
    }

    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_tab_mine;
    }

    @Override
    public void initToolbar() {
        setToolbar(R.id.main_toolbar, R.string.text_tab_mine, View.VISIBLE);
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
