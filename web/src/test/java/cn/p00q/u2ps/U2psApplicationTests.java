package cn.p00q.u2ps;

import cn.p00q.u2ps.entity.Node;
import cn.p00q.u2ps.entity.Tunnel;
import cn.p00q.u2ps.service.impl.ClientServerImpl;
import cn.p00q.u2ps.service.impl.NodeServiceImpl;
import cn.p00q.u2ps.service.impl.TunnelServiceImpl;
import cn.p00q.u2ps.utils.IpUtils;
import com.alibaba.fastjson.JSON;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class U2psApplicationTests {

    @Autowired
    TunnelServiceImpl tunnelService;
    @Autowired
    NodeServiceImpl nodeService;
    @Autowired
    ClientServerImpl clientServer;
    @Test
    void contextLoads() {
    }
    //根据隧道查询在线的node
    @Test
    void getNodeByTunnelsOnLin(){
        List<Tunnel> danbai = tunnelService.getTunnelsByUsername("danbai");
        List<Node> nodeByTunnels = nodeService.getNodeByTunnelsOnLin(danbai);
        System.out.println(JSON.toJSONString(danbai));
        System.out.println(JSON.toJSONString(nodeByTunnels));
    }
    @Test
    void getClientById(){
        System.out.println(JSON.toJSONString(clientServer.getClientById(1)));
    }

    @Test
    void getIpInfo(){
        String ip = "182.136.38.222";
        System.out.println(IpUtils.getCityInfo(ip));
    }
}
