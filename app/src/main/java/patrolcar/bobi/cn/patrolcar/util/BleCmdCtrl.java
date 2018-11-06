package patrolcar.bobi.cn.patrolcar.util;

import android.util.Log;

import patrolcar.bobi.cn.patrolcar.view.activity.MainActivity;

import static patrolcar.bobi.cn.patrolcar.app.AppConstant.TYPE_MOTOR_CTRL_BRAKE_OFF;
import static patrolcar.bobi.cn.patrolcar.app.AppConstant.TYPE_MOTOR_CTRL_BRAKE_ON;
import static patrolcar.bobi.cn.patrolcar.app.AppConstant.TYPE_MOTOR_CTRL_DEV_OFF;
import static patrolcar.bobi.cn.patrolcar.app.AppConstant.TYPE_MOTOR_CTRL_DEV_ON;
import static patrolcar.bobi.cn.patrolcar.app.AppConstant.TYPE_MOTOR_CTRL_DOWN;
import static patrolcar.bobi.cn.patrolcar.app.AppConstant.TYPE_MOTOR_CTRL_LEFT;
import static patrolcar.bobi.cn.patrolcar.app.AppConstant.TYPE_MOTOR_CTRL_MOTOR_OFF;
import static patrolcar.bobi.cn.patrolcar.app.AppConstant.TYPE_MOTOR_CTRL_MOTOR_ON;
import static patrolcar.bobi.cn.patrolcar.app.AppConstant.TYPE_MOTOR_CTRL_RIGHT;
import static patrolcar.bobi.cn.patrolcar.app.AppConstant.TYPE_MOTOR_CTRL_STOP;
import static patrolcar.bobi.cn.patrolcar.app.AppConstant.TYPE_MOTOR_CTRL_UP;

public class BleCmdCtrl {
    private static final String TAG = "BleCmdCtrl";
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

    private static byte[] pkgMotorCtrl(int pwrSwitch, int motorSwitch, int brakeSignal, int driveMotor,
                                       int aDriveMotor, int turnMotorTime, int turnMotorSpeed) {
        byte[] motorCtrl = new byte[7];
        motorCtrl[0] = (byte) pwrSwitch;
        motorCtrl[1] = (byte) motorSwitch;
        motorCtrl[2] = (byte) brakeSignal;
        motorCtrl[3] = (byte) driveMotor;
        motorCtrl[4] = (byte) aDriveMotor;
        motorCtrl[5] = (byte) turnMotorTime;
        motorCtrl[6] = (byte) turnMotorSpeed;
        return motorCtrl;
    }

    public static void sendCmdMotorCtrl(final String mac, String type) {
        Log.i(TAG, "sendCmdMotorCtrl1: ");
        int pwrSwitch = 0, motorSwitch = 0, brakeSignal = 0, driveMotor = 0, aDriveMotor = 0, turnMotorTime = 0, turnMotorSpeed = 0;
        switch (type) {
            case TYPE_MOTOR_CTRL_STOP:
                break;
            case TYPE_MOTOR_CTRL_UP:
                driveMotor = 50;
                aDriveMotor = 5;
                break;
            case TYPE_MOTOR_CTRL_DOWN:
                driveMotor = -50;
                aDriveMotor = 5;
                break;
            case TYPE_MOTOR_CTRL_LEFT:
                turnMotorTime = 10;
                turnMotorSpeed = -20;
                break;
            case TYPE_MOTOR_CTRL_RIGHT:
                turnMotorTime = 10;
                turnMotorSpeed = 20;
                break;
            case TYPE_MOTOR_CTRL_DEV_ON:
                pwrSwitch = 0X66;
                break;
            case TYPE_MOTOR_CTRL_DEV_OFF:
                pwrSwitch = 0X77;
                break;
            case TYPE_MOTOR_CTRL_BRAKE_ON:
                brakeSignal = 1;
                break;
            case TYPE_MOTOR_CTRL_BRAKE_OFF:
                brakeSignal = 2;
                break;
            case TYPE_MOTOR_CTRL_MOTOR_ON:
                motorSwitch = 0X88;
                break;
            case TYPE_MOTOR_CTRL_MOTOR_OFF:
                motorSwitch = 0X55;
                break;
        }

        byte[] bytes = pkgMotorCtrl(pwrSwitch, motorSwitch, brakeSignal, driveMotor, aDriveMotor, turnMotorTime, turnMotorSpeed);
        MainActivity.getAppCtrl().sendCmd(mac, BleDevProtocol.cmdPkg(SEND_HEADER, CMD_MOTOR_CTRL, bytes, CRC_MOTOR_CTRL));
    }

    public static void sendCmdMotorCtrl(final String mac, String type, int para1, int para2) {
        int pwrSwitch = 0, motorSwitch = 0, brakeSignal = 0, driveMotor = 0, aDriveMotor = 0, turnMotorTime = 0, turnMotorSpeed = 0;
        switch (type) {
            case TYPE_MOTOR_CTRL_STOP:
                break;
            case TYPE_MOTOR_CTRL_UP:
                driveMotor = para1;
                aDriveMotor = para2;
                break;
            case TYPE_MOTOR_CTRL_DOWN:
                driveMotor = para1;
                aDriveMotor = para2;
                break;
            case TYPE_MOTOR_CTRL_LEFT:
                turnMotorTime = para1;
                turnMotorSpeed = para2;
                break;
            case TYPE_MOTOR_CTRL_RIGHT:
                turnMotorTime = para1;
                turnMotorSpeed = para2;
                break;
            case TYPE_MOTOR_CTRL_DEV_ON:
                pwrSwitch = para1;
                break;
            case TYPE_MOTOR_CTRL_DEV_OFF:
                pwrSwitch = para1;
                break;
            case TYPE_MOTOR_CTRL_BRAKE_ON:
                brakeSignal = para1;
                break;
            case TYPE_MOTOR_CTRL_BRAKE_OFF:
                brakeSignal = para1;
                break;
            case TYPE_MOTOR_CTRL_MOTOR_ON:
                motorSwitch = para1;
                break;
            case TYPE_MOTOR_CTRL_MOTOR_OFF:
                motorSwitch = para1;
                break;
        }


        byte[] bytes = pkgMotorCtrl(pwrSwitch, motorSwitch, brakeSignal, driveMotor, aDriveMotor, turnMotorTime, turnMotorSpeed);
        MainActivity.getAppCtrl().sendCmd(mac, BleDevProtocol.cmdPkg(SEND_HEADER, CMD_MOTOR_CTRL, bytes, CRC_MOTOR_CTRL));
    }

}

