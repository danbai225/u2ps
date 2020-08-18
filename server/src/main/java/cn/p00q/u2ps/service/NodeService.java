package cn.p00q.u2ps.service;

import cn.p00q.u2ps.bean.Result;
import cn.p00q.u2ps.entity.Node;
import cn.p00q.u2ps.entity.Tunnel;

import java.util.List;

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
     * 获取倍率
     * @param id
     * @return
     */
    Node getNode(Integer id);
}
