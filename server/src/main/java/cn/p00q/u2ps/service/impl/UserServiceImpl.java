package cn.p00q.u2ps.service.impl;


import cn.p00q.u2ps.bean.Flow;
import cn.p00q.u2ps.entity.Node;
import cn.p00q.u2ps.entity.Tunnel;
import cn.p00q.u2ps.entity.User;
import cn.p00q.u2ps.mapper.TunnelMapper;
import cn.p00q.u2ps.mapper.UserMapper;
import cn.p00q.u2ps.service.NodeService;
import cn.p00q.u2ps.service.TunnelService;
import cn.p00q.u2ps.service.UserService;
import cn.p00q.u2ps.utils.DateUtils;
import cn.p00q.u2ps.utils.SpringUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
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
    private static ConcurrentHashMap<String, Long> UserFlowCache = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String, Date> UserFlowUpdateTime = new ConcurrentHashMap<>();
    private UserMapper userMapper;
    private RedisTemplate redisTemplate;
    private TunnelService tunnelService;
    private NodeService nodeService;
    private TunnelMapper tunnelMapper;

    public UserServiceImpl(UserMapper userMapper, RedisTemplate redisTemplate, TunnelService tunnelService, NodeService nodeService, TunnelMapper tunnelMapper) {
        this.userMapper = userMapper;
        this.redisTemplate = redisTemplate;
        this.tunnelService = tunnelService;
        this.nodeService = nodeService;
        this.tunnelMapper = tunnelMapper;
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

    @Override
    public void flowCalculation(Integer tid, Long flow) {
        Tunnel byId = tunnelService.getById(tid);
        if (byId==null){
            return;
        }
        Node nodeById = nodeService.getNode(byId.getNodeId());
        if (nodeById == null) {
            return;
        }
        BigDecimal f = BigDecimal.valueOf(flow).setScale(0, BigDecimal.ROUND_HALF_UP);
        f = f.multiply(BigDecimal.valueOf(nodeById.getFlowRatio()));
        flow = f.longValue();
        Long userFlowCache = UserFlowCache.get(byId.getUsername());
        //缓存
        if (userFlowCache == null) {
            UserFlowCache.put(byId.getUsername(), flow);
            return;
        }
        Date update = UserFlowUpdateTime.get(byId.getUsername());

        Date date = new Date();
        if (update != null && (date.getTime() - update.getTime()) < DateUtils.Second * 10&&userFlowCache<Flow.MB100) {
            userFlowCache += flow;
            UserFlowCache.put(byId.getUsername(), userFlowCache);
            return;
        }
        UserFlowUpdateTime.put(byId.getUsername(), date);
        flow += userFlowCache;
        UserFlowCache.remove(byId.getUsername());
        //单位kb
        Integer FlowToday = (Integer) redisTemplate.opsForValue().get(Flow.UserFlowToDayPrefix + byId.getUsername() + DateUtils.getDay());
        if (FlowToday == null) {
            redisTemplate.opsForValue().set(Flow.UserFlowToDayPrefix + byId.getUsername() + DateUtils.getDay(), (int) (flow / 1024), 30, TimeUnit.DAYS);
        } else {
            FlowToday += (int) (flow / 1024);
            redisTemplate.opsForValue().set(Flow.UserFlowToDayPrefix + byId.getUsername() + DateUtils.getDay(), FlowToday, 30, TimeUnit.DAYS);
        }
        if (byId != null) {
            //获取俩用户
            String username = byId.getUsername();
            if (username.equals(nodeById.getUsername())) {
                return;
            }
            User srcUser = getUserByUsername(username);
            User tagUser = getUserByUsername(nodeById.getUsername());
            userMapper.updateFlow(srcUser.getId(), srcUser.getFlow() - flow);
            userMapper.updateFlow(tagUser.getId(), srcUser.getFlow() + flow);

            //流量不足停止隧道
            if (srcUser.getFlow() < 0) {
                List<Tunnel> tunnelsByUsername = tunnelService.getTunnelsByUsername(username);
                List<Node> nodeByTunnelsOnLin = nodeService.getNodeByTunnelsOnLin(tunnelsByUsername);
                Map<Integer, Node> nodeMap = new HashMap<>(8);
                nodeByTunnelsOnLin.forEach(node -> nodeMap.put(node.getId(), node));
                PsServiceImpl ps = SpringUtil.getBean(PsServiceImpl.class);
                tunnelsByUsername.forEach(tunnel -> {
                    if (tunnel.getOpen()) {
                        tunnel.setOpen(false);
                        assert ps != null;
                        ps.updateTunnel(tunnel, nodeMap.get(tunnel.getNodeId()).getIp());
                        tunnelMapper.updateByPrimaryKey(tunnel);
                    }
                });
            }
            userMapper.updateByPrimaryKey(srcUser);
            userMapper.updateByPrimaryKey(tagUser);
        }
    }
}
