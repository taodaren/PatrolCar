package patrolcar.bobi.cn.patrolcar.model;

public class DisValsDataType {
    public static final int DIS_MODLUE_CNT_MAX = 16;
    public static final int DISTANCE_TYPE_MAX = 24;

    public OneDisValsData[] disVals;
    public int disUsableCnt;// 有用的模块数量

    public DisValsDataType() {
        disVals = new OneDisValsData[DIS_MODLUE_CNT_MAX];
        for (int i = 0; i < DIS_MODLUE_CNT_MAX; i++) {
            disVals[i] = new OneDisValsData();
        }
    }

    public class OneDisValsData {
        public int addr;
        public int usableTime;  // 可以被使用的次数。没事收到距离数据，可用次数设置为5次，如果5次内没有新的数据，需要将地址和有效距离清除
        public int usableCnt;
        public int dataCnt;     // 距离数据长度
        public byte[] disData;  // 放置接收和需要发送的数据 每个距离占用 1.5 字节
        public int[] disVals;   // 放置距离数据，每个距离占用 2 字节

        public OneDisValsData() {
            disData = new byte[(DISTANCE_TYPE_MAX / 2) * 3];
            disVals = new int[DISTANCE_TYPE_MAX];
        }
    }
}
