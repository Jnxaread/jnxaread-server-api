package com.jnxaread.model;

import lombok.Data;

import java.util.Date;

/**
 * @author 未央
 * @create 2020-03-15 14:30
 */
@Data
public class UserModel {

    private Integer id;

    private String account;

    private String username;

    private String email;

    private Integer gender;

    private Integer level;

    private Integer fictionCount;

    private Integer chapterCount;

    /*private Integer commentCount;

    private Integer topicCount;

    private Integer replyCount;

    private Integer loginCount;*/

    private String signature;

    private String introduction;

    private Date createTime;

}
