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
     * 查询用户的所有隧道
     * @param username
     * @return
     */
    List<Tunnel> getTunnelsByUsername(String username);
    /**
     * 查询客户端的所有隧道
     * @param id
     * @return
     */
    List<Tunnel> getTunnelsByClientId(Integer id);

    /**
     * 获取隧道根据id
     * @param id
     * @return
     */
    Tunnel getTunnelById(Integer id);
    /**
     * 更新隧道信息根据ID
     * @param tunnel
     * @return
     */
    Result updateById(Tunnel tunnel);

    /**
     * 创建新的隧道
     * @param tunnel
     * @return
     */
    Result create(Tunnel tunnel);

    /**
     * 删除隧道
     * @param id
     * @return
     */
    Result delete(Integer id);

    /**
     * 获取用户隧道数量
     * @param username
     * @return
     */
    int getUserTunnelNum(String username);
}
