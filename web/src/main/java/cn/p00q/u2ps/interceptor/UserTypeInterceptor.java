package cn.p00q.u2ps.interceptor;

import cn.p00q.u2ps.bean.Result;
import cn.p00q.u2ps.entity.User;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * @program: web
 * @description: 用户类型拦截器
 * @author: DanBai
 * @create: 2020-08-17 20:18
 **/
@Component
public class UserTypeInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response, Object handler) throws Exception {
        User user = (User) request.getSession().getAttribute(User.class.getSimpleName());
        if (user.getType()<3) {
            sendJsonMessage(response,Result.err("请先完成实名认证"));
            return false;
        }
        return true;
    }
    public  void sendJsonMessage(HttpServletResponse response, Object obj) throws Exception {
        response.setContentType("application/json; charset=utf-8");
        PrintWriter writer = response.getWriter();
        writer.print(JSONObject.toJSONString(obj, SerializerFeature.WriteMapNullValue,
                SerializerFeature.WriteDateUseDateFormat));
        writer.close();
        response.flushBuffer();
    }

}
