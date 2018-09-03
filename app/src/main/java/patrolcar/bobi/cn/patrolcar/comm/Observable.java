package patrolcar.bobi.cn.patrolcar.comm;


import patrolcar.bobi.cn.blelib.data.BleDevice;

public interface Observable {

    void addObserver(Observer obj);

    void deleteObserver(Observer obj);

    void notifyObserver(BleDevice bleDevice);

}
