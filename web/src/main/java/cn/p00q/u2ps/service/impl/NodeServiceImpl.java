package cn.p00q.u2ps.service.impl;

import cn.p00q.u2ps.bean.Result;
import cn.p00q.u2ps.entity.Client;
import cn.p00q.u2ps.entity.Node;
import cn.p00q.u2ps.entity.Tunnel;
import cn.p00q.u2ps.mapper.NodeMapper;
import cn.p00q.u2ps.mapper.TunnelMapper;
import cn.p00q.u2ps.service.ClientService;
import cn.p00q.u2ps.service.NodeService;
import cn.p00q.u2ps.service.PsService;
import cn.p00q.u2ps.utils.IpUtils;
import com.alibaba.dubbo.config.annotation.Reference;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.*;

/**
 * @program: u2ps
 * @description: node服务实现
 * @author: DanBai
 * @create: 2020-08-02 19:31
 **/
@Service
public class NodeServiceImpl implements NodeService {
    private  NodeMapper nodeMapper;
    private ClientService clientService;
    private TunnelMapper tunnelMapper;
    @Reference
    PsService psService;

    public NodeServiceImpl(NodeMapper nodeMapper, ClientService clientService, TunnelMapper tunnelMapper) {
        this.nodeMapper = nodeMapper;
        this.clientService = clientService;
        this.tunnelMapper = tunnelMapper;
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
    public Node getNodeById(int id) {
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
        if(node.getAllowWeb()==null){
            node.setAllowWeb(false);
        }
        node.setOpen(true);
        node.setOnline(false);
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
            Example commentExample = new Example(Tunnel.class);
            commentExample.createCriteria().andEqualTo("nodeId",id);
            tunnelMapper.deleteByExample(commentExample);
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
                List<Client> clientsByIp = clientService.getClientsByIp(node.getIp());
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

    @Override
    public int onlineNodeCount() {
        //按条件查询
        Node node = new Node();
        node.setOnline(true);
        return nodeMapper.selectCount(node);
    }

    @Override
    public List<Node> getList() {
        Example commentExample = new Example(Node.class);
        commentExample.selectProperties("id","ip","countriesRegions","ports","allowWeb","flowRatio","creationTime","bandwidth");
        commentExample.createCriteria().andEqualTo("open",true).andEqualTo("online",true);
        return nodeMapper.selectByExample(commentExample);
    }

    @Override
    public List<Node> getMyList(String Username) {
        Example commentExample = new Example(Node.class);
        commentExample.selectProperties("id","ip","open","ports","allowWeb","flowRatio","bandwidth","port");
        commentExample.createCriteria().andEqualTo("username",Username);
        return nodeMapper.selectByExample(commentExample);
    }

    @Override
    public Map<Integer,Node> getNodes(List<Tunnel> list) {
        Set<Integer> nodeIds = new HashSet<>();
        list.forEach((t) -> {
            nodeIds.add(t.getNodeId());
        });
        Example example = new Example(Node.class);
        Example.Criteria criteria = example.createCriteria();
        example.selectProperties("id","ip","online");
        nodeIds.forEach((id) -> {
            criteria.orEqualTo("id", id);
        });
        Map<Integer, Node> nodeMap = new HashMap<>();
        List<Node> nodes = nodeMapper.selectByExample(example);
        nodes.forEach(node -> nodeMap.put(node.getId(), node));

        return nodeMap;
    }

    @Override
    public List<Node> getNodesNewTunnel() {
        Example commentExample = new Example(Node.class);
        commentExample.selectProperties("id","countriesRegions","ports");
        commentExample.createCriteria().andEqualTo("open",true).andEqualTo("online",true);
        return nodeMapper.selectByExample(commentExample);
    }

    @Override
    public String getIp(Integer id) {
        Example commentExample = new Example(Node.class);
        commentExample.selectProperties("ip");
        commentExample.createCriteria().andEqualTo("id",id);
        return nodeMapper.selectOneByExample(commentExample).getIp();
    }
}
