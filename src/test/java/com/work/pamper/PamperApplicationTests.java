package com.work.pamper;

import com.work.pamper.utils.JwtUtils;
import io.jsonwebtoken.Jwt;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class PamperApplicationTests {

    @Test
    void contextLoads() {
        String jwt = JwtUtils.createJwt("1",3600000);
        System.out.println("生成的JWT: " + jwt);
        String subject = JwtUtils.getSubject(jwt);
        System.out.println("解析后的Subject: " + subject);
        System.out.println("JWT是否过期: " + JwtUtils.isExpired(jwt));
    }

}
