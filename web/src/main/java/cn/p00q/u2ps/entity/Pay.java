package cn.p00q.u2ps.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

/**
 * @author DanBai
 */
public class Pay implements Serializable {
    /**
     * 类型 购买认证
     */
    public static final int Type_Authentication=1;
    public static final int Type_Flow=2;
    /**
     * 支付id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 费用
     */
    private Float fee;

    /**
     * 支付方式 1.支付宝 2.微信
     */
    private Integer paytype;

    /**
     * 支付状态 0未支付，1已支付，2已支付已通知
     */
    private Integer sign;

    /**
     * 支付类型
     */
    private Integer type;

    /**
     * 用户名
     */
    private String username;

    /**
     * 创建时间
     */
    @Column(name = "creationTime")
    private Date creationtime;

    /**
     * 获取支付id
     *
     * @return id - 支付id
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置支付id
     *
     * @param id 支付id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取费用
     *
     * @return fee - 费用
     */
    public Float getFee() {
        return fee;
    }

    /**
     * 设置费用
     *
     * @param fee 费用
     */
    public void setFee(Float fee) {
        this.fee = fee;
    }

    /**
     * 获取支付方式 1.支付宝 2.微信
     *
     * @return paytype - 支付方式 1.微信 2.支付宝
     */
    public Integer getPaytype() {
        return paytype;
    }

    /**
     * 设置支付方式 1.支付宝 2.微信
     *
     * @param paytype 支付方式 1.微信 2.支付宝
     */
    public void setPaytype(Integer paytype) {
        this.paytype = paytype;
    }

    /**
     * 获取支付状态 0未支付，1已支付，2已支付已通知
     *
     * @return sign - 支付状态 0未支付，1已支付，2已支付已通知
     */
    public Integer getSign() {
        return sign;
    }

    /**
     * 设置支付状态 0未支付，1已支付，2已支付已通知
     *
     * @param sign 支付状态 0未支付，1已支付，2已支付已通知
     */
    public void setSign(Integer sign) {
        this.sign = sign;
    }

    /**
     * 获取支付类型
     *
     * @return type - 支付类型
     */
    public Integer getType() {
        return type;
    }

    /**
     * 设置支付类型
     *
     * @param type 支付类型
     */
    public void setType(Integer type) {
        this.type = type;
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
     * 获取创建时间
     *
     * @return creationTime - 创建时间
     */
    public Date getCreationtime() {
        return creationtime;
    }

    /**
     * 设置创建时间
     *
     * @param creationtime 创建时间
     */
    public void setCreationtime(Date creationtime) {
        this.creationtime = creationtime;
    }

    public static Pay PayInit() {
        Pay pay = new Pay();
        pay.creationtime=new Date();
        pay.sign=0;
        return pay;
    }
    public Pay() {
    }
    public Pay(Float fee, Integer paytype, Integer sign, Integer type, String username, Date creationtime) {
        this.fee = fee;
        this.paytype = paytype;
        this.sign = sign;
        this.type = type;
        this.username = username;
        this.creationtime = creationtime;
    }

}