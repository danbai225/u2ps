package cn.p00q.u2ps.service.impl;

import cn.p00q.u2ps.bean.Result;
import cn.p00q.u2ps.entity.Client;
import cn.p00q.u2ps.entity.Node;
import cn.p00q.u2ps.entity.Tunnel;
import cn.p00q.u2ps.mapper.NodeMapper;
import cn.p00q.u2ps.service.ClientServer;
import cn.p00q.u2ps.service.NodeService;
import cn.p00q.u2ps.service.PsService;
import cn.p00q.u2ps.utils.IpUtils;
import com.alibaba.dubbo.config.annotation.Reference;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @program: u2ps
 * @description: node服务实现
 * @author: DanBai
 * @create: 2020-08-02 19:31
 **/
@Service
public class NodeServiceImpl implements NodeService {
    private final NodeMapper nodeMapper;
    private final ClientServer clientServer;
    @Reference
    PsService psService;
    public NodeServiceImpl(NodeMapper nodeMapper, ClientServer clientServer) {
        this.nodeMapper = nodeMapper;
        this.clientServer = clientServer;
    }

    @Override
    public Node getNodeByIp(String nodeIp) {
        Node node = new Node();
        node.setIp(nodeIp);
        return nodeMapper.selectOne(node);
    }

    @Override
    public void setOnline(String nodeIp, boolean online) {
        Node nodeByIp = getNodeByIp(nodeIp);
        if(nodeByIp!=null){
            nodeByIp.setOnline(online);
            updateByNode(nodeByIp);
        }
    }

    @Override
    public boolean updateByNode(Node node) {
        return nodeMapper.updateByPrimaryKey(node) == 1;
    }

    @Override
    public List<Node> getNodeByTunnelsOnLin(List<Tunnel> tunnels) {
        Set<Integer> nodeIds = new HashSet<>();
        tunnels.forEach((t) -> {
            nodeIds.add(t.getNodeId());
        });
        Example example = new Example(Node.class);
        Example.Criteria criteria = example.createCriteria();
        nodeIds.forEach((id) -> {
            criteria.orEqualTo("id", id);
        });
        criteria.andEqualTo("online", true);
        return nodeMapper.selectByExample(example);
    }

    @Override
    public Node getNodeById(Integer id) {
        Node node = new Node();
        node.setId(id);
        return nodeMapper.selectOne(node);
    }

    @Override
    public Result create(Node node) {
        Node nodeByIp = getNodeByIp(node.getIp());
        if (nodeByIp != null) {
            return Result.err("相同ip节点已存在");
        }
        if (!IpUtils.verifyPorts(node.getPorts())){
            return Result.err("端口范围描述不规范 例:225/2255");
        }
        if (node.getPort() < 1 || node.getPort() > 65535) {
            return Result.err("端口不正确,1-65535");
        }
        node.setCountriesRegions(IpUtils.getCityInfo(node.getIp()));
        if (nodeMapper.insert(node) > 0) {
            return Result.success("创建成功");
        }
        return null;
    }

    @Override
    public boolean delete(Integer id) {
        Node nodeById = getNodeById(id);
        if(nodeById==null){
            return true;
        }
        if(nodeMapper.delete(nodeById)>0){
            psService.deleteNode(nodeById.getIp());
            return true;
        }
        return false;
    }

    @Override
    public Result updateById(Node node) {
        Node nodeById = getNodeById(node.getId());
        if (node.getPort() != null&&!nodeById.getPort().equals(node.getPort())) {
            if (psService.isNodePortUse(nodeById.getIp(), node.getPort())) {
                return Result.err("服务端口被占用,请尝试换一个端口");
            }
        }
        if (StringUtils.isNotEmpty(node.getPorts())) {
            if (!IpUtils.verifyPorts(node.getPorts())) {
                return Result.err("端口范围描述不规范 例:225/2255");
            }
        }
        if (node.getOpen() != null) {
            nodeById.setOpen(node.getOpen());
        }
        if (node.getMaxTunnel() != null) {
            if (node.getMaxTunnel() < 1) {
                node.setMaxTunnel(100);
            }
        }
        if (nodeMapper.updateByPrimaryKeySelective(node) > 0) {
            Node nowNode = getNodeById(node.getId());
            //客户端更新
            if (!nodeById.getPort().equals(node.getPort())) {
                List<Client> clientsByIp = clientServer.getClientsByIp(node.getIp());
                clientsByIp.forEach(client -> {
                    new Thread(() -> {
                        psService.updateNodeToC(client.getId(),nowNode);
                    }).start();
                });

            }
            //node更新
            psService.updateNodeToN(nowNode.getIp(), nowNode);
            return Result.success("更新成功");
        }
        return Result.err("更新失败");
    }
}
