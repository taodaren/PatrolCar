package patrolcar.bobi.cn.patrolcar.util;

import android.util.Log;

import patrolcar.bobi.cn.patrolcar.view.activity.MainActivity;

public class BleCmdCtrl {
    private static final String TAG = "BleCmdCtrl";
    private static final int SEND_HEADER = 0XA5;          // 发送命令包头
    private static final int REPLY_HEADER = 0xCD;          // 应答报文包头

    private static final int CMD_UPLOAD_SENSOR_VALUE = 1;             // 上传传感器值
    private static final int CMD_MOTOR_CTRL = 2;             // 电机控制
    private static final int CMD_LIGHT_CTRL = 3;             // 车灯控制
    private static final int CMD_MOTOR_STATUS = 4;             // 上传电机板内部状态
    private static final int CMD_MOTOR_TURN = 5;             // 告诉电机板当前车轮转向

    private static final int CRC_UPLOAD_SENSOR_VALUE = 0x7A5B;        // 上传传感器值
    private static final int CRC_MOTOR_CTRL = 0x17AB;        // 电机控制
    private static final int CRC_LIGHT_CTRL = 0xC561;        // 车灯控制
    private static final int CRC_MOTOR_TURN = 0xA59C;        // 告诉电机板当前车轮转向

    private static byte[] pkgMotorCtrl(int pwrSwitch, int motorSwitch, int brakeSignal, int driveMotor,
                                       int aDriveMotor, int turnMotorTime, int turnMotorSpeed) {
        Log.i(TAG, "pkgMotorCtrl");
        byte[] motorCtrl = new byte[7];
        motorCtrl[0] = (byte) (pwrSwitch & 0XFF);
        motorCtrl[1] = (byte) (motorSwitch & 0XFF);
        motorCtrl[2] = (byte) (brakeSignal & 0XFF);
        motorCtrl[3] = (byte) (driveMotor & 0XFF);
        motorCtrl[4] = (byte) (aDriveMotor & 0XFF);
        motorCtrl[5] = (byte) (turnMotorTime & 0XFF);
        motorCtrl[6] = (byte) (turnMotorSpeed & 0XFF);
        return motorCtrl;
    }

    /**
     * 电机控制命令
     *
     * @param mac            设备 MAC 地址
     * @param pwrSwitch      电源总开关
     * @param motorSwitch    电机总开关
     * @param brakeSignal    刹车信号
     * @param driveMotor     驱动电机
     * @param acc            加速度
     * @param turnMotorTime  转向电机时间
     * @param turnMotorSpeed 转向电机速度
     */
    public static void sendCmdMotorCtrl(String mac, int pwrSwitch, int motorSwitch, int brakeSignal, int driveMotor, int acc, int turnMotorTime, int turnMotorSpeed) {
        byte[] bytes = pkgMotorCtrl(pwrSwitch, motorSwitch, brakeSignal, driveMotor, acc, turnMotorTime, turnMotorSpeed);
        MainActivity.getAppCtrl().sendCmd(mac, BleDevProtocol.cmdPkg(SEND_HEADER, CMD_MOTOR_CTRL, bytes, CRC_MOTOR_CTRL));
    }

}

