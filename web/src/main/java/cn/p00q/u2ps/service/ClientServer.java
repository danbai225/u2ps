package cn.p00q.u2ps.service;

import cn.p00q.u2ps.bean.Result;
import cn.p00q.u2ps.entity.Client;

import java.util.List;

/**
 * @program: u2ps
 * @description: 客户端接口
 * @author: DanBai
 * @create: 2020-08-04 21:59
 **/
public interface ClientServer {
    /**
     * 根据Key 获取客户端
     * @param key key
     * @return
     */
    Client getClientByKey(String key);
    /**
     * 设置是否在线
     * @param clientId
     * @param online
     */
    void setOnline(Integer clientId,boolean online);

    /**
     * 获取客户端根据ID
     * @param id
     * @return
     */
    Client getClientById(Integer id);

    /**
     * 设置在线状态和ip
     * @param clientId
     * @param online
     * @param Ip
     */
    void setOnlineAndIp(Integer clientId,boolean online,String Ip);

    /**
     * 根据ip获取客户端列表
     * @param ip
     * @return
     */
    List<Client> getClientsByIp(String ip);

    /**
     * 统计在线客户端
     * @return
     */
    Integer onlineClientCount();

    /**
     * 获取用户客户端
     * @param username
     * @return
     */
    List<Client> getClientByUsername(String username);

    /**
     * 根据id更新
     * @param c 更新信息
     * @return
     */
    Result updateById(Client c);

    /**
     * 删除
     * @param id
     * @return
     */
    boolean delete(Integer id);

    /**
     * 创建客户端
     * @param client
     * @return
     */
    Result create(Client client);

    /**
     * 获取用户客户端
     * @param username
     * @return
     */
    List<Client> getUserClientNewTunnel(String username);
}
