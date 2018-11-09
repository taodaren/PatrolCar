package patrolcar.bobi.cn.patrolcar.view.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import patrolcar.bobi.cn.patrolcar.R;
import patrolcar.bobi.cn.patrolcar.model.DistanceBean;

public class TabDistanceAdapter extends RecyclerView.Adapter<TabDistanceAdapter.ViewHolder> {
    private List<DistanceBean> mList;

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_distance_did)         TextView tvDid;
        @BindView(R.id.tv_distance_dis1)        TextView tvDis1;
        @BindView(R.id.tv_distance_dis2)        TextView tvDis2;
        @BindView(R.id.tv_distance_dis3)        TextView tvDis3;
        @BindView(R.id.tv_distance_dis4)        TextView tvDis4;
        @BindView(R.id.tv_distance_dis5)        TextView tvDis5;
        @BindView(R.id.tv_distance_dis6)        TextView tvDis6;
        @BindView(R.id.tv_distance_dis7)        TextView tvDis7;
        @BindView(R.id.tv_distance_dis8)        TextView tvDis8;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public TabDistanceAdapter(List<DistanceBean> list) {
        this.mList = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_distance, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DistanceBean bean = mList.get(position);
        holder.tvDid.setText(bean.getDid());
        try {
            holder.tvDis1.setText(bean.getDisArr()[0]);
            holder.tvDis2.setText(bean.getDisArr()[1]);
            holder.tvDis3.setText(bean.getDisArr()[2]);
            holder.tvDis4.setText(bean.getDisArr()[3]);
            holder.tvDis5.setText(bean.getDisArr()[4]);
            holder.tvDis6.setText(bean.getDisArr()[5]);
            holder.tvDis7.setText(bean.getDisArr()[6]);
            holder.tvDis8.setText(bean.getDisArr()[7]);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }
}
