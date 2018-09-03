package patrolcar.bobi.cn.patrolcar.view.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;

import patrolcar.bobi.cn.blelib.BleManager;
import patrolcar.bobi.cn.blelib.data.BleDevice;
import patrolcar.bobi.cn.patrolcar.R;

public class DeviceAdapter extends BaseAdapter {

    private Context mContext;
    private List<BleDevice> mBleDevList;

    public DeviceAdapter(Context context) {
        this.mContext = context;
        this.mBleDevList = new ArrayList<>();
    }

    public void addDevice(BleDevice bleDevice) {
        removeDevice(bleDevice);
        mBleDevList.add(bleDevice);
    }

    public void removeDevice(BleDevice bleDevice) {
        for (int i = 0; i < mBleDevList.size(); i++) {
            BleDevice device = mBleDevList.get(i);
            if (bleDevice.getKey().equals(device.getKey())) {
                mBleDevList.remove(i);
            }
        }
    }

    public void clearConnectedDevice() {
        for (int i = 0; i < mBleDevList.size(); i++) {
            BleDevice device = mBleDevList.get(i);
            if (BleManager.getInstance().isConnected(device)) {
                mBleDevList.remove(i);
            }
        }
    }

    public void clearScanDevice() {
        for (int i = 0; i < mBleDevList.size(); i++) {
            BleDevice device = mBleDevList.get(i);
            if (!BleManager.getInstance().isConnected(device)) {
                mBleDevList.remove(i);
            }
        }
    }

    public void clear() {
        clearConnectedDevice();
        clearScanDevice();
    }

    @Override
    public int getCount() {
        return mBleDevList.size();
    }

    @Override
    public BleDevice getItem(int position) {
        if (position > mBleDevList.size())
            return null;
        return mBleDevList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            convertView = View.inflate(mContext, R.layout.adapter_device, null);
            holder = new ViewHolder();
            convertView.setTag(holder);
            holder.imgBlue = convertView.findViewById(R.id.img_blue);
            holder.txtName = convertView.findViewById(R.id.txt_name);
            holder.txtMac = convertView.findViewById(R.id.txt_mac);
            holder.txtRssi = convertView.findViewById(R.id.txt_rssi);
            holder.layoutIdle = convertView.findViewById(R.id.layout_idle);
            holder.layoutConnected = convertView.findViewById(R.id.layout_connected);
            holder.btnDisconnect = convertView.findViewById(R.id.btn_disconnect);
            holder.btnConnect = convertView.findViewById(R.id.btn_connect);
            holder.btnDetail = convertView.findViewById(R.id.btn_detail);
        }

        final BleDevice bleDevice = getItem(position);
        if (bleDevice != null) {
            boolean isConnected = BleManager.getInstance().isConnected(bleDevice);
            String name = bleDevice.getName();
            String mac = bleDevice.getMac();
            int rssi = bleDevice.getRssi();
            holder.txtName.setText(name);
            holder.txtMac.setText(mac);
            holder.txtRssi.setText(String.valueOf(rssi));
            if (isConnected) {
                holder.imgBlue.setImageResource(R.mipmap.ic_blue_connected);
                holder.txtName.setTextColor(mContext.getResources().getColor(R.color.colorPrimary));
                holder.txtMac.setTextColor(mContext.getResources().getColor(R.color.colorPrimary));
                holder.layoutIdle.setVisibility(View.GONE);
                holder.layoutConnected.setVisibility(View.VISIBLE);
            } else {
                holder.imgBlue.setImageResource(R.mipmap.ic_blue_remote);
                holder.txtName.setTextColor(mContext.getResources().getColor(R.color.colorBlack));
                holder.txtMac.setTextColor(mContext.getResources().getColor(R.color.colorBlack));
                holder.layoutIdle.setVisibility(View.VISIBLE);
                holder.layoutConnected.setVisibility(View.GONE);
            }
        }

        holder.btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onConnect(bleDevice);
                }
            }
        });

        holder.btnDisconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onDisConnect(bleDevice);
                }
            }
        });

        holder.btnDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onDetail(bleDevice);
                }
            }
        });

        return convertView;
    }

    class ViewHolder {
        ImageView imgBlue;
        TextView txtName, txtMac, txtRssi;
        LinearLayout layoutIdle, layoutConnected;
        Button btnDisconnect, btnConnect, btnDetail;
    }

    public interface OnDeviceClickListener {
        void onConnect(BleDevice bleDevice);

        void onDisConnect(BleDevice bleDevice);

        void onDetail(BleDevice bleDevice);
    }

    private OnDeviceClickListener mListener;

    public void setOnDeviceClickListener(OnDeviceClickListener listener) {
        this.mListener = listener;
    }

}
