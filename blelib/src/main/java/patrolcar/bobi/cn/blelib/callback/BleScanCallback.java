package patrolcar.bobi.cn.blelib.callback;

import java.util.List;

import patrolcar.bobi.cn.blelib.data.BleDevice;

public abstract class BleScanCallback implements BleScanPresenterImp {

    public abstract void onScanFinished(List<BleDevice> scanResultList);

    public void onLeScan(BleDevice bleDevice) {
    }

}
