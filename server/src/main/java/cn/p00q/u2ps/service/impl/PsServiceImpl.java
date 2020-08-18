package cn.p00q.u2ps.service.impl;

import cn.p00q.u2ps.bean.Flow;
import cn.p00q.u2ps.bean.FlowType;
import cn.p00q.u2ps.bean.Msg;
import cn.p00q.u2ps.entity.Client;
import cn.p00q.u2ps.entity.Node;
import cn.p00q.u2ps.entity.Tunnel;
import cn.p00q.u2ps.entity.User;
import cn.p00q.u2ps.service.*;
import cn.p00q.u2ps.utils.DateUtils;
import cn.p00q.u2ps.utils.IpUtils;
import cn.p00q.u2ps.utils.MapUtil;
import cn.p00q.u2ps.utils.SpringUtil;
import com.alibaba.fastjson.JSON;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;

/**
 * @program: server
 * @description: ps实现类
 * @author: DanBai
 * @create: 2020-08-13 14:49
 **/
@org.springframework.stereotype.Service()
@Slf4j
@com.alibaba.dubbo.config.annotation.Service
public class PsServiceImpl implements PsService {
    private static Integer AskNodeNum;
    private static Integer AskNodeInterval;
    private static Flow CacheFlowCount = new Flow(0L, 0L);
    private static Flow CacheFlowToday = new Flow(0L, 0L);
    private static ConcurrentHashMap<Integer, ChannelHandlerContext> clientCtxMap;
    private static ConcurrentHashMap<String, ChannelHandlerContext> nodeCtxMap;
    private static ConcurrentHashMap<String, String> usernameMap;
    private static HashMap<String, Boolean> HeartbeatNMap;
    private static HashMap<Integer, Boolean> HeartbeatCMap;
    private RedisTemplate redisTemplate;
    private UserService userService;
    private NodeService nodeService;
    private TunnelService tunnelService;
    private ClientServer clientServer;

    static {
        clientCtxMap = new ConcurrentHashMap<>();
        nodeCtxMap = new ConcurrentHashMap<>();
        usernameMap = new ConcurrentHashMap<>();
        HeartbeatNMap=new HashMap<>();
        HeartbeatCMap=new HashMap<>();
    }

    @Value("${u2ps.askNode.num:100}")
    public void setAskNodeNum(Integer askNodeNum) {
        AskNodeNum = askNodeNum;
    }

    @Value("${u2ps.askNode.interval:100}")
    public void setAskNodeInterval(Integer askNodeInterval) {
        AskNodeInterval = askNodeInterval;
    }

    public PsServiceImpl(RedisTemplate redisTemplate, UserService userService, NodeService nodeService, TunnelService tunnelService, ClientServer clientServer) {
        this.redisTemplate = redisTemplate;
        this.userService = userService;
        this.nodeService = nodeService;
        this.tunnelService = tunnelService;
        this.clientServer = clientServer;
    }

    /**
     * 节点or普通用户判断
     *
     * @param ctx 连接
     * @return 是否
     */
    public boolean isNode(ChannelHandlerContext ctx) {
        String ip = IpUtils.GetIPByCtx(ctx);
        if (!nodeCtxMap.containsKey(ip)) {
            return false;
        }
        List<Client> clientsByIp = clientServer.getClientsByIp(ip);
        for (Client client : clientsByIp) {
            ChannelHandlerContext clientCtx = getClientCtx(client.getId());
            if (clientCtx != null) {
                if (clientCtx.channel().id().asShortText().equals(ctx.channel().id().asShortText())) {
                    return false;
                }
            }
        }
        return true;
    }

    public static void addClientCtx(Integer clientId, ChannelHandlerContext ctx) {
        clientCtxMap.put(clientId, ctx);
    }

    public static void addNodeCtx(String ip, ChannelHandlerContext ctx) {
        nodeCtxMap.put(ip, ctx);
    }

    public static void addUsername(String id, String name) {
        usernameMap.put(id, name);
    }

    public static String getUsername(String id) {
        return usernameMap.get(id);
    }

    public static ChannelHandlerContext getClientCtx(Integer clientId) {
        try {
            return clientCtxMap.get(clientId);
        } catch (NullPointerException ignored) {
        }
        return null;
    }

    public static ChannelHandlerContext getNodeCtx(String ip) {
        return nodeCtxMap.get(ip);
    }

    public static void sendMsg(ChannelHandlerContext ctx, String type, String msg, Object data) {
        sendMsg(ctx, new Msg(type, msg, JSON.toJSONString(data)).toJsonStr() + System.getProperty("line.separator"));
    }

    public static void sendMsg(ChannelHandlerContext ctx, String type, String msg) {
        sendMsg(ctx, new Msg(type, msg, null).toJsonStr() + System.getProperty("line.separator"));
    }

    public static void sendDataMsg(ChannelHandlerContext ctx, String type, Object data) {
        sendMsg(ctx, new Msg(type, "", JSON.toJSONString(data)).toJsonStr() + System.getProperty("line.separator"));
    }

    public static void sendMsg(ChannelHandlerContext ctx, String msg) {
        try {
            byte[] reqMsgByte = msg.getBytes(StandardCharsets.UTF_8);
            ByteBuf reqByteBuf = Unpooled.buffer(reqMsgByte.length);
            reqByteBuf.writeBytes(reqMsgByte);
            ctx.writeAndFlush(reqByteBuf);
        } catch (Exception e) {
            PsServiceImpl psServer = SpringUtil.getBean(PsServiceImpl.class);
            psServer.disconnect(ctx);
            log.error(e.getMessage());
            e.printStackTrace();
        }
    }


    /**
     * 验证连接
     *
     * @param ctx
     * @param msg
     */
    public void Authentication(ChannelHandlerContext ctx, Msg msg) {
        //客户端认证
        String ipAddr = IpUtils.GetIPByCtx(ctx);
        if (msg.getType().equals(Msg.AuthenticationClient)) {
            Client clientByKey = clientServer.getClientByKey(Msg.getStringData(msg));
            if (clientByKey != null) {
                //更新客户端在线
                clientServer.setOnlineAndIp(clientByKey.getId(), true, ipAddr);
                //添加到客户端Map
                addClientCtx(clientByKey.getId(), ctx);
                Map<String, Object> rs = new HashMap<>(4);
                //返回客户端所需数据
                List<Tunnel> tunnelsByClientId = tunnelService.getTunnelsByClientId(clientByKey.getId());
                List<Node> nodeByTunnels = nodeService.getNodeByTunnelsOnLin(tunnelsByClientId);
                rs.put("tunnels", tunnelsByClientId);
                rs.put("nodes", nodeByTunnels);
                rs.put("client", clientByKey);
                rs.put("versions", redisTemplate.opsForValue().get("up2s_versions"));
                sendMsg(ctx, Msg.TypeAuthenticationResultOk, "连接服务端成功!", rs);
            } else {
                sendMsg(ctx, Msg.AuthenticationResultErr, "请检查你的Key是否正确!");
                ctx.close();
            }
            return;
        }
        //Node认证
        Node nodeByIp = nodeService.getNodeByIp(ipAddr);
        User user = userService.checkToken(Msg.getStringData(msg));
        if (user != null) {
            addUsername(ctx.channel().id().asShortText(), user.getUsername());
            if (nodeByIp != null) {
                addNodeCtx(ipAddr, ctx);
                //更改服务器在线状态
                nodeService.setOnline(ipAddr, true);
                Map<String, Object> rs = new HashMap<>(2);
                rs.put("node", nodeByIp);
                rs.put("versions", redisTemplate.opsForValue().get("up2s_versions"));
                sendMsg(ctx, Msg.TypeAuthenticationResultOk, "连接服务端成功!", rs);

                List<Tunnel> tunnelsByNodeId = tunnelService.getTunnelsByNodeId(nodeByIp.getId());
                List<Client> clientsByIp = clientServer.getClientsByIp(nodeByIp.getIp());
                //向在线的客户端更新节点
                if(clientsByIp!=null){
                    clientsByIp.forEach(client -> {
                        ChannelHandlerContext clientCtx = getClientCtx(client.getId());
                        if(clientCtx!=null){
                            updateNode(clientCtx,nodeByIp);
                        }
                    });
                }
                //更新隧道
                if(tunnelsByNodeId!=null){
                    tunnelsByNodeId.forEach(tunnel -> {
                        ChannelHandlerContext clientCtx = getClientCtx(tunnel.getClientId());
                        if(clientCtx!=null){
                            updateTunnel(clientCtx,tunnel);
                        }
                    });
                }
                return;
            }
            sendMsg(ctx, Msg.AuthenticationResultErr, "没有找到节点信息!");
            ctx.close();
            return;
        }
        sendMsg(ctx, Msg.AuthenticationResultErr, "请检查你的Token是否正确!");
        ctx.close();

    }

    /**
     * 向节点发送它的所有隧道信息
     *
     * @param ctx
     */
    public void sendAllTunnel(ChannelHandlerContext ctx) {
        if (isNode(ctx)) {
            List<Tunnel> tunnelsByNodeIp = tunnelService.getTunnelsByNodeIp(IpUtils.GetIPByCtx(ctx));
            sendDataMsg(ctx, Msg.AllTunnel, tunnelsByNodeIp);
        }
    }

    /**
     * 连接断开处理
     *
     * @param ctx
     */
    public void disconnect(ChannelHandlerContext ctx) {
        String ip = IpUtils.GetIPByCtx(ctx);
        if (isNode(ctx)) {
            log.info("Node端退出");
            nodeCtxMap.remove(ip);
            //改变节点状态
            nodeService.setOnline(ip, false);
        } else {
            log.info("客户端退出");
            //改变客户端状态
            List<Integer> key = (List<Integer>) MapUtil.getKey(clientCtxMap, ctx);
            if (key.size() > 0) {
                Integer id = key.get(0);
                clientCtxMap.remove(id);
                clientServer.setOnline(id, false);
            }
        }
    }

    /**
     * 询问Node端口是否被占用
     *
     * @param ip
     * @param port
     * @return
     */
    @Override
    public boolean isNodePortUse(String ip, Integer port) {
        ChannelHandlerContext ctx = nodeCtxMap.get(ip);
        if (ctx != null) {
            String Key = UUID.randomUUID().toString().replace("-", "");
            sendMsg(ctx, Msg.IsPortUse, Key, port);
            for (int i = 0; i < AskNodeNum; i++) {
                Object r = redisTemplate.opsForValue().get(Key);
                if (r != null) {
                    redisTemplate.delete(Key);
                    return (boolean) r;
                }
                try {
                    Thread.sleep(AskNodeInterval);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }

    @Override
    public void updateNodeToC(Integer id, Node node) {
        updateNode(getClientCtx(id), node);
    }

    @Override
    public void updateNodeToN(String ip, Node node) {
        updateNode(getNodeCtx(ip), node);
    }

    @Override
    public void updateTunnel(Tunnel tunnel, String nIp) {
        //客户端
        ChannelHandlerContext clientCtx = getClientCtx(tunnel.getClientId());
        if (clientCtx != null) {
            updateTunnel(clientCtx, tunnel);
            updateNode(clientCtx, nodeService.getNode(tunnel.getNodeId()));
        }
        //node
        ChannelHandlerContext nodeCtx = getNodeCtx(nIp);
        if (nodeCtx != null) {
            updateTunnel(nodeCtx, tunnel);
        }
    }

    @Override
    public void deleteTunnel(Tunnel tunnel, String nIp) {
        //客户端
        ChannelHandlerContext clientCtx = getClientCtx(tunnel.getClientId());
        if (clientCtx != null) {
            deleteTunnel(clientCtx, tunnel.getId());
        }
        //node
        ChannelHandlerContext nodeCtx = getNodeCtx(nIp);
        if (nodeCtx != null) {
            deleteTunnel(nodeCtx, tunnel.getId());
        }
    }

    /**
     * node返回应答处理
     *
     * @param msg
     */
    public void isNodePortUse(Msg msg) {
        redisTemplate.opsForValue().set(msg.getMsg(), Msg.getBooleanData(msg), 1, TimeUnit.MINUTES);
    }

    public void TcpWeb(ChannelHandlerContext ctx, Msg msg) {
        Integer tid = Msg.getData(msg, Integer.class);
        tunnelService.deleteTunnelByNodeIp(tid, IpUtils.GetIPByCtx(ctx));
    }

    /**
     * 更新 增加 隧道
     *
     * @param ctx
     * @param tunnel
     */
    public void updateTunnel(ChannelHandlerContext ctx, Tunnel tunnel) {
        sendDataMsg(ctx, Msg.UpdateTunnel, tunnel);
    }

    /**
     * 删除隧道
     *
     * @param ctx
     * @param id
     */
    public void deleteTunnel(ChannelHandlerContext ctx, Integer id) {
        sendDataMsg(ctx, Msg.DeleteTunnel, id);
    }

    /**
     * 删除node
     *
     * @param ctx
     */
    public void deleteNode(ChannelHandlerContext ctx) {
        sendDataMsg(ctx, Msg.DeleteNode, null);
    }

    /**
     * 更新节点
     *
     * @param ctx
     * @param node
     */
    public void updateNode(ChannelHandlerContext ctx, Node node) {
        sendDataMsg(ctx, Msg.UpdateNode, node);
    }

    /**
     * 流量更新
     *
     * @param msg
     */
    public void updateFlow(Msg msg) {
        FlowType flowType = Msg.getData(msg, FlowType.class);
        if (flowType.getNodeId() == -1) {
            Flow tunnelFlow = (Flow) redisTemplate.opsForValue().get(Flow.TunnelFlowPrefix + flowType.getTunnelId() + Flow.ClientSuffix);
            //增加隧道流量
            if (tunnelFlow == null) {
                tunnelFlow = new Flow(flowType.getUp() ? flowType.getFlow() : 0, flowType.getUp() ? 0 : flowType.getFlow());
            } else {
                //是上传?
                if (flowType.getUp()) {
                    tunnelFlow.addUP(flowType.getFlow());
                }//噢,不是下载啦!
                else {
                    tunnelFlow.addDown(flowType.getFlow());
                }
            }
            redisTemplate.opsForValue().set(Flow.TunnelFlowPrefix + flowType.getTunnelId() + Flow.ClientSuffix, tunnelFlow);
            return;
        }
        //计算流量
        userService.flowCalculation(flowType.getTunnelId(), flowType.getFlow());
        Flow nodeFlow = (Flow) redisTemplate.opsForValue().get(Flow.NodeFlowPrefix + flowType.getNodeId());
        Flow tunnelFlow = (Flow) redisTemplate.opsForValue().get(Flow.TunnelFlowPrefix + flowType.getTunnelId());
        //增加节点流量
        if (nodeFlow == null) {
            nodeFlow = new Flow(flowType.getFlow(), flowType.getFlow());
        } else {
            nodeFlow.addUP(flowType.getFlow());
            nodeFlow.addDown(flowType.getFlow());
        }
        redisTemplate.opsForValue().set(Flow.NodeFlowPrefix + flowType.getNodeId(), nodeFlow);
        //增加隧道流量
        if (tunnelFlow == null) {
            tunnelFlow = new Flow(0L, 0L);
        }
        //是上传?
        if (flowType.getUp()) {
            //计入总数
            CacheFlowCount.addUP(flowType.getFlow());
            CacheFlowToday.addUP(flowType.getFlow());
            tunnelFlow.addUP(flowType.getFlow());
        }//噢,不是下载啦!
        else {
            CacheFlowCount.addDown(flowType.getFlow());
            CacheFlowToday.addDown(flowType.getFlow());
            tunnelFlow.addDown(flowType.getFlow());
        }
        redisTemplate.opsForValue().set(Flow.TunnelFlowPrefix + flowType.getTunnelId(), tunnelFlow);
        if (CacheFlowCount.count() > Flow.MB100) {
            Flow flow = (Flow) redisTemplate.opsForValue().get(Flow.FlowCount);
            if (flow == null) {
                redisTemplate.opsForValue().set(Flow.FlowCount, CacheFlowCount);
            } else {
                flow.add(CacheFlowCount);
                redisTemplate.opsForValue().set(Flow.FlowCount, flow);
            }
            CacheFlowCount.clear();
        }
        if (CacheFlowToday.count() > Flow.MB100) {
            Flow flow = (Flow) redisTemplate.opsForValue().get(Flow.FlowToDayPrefix + DateUtils.getDay());
            if (flow == null) {
                redisTemplate.opsForValue().set(Flow.FlowToDayPrefix + DateUtils.getDay(), CacheFlowToday, 30, TimeUnit.DAYS);
            } else {
                flow.add(CacheFlowToday);
                redisTemplate.opsForValue().set(Flow.FlowToDayPrefix + DateUtils.getDay(), flow, 30, TimeUnit.DAYS);
            }
            CacheFlowToday.clear();
        }
    }

    @Override
    public boolean test() {
        return true;
    }

    @Override
    public void deleteNode(String ip) {
        deleteNode(getNodeCtx(ip));
    }
    public void Heartbeat(Msg msg){
        if(msg.getMsg().equals("Client")){
            HeartbeatCMap.put(Msg.getData(msg, Integer.class), true);
        }else {
            HeartbeatNMap.put(Msg.getStringData(msg), true);
        }
    }
    @Scheduled(fixedDelay = 30000)
    public void ConnectionDetection() {
        //遍历客户端心跳map
        HeartbeatCMap.forEach((k,v)->{
            if(!v){
                //把没有回复心跳的连接删除
                clientCtxMap.remove(k);
                clientServer.setOnline(k, false);
            }
        });
        HeartbeatNMap.forEach((k,v)->{
            if(!v){
                //把没有回复心跳的连接删除
                nodeCtxMap.remove(k);
                nodeService.setOnline(k, false);
            }
        });
        //清空
        HeartbeatCMap.clear();
        HeartbeatNMap.clear();

        clientCtxMap.forEach((k,v)->{
            if(v!=null){
                sendMsg(v,Msg.Heartbeat,"");
                HeartbeatCMap.put(k,false);
            }else {
                clientCtxMap.remove(k);
                clientServer.setOnline(k, false);
            }
        });

        nodeCtxMap.forEach((k,v)->{
            if(v!=null){
                sendMsg(v,Msg.Heartbeat,"");
                HeartbeatNMap.put(k,false);
            }else {
                nodeCtxMap.remove(k);
                nodeService.setOnline(k, false);
            }
        });
    }
}
