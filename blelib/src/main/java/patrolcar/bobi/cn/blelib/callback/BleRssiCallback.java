package patrolcar.bobi.cn.blelib.callback;

import patrolcar.bobi.cn.blelib.exception.BleException;

public abstract class BleRssiCallback extends BleBaseCallback {

    public abstract void onRssiFailure(BleException exception);

    public abstract void onRssiSuccess(int rssi);

}