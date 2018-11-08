package patrolcar.bobi.cn.patrolcar.util;

import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Locale;

/**
 * 工具类
 */

public class Util {
    public static String hex(byte[] data, int len) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len && i < data.length; i++) {
            sb.append(String.format(Locale.US, "%02X ", (int) data[i] & 0xff));
        }
        return sb.toString();
    }

    public static byte[] int2byte(int[] d) {
        byte[] r = new byte[d.length];
        for (int i = 0; i < d.length; i++) {
            r[i] = (byte) d[i];
        }
        return r;
    }

    public static String byteToString(byte bt) {
        char strHex[] = new char[2];
        strHex[0] = (char) ((bt >> 4) & 0X0F);
        if (strHex[0] < 10) {
            strHex[0] += '0';
        } else {
            strHex[0] += 'A' - 10;
        }
        strHex[1] = (char) ((bt) & 0X0F);
        if (strHex[1] < 10) {
            strHex[1] += '0';
        } else {
            strHex[1] += 'A' - 10;
        }
        return String.valueOf(strHex);
    }

    public static byte[] bytesGetSub(byte[] bts,int beginId,int cnt) {
        if ((beginId < 0) || (cnt <= 0) || beginId >= bts.length) {
            return null;
        }
        if ((beginId + cnt) > bts.length) {
            return null;
        }
        byte rt[] = new byte[cnt];
        System.arraycopy(bts, beginId, rt, 0, cnt);
        return rt;
    }

    public static String bytesToString(byte[] bts, int cnt) {
        if (bts.length < cnt) {
            cnt = bts.length;
        }
        char strHex[] = new char[cnt * 3 - 1];
        for (int i = 0; i < cnt; i++) {
            byte bt = bts[i];
            int j = i * 3;
            strHex[j] = (char) ((bt >> 4) & 0X0F);
            if (strHex[j] < 10) {
                strHex[j] += '0';
            } else {
                strHex[j] += 'A' - 10;
            }
            j++;
            strHex[j] = (char) ((bt) & 0X0F);
            if (strHex[j] < 10) {
                strHex[j] += '0';
            } else {
                strHex[j] += 'A' - 10;
            }
            j++;
            if (j < strHex.length) {
                strHex[j] = ' ';
            } else {
                break;
            }
        }
        return String.valueOf(strHex);
    }

    /**
     * 将 4 字节形式的 float 数据转换成 float
     * 先确定byte形式的数据顺序（大端模式/小端模式）
     * 将byte数据封装为ByteArrayInputStream 类型
     * 再将ByteArrayInputStream 封装为DataInputStream 类型
     * 调用DataInputStream 的readFloat方法获得float数据
     */
    public static float bytesToFloat(byte bigLow, byte bigHigh, byte smallHigh, byte smallLow) {
        // float 类型值为 123.456 以大端模式存储数据即高字节存于低地址，低字节存于高地址，小端模式反之
        byte bytes[] = {bigLow, bigHigh, smallHigh, smallLow};
        // 创建一个 ByteArrayInputStream，使用 bytes 作为其缓冲区数组
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        // 再将 bais 封装为 DataInputStream 类型
        DataInputStream dis = new DataInputStream(bais);
        float flt;
        try {
            flt = dis.readFloat();
            dis.close();
            return flt;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0f;
    }

    public static int byteTowToU16(byte valH,byte valL){
        int val = (int)(valH);
        val &= 0X0FF;
        val <<= 8;
        val  |= ((int)(valL))&0X0FF;
        return val;
    }
    public static int byteTowToS16(byte valH,byte valL){
        int val = (int)(valH);
        val <<= 8;
        val  |= ((int)(valL))&0X0FF;
        return val;
    }

}
