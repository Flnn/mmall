package com.mmall.service.impl;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.common.TokenCache;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Created by flnnf on 2017/6/24.
 * 用户模块服务层
 */
@SuppressWarnings("SpringJavaAutowiredMembersInspection")
@Service("iUserService")
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public ServerResponse<User> login(String username, String password) {
        int resultCount = userMapper.checkUserName(username);
        if (resultCount == 0) {
            return ServerResponse.createByErrorMessage("用户不存在");
        }

        //密码MD5加密
        String md5Password = MD5Util.MD5EncodeUtf8(password);
        User user = userMapper.selectLogin(username, md5Password);
        if (user == null) {
            return ServerResponse.createByErrorMessage("密码错误");
        }

        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess("登录成功", user);
    }

    /**
     * 用户注册
     *
     * @param user
     * @return
     */
    public ServerResponse<String> register(User user) {
        ServerResponse<String> checkResponse = checkValid(user.getUsername(), Const.USERNAME);
        if (!checkResponse.isSuccess()) {
            return checkResponse;
        }
        checkResponse = checkValid(user.getEmail(), Const.EMAIL);
        if (!checkResponse.isSuccess()) {
            return checkResponse;
        }
        user.setRole(Const.Role.ROLE_CUSTOMER);
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));
        int result = userMapper.insert(user);
        if (result == 0) {
            return ServerResponse.createByErrorMessage("注册失败");
        }
        return ServerResponse.createBySuccessMessage("注册成功");
    }

    /**
     * 校验用户名，邮箱
     *
     * @param str
     * @param type
     * @return
     */
    public ServerResponse<String> checkValid(String str, String type) {
        if (StringUtils.isNoneBlank(type)) {
            if (Const.USERNAME.equals(type)) {
                int result = userMapper.checkUserName(str);
                if (result > 0) {
                    return ServerResponse.createByErrorMessage("用户名已存在");
                }
            }
            if (Const.EMAIL.equals(type)) {
                int result = userMapper.checkEmail(str);
                if (result > 0) {
                    return ServerResponse.createByErrorMessage("email已存在");
                }
            }
            return ServerResponse.createBySuccessMessage("校验成功");
        } else {
            return ServerResponse.createByErrorMessage("参数错误");
        }
    }

    public ServerResponse<String> selectQuestion(String username) {
        ServerResponse<String> response = checkValid(username, Const.USERNAME);
        if (response.isSuccess()) {
            return ServerResponse.createByErrorMessage("用户名不存在");
        }
        String question = userMapper.selectQuestionByUsername(username);
        if (StringUtils.isNoneBlank(question)) {
            return ServerResponse.createBySuccessMessage(question);
        }
        return ServerResponse.createByErrorMessage("找回密码的问题是空的");
    }

    /**
     * 验证问题答案，成功后生成token放入本地缓存
     * @param username
     * @param question
     * @param answer
     * @return
     */
    public ServerResponse<String> checkAnswer(String username,String question,String answer){
        int resultCount = userMapper.checkAnswer(username, question, answer);
        if(resultCount > 0){
            //找回密码问题回答正确,返回token
            String forgetToken = UUID.randomUUID().toString();
            TokenCache.setKey(TokenCache.TOKEN_PREFIX + username,forgetToken);
            return ServerResponse.createBySuccessMessage(forgetToken);
        }
        return ServerResponse.createByErrorMessage("问题的答案错误");
    }

    @Override
    public ServerResponse<String> forgetResetPassword(String username, String newPassword, String forgetToken) {
        ServerResponse response = checkValid(username, Const.USERNAME);
        if(response.isSuccess()){
            return ServerResponse.createByErrorMessage("用户不存在");
        }
        if(StringUtils.isBlank(forgetToken)){
            return ServerResponse.createByErrorMessage("参数错误，token需要传递");
        }
        String token = TokenCache.getKey(TokenCache.TOKEN_PREFIX + username);
        if (StringUtils.isBlank(token)) {
            return ServerResponse.createByErrorMessage("token无效或者已经过期");
        }
        if(StringUtils.equals(token,forgetToken)){
            String md5Password = MD5Util.MD5EncodeUtf8(newPassword);
            int updateCount = userMapper.updatePasswordByUsername(username, md5Password);
            if(updateCount > 0){
                return ServerResponse.createBySuccessMessage("修改密码成功");
            }
        }else{
            return ServerResponse.createByErrorMessage("token错误，请重新获取");
        }
        return ServerResponse.createByErrorMessage("修改密码失败");
    }

    /**
     * 更新密码
     * @param oldPassword
     * @param newPassword
     * @param user
     * @return
     */

    public ServerResponse<String> resetPassword(String oldPassword,String newPassword,User user){
        int resultCount = userMapper.checkPassword(MD5Util.MD5EncodeUtf8(oldPassword),user.getId());
        if(resultCount == 0){
            return ServerResponse.createByErrorMessage("密码错误");
        }
        user.setPassword(MD5Util.MD5EncodeUtf8(newPassword));
        int updateCount = userMapper.updateByPrimaryKeySelective(user);
        if (updateCount > 0) {
            return ServerResponse.createBySuccessMessage("密码更新成功");
        }
        return ServerResponse.createByErrorMessage("密码更新失败");
    }

    public ServerResponse<User> updateInformation(User user){
        //用户名不能更改
        int resultCount = userMapper.checkEmailByUserId(user.getEmail(), user.getId());
        if (resultCount > 0) {
            return ServerResponse.createByErrorMessage("Email不可用，请更换Email再重试");
        }
        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setEmail(user.getEmail());
        updateUser.setQuestion(user.getQuestion());
        updateUser.setAnswer(user.getAnswer());
        updateUser.setPhone(user.getPhone());
        int updateCount = userMapper.updateByPrimaryKeySelective(updateUser);
        if (updateCount > 0) {
            return ServerResponse.createBySuccess("更新用户信息成功", updateUser);
        }
        return ServerResponse.createByErrorMessage("更新个人信息失败");
    }

    @Override
    public ServerResponse<User> getInformation(User loginUser) {
        User user = userMapper.selectByPrimaryKey(loginUser.getId());
        if(user == null){
            return ServerResponse.createByErrorMessage("未找到用户");
        }else{
            return ServerResponse.createBySuccess(user);
        }
    }

    /**
     * 检查是否是管理员用户
     * @param user
     * @return
     */
    public ServerResponse checkAdminRole(User user){
        return null;
    }
}
