package com.cs183.tasty.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
@Data
@Component
@ConfigurationProperties(prefix = "tasty.sms")
public class SmsCodeProperty {

        public String accessKeyID;
        public String accessKeySecret;
        public String signName;
        public String templateId_R;
        public String templateId_F;

    }
