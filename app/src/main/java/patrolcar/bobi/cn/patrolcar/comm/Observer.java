package patrolcar.bobi.cn.patrolcar.comm;

import patrolcar.bobi.cn.blelib.data.BleDevice;

public interface Observer {

    void disConnected(BleDevice bleDevice);

}
