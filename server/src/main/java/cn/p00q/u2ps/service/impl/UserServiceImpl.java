package cn.p00q.u2ps.service.impl;

import cn.p00q.u2ps.bean.Result;
import cn.p00q.u2ps.entity.User;
import cn.p00q.u2ps.mapper.UserMapper;
import cn.p00q.u2ps.service.UserService;
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
    private static final String TOKEN_PREFIX = "token_";
    private final UserMapper userMapper;
    private final RedisTemplate redisTemplate;

    public UserServiceImpl(UserMapper userMapper, RedisTemplate redisTemplate) {
        this.userMapper = userMapper;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public User getUserByUsername(String username) {
        User user = new User();
        user.setUsername(username);
        return userMapper.selectOne(user);
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
}
