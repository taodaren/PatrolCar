package patrolcar.bobi.cn.patrolcar.view.fragment;

import android.view.View;

import patrolcar.bobi.cn.patrolcar.view.base.BaseFragment;
import patrolcar.bobi.cn.patrolcar.R;

/**
 * 遥控模块
 */

public class TabControlFragment extends BaseFragment {

    public static TabControlFragment newInstance() {
        return new TabControlFragment();
    }

    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_tab_control;
    }

    @Override
    public void initToolbar() {
        setToolbar(R.id.main_toolbar, R.string.text_tab_control, View.VISIBLE);
    }

    @Override
    public void initView(View rootView) {

    }

    @Override
    public void initData() {

    }

}
