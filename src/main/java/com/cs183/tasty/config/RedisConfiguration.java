package com.cs183.tasty.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@Slf4j
public class RedisConfiguration {

    @Bean
    public RedisTemplate redisTemplate(RedisConnectionFactory redisConnectionFactory){
        log.info("The redis template object is created...");
        RedisTemplate redisTemplate = new RedisTemplate();
        //Set the connection factory object for redis
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        //Set the serializer for redis key
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        //The serializer that sets the redis value
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        //Set the serializer for redis hash
        return redisTemplate;
    }

}
