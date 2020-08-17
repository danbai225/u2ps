package cn.p00q.u2ps.config;

import cn.p00q.u2ps.interceptor.LoginInterceptor;
import cn.p00q.u2ps.interceptor.UserTypeInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @program: u2ps
 * @description: app配置
 * @author: DanBai
 * @create: 2020-08-01 16:41
 **/
@Configuration
public class AppConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginInterceptor()).addPathPatterns("/panel/**").addPathPatterns("/autonym").addPathPatterns("/pay/*");
        registry.addInterceptor(new UserTypeInterceptor()).addPathPatterns("/v1/**").excludePathPatterns("/v1/user");
    }
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        //添加映射路径
        registry.addMapping("/**")
                //放行哪些原始域
                .allowedOrigins("*")
                //是否发送Cookie信息
                .allowCredentials(true)
                //放行哪些原始域(请求方式)
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                //放行哪些原始域(头部信息)
                .allowedHeaders("*");
    }
}
