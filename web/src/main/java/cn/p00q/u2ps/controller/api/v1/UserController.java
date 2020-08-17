package cn.p00q.u2ps.controller.api.v1;

import cn.p00q.u2ps.bean.Flow;
import cn.p00q.u2ps.bean.Result;
import cn.p00q.u2ps.entity.User;
import cn.p00q.u2ps.service.UserService;
import cn.p00q.u2ps.utils.IpUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotBlank;

/**
 * @program: u2ps
 * @description: User控制器
 * @author: DanBai
 * @create: 2020-08-01 17:06
 **/
@RequestMapping("/v1/user")
@RestController
@Validated
public class UserController {
    private  UserService userService;
    public UserController(UserService userService){
        this.userService=userService;
    }
    @PostMapping("/reg")
    public Result reg(@Validated({User.RegGroup.class}) User user, HttpServletRequest request){
        //注册IP
        user.setRegisterIp(IpUtils.getIpAddr(request));
        return userService.reg(user);
    }
    @PostMapping("/login")
    public Result login(@Validated({User.LoginGroup.class}) User user, HttpServletRequest request){
        //登录IP
        user.setLoginIp(IpUtils.getIpAddr(request));
        String token = userService.login(user);
        if(token==null){
            return Result.err("账号或密码错误");
        }
        return Result.success("登录成功",token);
    }
    @GetMapping("/validate/email")
    public Result validateEmail(String code){
        if(userService.validateEmail(code)){
            return Result.success("验证成功");
        }
        return Result.err("此验证连接已经失效");
    }
    @PostMapping("/signReward")
    public Result signReward(@NotBlank String token){
        User user = userService.checkToken(token);
        if(user!=null){
            Integer integer = userService.signReward(user.getUsername());
            if (integer<0){
                return Result.err("已经签到过了。");
            }
            return Result.success("签到成功,获得流量:"+integer+"MB",integer);
        }
        return Result.err("验证未通过,无权签到。");
    }
    @PostMapping("/cdKey")
    public Result cdKey(@NotBlank String token,@NotBlank String cdkey){
        User user = userService.checkToken(token);
        if(user!=null){
            Integer integer = userService.cdKey(user.getUsername(),cdkey);
            if (integer<0){
                return Result.err("该兑换码无效!");
            }
            return Result.success("兑换成功,获得流量:"+integer+"MB",integer);
        }
        return Result.err("验证未通过,无权兑换。");
    }
}
