package cn.p00q.u2ps.controller.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @program: u2ps
 * @description: 面板
 * @author: DanBai
 * @create: 2020-08-12 18:08
 **/
@Controller
@RequestMapping("/panel")
public class PanelView {
    @GetMapping(value = "/index")
    public String index() {
        return "panel/index";
    }
}
