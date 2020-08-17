package cn.p00q.u2ps.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * @author DanBai
 */
public class Tunnel implements Serializable {
    public interface Update {
    }
    public interface Create {
    }
    /**
     * 隧道id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NotNull(message = "id不能为空",groups = {Update.class})
    @Null(message = "id数据库生成",groups = {Create.class})
    private Integer id;

    /**
     * 节点id
     */
    @Column(name = "node_id")
    @Null(message = "不能修改节点ID,只能删除隧道重新选择节点创建",groups = {Update.class})
    @NotNull(message = "节点ID不能为空请选择节点",groups = {Create.class})
    private Integer nodeId;

    /**
     * 目标ip
     */
    @Column(name = "target_ip")
    @NotBlank(message = "目标ip不能为空",groups = {Create.class})
    private String targetIp;

    /**
     * 目标端口
     */
    @Column(name = "target_port")
    @NotNull(message = "目标端口不能为null",groups = {Create.class})
    @Min(value = 1, groups = {Create.class})
    @Max(value = 65535, groups = {Create.class})
    @NotNull(groups = {Create.class})
    private Integer targetPort;

    /**
     * 所属用户
     */
    @Null(message = "不能修改所属用户",groups = {Update.class})
    private String username;

    /**
     * 客户端id
     */
    @Null(message = "不能修改客户端ID,只能删除隧道重新选择客户端创建",groups = {Update.class})
    @Min(value = 1 ,groups = {Create.class})
    @NotNull(groups = {Create.class})
    private Integer clientId;

    /**
     * 服务端口
     */
    @Column(name = "service_port")
    @Min(value = 1, groups = {Create.class})
    @Max(value = 65535, groups = {Create.class})
    @NotNull(groups = {Create.class})
    private Integer servicePort;

    /**
     * 隧道类型 1.tcp 2.udp 3.http 4.socks5
     */
    @Min(value = 1, groups = {Create.class})
    @Max(value = 4, groups = {Create.class})
    @NotNull(groups = {Create.class})
    private Integer type;

    /**
     * 是否开启
     */
    private Boolean open;

    /**
     * 创建时间
     */
    @Column(name = "creation_time")
    @Null(message = "不能修改隧道创建时间",groups = {Update.class})
    private Date creationTime;

    /**
     * 获取隧道id
     *
     * @return id - 隧道id
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置隧道id
     *
     * @param id 隧道id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取节点id
     *
     * @return node_id - 节点id
     */
    public Integer getNodeId() {
        return nodeId;
    }

    /**
     * 设置节点id
     *
     * @param nodeId 节点id
     */
    public void setNodeId(Integer nodeId) {
        this.nodeId = nodeId;
    }

    /**
     * 获取目标ip
     *
     * @return target_ip - 目标ip
     */
    public String getTargetIp() {
        return targetIp;
    }

    /**
     * 设置目标ip
     *
     * @param targetIp 目标ip
     */
    public void setTargetIp(String targetIp) {
        this.targetIp = targetIp;
    }

    /**
     * 获取目标端口
     *
     * @return target_port - 目标端口
     */
    public Integer getTargetPort() {
        return targetPort;
    }

    /**
     * 设置目标端口
     *
     * @param targetPort 目标端口
     */
    public void setTargetPort(Integer targetPort) {
        this.targetPort = targetPort;
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
     * 获取客户端id
     *
     * @return client_id - 客户端id
     */
    public Integer getClientId() {
        return clientId;
    }

    /**
     * 设置客户端ip
     *
     * @param clientId 客户端ip
     */
    public void setClientId(Integer clientId) {
        this.clientId = clientId;
    }

    /**
     * 获取服务端口
     *
     * @return service_port - 服务端口
     */
    public Integer getServicePort() {
        return servicePort;
    }

    /**
     * 设置服务端口
     *
     * @param servicePort 服务端口
     */
    public void setServicePort(Integer servicePort) {
        this.servicePort = servicePort;
    }

    /**
     * 获取隧道类型 1.tcp 2.udp 3.http
     *
     * @return type - 隧道类型 1.tcp 2.udp 3.http
     */
    public Integer getType() {
        return type;
    }

    /**
     * 设置隧道类型 1.tcp 2.udp 3.http
     *
     * @param type 隧道类型 1.tcp 2.udp 3.http
     */
    public void setType(Integer type) {
        this.type = type;
    }


    /**
     * 获取是否开启
     *
     * @return online - 是否开启
     */
    public Boolean getOpen() {
        return open;
    }

    /**
     * 设置是否开启
     *
     * @param open 是否开启
     */
    public void setOpen(Boolean open) {
        this.open = open;
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

    public Tunnel(Integer nodeId, String targetIp, Integer targetPort, String username, Integer clientId, Integer servicePort, Integer type, Boolean open, Date creationTime) {
        this.nodeId = nodeId;
        this.targetIp = targetIp;
        this.targetPort = targetPort;
        this.username = username;
        this.clientId = clientId;
        this.servicePort = servicePort;
        this.type = type;
        this.open = open;
        this.creationTime = creationTime;
    }

    public Tunnel() {
    }
}