package cn.p00q.u2ps.service;

import cn.p00q.u2ps.bean.Result;
import cn.p00q.u2ps.entity.Booth;
import cn.p00q.u2ps.entity.Pay;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @program: web
 * @description: 支付服务接口
 * @author: DanBai
 * @create: 2020-08-17 15:33
 **/
public interface PayService {
    /**
     * 购买认证机会
     * @param username
     * @param payType
     * @return
     */
    String buyAuthentication(String username,Integer payType);

    /**
     * 统计行
     *
     * @return
     */
    Integer countPay();

    /**
     * 支付回调
     * @param Salt
     * @param order_id
     * @param fee
     * @param sign
     * @return
     */
    String callBack(@NotBlank String Salt, Integer order_id, String fee, Integer sign);

    /**
     * 购买流量
     * @param username
     * @param payType
     * @param boothId
     * @param buyNum
     * @return
     */
    Result buyFlow(String username, Integer payType, Integer boothId, Integer buyNum);
    /**
     * 查询
     * @param id
     * @return
     */
    Pay getById(Integer id);

    /**
     * 流量单价
     * @return
     */
    Float getFlowPrice();

    /**
     * 出售流量
     * @param username
     * @param price
     * @param quantity
     * @return
     */
    Result sellFlow(String username, Float price, Integer quantity);

    /**
     * 获取有流量的摊位
     * @return
     */
    List<Booth> getNormalBooth();

    /**
     * 获取余额
     * @param username
     * @return
     */
    Float getRNum(String username);

    /**
     * 取消回调
     * @param Salt
     * @param order_id
     * @param fee
     * @param sign
     */
    void cancel(@NotBlank String Salt, Integer order_id, String fee, Integer sign);
}
