package com.mmall.controller.portal;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * Created by flnnf on 2017/6/24.
 */
@Controller
@RequestMapping("/user/")
public class UserController {

    @RequestMapping(value="login", method = RequestMethod.POST)
    @ResponseBody
    public Object login(String userName, String passwordm, HttpSession session){

        return null;
    }
}
