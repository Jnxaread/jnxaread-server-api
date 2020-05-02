package com.jnxaread.bean.wrap;

import com.jnxaread.bean.Topic;

import java.util.Date;

/**
 * @author 未央
 * @create 2019-11-01 23:09
 */
public class WrapTopic extends Topic {

    private String username;
    //最后回复者
    private String lastReply;
    //最后回复时间
    private Date lastSubmit;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getLastReply() {
        return lastReply;
    }

    public void setLastReply(String lastReply) {
        this.lastReply = lastReply;
    }

    public Date getLastSubmit() {
        return lastSubmit;
    }

    public void setLastSubmit(Date lastSubmit) {
        this.lastSubmit = lastSubmit;
    }
}
