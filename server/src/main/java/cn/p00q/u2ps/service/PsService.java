package cn.p00q.u2ps.service;

import cn.p00q.u2ps.entity.Node;
import cn.p00q.u2ps.entity.Tunnel;

/**
 * @program: server
 * @description: ps服务接口
 * @author: DanBai
 * @create: 2020-08-13 14:48
 **/
public interface PsService {
    /**
     * 测试
     * @return
     */
    boolean test();

    /**
     * 删除节点
     * @param ip
     */
    void deleteNode(String ip);

    /**
     * 节点端口占用
     * @param ip
     * @param port
     * @return
     */
    boolean isNodePortUse(String ip,Integer port);

    /**
     * 向客户端更新节点
     * @param id
     * @param node
     */
    void updateNodeToC(Integer id, Node node);

    /**
     * 向节点更新节点
     * @param ip
     * @param node
     */
    void updateNodeToN(String ip, Node node);

    /**
     * 更新隧道
     * @param tunnel
     * @param nIp
     */
    void updateTunnel(Tunnel tunnel,String nIp);

    /**
     * 删除隧道
     * @param tunnel
     * @param nIp
     */
    void deleteTunnel(Tunnel tunnel,String nIp);
}
