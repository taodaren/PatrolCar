package patrolcar.bobi.cn.patrolcar.model;

public class DealWithPkgEvent {
    private byte[] pkgInfo;

    public DealWithPkgEvent(byte[] pkgInfo) {
        this.pkgInfo = pkgInfo;
    }

    public byte[] getPkgInfo() {
        return pkgInfo;
    }

    public void setPkgInfo(byte[] pkgInfo) {
        this.pkgInfo = pkgInfo;
    }
}
