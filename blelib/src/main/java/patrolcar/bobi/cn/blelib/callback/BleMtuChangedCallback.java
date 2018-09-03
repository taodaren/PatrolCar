package patrolcar.bobi.cn.blelib.callback;

import patrolcar.bobi.cn.blelib.exception.BleException;

public abstract class BleMtuChangedCallback extends BleBaseCallback {

    public abstract void onSetMTUFailure(BleException exception);

    public abstract void onMtuChanged(int mtu);

}
