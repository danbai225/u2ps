package cn.p00q.u2ps.entity;

import javax.persistence.*;

/**
 * @author DanBai
 */
public class Booth {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 所属用户名
     */
    private String username;

    /**
     * 是否为官方
     */
    private Boolean official;

    /**
     * 流量数/GB
     */
    private Integer quantity;

    /**
     * 单价
     */
    private Float price;

    /**
     * @return id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(Integer id) {
        this.id = id;
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
     * 获取是否为官方
     *
     * @return official - 是否为官方
     */
    public Boolean getOfficial() {
        return official;
    }

    /**
     * 设置是否为官方
     *
     * @param official 是否为官方
     */
    public void setOfficial(Boolean official) {
        this.official = official;
    }

    /**
     * 获取流量数/GB
     *
     * @return quantity - 流量数/GB
     */
    public Integer getQuantity() {
        return quantity;
    }

    /**
     * 设置流量数/GB
     *
     * @param quantity 流量数/GB
     */
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    /**
     * 获取单价
     *
     * @return price - 单价
     */
    public Float getPrice() {
        return price;
    }

    /**
     * 设置单价
     *
     * @param price 单价
     */
    public void setPrice(Float price) {
        this.price = price;
    }

    public Booth() {
    }

    public Booth(String username, Boolean official, Integer quantity, Float price) {
        this.username = username;
        this.official = official;
        this.quantity = quantity;
        this.price = price;
    }
}