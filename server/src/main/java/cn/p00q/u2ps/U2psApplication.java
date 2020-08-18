package cn.p00q.u2ps;

import com.alibaba.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * @author DanBai
 */
@SpringBootApplication
@EnableDubbo
@MapperScan(basePackages = "cn.p00q.u2ps.mapper")
@EnableScheduling
public class U2psApplication {
    public static void main(String[] args) {
        SpringApplication.run(U2psApplication.class, args);
    }
}
