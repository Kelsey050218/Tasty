package com.cs183.tasty;

import com.cs183.tasty.service.impl.SmsServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class TastyApplicationTests {
    @Test
    public void testSentCode(@Autowired SmsServiceImpl smsServiceImpl) throws Exception {
        String phone = "18960935500";
        String code = smsServiceImpl.creatCode();
        boolean result = smsServiceImpl.sendCode(phone,code);
        System.out.println(result);
    }


}
