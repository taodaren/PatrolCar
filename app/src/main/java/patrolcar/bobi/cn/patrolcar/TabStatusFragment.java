package patrolcar.bobi.cn.patrolcar;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * 状态模块
 */

public class TabStatusFragment extends BaseFragment {
    private static final String TAG = "TabStatusFragment";

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
    public void initToolbar() {
        setToolbar(R.id.main_toolbar, R.string.txt_tab_device, View.VISIBLE);
    }

    public void initStatus() {
        StatusBean accx             =    new StatusBean    ("accx", "8", "m/s2");
        StatusBean accy             =    new StatusBean    ("accy", "167", "m/s2");
        StatusBean accz             =    new StatusBean    ("accz", "1998", "m/s2");
        StatusBean directionX       =    new StatusBean    ("方位x", "866", "°");
        StatusBean directionY       =    new StatusBean    ("方位y", "-43", "°");
        StatusBean directionZ       =    new StatusBean    ("方位z", "13566", "°");
        StatusBean ccX              =    new StatusBean    ("磁场x", "454", "°");
        StatusBean ccY              =    new StatusBean    ("磁场y", "46", "°");
        StatusBean ccZ              =    new StatusBean    ("磁场z", "-983", "°");
        StatusBean temperature      =    new StatusBean    ("温度",  "28", "℃");
        StatusBean humidity         =    new StatusBean    ("湿度",  "24", "％");
        StatusBean longitude        =    new StatusBean    ("经度",  "-10.00", "°");
        StatusBean latitude         =    new StatusBean    ("纬度",  "-10.00", "°");
        StatusBean height           =    new StatusBean    ("高度",  "0.00", "m");
        StatusBean changeDirection  =    new StatusBean    ("转向",  "88", "°");
        StatusBean speed1           =    new StatusBean    ("速度1", "0", "个");
        StatusBean speed2           =    new StatusBean    ("速度2", "0", "个");
        StatusBean speed3           =    new StatusBean    ("速度3", "0", "个");
        StatusBean speed4           =    new StatusBean    ("速度4", "0", "个");
        StatusBean battery          =    new StatusBean    ("电池",  "48.50", "V");
        StatusBean eStop            =    new StatusBean    ("急停", "0", "");

        mList.add(accx);              mList.add(accy);              mList.add(accz);
        mList.add(directionX);        mList.add(directionY);        mList.add(directionZ);
        mList.add(ccX);               mList.add(ccY);               mList.add(ccZ);
        mList.add(temperature);       mList.add(humidity);
        mList.add(longitude);         mList.add(latitude);
        mList.add(height);
        mList.add(changeDirection);
        mList.add(speed1);            mList.add(speed2);            mList.add(speed3);            mList.add(speed4);
        mList.add(battery);
        mList.add(eStop);
    }

    @Override
    public void initView(View rootView) {
        initStatus();
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        rvRobotStatus.setLayoutManager(manager);
        TabStatusAdapter adapter = new TabStatusAdapter(mList);
        rvRobotStatus.setAdapter(adapter);
    }

}
