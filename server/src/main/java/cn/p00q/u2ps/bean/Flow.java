package cn.p00q.u2ps.bean;


/**
 * @program: u2ps
 * @description: 流量
 * @author: DanBai
 * @create: 2020-08-11 14:58
 **/
public class Flow {
    public static final String NodeFlowPrefix="NodeFlowPrefix_";
    public static final String TunnelFlowPrefix="TunnelFlowPrefix_";
    /**
     * 上行
     */
    private long up;
    /**
     * 下行
     */
    private long down;

    public Flow(long up, long down) {
        this.up = up;
        this.down = down;
    }

    public long getUp() {
        return up;
    }

    public void setUp(long up) {
        this.up = up;
    }

    public long getDown() {
        return down;
    }

    public void setDown(long down) {
        this.down = down;
    }
    public void addUP(long flow){
        this.up+=flow;
    }
    public void addDown(long flow){
        this.down+=flow;
    }
    @Override
    public String toString() {
        return "Flow{" +
                "up=" + up +
                ", down=" + down +
                '}';
    }

    public Flow() {
    }
}
