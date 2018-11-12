package patrolcar.bobi.cn.patrolcar.util;

import patrolcar.bobi.cn.patrolcar.view.activity.MainActivity;

public class BleCmdCtrl {
    private static final String TAG = "BleCmdCtrl";
    private static final int SEND_HEADER             = 0XA5;          // 发送命令包头
    private static final int REPLY_HEADER            = 0xCD;          // 应答报文包头

    private static final int CMD_UPLOAD_SENSOR_VALUE = 1;             // 上传传感器值
    private static final int CMD_MOTOR_CTRL          = 2;             // 电机控制
    private static final int CMD_LIGHT_CTRL          = 3;             // 车灯控制
    private static final int CMD_MOTOR_STATUS        = 4;             // 上传电机板内部状态
    private static final int CMD_MOTOR_TURN          = 5;             // 告诉电机板当前车轮转向
    private static final int CMD_APP_TO_PC           = 9;             // APP 发送命令给工控机

    private static final int CRC_UPLOAD_SENSOR_VALUE = 0x7A5B;        // 上传传感器值
    private static final int CRC_MOTOR_CTRL          = 0x17AB;        // 电机控制
    private static final int CRC_LIGHT_CTRL          = 0xC561;        // 车灯控制
    private static final int CRC_MOTOR_TURN          = 0xA59C;        // 告诉电机板当前车轮转向
    private static final int CRC_APP_TO_PC           = 0X6EC5;        // APP 发送命令给工控机
    private static final int CRC_DIS_REPLY           = 0X85FC;        // 等待距离回复

    private static byte[] pkgMotorCtrl(int pwrSwitch, int motorSwitch, int brakeSignal, int driveMotor, int acc, int turnTime, int turnVelocity) {
        byte[] motorCtrl = new byte[7];
        motorCtrl[0] = (byte) (pwrSwitch & 0XFF);
        motorCtrl[1] = (byte) (motorSwitch & 0XFF);
        motorCtrl[2] = (byte) (brakeSignal & 0XFF);
        motorCtrl[3] = (byte) (driveMotor & 0XFF);
        motorCtrl[4] = (byte) (acc & 0XFF);
        motorCtrl[5] = (byte) (turnTime & 0XFF);
        motorCtrl[6] = (byte) (turnVelocity & 0XFF);
        return motorCtrl;
    }

    private static byte[] pkgRobotCtrl(int cmdRobot) {
        byte[] robotCtrl = new byte[1];
        robotCtrl[0] = (byte) (cmdRobot & 0XFF);
        return robotCtrl;
    }

    private static byte[] pkgClearGPS(int cmdRobot) {
        byte[] robotCtrl = new byte[1];
        robotCtrl[0] = (byte) (cmdRobot & 0XFF);
        return robotCtrl;
    }

    private static byte[] pkgAutoCruise(int cmdAuto, int cruType) {
        byte[] autoCruise = new byte[3];
        autoCruise[0] = (byte) (cmdAuto & 0XFF);
        autoCruise[1] = (byte) (cruType & 0XFF);
        autoCruise[2] = (byte) (0x00);
        return autoCruise;
    }

    private static byte[] pkgDisReply(int uploadDis) {
        byte[] disReply = new byte[1];
        disReply[0] = (byte) (uploadDis & 0XFF);
        return disReply;
    }

    /**
     * 电机控制命令【0X02】
     *
     * @param mac          设备 MAC 地址
     * @param pwrSwitch    电源总开关
     * @param motorSwitch  电机总开关
     * @param brakeSignal  刹车信号
     * @param driveMotor   驱动电机
     * @param acc          加速度
     * @param turnTime     转向电机时间
     * @param turnVelocity 转向电机速度
     */
    public static void sendCmdMotorCtrl(String mac, int pwrSwitch, int motorSwitch, int brakeSignal, int driveMotor, int acc, int turnTime, int turnVelocity) {
        byte[] bytes = pkgMotorCtrl(pwrSwitch, motorSwitch, brakeSignal, driveMotor, acc, turnTime, turnVelocity);
        MainActivity.getAppCtrl().sendCmd(mac, BleDevProtocol.cmdPkg(SEND_HEADER, CMD_MOTOR_CTRL, bytes, CRC_MOTOR_CTRL));
    }

    /**
     * APP 发送命令给工控机【0X09】
     *
     * @param mac         设备 MAC 地址
     * @param cmd         命令类型 0x01-机器人遥控；0x02-自动巡检；0x04-清空GPS
     * @param autoCruType 巡检类型 0-按照一条线路巡检一次；1-按照线路循环执行；2-按照现有内部地图，随机巡检
     */
    public static void sendCmdAppToPC(String mac, int cmd, int autoCruType) {
        switch (cmd) {
            case (byte) 0x01:
                MainActivity.getAppCtrl().sendCmd(mac, BleDevProtocol.cmdPkg(SEND_HEADER, CMD_APP_TO_PC, pkgRobotCtrl(cmd), CRC_APP_TO_PC));
                break;
            case (byte) 0x02:
                MainActivity.getAppCtrl().sendCmd(mac, BleDevProtocol.cmdPkg(SEND_HEADER, CMD_APP_TO_PC, pkgAutoCruise(cmd, autoCruType), CRC_APP_TO_PC));
                break;
            case (byte) 0x04:
                MainActivity.getAppCtrl().sendCmd(mac, BleDevProtocol.cmdPkg(SEND_HEADER, CMD_APP_TO_PC, pkgClearGPS(cmd), CRC_APP_TO_PC));
                break;
        }
    }

    /**
     * 等待距离命令回复【0x87】
     *
     * @param mac       设备 MAC 地址
     * @param uploadDis 0x07
     */
    public static void sendCmdDisReply(String mac, int uploadDis) {
        byte[] bytes = pkgDisReply(uploadDis);
        MainActivity.getAppCtrl().sendCmd(mac, BleDevProtocol.cmdPkg(SEND_HEADER, 0x87, bytes, CRC_DIS_REPLY));
    }

}