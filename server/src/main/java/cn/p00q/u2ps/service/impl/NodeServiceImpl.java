package cn.p00q.u2ps.service.impl;

import cn.p00q.u2ps.entity.Node;
import cn.p00q.u2ps.entity.Tunnel;
import cn.p00q.u2ps.mapper.NodeMapper;
import cn.p00q.u2ps.service.ClientServer;
import cn.p00q.u2ps.service.NodeService;
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
    private  NodeMapper nodeMapper;
    private  ClientServer clientServer;

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
    public Node getNode(Integer id) {
        Node node = new Node();
        node.setId(id);
        return nodeMapper.selectOne(node);
    }
}
