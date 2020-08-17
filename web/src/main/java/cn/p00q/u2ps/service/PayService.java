package cn.p00q.u2ps.service;

import cn.p00q.u2ps.entity.Pay;

import javax.validation.constraints.NotBlank;

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
     * 查询
     * @param id
     * @return
     */
    Pay getById(Integer id);

}
