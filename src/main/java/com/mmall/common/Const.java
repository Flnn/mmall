package com.mmall.common;

/**
 * Created by flnnf on 2017/6/27.
 */
public class Const {

    public static final String CURRENT_USER = "current_user";

    public static final String USERNAME = "username";

    public static final String EMAIL = "email";

    /**
     * 常量类中声明接口用于将常量分组
     */
    public interface Role{
        int ROLE_CUSTOMER = 0;    //普通用户
        int ROLE_ADMIN = 1;       //管理员用户
    }
}
