package cn.p00q.u2ps.controller.api.v1;

import cn.p00q.u2ps.bean.Result;
import cn.p00q.u2ps.entity.Client;
import cn.p00q.u2ps.entity.Node;
import cn.p00q.u2ps.entity.Tunnel;
import cn.p00q.u2ps.entity.User;
import cn.p00q.u2ps.service.ClientServer;
import cn.p00q.u2ps.service.UserService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @program: web
 * @description:
 * @author: DanBai
 * @create: 2020-08-16 12:16
 **/
@RequestMapping("/v1/client")
@RestController
@Validated
public class ClientController {
    private  UserService userService;
    private  ClientServer clientServer;

    public ClientController(UserService userService, ClientServer clientServer) {
        this.userService = userService;
        this.clientServer = clientServer;
    }
    @PostMapping("/update")
    public Result update(@Validated(Client.Update.class)Client client, @NotBlank String token){
        User user = userService.checkToken(token);
        Client client1 = clientServer.getClientById(client.getId());
        if(user!=null&&client1!=null){
            if(user.getUsername().equals(client1.getUsername())){
                if(client.getSecretKey()!=null&&client.getSecretKey().equals(client1.getSecretKey())){
                    client.setSecretKey(null);
                }
                return clientServer.updateById(client);
            }
        }
        return Result.err("验证未通过,无权更新。");
    }
    @PostMapping("/delete")
    public Result delete(@NotNull Integer id, @NotBlank String token){
        User user = userService.checkToken(token);
        Client client1 = clientServer.getClientById(id);
        if(user!=null&&client1!=null&&client1.getUsername().equals(user.getUsername())){
            return clientServer.delete(id)?Result.success("删除成功"):Result.err("删除失败");
        }
        return Result.err("验证未通过,无权删除。");
    }
    @PostMapping("/create")
    public Result create(@Validated(Client.Create.class) Client client,@NotBlank String token){
        User user = userService.checkToken(token);
        if(user!=null){
            client.setUsername(user.getUsername());
            return clientServer.create(client);
        }
        return Result.err("验证未通过,无权创建。");
    }
}
