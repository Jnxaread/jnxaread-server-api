package com.jnxaread.controller;

import com.jnxaread.bean.wrap.WrapUser;
import com.jnxaread.entity.UnifiedResult;
import com.jnxaread.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @author 未央
 * @create 2020-04-21 17:11
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/signUp")
    public UnifiedResult signUp(HttpServletRequest request, WrapUser newUser) {
        return null;
    }

}
