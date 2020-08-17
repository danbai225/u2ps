package cn.p00q.u2ps.service.impl;

import cn.p00q.u2ps.bean.Result;
import cn.p00q.u2ps.entity.Client;
import cn.p00q.u2ps.entity.Node;
import cn.p00q.u2ps.entity.Tunnel;
import cn.p00q.u2ps.mapper.TunnelMapper;
import cn.p00q.u2ps.service.PsService;
import cn.p00q.u2ps.service.TunnelService;
import cn.p00q.u2ps.service.UserService;
import cn.p00q.u2ps.utils.IpUtils;
import com.alibaba.dubbo.config.annotation.Reference;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

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
    private ClientServiceImpl clientServer;
    private  RedisTemplate redisTemplate;
    private  UserService userService;
    @Reference
    PsService psService;


    public TunnelServiceImpl(NodeServiceImpl nodeService, TunnelMapper tunnelMapper, ClientServiceImpl clientServer, RedisTemplate redisTemplate, UserService userService) {
        this.nodeService = nodeService;
        this.tunnelMapper = tunnelMapper;
        this.clientServer = clientServer;
        this.redisTemplate = redisTemplate;
        this.userService = userService;
    }

    @Override
    public List<Tunnel> getTunnelsByNodeIp(String nodeIp) {
        Node nodeByIp = nodeService.getNodeByIp(nodeIp);
        return getTunnelsByNodeId(nodeByIp.getId());
    }

    @Override
    public List<Tunnel> getTunnelsByNodeId(Integer id) {
        Tunnel tunnel = new Tunnel();
        tunnel.setNodeId(id);
        return tunnelMapper.select(tunnel);
    }

    @Override
    public List<Tunnel> getTunnelsByUsername(String username) {
        Tunnel tunnel = new Tunnel();
        tunnel.setUsername(username);
        return tunnelMapper.select(tunnel);
    }

    @Override
    public List<Tunnel> getTunnelsByClientId(Integer id) {
        Tunnel tunnel = new Tunnel();
        tunnel.setClientId(id);
        return tunnelMapper.select(tunnel);
    }


    @Override
    public Tunnel getTunnelById(Integer id) {
        Tunnel tunnel = new Tunnel();
        tunnel.setId(id);
        return tunnelMapper.selectOne(tunnel);
    }

    @Override
    public Result updateById(Tunnel tunnel) {
        Tunnel byId = getTunnelById(tunnel.getId());
        if(tunnel.getOpen()!=null&&tunnel.getOpen()){
            if(userService.getUserByUsername(byId.getUsername()).getFlow()<0){
                return Result.err("您的流量已经不足以启动隧道");
            }
        }
        Node nodeById = nodeService.getNodeById(byId.getNodeId());
        //如果服务端口和原来一样就=null 不做端口占用判断
        if (tunnel.getServicePort() != null) {
            if (tunnel.getServicePort().equals(byId.getServicePort())) {
                tunnel.setServicePort(null);
            }
        }
        //是否更新服务端口
        if (tunnel.getServicePort() != null) {
            String ports = nodeById.getPorts();
            //是否符合node允许的端口
            if (!IpUtils.verifyPort(ports, tunnel.getServicePort())) {
                return Result.err("服务端口不在运行范围内:"+ports);
            }
            Example example = new Example(Tunnel.class);
            example.createCriteria().andEqualTo("nodeId",tunnel.getNodeId()).andEqualTo("servicePort", tunnel.getServicePort());
            if(tunnelMapper.selectCountByExample(example)>0){
                return Result.err("服务端口被占用,请尝试换一个端口");
            }
            //询问端口是否可以使用(是否占用)
            if (psService.isNodePortUse(nodeById.getIp(), tunnel.getServicePort())) {
                return Result.err("服务端口被占用,请尝试换一个端口");
            }
        }
        //是否更新类型
        if (tunnel.getType() != null) {
            if (tunnel.getType() < 1 || tunnel.getType() > 4) {
                return Result.err("类型不符合 1.tcp 2.udp 3.http 4.socks5");
            }
            if(tunnel.getType().equals(3)&&!nodeById.getAllowWeb()){
                return Result.err("该节点不允许web(HTTP)代理.");
            }
        }
        //目标ip是否符合ipv4标准
        if (StringUtils.isNotEmpty(tunnel.getTargetIp())) {
            if (!IpUtils.isValidIPV4(tunnel.getTargetIp())) {
                return Result.err("目标ip不正确");
            }
        }
        //是否更新目标端口
        if (tunnel.getTargetPort() != null) {
            if (tunnel.getTargetPort() < 1 || tunnel.getTargetPort() > 65535) {
                return Result.err("目标端口不正确1-65535");
            }
        }
        //更新
        if (tunnelMapper.updateByPrimaryKeySelective(tunnel) > 0) {
            Tunnel nowTunnel = getTunnelById(tunnel.getId());
            //向node和客户端发送更新信息
            if(nowTunnel.getOpen()){
                psService.updateTunnel(nowTunnel, nodeById.getIp());
            }
            return Result.success("更新成功");
        }
        return Result.err("您的参数正确,但更新失败");
    }

    @Override
    public Result create(Tunnel tunnel) {
        tunnel.setOpen(true);
        Node node = nodeService.getNodeById(tunnel.getNodeId());
        Client client =clientServer.getClientById(tunnel.getClientId());
        if (node != null) {
            if(client!=null){
                String ports = node.getPorts();
                //是否符合node允许的端口
                if (!IpUtils.verifyPort(ports, tunnel.getServicePort())) {
                    return Result.err("服务端口不在运行范围内:"+ports);
                }
                Example example = new Example(Tunnel.class);
                example.createCriteria().andEqualTo("nodeId",tunnel.getNodeId()).andEqualTo("servicePort", tunnel.getServicePort());
                if(tunnelMapper.selectCountByExample(example)>0){
                    return Result.err("服务端口被占用,请尝试换一个端口");
                }
                //询问端口是否可以使用(是否占用)
                if (psService.isNodePortUse(node.getIp(), tunnel.getServicePort())) {
                    return Result.err("服务端口被占用,请尝试换一个端口");
                }
                //隧道类型
                if (tunnel.getType() < 1 || tunnel.getType() > 4) {
                    return Result.err("类型不符合 1.tcp 2.udp 3.http 4.socks5");
                }
                if(tunnel.getType().equals(3)&&!node.getAllowWeb()){
                    return Result.err("该节点不允许web(HTTP)代理.");
                }
                if (!IpUtils.isValidIPV4(tunnel.getTargetIp())) {
                    return Result.err("目标ip不正确");
                }
                if (tunnel.getTargetPort() < 1 || tunnel.getTargetPort() > 65535) {
                    return Result.err("目标端口不正确1-65535");
                }
                if(tunnelMapper.insert(tunnel)>0){
                    psService.updateTunnel(tunnel, node.getIp());
                    return Result.success("创建成功");
                }
                return Result.err("您的参数正确,创建失败");
            }
            return Result.err("客户端不存在");
        }
        return Result.err("节点不存在");
    }

    @Override
    public Result delete(Integer id) {
        Tunnel tunnel = getTunnelById(id);
        if(tunnel!=null){
            Node node = nodeService.getNodeById(tunnel.getNodeId());
            tunnelMapper.delete(tunnel);
            psService.deleteTunnel(tunnel, node.getIp());
            return Result.success("删除成功");
        }
        return null;
    }

    @Override
    public int getUserTunnelNum(String username) {
        //统计总数
        Example example = new Example(Tunnel.class);
        example.createCriteria().andEqualTo("username",username);
        int count = tunnelMapper.selectCountByExample(example);
        return count;
    }

}
