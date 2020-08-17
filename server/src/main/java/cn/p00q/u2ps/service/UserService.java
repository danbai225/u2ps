package cn.p00q.u2ps.service;

import cn.p00q.u2ps.entity.User;

/**
 * @program: u2ps
 * @description: User服务接口
 * @author: DanBai
 * @create: 2020-08-01 21:12
 **/
public interface UserService {
    /**
     * 根据用户名获取user
     * @param username 用户名
     * @return 获取的用户
     */
    User getUserByUsername(String username);
    /**
     * 检测token有效性
     * @param token token
     * @return 是否有效
     */
     User checkToken(String token);

    /**
     * 计算流量消耗
     * @param tid
     * @param flow
     */
     void flowCalculation(Integer tid,Long flow);
}
