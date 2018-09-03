package patrolcar.bobi.cn.blelib.callback;

import patrolcar.bobi.cn.blelib.data.BleDevice;

public interface BleScanPresenterImp {

    void onScanStarted(boolean success);

    void onScanning(BleDevice bleDevice);

}
