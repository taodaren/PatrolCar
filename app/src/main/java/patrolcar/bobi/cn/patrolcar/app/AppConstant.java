package patrolcar.bobi.cn.patrolcar.app;

/**
 * 全局常量
 */

public class AppConstant {
    public static final int CCM_STATE_CMD_TPYE = 0X06;
    public static final int CCM_STATE_ACCL_XL = 0;
    public static final int CCM_STATE_ACCL_XH = 1;
    public static final int CCM_STATE_ACCL_YL = 2;
    public static final int CCM_STATE_ACCL_YH = 3;
    public static final int CCM_STATE_ACCL_ZL = 4;
    public static final int CCM_STATE_ACCL_ZH = 5;
    public static final int CCM_STATE_ANGLE_XL = 6;
    public static final int CCM_STATE_ANGLE_XH = 7;
    public static final int CCM_STATE_ANGLE_YL = 8;
    public static final int CCM_STATE_ANGLE_YH = 9;
    public static final int CCM_STATE_ANGLE_ZL = 10;
    public static final int CCM_STATE_ANGLE_ZH = 11;
    public static final int CCM_STATE_MAGN_XL = 12;
    public static final int CCM_STATE_MAGN_XH = 13;
    public static final int CCM_STATE_MAGN_YL = 14;
    public static final int CCM_STATE_MAGN_YH = 15;
    public static final int CCM_STATE_MAGN_ZL = 16;
    public static final int CCM_STATE_MAGN_ZH = 17;
    public static final int CCM_STATE_TEMP = 18;
    public static final int CCM_STATE_HUMI = 19;
    public static final int CCM_STATE_GPS_LON0 = 20;
    public static final int CCM_STATE_GPS_LON1 = 21;
    public static final int CCM_STATE_GPS_LON2 = 22;
    public static final int CCM_STATE_GPS_LON3 = 23;
    public static final int CCM_STATE_GPS_LAT0 = 24;
    public static final int CCM_STATE_GPS_LAT1 = 25;
    public static final int CCM_STATE_GPS_LAT2 = 26;
    public static final int CCM_STATE_GPS_LAT3 = 27;
    public static final int CCM_STATE_GPS_ALL = 28;
    public static final int CCM_STATE_GPS_ALH = 29;
    public static final int CCM_STATE_TURN_DIR = 30;
    public static final int CCM_STATE_WHEEL_LFL = 31;
    public static final int CCM_STATE_WHEEL_LFH = 32;
    public static final int CCM_STATE_WHEEL_RFL = 33;
    public static final int CCM_STATE_WHEEL_RFH = 34;
    public static final int CCM_STATE_WHEEL_LBL = 35;
    public static final int CCM_STATE_WHEEL_LBH = 36;
    public static final int CCM_STATE_WHEEL_RBL = 37;
    public static final int CCM_STATE_WHEEL_RBH = 38;
    public static final int CCM_STATE_BATVOL_40V = 39;
    public static final int CCM_STATE_BURST_STOP = 40;
    public static final int CCM_STATE_DATA_CNT = 41;

    public static final String TYPE_MOTOR_CTRL_STOP      = "停止";
    public static final String TYPE_MOTOR_CTRL_UP        = "前进";
    public static final String TYPE_MOTOR_CTRL_DOWN      = "后退";
    public static final String TYPE_MOTOR_CTRL_LEFT      = "左转";
    public static final String TYPE_MOTOR_CTRL_RIGHT     = "右转";
    public static final String TYPE_MOTOR_CTRL_DEV_ON    = "开机";
    public static final String TYPE_MOTOR_CTRL_DEV_OFF   = "关机";
    public static final String TYPE_MOTOR_CTRL_BRAKE_ON  = "刹车";
    public static final String TYPE_MOTOR_CTRL_BRAKE_OFF = "松刹车";
    public static final String TYPE_MOTOR_CTRL_MOTOR_ON  = "开电机";
    public static final String TYPE_MOTOR_CTRL_MOTOR_OFF = "关电机";
}
