package com.jnxaread.bean.wrap;

import com.jnxaread.bean.Notice;

/**
 * @author 未央
 * @create 2020-01-19 16:12
 */
public class WrapNotice extends Notice {

    private String username;

    private String lastEdit;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getLastEdit() {
        return lastEdit;
    }

    public void setLastEdit(String lastEdit) {
        this.lastEdit = lastEdit;
    }
}
