package cn.p00q.u2ps.controller.view;

import cn.p00q.u2ps.entity.User;
import cn.p00q.u2ps.service.PayService;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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
    private PayService payService;

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
        return "redirect:"+payService.callBack(Salt,order_id,fee,sign);
    }
}
