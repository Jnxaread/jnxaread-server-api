package com.jnxaread.service.impl;

import com.jnxaread.dao.UserMapper;
import com.jnxaread.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 用户Service实现类
 * @author 未央
 * @create 2020-04-21 17:08
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

}
