package patrolcar.bobi.cn.blelib.callback;


import patrolcar.bobi.cn.blelib.exception.BleException;

public abstract class BleWriteCallback extends BleBaseCallback {

    public abstract void onWriteSuccess(int current, int total, byte[] justWrite);

    public abstract void onWriteFailure(BleException exception);

}
