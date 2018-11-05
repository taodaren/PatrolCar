package patrolcar.bobi.cn.patrolcar.util;

import patrolcar.bobi.cn.patrolcar.view.activity.MainActivity;

public class BleCmdCtrl {
    private static final int SEND_HEADER             = 0XA5;          // 发送命令包头
    private static final int REPLY_HEADER            = 0xCD;          // 应答报文包头

    private static final int CMD_UPLOAD_SENSOR_VALUE = 1;             // 上传传感器值
    private static final int CMD_MOTOR_CTRL          = 2;             // 电机控制
    private static final int CMD_LIGHT_CTRL          = 3;             // 车灯控制
    private static final int CMD_MOTOR_STATUS        = 4;             // 上传电机板内部状态
    private static final int CMD_MOTOR_TURN          = 5;             // 告诉电机板当前车轮转向

    private static final int CRC_UPLOAD_SENSOR_VALUE = 0x7A5B;        // 上传传感器值
    private static final int CRC_MOTOR_CTRL          = 0x17AB;        // 电机控制
    private static final int CRC_LIGHT_CTRL          = 0xC561;        // 车灯控制
    private static final int CRC_MOTOR_TURN          = 0xA59C;        // 告诉电机板当前车轮转向

    private static byte[] pkgMotorCtrl(byte pwrSwitch, byte motorSwitch, byte brakeSignal, byte driveMotor,
                                       byte aDriveMotor, byte turnMotorTime, byte turnMotorSpeed) {
        byte[] motorCtrl = new byte[7];
        motorCtrl[0] = pwrSwitch;
        motorCtrl[1] = motorSwitch;
        motorCtrl[2] = brakeSignal;
        motorCtrl[3] = driveMotor;
        motorCtrl[4] = aDriveMotor;
        motorCtrl[5] = turnMotorTime;
        motorCtrl[6] = turnMotorSpeed;
        return motorCtrl;
    }

    public static void sendCmdBrakeStart(final String mac) {
        byte[] bytes = pkgMotorCtrl((byte) 0, (byte) 0, (byte) 1, (byte) 0, (byte) 0, (byte) 0, (byte) 0);
        MainActivity.getAppCtrl().sendCmd(mac, BleDevProtocol.cmdPkg(SEND_HEADER, CMD_MOTOR_CTRL, bytes, CRC_MOTOR_CTRL));
    }

    public static void sendCmdBrakeRelease(final String mac) {
        byte[] bytes = pkgMotorCtrl((byte) 0, (byte) 0, (byte) 2, (byte) 0, (byte) 0, (byte) 0, (byte) 0);
        MainActivity.getAppCtrl().sendCmd(mac, BleDevProtocol.cmdPkg(SEND_HEADER, CMD_MOTOR_CTRL, bytes, CRC_MOTOR_CTRL));
    }

    public static void sendCmdStop(final String mac) {
        byte[] bytes = pkgMotorCtrl((byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0);
        MainActivity.getAppCtrl().sendCmd(mac, BleDevProtocol.cmdPkg(SEND_HEADER, CMD_MOTOR_CTRL, bytes, CRC_MOTOR_CTRL));
    }

    public static void sendCmdUp(final String mac) {
        byte[] bytes = pkgMotorCtrl((byte) 0, (byte) 0, (byte) 0, (byte) 50, (byte) 5, (byte) 0, (byte) 0);
        MainActivity.getAppCtrl().sendCmd(mac, BleDevProtocol.cmdPkg(SEND_HEADER, CMD_MOTOR_CTRL, bytes, CRC_MOTOR_CTRL));
    }

    public static void sendCmdDown(final String mac) {
        byte[] bytes = pkgMotorCtrl((byte) 0, (byte) 0, (byte) 0, (byte) -50, (byte) 5, (byte) 0, (byte) 0);
        MainActivity.getAppCtrl().sendCmd(mac, BleDevProtocol.cmdPkg(SEND_HEADER, CMD_MOTOR_CTRL, bytes, CRC_MOTOR_CTRL));
    }

    public static void sendCmdLeft(final String mac) {
        byte[] bytes = pkgMotorCtrl((byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 5, (byte) 10, (byte) -20);
        MainActivity.getAppCtrl().sendCmd(mac, BleDevProtocol.cmdPkg(SEND_HEADER, CMD_MOTOR_CTRL, bytes, CRC_MOTOR_CTRL));
    }

    public static void sendCmdRight(final String mac) {
        byte[] bytes = pkgMotorCtrl((byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 5, (byte) 10, (byte) 20);
        MainActivity.getAppCtrl().sendCmd(mac, BleDevProtocol.cmdPkg(SEND_HEADER, CMD_MOTOR_CTRL, bytes, CRC_MOTOR_CTRL));
    }

    public static void sendCmdOpenDevice(final String mac) {
        byte[] bytes = pkgMotorCtrl((byte) 0X66, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0);
        MainActivity.getAppCtrl().sendCmd(mac, BleDevProtocol.cmdPkg(SEND_HEADER, CMD_MOTOR_CTRL, bytes, CRC_MOTOR_CTRL));
    }

    public static void sendCmdCloseDevice(final String mac) {
        byte[] bytes = pkgMotorCtrl((byte) 0X77, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0);
        MainActivity.getAppCtrl().sendCmd(mac, BleDevProtocol.cmdPkg(SEND_HEADER, CMD_MOTOR_CTRL, bytes, CRC_MOTOR_CTRL));
    }

    public static void sendCmdOpenMotor(final String mac) {
        byte[] bytes = pkgMotorCtrl((byte) 0, (byte) 0X88, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0);
        MainActivity.getAppCtrl().sendCmd(mac, BleDevProtocol.cmdPkg(SEND_HEADER, CMD_MOTOR_CTRL, bytes, CRC_MOTOR_CTRL));
    }

    public static void sendCmdCloseMotor(final String mac) {
        byte[] bytes = pkgMotorCtrl((byte) 0, (byte) 0X55, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0);
        MainActivity.getAppCtrl().sendCmd(mac, BleDevProtocol.cmdPkg(SEND_HEADER, CMD_MOTOR_CTRL, bytes, CRC_MOTOR_CTRL));
    }

}
