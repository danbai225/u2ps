package cn.p00q.u2ps.controller.view;

import cn.p00q.u2ps.bean.Result;
import cn.p00q.u2ps.entity.User;
import cn.p00q.u2ps.service.PayService;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @program: web
 * @description: 支付控制器
 * @author: DanBai
 * @create: 2020-08-17 15:25
 **/
@Controller
@Validated
@RequestMapping("/pay")
public class PayController {
    private final PayService payService;

    public PayController(PayService payService) {
        this.payService = payService;
    }

    @GetMapping("/authentication")
    public String authentication(HttpServletRequest request,@NotNull Integer payType){
        User user = (User) request.getSession().getAttribute(User.class.getSimpleName());
        return "redirect:"+payService.buyAuthentication(user.getUsername(), payType);
    }
    @RequestMapping("/callBack")
    public String callBack(@NotBlank String Salt, Integer order_id, String fee, Integer sign){
        System.out.println("回调"+order_id);
        return "redirect:"+payService.callBack(Salt,order_id,fee,sign);
    }
    @RequestMapping("/buyFlow")
    @ResponseBody
    public Result buyFlow(HttpServletRequest request, @NotNull Integer payType, @NotNull Integer boothId, @NotNull Integer buyNum){
        User user = (User) request.getSession().getAttribute(User.class.getSimpleName());
        return payService.buyFlow(user.getUsername(),payType,boothId,buyNum);
    }
    @RequestMapping("/cancel")
    public String cancel(@NotBlank String Salt,Integer order_id,String fee,Integer sign){
        payService.cancel(Salt,order_id,fee,sign);
        return "redirect:../panel/traffic_market";
    }
    @RequestMapping("/sellFlow")
    @ResponseBody
    public Result sellFlow(HttpServletRequest request, @NotNull Float price, @NotNull Integer quantity){
        User user = (User) request.getSession().getAttribute(User.class.getSimpleName());
        return payService.sellFlow(user.getUsername(),price,quantity);
    }
}
