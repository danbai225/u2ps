package cn.p00q.u2ps.controller.api.v1;

import cn.p00q.u2ps.bean.Result;
import cn.p00q.u2ps.entity.Tunnel;
import cn.p00q.u2ps.entity.User;
import cn.p00q.u2ps.service.ClientServer;
import cn.p00q.u2ps.service.NodeService;
import cn.p00q.u2ps.service.TunnelService;
import cn.p00q.u2ps.service.UserService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
    private final TunnelService tunnelService;
    private final UserService userService;
    private final NodeService nodeService;
    private final ClientServer clientServer;

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
}
