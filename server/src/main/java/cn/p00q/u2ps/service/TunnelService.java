package cn.p00q.u2ps.service;

import cn.p00q.u2ps.bean.Result;
import cn.p00q.u2ps.entity.Tunnel;

import java.util.List;

/**
 * @program: u2ps
 * @description: 隧道服务接口
 * @author: DanBai
 * @create: 2020-08-04 13:41
 **/
public interface TunnelService {
    /**
     * 获取隧道根据节点Ip
     * @param nodeIp 节点ip
     * @return
     */
    List<Tunnel> getTunnelsByNodeIp(String nodeIp);

    /**
     * 获取隧道根据节点Id
     * @param id 节点id
     * @return
     */
    List<Tunnel> getTunnelsByNodeId(Integer id);
    /**
     * 查询客户端的所有隧道
     * @param id
     * @return
     */
    List<Tunnel> getTunnelsByClientId(Integer id);

    /**
     * 获取隧道
     * @param id
     * @return
     */
    Tunnel getById(Integer id);
    /**
     * 查询用户的所有隧道
     * @param username
     * @return
     */
    List<Tunnel> getTunnelsByUsername(String username);

    /**
     * 删除节点隧道
     * @param tunnelId
     * @param nIp
     */
    void deleteTunnelByNodeIp(Integer tunnelId,String nIp);
}
