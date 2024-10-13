package com.cs183.tasty;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

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
