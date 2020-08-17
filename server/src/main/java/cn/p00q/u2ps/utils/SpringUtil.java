package cn.p00q.u2ps.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @author danbai
 * @date 2019/10/13
 */
@Component
@Slf4j
public class SpringUtil implements ApplicationContextAware {
    private static ApplicationContext applicationContext;
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if(SpringUtil.applicationContext == null) {
            SpringUtil.applicationContext = applicationContext;
        }
        log.info("ApplicationContext配置成功,applicationContext对象："+SpringUtil.applicationContext);
    }
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }
    public static Object getBean(String name) {
        return getApplicationContext()!=null?getApplicationContext().getBean(name):null;
    }
    public static <T> T getBean(Class<T> clazz) {
        return getApplicationContext()!=null?getApplicationContext().getBean(clazz):null;
    }
    public static <T> T getBean(String name,Class<T> clazz) {
        return getApplicationContext().getBean(name,clazz);
    }
}
