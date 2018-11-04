package patrolcar.bobi.cn.patrolcar.view.fragment;

import patrolcar.bobi.cn.patrolcar.R;
import patrolcar.bobi.cn.patrolcar.view.base.BaseFragment;

/**
 * 测试视频模仿
 */

public class TabVideoFragment extends BaseFragment {

    public static TabVideoFragment newInstance() {
        return new TabVideoFragment();
    }

    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_tab_video;
    }

}
