package com.jnxaread.bean.wrap;

import com.jnxaread.bean.User;

/**
 * @author 未央
 * @create 2019-11-11 10:27
 */
public class UserWrap extends User {

    //邮箱验证码
    private String emailCode;

    public String getEmailCode() {
        return emailCode;
    }

    public void setEmailCode(String emailCode) {
        this.emailCode = emailCode;
    }
}
