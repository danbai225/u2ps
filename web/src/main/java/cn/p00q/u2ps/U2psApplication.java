package cn.p00q.u2ps;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * @author DanBai
 */
@SpringBootApplication
@MapperScan(basePackages = "cn.p00q.u2ps.mapper")

public class U2psApplication {
    public static void main(String[] args) {
        SpringApplication.run(U2psApplication.class, args);
    }
}
