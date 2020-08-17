package cn.p00q.u2ps.service.impl;

import cn.p00q.u2ps.entity.Node;
import cn.p00q.u2ps.entity.Tunnel;
import cn.p00q.u2ps.mapper.TunnelMapper;
import cn.p00q.u2ps.service.TunnelService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @program: u2ps
 * @description: 隧道服务实现
 * @author: DanBai
 * @create: 2020-08-04 13:42
 **/
@Service
public class TunnelServiceImpl implements TunnelService {
    private  NodeServiceImpl nodeService;
    private  TunnelMapper tunnelMapper;
    private  ClientServerImpl clientServer;
    private  RedisTemplate redisTemplate;

    public TunnelServiceImpl(NodeServiceImpl nodeService, TunnelMapper tunnelMapper, ClientServerImpl clientServer, RedisTemplate redisTemplate ) {
        this.nodeService = nodeService;
        this.tunnelMapper = tunnelMapper;
        this.clientServer = clientServer;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public List<Tunnel> getTunnelsByNodeIp(String nodeIp) {
        Node nodeByIp = nodeService.getNodeByIp(nodeIp);
        if(nodeByIp==null){
            return null;
        }
        return getTunnelsByNodeId(nodeByIp.getId());
    }

    @Override
    public List<Tunnel> getTunnelsByNodeId(Integer id) {
        Tunnel tunnel = new Tunnel();
        tunnel.setNodeId(id);
        List<Tunnel> select = tunnelMapper.select(tunnel);
        if(select.size()>0){
            return select;
        }
        return null;
    }

    @Override
    public List<Tunnel> getTunnelsByClientId(Integer id) {
        Tunnel tunnel = new Tunnel();
        tunnel.setClientId(id);
        return tunnelMapper.select(tunnel);
    }

    @Override
    public Tunnel getById(Integer id) {
        Tunnel tunnel = new Tunnel();
        tunnel.setId(id);
        return tunnelMapper.selectOne(tunnel);
    }

    @Override
    public List<Tunnel> getTunnelsByUsername(String username) {
        Tunnel tunnel = new Tunnel();
        tunnel.setUsername(username);
        return tunnelMapper.select(tunnel);
    }

    @Override
    public void deleteTunnelByNodeIp(Integer tunnelId, String nIp) {
        Node nodeByIp = nodeService.getNodeByIp(nIp);
        Tunnel byId = getById(tunnelId);
        if(nodeByIp!=null&&byId!=null){
            tunnelMapper.delete(byId);
        }
    }
}
