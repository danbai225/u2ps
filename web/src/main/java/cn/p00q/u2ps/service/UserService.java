package cn.p00q.u2ps.service;

import cn.p00q.u2ps.bean.Result;
import cn.p00q.u2ps.entity.User;

/**
 * @program: u2ps
 * @description: User服务接口
 * @author: DanBai
 * @create: 2020-08-01 21:12
 **/
public interface UserService {
    /**
     * login
     * @param user 登录用户信息
     * @return token
     */
    String login(User user);

    /**
     * 根据用户名获取user
     * @param username 用户名
     * @return 获取的用户
     */
    User getUserByUsername(String username);
    /**
     * 关联用户和token
     * @param username 用户名
     * @return token
     */
    String createToken(String username);

    /**
     * 检测token有效性
     * @param token token
     * @return 是否有效
     */
    User checkToken(String token);

    /**
     * 删除token
     * @param username 用户名
     */
    void deleteToken (String username);

    /**
     * 用户注册
     * @param user 用户
     * @return 成功与否
     */
    Result reg(User user);

    /**
     * 验证邮箱
     * @param code 验证码
     * @return 成功与否
     */
    boolean validateEmail(String code);

    /**
     * 发送验证邮件
     * @param user 验证的用户
     */
    void sendValidateEmail(User user);

    /**
     * 找回修改密码
     * @param email
     * @param password
     * @return
     */
    Result forgotPassword(String email,String password);

    /**
     * 总注册用户
     * @return
     */
    Integer count();

    /**
     * 获取token
     * @param username
     * @return
     */
    String getToken(String username);
}
