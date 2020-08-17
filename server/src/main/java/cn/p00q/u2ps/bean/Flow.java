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
    public static final String ClientSuffix="_Client";
    public static final String FlowCount="FlowCount";
    public static final String FlowToDayPrefix="FlowToDay_";
    public static final String UserFlowToDayPrefix="UserFlowToDay_";
    public static long  MB100=1024*1024*100;
    /**
     * 上行
     */
    private Long up;
    /**
     * 下行
     */
    private Long down;

    public Flow(Long up, Long down) {
        this.up = up;
        this.down = down;
    }

    public Long getUp() {
        return up;
    }

    public void setUp(Long up) {
        this.up = up;
    }

    public Long getDown() {
        return down;
    }

    public void setDown(Long down) {
        this.down = down;
    }
    public void addUP(Long flow){
        this.up+=flow;
    }
    public void addDown(Long flow){
        this.down+=flow;
    }
    public void add(Flow flow){
        this.up+=flow.up;
        this.down+=flow.down;
    }
    public Long count(){
        return up+down;
    }
    public void clear(){
        this.up=0L;
        this.down=0L;
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
