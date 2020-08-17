package cn.p00q.u2ps.controller.view;

import cn.p00q.u2ps.entity.Tunnel;
import cn.p00q.u2ps.entity.User;
import cn.p00q.u2ps.service.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @program: u2ps
 * @description: 面板
 * @author: DanBai
 * @create: 2020-08-12 18:08
 **/
@Controller
@Validated
@RequestMapping("/panel")
public class PanelView {
    private UserService userService;
    private NodeService nodeService;
    private TunnelService tunnelService;
    private ClientService clientService;
    private FlowService flowService;
    private RedisTemplate redisTemplate;

    public PanelView(UserService userService, NodeService nodeService, TunnelService tunnelService, ClientService clientService, FlowService flowService, RedisTemplate redisTemplate) {
        this.userService = userService;
        this.nodeService = nodeService;
        this.tunnelService = tunnelService;
        this.clientService = clientService;
        this.flowService = flowService;
        this.redisTemplate = redisTemplate;
    }
    @GetMapping(value = {"/index","/",""})
    public String index(Model model, HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute(User.class.getSimpleName());
        model.addAttribute("UserNum", userService.count());
        model.addAttribute("OnlineNode",nodeService.onlineNodeCount());
        model.addAttribute("OnlineClient", clientService.onlineClientCount());
        model.addAttribute("CountFlow",flowService.countFlow().toGB());
        model.addAttribute("User30Flow",flowService.get30DaysUserFlow(user.getUsername()));
        model.addAttribute("Count30Flow",flowService.get30Days());
        model.addAttribute("User",userService.getUserByUsername(user.getUsername()));
        model.addAttribute("Token",userService.getToken(user.getUsername()));
        model.addAttribute("Gg",redisTemplate.opsForValue().get("Gg"));
        model.addAttribute("SignReward",userService.isSignReward(user.getUsername()));
        return "panel/index";
    }
    @GetMapping(value = {"/node/my"})
    public String nodeMyList(Model model,HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute(User.class.getSimpleName());
        model.addAttribute("Nodes",flowService.getNodeFlow(nodeService.getMyList(user.getUsername())));
        return "panel/node/my";
    }
    @GetMapping(value = {"/node/list"})
    public String nodeList(Model model) {
        model.addAttribute("Nodes",nodeService.getList());
        return "panel/node/list";
    }
    @GetMapping(value = {"/node/new"})
    public String nodeNew() {
        return "panel/node/new";
    }
    @GetMapping(value = {"/client/list"})
    public String clientList(Model model,HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute(User.class.getSimpleName());
        model.addAttribute("Clients", clientService.getClientByUsername(user.getUsername()));
        return "panel/client/list";
    }
    @GetMapping(value = {"/client/new"})
    public String clientNew() {
        return "panel/client/new";
    }
    @GetMapping(value = {"/tunnel/list"})
    public String tunnelList(Model model,HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute(User.class.getSimpleName());
        List<Tunnel> tunnelsByUsername = tunnelService.getTunnelsByUsername(user.getUsername());
        model.addAttribute("Tunnels",flowService.getTunnelFlow(tunnelsByUsername,nodeService.getNodes(tunnelsByUsername)));
        return "panel/tunnel/list";
    }
    @GetMapping(value = {"/tunnel/new"})
    public String tunnelNew(Model model,HttpServletRequest request,Integer id) {
        if(id!=null){
            model.addAttribute("nodeId", id);
        }
        User user = (User) request.getSession().getAttribute(User.class.getSimpleName());
        model.addAttribute("Nodes",nodeService.getNodesNewTunnel());
        model.addAttribute("Client", clientService.getUserClientNewTunnel(user.getUsername()));
        return "panel/tunnel/new";
    }
}
