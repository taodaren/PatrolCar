package patrolcar.bobi.cn.patrolcar;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * 状态模块适配器
 */

public class TabStatusAdapter extends RecyclerView.Adapter<TabStatusAdapter.StatusViewHolder> {
    private List<StatusBean> mList;

    static class StatusViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvValue, tvUnit;

        StatusViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_status_name);
            tvValue = itemView.findViewById(R.id.tv_status_value);
            tvUnit = itemView.findViewById(R.id.tv_status_unit);
        }
    }

    TabStatusAdapter(List<StatusBean> list) {
        this.mList = list;
    }

    @NonNull
    @Override
    public TabStatusAdapter.StatusViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
        return new StatusViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_status_robot, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TabStatusAdapter.StatusViewHolder holder, int position) {
        StatusBean bean = mList.get(position);
        holder.tvName.setText(bean.getName());
        holder.tvValue.setText(bean.getValue());
        holder.tvUnit.setText(bean.getUnit());
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }
}
