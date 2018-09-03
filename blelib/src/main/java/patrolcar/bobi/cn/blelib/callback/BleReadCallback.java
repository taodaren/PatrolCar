package patrolcar.bobi.cn.blelib.callback;

import patrolcar.bobi.cn.blelib.exception.BleException;

public abstract class BleReadCallback extends BleBaseCallback {

    public abstract void onReadSuccess(byte[] data);

    public abstract void onReadFailure(BleException exception);

}
