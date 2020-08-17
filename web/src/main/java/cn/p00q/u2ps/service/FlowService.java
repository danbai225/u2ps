package cn.p00q.u2ps.service;

import cn.p00q.u2ps.bean.DateVal;
import cn.p00q.u2ps.bean.Flow;
import cn.p00q.u2ps.entity.Node;
import cn.p00q.u2ps.entity.Tunnel;

import java.util.List;
import java.util.Map;

/**
 * @program: web
 * @description: 流量服务
 * @author: DanBai
 * @create: 2020-08-15 15:38
 **/
public interface FlowService {
    /**
     * 平台总流量
     * @return
     */
    Flow countFlow();

    /**
     * 获取近30天的平台流量GB
     * @return
     */
    List<DateVal> get30Days();

    /**
     * 获取30天用户流量 上下载一起 单位GB
     * @param username
     * @return
     */
    List<DateVal> get30DaysUserFlow(String username);

    /**
     * 获取node流量
     * @param nodes
     * @return
     */
    List<Map<String,Object>> getNodeFlow(List<Node> nodes);

    /**
     * 获取隧道流量  整合node
     * @param tunnels
     * @param nodes
     * @return
     */
    List<Map<String,Object>> getTunnelFlow(List<Tunnel> tunnels,Map<Integer,Node> nodes);
}
