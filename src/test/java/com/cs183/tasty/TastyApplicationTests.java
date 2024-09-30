package com.cs183.tasty;

import cn.hutool.core.util.RandomUtil;
import com.cs183.tasty.service.impl.SmsServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.TimeUnit;

import static com.cs183.tasty.utils.RedisConstants.VERIFY_CODE;
import static com.cs183.tasty.utils.RedisConstants.VERIFY_CODE_TTL;

@SpringBootTest
class TastyApplicationTests {

    @Autowired
    private RedisTemplate redisTemplate;


//    @Test
//    public void testVerifyCode(){
//        String code = RandomUtil.randomNumbers(4);
//        redisTemplate.opsForValue().set(VERIFY_CODE, code, VERIFY_CODE_TTL, TimeUnit.MINUTES);
//        String vCode = (String)redisTemplate.opsForValue().get(VERIFY_CODE);
//        System.out.println(vCode);
//    }


}
