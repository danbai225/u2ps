package cn.p00q.u2ps.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

/**
 * @author DanBai
 */
public class Client implements Serializable {
    public interface Create {
    }
    public interface Update {
    }
    public static final String AutoGenerate="AutoGenerate";
    /**
     * 客户端id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Null(message = "id数据库生成",groups = {Create.class})
    @NotNull(groups = {Update.class})
    private Integer id;

    /**
     * 唯一密钥
     */
    @Column(name = "secret_key")
    @NotNull(message = "key不能为空",groups = {Create.class})
    private String secretKey;

    /**
     * 客户端IP
     */
    @Column(name = "client_ip")
    @Null(groups = {Update.class})
    private String clientIp;

    /**
     * 是否在线
     */
    @Column(name = "on_line")
    @Null(groups = {Update.class})
    private Boolean onLine;

    /**
     * 所属用户名
     */
    @Null(groups = {Update.class})
    private String username;

    /**
     * 创建时间
     */
    @Column(name = "creation_time")
    @Null(groups = {Update.class})
    private Date creationTime;

    /**
     * 备注
     */
    private String remark;

    /**
     * 获取客户端id
     *
     * @return id - 客户端id
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置客户端id
     *
     * @param id 客户端id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取唯一密钥
     *
     * @return secret_key - 唯一密钥
     */
    public String getSecretKey() {
        return secretKey;
    }

    /**
     * 设置唯一密钥
     *
     * @param secretKey 唯一密钥
     */
    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    /**
     * 获取客户端IP
     *
     * @return client_ip - 客户端IP
     */
    public String getClientIp() {
        return clientIp;
    }

    /**
     * 设置客户端IP
     *
     * @param clientIp 客户端IP
     */
    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    /**
     * 获取是否在线
     *
     * @return on_line - 是否在线
     */
    public Boolean getOnLine() {
        return onLine;
    }

    /**
     * 设置是否在线
     *
     * @param onLine 是否在线
     */
    public void setOnLine(Boolean onLine) {
        this.onLine = onLine;
    }

    /**
     * 获取所属用户名
     *
     * @return username - 所属用户名
     */
    public String getUsername() {
        return username;
    }

    /**
     * 设置所属用户名
     *
     * @param username 所属用户名
     */
    public void setUsername(String username) {
        this.username = username;
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
     * 获取备注
     *
     * @return remark - 备注
     */
    public String getRemark() {
        return remark;
    }

    /**
     * 设置备注
     *
     * @param remark 备注
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Client(String secretKey, String clientIp, Boolean onLine, String username, Date creationTime, String remark) {
        this.secretKey = secretKey;
        this.clientIp = clientIp;
        this.onLine = onLine;
        this.username = username;
        this.creationTime = creationTime;
        this.remark = remark;
    }

    public Client() {
    }
}