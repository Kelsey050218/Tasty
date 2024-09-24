package com.cs183.tasty.utils;

public class RedisConstants {
    public static final String LOGIN_USER_KEY = "login:userId:";
    public static final String VERIFY_CODE = "verify:code:";
    public static final Long VERIFY_CODE_TTL= 1L;
    public static final Long LOGIN_USER_TTL = 36000L;

}
