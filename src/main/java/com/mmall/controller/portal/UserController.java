package com.mmall.controller.portal;

import com.dev.redis.RedisClientUtil;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.service.impl.UserServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.jws.soap.SOAPBinding;
import javax.servlet.http.HttpSession;

/**
 * Created by flnnf on 2017/6/24.
 */
@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private IUserService iUserService;

    @RequestMapping(value = "login", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> login(String username, String password, HttpSession session) {
        ServerResponse<User> response = iUserService.login(username, password);
        if (response.isSuccess()) {
            response.getData().setPassword(StringUtils.EMPTY);
            session.setAttribute(Const.CURRENT_USER, response.getData());
        }
        return response;
    }

    /**
     * 退出登录
     *
     * @param session
     * @return
     */
    @RequestMapping(value = "logout", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> logout(HttpSession session) {
        session.removeAttribute(Const.CURRENT_USER);
        return ServerResponse.createBySuccess();
    }

    /**
     * 注册
     *
     * @param user
     * @return
     */
    @RequestMapping(value = "register", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> register(User user) {
        return iUserService.register(user);
    }

    /**
     * 信息校验
     *
     * @param str
     * @param type
     * @return
     */
    @RequestMapping(value = "checkValid", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> checkValid(String str, String type) {
        return iUserService.checkValid(str, type);
    }

    /**
     * 获取用户信息
     *
     * @param session
     * @return
     */
    @RequestMapping(value = "getUserInfo", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> getUserInfo(HttpSession session) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user != null) {
            return ServerResponse.createBySuccess(user);
        }
        return ServerResponse.createByErrorMessage("用户为登录，无法获取用户信息");
    }

    /**
     * 忘记密码，返回提示问题
     * @param username
     * @return
     */
    @RequestMapping(value = "forgetGetQuestion", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetGetQuestion(String username){
        return iUserService.selectQuestion(username);
    }

    /**
     * 校验问题答案
     * @param username
     * @param question
     * @param answer
     * @return
     */
    @RequestMapping(value = "forgetCheckAnswer", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetCheckAnswer(String username,String question,String answer){
        return iUserService.checkAnswer(username, question, answer);
    }

    /**
     * 使用获取到的token重置密码
     * @param username
     * @param newPassword
     * @param token
     * @return
     */
    @RequestMapping(value = "forgetResetPassword", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetResetPassword(String username,String newPassword,String token){
        return iUserService.forgetResetPassword(username,newPassword,token);
    }

    /**
     * 登录状态下的重置密码
     * @param username
     * @param oldPassword
     * @param newPassword
     * @return
     */
    @RequestMapping(value = "resetPassword", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> resetPassword(String username, String oldPassword, String newPassword,HttpSession session) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorMessage("用户未登录");
        }
        return iUserService.resetPassword(oldPassword,newPassword,user);
    }

    /**
     * 用户信息更新
     * @param session
     * @param user
     * @return
     */
    @RequestMapping(value = "updateInformation", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> updateInformation(HttpSession session,User user){
        User userInSession = (User) session.getAttribute(Const.CURRENT_USER);
        if(userInSession == null){
            return ServerResponse.createByErrorMessage("用户未登录");
        }
        user.setId(userInSession.getId());
        user.setUsername(userInSession.getUsername());
        ServerResponse<User> response = iUserService.updateInformation(user);
        if(response.isSuccess()){
            response.getData().setUsername(userInSession.getUsername());
            session.setAttribute(Const.CURRENT_USER,response.getData());
        }
        return response;
    }

    /**
     * 获取用户详细信息
     * @param session
     * @return
     */
    @RequestMapping(value = "getInformation", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> getInformation(HttpSession session) {
        User loginUser = (User) session.getAttribute(Const.CURRENT_USER);
        if (loginUser == null) {
            //返回强制登录的标志
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
                    "未登录，需要强制登录，status=10");
        }
        return iUserService.getInformation(loginUser);
    }
}
