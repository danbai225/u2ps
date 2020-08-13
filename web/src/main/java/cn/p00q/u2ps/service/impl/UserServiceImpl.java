package cn.p00q.u2ps.service.impl;

import cn.p00q.u2ps.bean.Result;
import cn.p00q.u2ps.entity.User;
import cn.p00q.u2ps.mapper.UserMapper;
import cn.p00q.u2ps.service.UserService;
import cn.p00q.u2ps.utils.EmailUtil;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @program: u2ps
 * @description: user服务实现
 * @author: DanBai
 * @create: 2020-08-01 21:22
 **/
@Service
public class UserServiceImpl implements UserService {
    @Value("${u2ps.domain:http://127.0.0.1:2252}")
    private String doMainUrl;
    private static final String REDIS_VALIDATE_EMAIL_PREFIX = "validateEmail_";
    private static final String REDIS_USER_NEW_PASS_PREFIX="Password_";
    private static final String TOKEN_PREFIX = "token_";
    private final UserMapper userMapper;
    private final RedisTemplate redisTemplate;
    private final EmailUtil emailUtil;

    public UserServiceImpl(UserMapper userMapper, RedisTemplate redisTemplate, EmailUtil emailUtil) {
        this.userMapper = userMapper;
        this.redisTemplate = redisTemplate;
        this.emailUtil = emailUtil;
    }

    @Override
    public String login(User user) {
        User dataBaseUser = getUserByUsername(user.getUsername());
        if (dataBaseUser != null) {
            //加密验证
            if (dataBaseUser.getPassword().equals(DigestUtils.md5DigestAsHex((user.getUsername() + user.getPassword()).getBytes()))) {
                dataBaseUser.setLoginIp(user.getLoginIp());
                dataBaseUser.setLoginTime(new Date());
                userMapper.updateByPrimaryKey(dataBaseUser);
                return createToken(user.getUsername());
            }
        }
        return null;
    }

    @Override
    public User getUserByUsername(String username) {
        User user = new User();
        user.setUsername(username);
        return userMapper.selectOne(user);
    }

    @Override
    public String createToken(String username) {
        String token = (String) redisTemplate.opsForValue().get(TOKEN_PREFIX + username);
        if(StringUtils.isNotEmpty(token)){
            redisTemplate.expire(TOKEN_PREFIX + token, 7, TimeUnit.DAYS);
            redisTemplate.expire(TOKEN_PREFIX + username, 7, TimeUnit.DAYS);
            return token;
        }
        String tokenid = UUID.randomUUID().toString().replace("-", "");
        redisTemplate.opsForValue().set(TOKEN_PREFIX + tokenid, username, 7, TimeUnit.DAYS);
        redisTemplate.opsForValue().set(TOKEN_PREFIX + username, tokenid, 7, TimeUnit.DAYS);
        return tokenid;
    }

    @Override
    public User checkToken(String token) {
        String username = (String) redisTemplate.opsForValue().get(TOKEN_PREFIX + token);
        if (StringUtils.isNotEmpty(username)) {
            redisTemplate.expire(TOKEN_PREFIX + token, 7, TimeUnit.DAYS);
            redisTemplate.expire(TOKEN_PREFIX + username, 7, TimeUnit.DAYS);
            return getUserByUsername(username);
        }
        return null;
    }

    @Override
    public void deleteToken(String username) {
        String token = (String) redisTemplate.opsForValue().get(TOKEN_PREFIX + username);
        redisTemplate.delete(TOKEN_PREFIX + token);
        redisTemplate.delete(TOKEN_PREFIX + username);
    }

    @Override
    public Result reg(User user) {
        User dataBaseUser = getUserByUsername(user.getUsername());
        if (dataBaseUser != null&&!dataBaseUser.getType().equals(1)) {
            return Result.err("存在相同用户名的用户");
        }
        if(dataBaseUser.getType().equals(1)){
            userMapper.delete(dataBaseUser);
        }
        User userEmail = new User();
        userEmail.setEmail(user.getEmail());
        User dataBaseUser1 = userMapper.selectOne(userEmail);
        if (dataBaseUser1 != null&&dataBaseUser != null&&!dataBaseUser.getUsername().equals(dataBaseUser1.getUsername())) {
            return Result.err("邮箱已被使用");
        }
        //密码加密
        user.setPassword(DigestUtils.md5DigestAsHex((user.getUsername() + user.getPassword()).getBytes()));
        user.setFlow(0d);
        user.setRegisterTime(new Date());
        user.setType(1);
        //邮箱验证
        sendValidateEmail(user);
        int insert = userMapper.insert(user);
        if (insert == 1) {
            return Result.success("注册成功,请检查邮箱的激活链接.");
        }
        return Result.err("新增用户失败");
    }

    @Override
    public boolean validateEmail(String code) {
        String username = (String) redisTemplate.opsForValue().get(REDIS_VALIDATE_EMAIL_PREFIX + code);
        if (StringUtils.isEmpty(username)) {
            return false;
        }
        User dataBaseUser = getUserByUsername(username);
        if(!dataBaseUser.getType().equals(1)){
            String pas = (String) redisTemplate.opsForValue().get(REDIS_USER_NEW_PASS_PREFIX + username);
            if(StringUtils.isNotEmpty(pas)){
                dataBaseUser.setPassword(pas);
            }
        }else {
            dataBaseUser.setType(2);
        }
        redisTemplate.delete(REDIS_USER_NEW_PASS_PREFIX + username);
        redisTemplate.delete(REDIS_VALIDATE_EMAIL_PREFIX + code);
        int i = userMapper.updateByPrimaryKey(dataBaseUser);
        return i == 1;
    }

    @Override
    public void sendValidateEmail(User user) {
        String code = RandomStringUtils.randomAlphabetic(16);
        String url = doMainUrl + "/v1/user/validate/email?code=" + code;
        //发送激活链接邮件
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒");
        String subject = "U2PS邮箱验证";
        String emailTemplate = "ValidationEmailTemplate";
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("email", user.getEmail());
        dataMap.put("url", url);
        dataMap.put("createTime", sdf.format(new Date()));
        try {
            emailUtil.sendTemplateMail(user.getEmail(), subject, emailTemplate, dataMap);
        } catch (Exception e) {
            return;
        }
        //添加code到redis里面
        redisTemplate.opsForValue().set(REDIS_VALIDATE_EMAIL_PREFIX + code, user.getUsername(), 1, TimeUnit.HOURS);
    }

    @Override
    public Result forgotPassword(String email, String password) {
        User userEmail = new User();
        userEmail.setEmail(email);
        User dataBaseUser1 = userMapper.selectOne(userEmail);
        if (dataBaseUser1!=null){
            sendValidateEmail(dataBaseUser1);
            //添加密码到redis里面
            redisTemplate.opsForValue().set(REDIS_USER_NEW_PASS_PREFIX+dataBaseUser1.getUsername(),DigestUtils.md5DigestAsHex((dataBaseUser1.getUsername() + password).getBytes()), 1, TimeUnit.HOURS);
            return Result.success("请查看验证邮件");
        }
        return Result.err("不存在此邮箱的账号");
    }
}
