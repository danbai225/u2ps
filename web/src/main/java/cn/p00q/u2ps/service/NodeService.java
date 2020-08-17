package cn.p00q.u2ps.service;

import cn.p00q.u2ps.bean.Result;
import cn.p00q.u2ps.entity.Node;
import cn.p00q.u2ps.entity.Tunnel;

import java.util.List;
import java.util.Map;

/**
 * @program: u2ps
 * @description: 节点服务器服务
 * @author: DanBai
 * @create: 2020-08-02 19:30
 **/
public interface NodeService {
    /**
     * 通过ip获取node
     * @param nodeIp IP
     * @return node
     */
    Node getNodeByIp(String nodeIp);

    /**
     * 设置是否在线
     * @param nodeIp
     * @param online
     */
    void setOnline(String nodeIp,boolean online);

    /**
     * 根据传入更新
     * @param node
     * @return
     */
    boolean updateByNode(Node node);

    /**
     * 根据隧道查询在线node
     * @param tunnels
     * @return
     */
    List<Node> getNodeByTunnelsOnLin(List<Tunnel> tunnels);
    /**
     * 通过iD获取node
     * @param id id
     * @return node
     */
    Node getNodeById(int id);

    /**
     * 创建节点
     * @param node
     * @return
     */
    Result create(Node node);

    /**
     * 删除node
     * @param id
     * @return
     */
    boolean delete(Integer id);

    /**
     * 根据id更新
     * @param node
     * @return
     */
    Result updateById(Node node);

    /**
     * 在线节点统计
     * @return
     */
    int onlineNodeCount();

    /**
     * 获取节点列表
     * @return
     */
    List<Node> getList();

    /**
     * 获取用户节点列表
     * @param Username
     * @return
     */
    List<Node> getMyList(String Username);

    /**
     * 获取节点列表
     * @param list
     * @return
     */
    Map<Integer,Node> getNodes(List<Tunnel> list);

    /**
     * 获取创建隧道时所需节点信息
     * @return
     */
    List<Node> getNodesNewTunnel();

    /**
     * 获取节点ip
     * @param id
     * @return
     */
    String getIp(Integer id);
}
