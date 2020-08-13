package cn.p00q.u2ps.bean;

/**
 * @program: u2ps
 * @description: 流量传输类
 * @author: DanBai
 * @create: 2020-08-11 15:09
 **/
public class FlowType {
    private Boolean isUp;
    private Integer NodeId;
    private Integer TunnelId;
    private long flow;

    public FlowType(Boolean isUp, Integer nodeId, Integer tunnelId, long flow) {
        this.isUp = isUp;
        NodeId = nodeId;
        TunnelId = tunnelId;
        this.flow = flow;
    }

    @Override
    public String toString() {
        return "FlowType{" +
                "isUp=" + isUp +
                ", NodeId=" + NodeId +
                ", TunnelId=" + TunnelId +
                ", flow=" + flow +
                '}';
    }

    public Boolean getUp() {
        return isUp;
    }

    public void setUp(Boolean up) {
        isUp = up;
    }

    public Integer getNodeId() {
        return NodeId;
    }

    public void setNodeId(Integer nodeId) {
        NodeId = nodeId;
    }

    public Integer getTunnelId() {
        return TunnelId;
    }

    public void setTunnelId(Integer tunnelId) {
        TunnelId = tunnelId;
    }

    public long getFlow() {
        return flow;
    }

    public void setFlow(long flow) {
        this.flow = flow;
    }

    public FlowType() {
    }
}
