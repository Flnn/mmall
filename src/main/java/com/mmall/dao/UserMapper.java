package com.mmall.dao;

import com.mmall.pojo.User;
import org.apache.ibatis.annotations.Param;

public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    int checkUserName(String username);

    User selectLogin(@Param("username") String username, @Param("password") String password);

    int checkEmail(String email);

    String selectQuestionByUsername(String username);

    int checkAnswer(@Param("username") String username, @Param("question")String question, @Param("answer")String answer);

    int updatePasswordByUsername(@Param("username") String username, @Param("md5Password") String md5Password);

    int checkPassword(@Param("md5Password") String md5Password, @Param("id") Integer id);

    int checkEmailByUserId(@Param("email") String email, @Param("id") Integer userId);
}