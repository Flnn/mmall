package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mysql.fabric.Server;

/**
 * Created by flnnf on 2017/6/24.
 */
public interface IUserService {

    ServerResponse<User> login(String userName, String password);

}
