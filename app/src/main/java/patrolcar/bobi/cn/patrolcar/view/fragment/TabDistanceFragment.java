package patrolcar.bobi.cn.patrolcar.view.fragment;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import patrolcar.bobi.cn.patrolcar.R;
import patrolcar.bobi.cn.patrolcar.model.DealWithPkgEvent;
import patrolcar.bobi.cn.patrolcar.model.DisValsDataType;
import patrolcar.bobi.cn.patrolcar.model.DistanceBean;
import patrolcar.bobi.cn.patrolcar.util.BleCmdCtrl;
import patrolcar.bobi.cn.patrolcar.util.LogUtil;
import patrolcar.bobi.cn.patrolcar.view.adapter.TabDistanceAdapter;
import patrolcar.bobi.cn.patrolcar.view.base.BaseFragment;

import static patrolcar.bobi.cn.patrolcar.model.DisValsDataType.DISTANCE_TYPE_MAX;

/**
 * 距离模块
 */

public class TabDistanceFragment extends BaseFragment {
    private static final String TAG = "TabDistanceFragment";
    private static final String MAC = "F7:4D:B8:21:A5:35";
    private static final int WHAT_DIS_REPLY = 1;

    @BindView(R.id.rv_distance)    RecyclerView rvDistance;

    private List<DistanceBean> mList = new ArrayList<>();
    private DisValsDataType gDisVals = new DisValsDataType();

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                BleCmdCtrl.sendCmdDisReply(MAC, 0x07);
                mHandler.sendEmptyMessageDelayed(WHAT_DIS_REPLY, 1000);
            }
        }
    };

    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_tab_distance;
    }

    public static TabDistanceFragment newInstance() {
        return new TabDistanceFragment();
    }

    @Override
    public void initToolbar() {
        setToolbar(R.id.main_toolbar, R.string.txt_tab_mine, View.VISIBLE);
    }

    @Override
    public void initView(View rootView) {
        mHandler.sendEmptyMessage(WHAT_DIS_REPLY);
        mList.add(new DistanceBean());
        initRecycler();
    }

    private void initRecycler() {
        LinearLayoutManager manager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL,false);
        rvDistance.setLayoutManager(manager);
        TabDistanceAdapter adapter = new TabDistanceAdapter(mList);
        rvDistance.setAdapter(adapter);
    }

    @Override
    public void onEventDealWithPkg(DealWithPkgEvent event) {
        super.onEventDealWithPkg(event);
        byte[] pkg = event.getPkgInfo();

        if (pkg[0] == (byte) 0xa5 && pkg[2] == (byte) 0x07) {
            gDisVals.disUsableCnt = 0;
            int dataLeft = pkg[1] - 5;
            byte position = 3;
            while (dataLeft > 2) {
                gDisVals.disVals[gDisVals.disUsableCnt].addr = pkg[position++];
                gDisVals.disVals[gDisVals.disUsableCnt].usableCnt = pkg[position++];
                if ((gDisVals.disVals[gDisVals.disUsableCnt].usableCnt <= 0) || (gDisVals.disVals[gDisVals.disUsableCnt].usableCnt > DISTANCE_TYPE_MAX)) {
                    LogUtil.e(TAG, "距离数据单个模块数量错误 ! " + gDisVals.disVals[gDisVals.disUsableCnt].usableCnt);
                    break;
                }
                gDisVals.disVals[gDisVals.disUsableCnt].dataCnt = ((gDisVals.disVals[gDisVals.disUsableCnt].usableCnt + 1) / 2 * 3 - (gDisVals.disVals[gDisVals.disUsableCnt].usableCnt & 0X01));
                System.arraycopy(pkg, position, gDisVals.disVals[gDisVals.disUsableCnt].disData, 0, gDisVals.disVals[gDisVals.disUsableCnt].dataCnt);
                position += gDisVals.disVals[gDisVals.disUsableCnt].dataCnt;
                dataLeft -= gDisVals.disVals[gDisVals.disUsableCnt].dataCnt + 2;
                gDisVals.disUsableCnt++;
            }
            DisValsDataTransDataToVals(gDisVals);
        }
    }

    /** 数据包分离 */
    private void DisValsDataTransDataToVals(DisValsDataType type) {
        mList.clear();
        for (int idCanPushData = 0; idCanPushData < type.disUsableCnt; idCanPushData++) {
            // 下面为距离数据，实际距离个数
            type.disVals[idCanPushData].usableCnt = (type.disVals[idCanPushData].dataCnt / 3) * 2 + ((type.disVals[idCanPushData].dataCnt % 3) == 2 ? 1 : 0);

            if (type.disVals[idCanPushData].dataCnt != ((type.disVals[idCanPushData].usableCnt + 1) / 2 * 3 - (type.disVals[idCanPushData].usableCnt & 0X01))) {
                type.disVals[idCanPushData].usableTime = 0;
            } else {
                type.disVals[idCanPushData].usableTime = 5;
            }
            for (int i = 0; i < type.disVals[idCanPushData].usableCnt; i++) {
                int valId = (i / 2) * 3;
                if (0 != (i & 0X01)) {
                    // 奇数
                    type.disVals[idCanPushData].disVals[i] = (type.disVals[idCanPushData].disData[valId + 1] & 0X0F) * 0X0100 + type.disVals[idCanPushData].disData[valId + 2];
                } else {
                    // 偶数
                    type.disVals[idCanPushData].disVals[i] = type.disVals[idCanPushData].disData[valId] * 0X0010 + (type.disVals[idCanPushData].disData[valId + 1] >> 4);
                }
            }
            updateDis(type.disVals[idCanPushData]);
        }
    }

    private void updateDis(DisValsDataType.OneDisValsData disVal) {
        DistanceBean bean = new DistanceBean();
        String[] strArr = new String[8];
        bean.setDid(String.valueOf(disVal.addr));
        for (int i = 0; i < disVal.usableCnt; i++) {
            strArr[i] = String.valueOf(disVal.disVals[i]);
        }
        bean.setDisArr(strArr);
        mList.add(bean);
        initRecycler();
    }

}
