package cn.p00q.u2ps.controller.api.v1;

import cn.p00q.u2ps.bean.Flow;
import cn.p00q.u2ps.bean.Result;
import cn.p00q.u2ps.entity.Tunnel;
import cn.p00q.u2ps.entity.User;
import cn.p00q.u2ps.service.*;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


/**
 * @program: u2ps
 * @description: 隧道控制器
 * @author: DanBai
 * @create: 2020-08-07 13:57
 **/
@RequestMapping("/v1/tunnel")
@RestController
@Validated
public class TunnelController {
    private  TunnelService tunnelService;
    private  UserService userService;
    private  NodeService nodeService;
    private  ClientServer clientServer;
    @Reference
    private PsService psService;
    public TunnelController(TunnelService tunnelService, UserService userService, NodeService nodeService, ClientServer clientServer) {
        this.tunnelService = tunnelService;
        this.userService = userService;
        this.nodeService = nodeService;
        this.clientServer = clientServer;
    }

    @PostMapping("/update")
    public Result update(@Validated(Tunnel.Update.class) Tunnel tunnel,@NotBlank String token){
        User user = userService.checkToken(token);
        Tunnel tunnelById = tunnelService.getTunnelById(tunnel.getId());
        if(user!=null&&tunnelById!=null){
            if(user.getUsername().equals(tunnelById.getUsername())){
                return tunnelService.updateById(tunnel);
            }
        }
        return Result.err("验证未通过,无权更新。");
    }
    @PostMapping("/create")
    public Result create(@Validated(Tunnel.Create.class) Tunnel tunnel,@NotBlank String token){
        User user = userService.checkToken(token);
        if(user!=null){
            int userTunnelNum = tunnelService.getUserTunnelNum(user.getUsername());
            if((Flow.toGB(user.getFlow())+1)<=userTunnelNum){
                return Result.err("您的流量不足以创建一条新的隧道(隧道最大数量取决于您的剩余流量),1GB/条");
            }
            tunnel.setUsername(user.getUsername());
           return tunnelService.create(tunnel);
        }
        return Result.err("验证未通过,无权创建。");
    }
    @PostMapping("/delete")
    public Result delete(@NotNull Integer id, @NotBlank String token){
        User user = userService.checkToken(token);
        Tunnel tunnelById = tunnelService.getTunnelById(id);
        if(tunnelById!=null&&user!=null){
           if(tunnelById.getUsername().equals(user.getUsername())){
               return tunnelService.delete(id);
           }
        }
        return Result.err("验证未通过,无权删除。");
    }
    @PostMapping("/port")
    public Result port(@NotNull @Max(value = 65535) @Min(value = 1) Integer servicePort, @NotNull Integer nodeId){
        boolean nodePortUse = psService.isNodePortUse(nodeService.getIp(nodeId), servicePort);
        return nodePortUse?Result.err("请换一个端口,该端口被占用"):Result.success("很好,这个端口是可以使用的");
    }
}
