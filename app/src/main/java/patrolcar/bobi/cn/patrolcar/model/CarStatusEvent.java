package patrolcar.bobi.cn.patrolcar.model;

public class CarStatusEvent {
    private byte[] pkgStatus;

    public CarStatusEvent(byte[] pkgStatus) {
        this.pkgStatus = pkgStatus;
    }

    public byte[] getPkgStatus() {
        return pkgStatus;
    }

    public void setPkgStatus(byte[] pkgStatus) {
        this.pkgStatus = pkgStatus;
    }
}
