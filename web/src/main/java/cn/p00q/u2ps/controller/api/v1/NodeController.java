package cn.p00q.u2ps.controller.api.v1;

import cn.p00q.u2ps.bean.Result;
import cn.p00q.u2ps.entity.Node;
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
 * @description: node控制器
 * @author: DanBai
 * @create: 2020-08-10 16:12
 **/
@RequestMapping("/v1/node")
@RestController
@Validated
public class NodeController {
    private  TunnelService tunnelService;
    private  UserService userService;
    private  NodeService nodeService;
    private  ClientServer clientServer;

    public NodeController(TunnelService tunnelService, UserService userService, NodeService nodeService, ClientServer clientServer) {
        this.tunnelService = tunnelService;
        this.userService = userService;
        this.nodeService = nodeService;
        this.clientServer = clientServer;
    }
    @PostMapping("/create")
    public Result create(@Validated(Node.Create.class) Node node, @NotBlank String token){
        User user = userService.checkToken(token);
        if(user!=null){
            node.setUsername(user.getUsername());
            return nodeService.create(node);
        }
        return Result.err("验证未通过,无权创建。");
    }
    @PostMapping("/delete")
    public Result delete(@NotNull Integer id, @NotBlank String token){
        User user = userService.checkToken(token);
        Node nodeById = nodeService.getNodeById(id);
        if(user!=null&&nodeById!=null&&nodeById.getUsername().equals(user.getUsername())){
            return nodeService.delete(id)?Result.success("删除成功"):Result.err("删除失败");
        }
        return Result.err("验证未通过,无权删除。");
    }
    @PostMapping("/update")
    public Result update(@Validated(Node.Update.class) Node node,@NotBlank String token){
        User user = userService.checkToken(token);
        Node nodeById = nodeService.getNodeById(node.getId());
        if(user!=null&&nodeById!=null){
            if(user.getUsername().equals(nodeById.getUsername())){
                return nodeService.updateById(node);
            }
        }
        return Result.err("验证未通过,无权更新。");
    }
}
