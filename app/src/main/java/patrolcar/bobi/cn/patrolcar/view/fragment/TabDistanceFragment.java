package patrolcar.bobi.cn.patrolcar.view.fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import patrolcar.bobi.cn.patrolcar.R;
import patrolcar.bobi.cn.patrolcar.model.DistanceBean;
import patrolcar.bobi.cn.patrolcar.view.adapter.TabDistanceAdapter;
import patrolcar.bobi.cn.patrolcar.view.base.BaseFragment;

/**
 * 距离模块
 */

public class TabDistanceFragment extends BaseFragment {

    @BindView(R.id.rv_distance)    RecyclerView rvDistance;

    private List<DistanceBean> mList = new ArrayList<>();


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
        initDistance();
        initRecycler();
    }

    private void initDistance() {
        mList.add(new DistanceBean("7", "160", "160", "3254", "0", "-", "-", "-", "-"));
        mList.add(new DistanceBean("12", "160", "1417", "0", "-", "-", "-", "-", "-"));
        mList.add(new DistanceBean("11", "160", "960", "0", "-", "-", "-", "-", "-"));
        mList.add(new DistanceBean("3", "160", "1024", "741", "666", "1741", "-", "-", "-"));
    }

    private void initRecycler() {
        LinearLayoutManager manager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL,false);
        rvDistance.setLayoutManager(manager);
        TabDistanceAdapter adapter = new TabDistanceAdapter(mList);
        rvDistance.setAdapter(adapter);
    }

}
