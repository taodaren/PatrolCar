package patrolcar.bobi.cn.patrolcar.model;

import patrolcar.bobi.cn.blelib.data.BleDevice;

public class BleDeviceEvent {
    private BleDevice bleDevice;
    private String isSuccess;

    public BleDeviceEvent(BleDevice bleDevice, String isSuccess) {
        this.bleDevice = bleDevice;
        this.isSuccess = isSuccess;
    }

    public BleDevice getBleDevice() {
        return bleDevice;
    }

    public void setBleDevice(BleDevice bleDevice) {
        this.bleDevice = bleDevice;
    }

    public String getIsSuccess() {
        return isSuccess;
    }

    public void setIsSuccess(String isSuccess) {
        this.isSuccess = isSuccess;
    }
}
