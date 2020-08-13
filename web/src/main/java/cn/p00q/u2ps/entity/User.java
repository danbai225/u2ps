package cn.p00q.u2ps.entity;

import org.hibernate.validator.constraints.Length;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;


/**
 * @author DanBai
 */
public class User implements Serializable {
    public interface RegGroup {
    }
    public interface LoginGroup {
    }
    /**
     * 用户ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 用户名
     */
    @Length(max = 16,min = 3,message = "用户名长度3-16" ,groups = {RegGroup.class,LoginGroup.class})
    @NotBlank(message = "用户名不能为空",groups = {RegGroup.class,LoginGroup.class})
    private String username;

    /**
     * 用户密码
     */
    @NotBlank(message = "用户密码不能为空",groups = {RegGroup.class,LoginGroup.class})
    @Length(max = 16,min = 6,message = "用户密码长度6-16" ,groups = {RegGroup.class,LoginGroup.class})
    private String password;

    /**
     * 用户流量
     */
    private Double flow;

    /**
     * 用户类型 0禁用 1未验证(未验证邮箱未认证) 2未实名认证用户 3普通用户(完成认证) 4审核员(审查) 5管理员(全部权限)
     */
    private Integer type;

    /**
     * 实名认证姓名
     */
    private String realname;

    /**
     * 实名认证身份证号码
     */
    @Column(name = "id_card")
    private String idCard;

    /**
     * 邮箱
     */
    @Email(groups = {RegGroup.class})
    private String email;

    /**
     * 手机号码
     */
    private String mobile;

    /**
     * 注册时间
     */
    @Column(name = "register_time")
    private Date registerTime;

    /**
     * 注册ip
     */
    @Column(name = "register_ip")
    private String registerIp;

    /**
     * 登录时间
     */
    @Column(name = "login_time")
    private Date loginTime;

    /**
     * 登录ip
     */
    @Column(name = "login_ip")
    private String loginIp;

    /**
     * 获取用户ID
     *
     * @return id - 用户ID
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置用户ID
     *
     * @param id 用户ID
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取用户名
     *
     * @return username - 用户名
     */
    public String getUsername() {
        return username;
    }

    /**
     * 设置用户名
     *
     * @param username 用户名
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * 获取用户密码
     *
     * @return password - 用户密码
     */
    public String getPassword() {
        return password;
    }

    /**
     * 设置用户密码
     *
     * @param password 用户密码
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * 获取用户流量
     *
     * @return flow - 用户流量
     */
    public Double getFlow() {
        return flow;
    }

    /**
     * 设置用户流量
     *
     * @param flow 用户流量
     */
    public void setFlow(Double flow) {
        this.flow = flow;
    }

    /**
     * 获取用户类型 0禁用 1未验证 2正常用户 3审核员 4管理员
     *
     * @return type - 用户类型 0禁用 1未验证 2正常用户 3审核员 4管理员
     */
    public Integer getType() {
        return type;
    }

    /**
     * 设置用户类型 0禁用 1未验证 2正常用户 3审核员 4管理员
     *
     * @param type 用户类型 0禁用 1未验证 2正常用户 3审核员 4管理员
     */
    public void setType(Integer type) {
        this.type = type;
    }

    /**
     * 获取实名认证姓名
     *
     * @return realname - 实名认证姓名
     */
    public String getRealname() {
        return realname;
    }

    /**
     * 设置实名认证姓名
     *
     * @param realname 实名认证姓名
     */
    public void setRealname(String realname) {
        this.realname = realname;
    }

    /**
     * 获取实名认证身份证号码
     *
     * @return id_card - 实名认证身份证号码
     */
    public String getIdCard() {
        return idCard;
    }

    /**
     * 设置实名认证身份证号码
     *
     * @param idCard 实名认证身份证号码
     */
    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    /**
     * 获取邮箱
     *
     * @return email - 邮箱
     */
    public String getEmail() {
        return email;
    }

    /**
     * 设置邮箱
     *
     * @param email 邮箱
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * 获取手机号码
     *
     * @return mobile - 手机号码
     */
    public String getMobile() {
        return mobile;
    }

    /**
     * 设置手机号码
     *
     * @param mobile 手机号码
     */
    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    /**
     * 获取注册时间
     *
     * @return register_time - 注册时间
     */
    public Date getRegisterTime() {
        return registerTime;
    }

    /**
     * 设置注册时间
     *
     * @param registerTime 注册时间
     */
    public void setRegisterTime(Date registerTime) {
        this.registerTime = registerTime;
    }

    /**
     * 获取注册ip
     *
     * @return register_ip - 注册ip
     */
    public String getRegisterIp() {
        return registerIp;
    }

    /**
     * 设置注册ip
     *
     * @param registerIp 注册ip
     */
    public void setRegisterIp(String registerIp) {
        this.registerIp = registerIp;
    }

    /**
     * 获取登录时间
     *
     * @return login_time - 登录时间
     */
    public Date getLoginTime() {
        return loginTime;
    }

    /**
     * 设置登录时间
     *
     * @param loginTime 登录时间
     */
    public void setLoginTime(Date loginTime) {
        this.loginTime = loginTime;
    }

    /**
     * 获取登录ip
     *
     * @return login_ip - 登录ip
     */
    public String getLoginIp() {
        return loginIp;
    }

    /**
     * 设置登录ip
     *
     * @param loginIp 登录ip
     */
    public void setLoginIp(String loginIp) {
        this.loginIp = loginIp;
    }

    public User(String username, String password, Double flow, Integer type, String realname, String idCard, String email, String mobile, Date registerTime, String registerIp, Date loginTime, String loginIp) {
        this.username = username;
        this.password = password;
        this.flow = flow;
        this.type = type;
        this.realname = realname;
        this.idCard = idCard;
        this.email = email;
        this.mobile = mobile;
        this.registerTime = registerTime;
        this.registerIp = registerIp;
        this.loginTime = loginTime;
        this.loginIp = loginIp;
    }

    public User() {
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", flow=" + flow +
                ", type=" + type +
                ", realname='" + realname + '\'' +
                ", idCard='" + idCard + '\'' +
                ", email='" + email + '\'' +
                ", mobile='" + mobile + '\'' +
                ", registerTime=" + registerTime +
                ", registerIp='" + registerIp + '\'' +
                ", loginTime=" + loginTime +
                ", loginIp='" + loginIp + '\'' +
                '}';
    }
}