package cn.p00q.u2ps.controller;

import cn.p00q.u2ps.bean.Result;
import cn.p00q.u2ps.entity.User;
import cn.p00q.u2ps.service.UserService;
import cn.p00q.u2ps.utils.IpUtils;
import cn.p00q.u2ps.utils.Vaptcha;
import cn.p00q.u2ps.utils.yzmUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotBlank;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @program: web
 * @description:
 * @author: DanBai
 * @create: 2020-08-17 18:22
 **/
@RestController
public class UtilsController {
    private RedisTemplate redisTemplate;
    private static final String YZMpPREFIX="Yzm_";
    private UserService userService;

    public UtilsController(RedisTemplate redisTemplate, UserService userService) {
        this.redisTemplate = redisTemplate;
        this.userService = userService;
    }

    @PostMapping("/mobileYzm")
    public Result mobileYzm(@NotBlank String token, HttpServletRequest request, @NotBlank String mobile){
        String ipAddr = IpUtils.getIpAddr(request);
        if(!Vaptcha.V(token,ipAddr)){
            return Result.err("人机认证失败,请先完成认证");
        }
        User user = (User) request.getSession().getAttribute(User.class.getSimpleName());
        if(redisTemplate.opsForSet().isMember(User.RedisAuthenticationSet, user.getUsername())){
            Random r = new Random();
            String yzm = "";
            for (int i = 0; i < 8; i++) {
                yzm += String.valueOf(r.nextInt(10));
            }
            redisTemplate.opsForValue().set(YZMpPREFIX+mobile, yzm, 3, TimeUnit.MINUTES);
            if(yzmUtils.sendYzm(mobile, yzm)){
                return Result.success("发送成功");
            }
            return Result.err("发送失败");
        }
        return Result.err("请现支付认证费用");
    }
    @PostMapping("/autonym")
    public Result autonym(HttpServletRequest request,@NotBlank String realname,@NotBlank String idCard,@NotBlank String mobile,@NotBlank String code){
        String rcode = (String) redisTemplate.opsForValue().get(YZMpPREFIX + mobile);
        if(rcode==null||!rcode.equals(code)){
            return Result.err("验证码不正确");
        }else {
            redisTemplate.delete(YZMpPREFIX + mobile);
        }
        User user = (User) request.getSession().getAttribute(User.class.getSimpleName());
        if(redisTemplate.opsForSet().isMember(User.RedisAuthenticationSet, user.getUsername())){
            redisTemplate.opsForSet().remove(User.RedisAuthenticationSet,user.getUsername());
            Result autonym = userService.autonym(realname, idCard, mobile, user.getUsername());
            if(autonym.isOk()){
                request.getSession().setAttribute(User.class.getSimpleName(),userService.getUserByUsername(user.getUsername()));
            }
            return autonym;
        }
        return Result.err("请现支付认证费用");
    }
}
