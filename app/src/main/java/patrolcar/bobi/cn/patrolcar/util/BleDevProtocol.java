package patrolcar.bobi.cn.patrolcar.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.greenrobot.eventbus.EventBus;

import java.security.SecureRandom;

import patrolcar.bobi.cn.patrolcar.model.DealWithPkgEvent;

/**
 * 蓝牙设备协议处理
 */

public class BleDevProtocol {
    private final static String TAG = "BleDevProtocol";
    private final static int MAX_PKG_LEN = 0x80;
    private final static int PKG_LEN_STATUS = 0x2e;
    private final static int CMD_GET_STATUS = 1;

    private final static int HEADER_LEN = 7;
    private int mPkgLen;
    private int nRevCnt;
    private byte[] mPkgStatus = new byte[PKG_LEN_STATUS];

    /** 接收 */
    public void bleReceive(byte[] data) {
        for (byte b : data) {
            onByte(b);
        }
    }

    /** 转义判断 */
    private void onByte(byte b) {
        if (nRevCnt == 0) {
            if (b == (byte) 0XA5) {
                mPkgStatus[0] = b;
                nRevCnt = 1;
            }
        } else if (nRevCnt == 1) {
            mPkgLen = b;
            mPkgStatus[nRevCnt] = b;
            nRevCnt = 2;
        } else {
            mPkgStatus[nRevCnt] = b;
            nRevCnt++;
            if (nRevCnt >= mPkgLen) {
                nRevCnt = 0;
                EventBus.getDefault().post(new DealWithPkgEvent(mPkgStatus));
            }
        }
    }

    private int type;                             // 报文加密位置类型
    private int dataLen;                          // 数据长度
    private final byte[] S = new byte[6];         // 随机数长度
    private int niS;                              // 随机数个数
    private final static int[] K = new int[]{     // 密码本
            30, 19, 3, 37, 17, 16, 35, 36, 16, 37, 22, 4, 42, 15, 21, 14, 29, 45, 20, 41, 38, 13,
            12, 5, 5, 40, 8, 1, 34, 7, 8, 2, 19, 13, 31, 27, 0, 20, 28, 24, 38, 36, 23, 10, 1, 14,
            43, 33, 13, 16, 15, 3, 37, 4, 25, 6, 40, 12, 5, 42, 25, 3, 31, 29, 18, 25, 39, 30, 24,
            47, 11, 47, 34, 22, 46, 8, 44, 39, 44, 21, 27, 9, 15, 2, 11, 28, 6, 19, 41, 21, 46, 45,
            39, 44, 31, 23, 18, 34, 33, 9, 24, 36, 23, 30, 17, 41, 22, 26, 9, 32, 6, 4, 26, 14, 18,
            40, 38, 11, 17, 12, 7, 35, 32, 27, 32, 2, 10, 0, 20, 0, 35, 45, 26, 1, 47, 33, 28, 46,
            42, 10, 43, 43, 29, 7
    };

    private static byte map(byte[] S, int i, byte d) {
        int m = 0;
        for (int j = 0; j < 8; j++) {
            int k = K[(i * 8 + j) % K.length];
            m |= ((((int) S[k >> 3] & (1 << (k & 7))) != 0) ? 1 : 0) << j;
        }
        return (byte) (((int) d & 0xff) ^ m);
    }


    /** 命令包 */
    @NonNull
    public static byte[] cmdPkg(int head, int cmd, @Nullable byte[] data, int b_crc) {
        final int dataLen = (data == null) ? 0 : data.length;
        final byte[] pkg = new byte[5 + dataLen];
        pkg[0] = (byte) head;
        pkg[1] = (byte) (dataLen + 5);
        pkg[2] = (byte) cmd;
        if (data != null) {
            System.arraycopy(data, 0, pkg, 3, dataLen);
        }
        int crc = CRC16.calculate(b_crc, pkg, (dataLen + 5 - 2));
        pkg[dataLen + 5 - 2] = (byte) (crc & 0xff);
        pkg[dataLen + 5 - 1] = (byte) ((crc >> 8) & 0xff);
        return pkg;
    }

    // 提供加密强随机数生成器
    private static final SecureRandom seedGen = new SecureRandom();

    /** 加密包 */
    @NonNull
    private static byte[] encryptPkg(@NonNull byte[] d) {
        final byte[] pkg = new byte[8 + d.length];
        final byte[] S = new byte[6];
        seedGen.nextBytes(S);
        pkg[0] = (byte) 0xDC;
        pkg[1] = (byte) d.length;
        System.arraycopy(S, 0, pkg, 2, S.length);
        for (int i = 0; i < d.length; i++) {
            pkg[8 + i] = map(S, i, d[i]);
        }
        return pkg;
    }

    private final static byte TRANSLATE_LEAD = (byte) 0xFE;

    @NonNull
    private static byte[] translate(@NonNull byte[] d) {
        int c = 0;
        for (int i = 1; i < d.length; i++) {
            if (shouldTranslate(d[i])) {
                c++;
            }
        }

        if (c > 0) {
            final byte[] pkg = new byte[d.length + c];
            pkg[0] = d[0];
            int j = 1;
            for (int i = 1; i < d.length; i++) {
                if (shouldTranslate(d[i])) {
                    pkg[j++] = TRANSLATE_LEAD;
                    pkg[j++] = (byte) ((int) d[i] ^ 0xff);
                } else {
                    pkg[j++] = d[i];
                }
            }
            return pkg;
        } else {
            return d;
        }
    }

    private static boolean shouldTranslate(byte b) {
        return b == (byte) 0xCD || b == (byte) 0xDC || b == TRANSLATE_LEAD;
    }

    @NonNull
    public static byte[] wrappedPackage(@NonNull byte[] data) {
        return translate(encryptPkg(data));
    }

}
