package com.jnxaread.controller;

import com.jnxaread.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 未央
 * @create 2020-04-21 17:11
 */
@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping("/world")
    public String hello() {
        return "hello world,jnxaread";
    }

}
