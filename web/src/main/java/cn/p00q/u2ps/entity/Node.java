package cn.p00q.u2ps.entity;


import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * @author DanBai
 */
public class Node implements Serializable {
    public interface Create {
    }
    public interface Update {
    }
    /**
     * node id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Null(message = "id数据库生成",groups = {Create.class})
    @NotNull(groups = {Update.class})
    private Integer id;

    /**
     * node ip
     */
    @NotBlank(message = "ip不能为空",groups = {Create.class})
    @Null(groups = {Update.class})
    private String ip;

    /**
     * 节点服务端口
     */
    @Min(value = 1, groups = {Create.class})
    @Max(value = 65535, groups = {Create.class})
    private Integer port;

    /**
     * node国家地区位置
     */
    @Column(name = "countries_regions")
    @Null(groups = {Update.class})
    private String countriesRegions;

    /**
     * 允许的端口 默认2252/22250  格式为“开始端口号/终止端口号”
     */
    private String ports;

    /**
     * 最多隧道数
     */
    @Column(name = "max_tunnel")
    @Min(value = 1, groups = {Create.class})
    private Integer maxTunnel;

    /**
     * 是否允许web
     */
    @Column(name = "allow_web")
    private Boolean allowWeb;

    /**
     * 流量倍率
     */
    @Column(name = "flow_ratio")
    @Max(value = 10,groups = {Create.class})
    private Float flowRatio;

    /**
     * 创建时间
     */
    @Column(name = "creation_time")
    @Null(groups = {Create.class,Update.class})
    private Date creationTime;

    /**
     * 所属用户
     */
    private String username;

    /**
     * 是否开放
     */
    private Boolean open;

    /**
     * 是否在线
     */
    private Boolean online;
    private Integer bandwidth;

    public Integer getBandwidth() {
        return bandwidth;
    }

    public void setBandwidth(Integer bandwidth) {
        this.bandwidth = bandwidth;
    }

    /**
     * 获取node id
     *
     * @return id - node id
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置node id
     *
     * @param id node id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取node ip
     *
     * @return ip - node ip
     */
    public String getIp() {
        return ip;
    }

    /**
     * 设置node ip
     *
     * @param ip node ip
     */
    public void setIp(String ip) {
        this.ip = ip;
    }

    /**
     * 获取节点服务端口
     *
     * @return port - 节点服务端口
     */
    public Integer getPort() {
        return port;
    }

    /**
     * 设置节点服务端口
     *
     * @param port 节点服务端口
     */
    public void setPort(Integer port) {
        this.port = port;
    }

    /**
     * 获取node国家地区位置
     *
     * @return countries_regions - node国家地区位置
     */
    public String getCountriesRegions() {
        return countriesRegions;
    }

    /**
     * 设置node国家地区位置
     *
     * @param countriesRegions node国家地区位置
     */
    public void setCountriesRegions(String countriesRegions) {
        this.countriesRegions = countriesRegions;
    }

    /**
     * 获取允许的端口 默认2252/22250  格式为“开始端口号/终止端口号”
     *
     * @return ports - 允许的端口 默认2252/22250  格式为“开始端口号/终止端口号”
     */
    public String getPorts() {
        return ports;
    }

    /**
     * 设置允许的端口 默认2252/22250  格式为“开始端口号/终止端口号”
     *
     * @param ports 允许的端口 默认2252/22250  格式为“开始端口号/终止端口号”
     */
    public void setPorts(String ports) {
        this.ports = ports;
    }


    /**
     * 获取最多隧道数
     *
     * @return max_tunnel - 最多隧道数
     */
    public Integer getMaxTunnel() {
        return maxTunnel;
    }

    /**
     * 设置最多隧道数
     *
     * @param maxTunnel 最多隧道数
     */
    public void setMaxTunnel(Integer maxTunnel) {
        this.maxTunnel = maxTunnel;
    }

    /**
     * 获取是否允许web
     *
     * @return allow_web - 是否允许web
     */
    public Boolean getAllowWeb() {
        return allowWeb;
    }

    /**
     * 设置是否允许web
     *
     * @param allowWeb 是否允许web
     */
    public void setAllowWeb(Boolean allowWeb) {
        this.allowWeb = allowWeb;
    }

    /**
     * 获取流量倍率
     *
     * @return flow_ratio - 流量倍率
     */
    public Float getFlowRatio() {
        return flowRatio;
    }

    /**
     * 设置流量倍率
     *
     * @param flowRatio 流量倍率
     */
    public void setFlowRatio(Float flowRatio) {
        this.flowRatio = flowRatio;
    }

    /**
     * 获取创建时间
     *
     * @return creation_time - 创建时间
     */
    public Date getCreationTime() {
        return creationTime;
    }

    /**
     * 设置创建时间
     *
     * @param creationTime 创建时间
     */
    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
    }

    /**
     * 获取所属用户
     *
     * @return username - 所属用户
     */
    public String getUsername() {
        return username;
    }

    /**
     * 设置所属用户
     *
     * @param username 所属用户
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * 获取是否开放
     *
     * @return open - 是否开放
     */
    public Boolean getOpen() {
        return open;
    }

    /**
     * 设置是否开放
     *
     * @param open 是否开放
     */
    public void setOpen(Boolean open) {
        this.open = open;
    }

    /**
     * 获取是否在线
     *
     * @return online - 是否在线
     */
    public Boolean getOnline() {
        return online;
    }

    /**
     * 设置是否在线
     *
     * @param online 是否在线
     */
    public void setOnline(Boolean online) {
        this.online = online;
    }

    public Node(String ip, Integer port, String countriesRegions, String ports, Integer maxTunnel,  Boolean allowWeb, Float flowRatio, Date creationTime, String username, Boolean open, Boolean online, Integer bandwidth) {
        this.ip = ip;
        this.port = port;
        this.countriesRegions = countriesRegions;
        this.ports = ports;
        this.maxTunnel = maxTunnel;
        this.allowWeb = allowWeb;
        this.flowRatio = flowRatio;
        this.creationTime = creationTime;
        this.username = username;
        this.open = open;
        this.online = online;
        this.bandwidth = bandwidth;
    }

    public Node() {
    }

    @Override
    public String toString() {
        return "Node{" +
                "id=" + id +
                ", ip='" + ip + '\'' +
                ", port=" + port +
                ", countriesRegions='" + countriesRegions + '\'' +
                ", ports='" + ports + '\'' +
                ", maxTunnel=" + maxTunnel +
                ", allowWeb=" + allowWeb +
                ", flowRatio=" + flowRatio +
                ", creationTime=" + creationTime +
                ", username='" + username + '\'' +
                ", open=" + open +
                ", online=" + online +
                ", bandwidth=" + bandwidth +
                '}';
    }
}