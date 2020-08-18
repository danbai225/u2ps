package cn.p00q.u2ps.controller.view;

import cn.p00q.u2ps.bean.Result;
import cn.p00q.u2ps.entity.User;

import cn.p00q.u2ps.service.UserService;
import cn.p00q.u2ps.utils.IpUtils;
import cn.p00q.u2ps.utils.Vaptcha;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
/**
 * @program: u2ps
 * @description: 主要视图
 * @author: DanBai
 * @create: 2020-08-12 15:29
 **/
@Controller
@Validated
public class MainView {
    private final UserService userService;
    public MainView(UserService userService) {
        this.userService = userService;
    }
    @GetMapping(value = {"/index","","/"})
    public String index() {
        return "index";
    }
    @GetMapping(value = "/login")
    public String login(HttpServletRequest request) {
        HttpSession session = request.getSession();
        if(session.getAttribute(User.class.getSimpleName())!=null){
            return "redirect:panel/index";
        }
        return "login";
    }
    @PostMapping(value = "/login")
    public String loginApi(@Validated({User.LoginGroup.class}) User user,boolean remember, HttpServletRequest request, Model model, HttpServletResponse response) {
        //登录IP
        user.setLoginIp(IpUtils.getIpAddr(request));
        String token = userService.login(user);
        if(StringUtils.isEmpty(token)){
            model.addAttribute("Msg", "账号或密码错误");
            return "login";
        }
        if (token.equals("1")){
            model.addAttribute("Msg", "请先完成邮箱验证,没收到验证码可以重新注册.");
            return "login";
        }
        User user1 = userService.getUserByUsername(user.getUsername());
        HttpSession session = request.getSession();
        session.setAttribute(User.class.getSimpleName(), user1);
        session.setAttribute("Token", token);
        session.setMaxInactiveInterval(60 * 60 * 24);
        if(remember){
            Cookie cookie = new Cookie("JSESSIONID", session.getId());
            cookie.setMaxAge(60 * 60 * 24 * 7);
            response.addCookie(cookie);
        }
        return "redirect:panel/index";
    }
    @GetMapping(value = "/reg")
    public String reg() {
        return "reg";
    }
    @PostMapping(value = "/reg")
    public String regApi(@NotBlank @RequestParam(name = "vaptcha_token")String vaptchaToken, @Validated({User.RegGroup.class}) User user, HttpServletRequest request, Model model) {
        String ipAddr = IpUtils.getIpAddr(request);
        //注册IP
        user.setRegisterIp(IpUtils.getIpAddr(request));
        if(!Vaptcha.V(vaptchaToken,ipAddr)){
            model.addAttribute("Msg", "人机认证失败");
        }else {
            Result reg = userService.reg(user);
            model.addAttribute("Msg", reg.getMsg());
        }
        return "reg";
    }
    @GetMapping(value = "/forgotPassword")
    public String forgotPassword() {
        return "forgot-password";
    }
    @PostMapping(value = "/forgotPassword")
    public String forgotPasswordApi( HttpServletRequest request,@NotBlank @RequestParam(name = "vaptcha_token")String vaptchaToken,@Email String email, @NotBlank @Length(max = 16,min = 6,message = "用户密码长度6-16") String password, Model model) {
        String ipAddr = IpUtils.getIpAddr(request);
        if(!Vaptcha.V(vaptchaToken,ipAddr)){
            model.addAttribute("Msg", "人机认证失败");
        }else {
            Result result = userService.forgotPassword(email, password);
            model.addAttribute("Msg", result.getMsg());
        }
        return "forgot-password";
    }
    @GetMapping("/loginOut")
    public String loginOut(HttpServletRequest request){
        request.getSession().removeAttribute("Token");
        request.getSession().removeAttribute(User.class.getSimpleName());
        return "redirect:/index";
    }
    @GetMapping("/autonym")
    public String autonym(){
        return "autonym";
    }
}
