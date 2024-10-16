package com.cs183.tasty.constant;

public class RedisConstants {
    public static final String LOGIN_USER_KEY = "login:userId:";
    public static final String VERIFY_CODE = "verify:code:";
    //用户的关注列表
    public static final String FOLLOW_USER = "follow:userId:";
    //用户的粉丝列表
    public static final String FANS_USER = "fans:userId:";
    public static final String CLICK_NOTE = "click:note";
    public static final String LIKE_NOTE = "like:noteId:";
    public static final String RESPONSE_COMMENT = "response:commentId:";
    public static final String SEARCH_RECORD = "search:record";
    public static final String COMMENT_NOTE = "comment:noteId:";
    public static final Long VERIFY_CODE_TTL= 1L;
    public static final Long LOGIN_USER_TTL = 36000L;

}
