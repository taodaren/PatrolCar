package patrolcar.bobi.cn.patrolcar.view.fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import patrolcar.bobi.cn.patrolcar.R;
import patrolcar.bobi.cn.patrolcar.model.StatusBean;
import patrolcar.bobi.cn.patrolcar.view.adapter.TabStatusAdapter;
import patrolcar.bobi.cn.patrolcar.view.base.BaseFragment;

/**
 * 状态模块
 */

public class TabStatusFragment extends BaseFragment {

    @BindView(R.id.rv_robot_status)    RecyclerView rvRobotStatus;

    private List<StatusBean> mList = new ArrayList<>();

    public static TabStatusFragment newInstance() {
        return new TabStatusFragment();
    }

    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_tab_status;
    }

    @Override
    public void initView(View rootView) {
        initStatus();
        initRecyclerView();
    }

    public void initStatus() {
        mList.add(new StatusBean("加速度X", "8", "m/s2"));
        mList.add(new StatusBean("加速度Y", "167", "m/s2"));
        mList.add(new StatusBean("加速度Z", "1998", "m/s2"));
        mList.add(new StatusBean("方位X", "866", "°"));
        mList.add(new StatusBean("方位Y", "-43", "°"));
        mList.add(new StatusBean("方位Z", "13566", "°"));
        mList.add(new StatusBean("磁场X", "454", "°"));
        mList.add(new StatusBean("磁场Y", "46", "°"));
        mList.add(new StatusBean("磁场Z", "-983", "°"));
        mList.add(new StatusBean("电路板温度", "28", "℃"));
        mList.add(new StatusBean("电路板湿度", "24", "％"));
        mList.add(new StatusBean("GPS经度", "-10.00", "°"));
        mList.add(new StatusBean("GPS纬度", "-10.00", "°"));
        mList.add(new StatusBean("GPS高度", "0.00", "m"));
        mList.add(new StatusBean("转向方向", "88", "°"));
        mList.add(new StatusBean("电机速度1", "0", "个"));
        mList.add(new StatusBean("电机速度2", "0", "个"));
        mList.add(new StatusBean("电机速度3", "0", "个"));
        mList.add(new StatusBean("电机速度4", "0", "个"));
        mList.add(new StatusBean("电池电压值", "48.50", "V"));
        mList.add(new StatusBean("急停检测", "0", ""));
    }

    private void initRecyclerView() {
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        rvRobotStatus.setLayoutManager(manager);
        TabStatusAdapter adapter = new TabStatusAdapter(mList);
        rvRobotStatus.setAdapter(adapter);
    }

}
