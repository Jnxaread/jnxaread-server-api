package com.jnxaread.model;

import lombok.Data;

import java.util.Date;

/**
 * @author 未央
 * @create 2020-05-09 9:16
 */
@Data
public class CommentModel {

    private String username;

    private Date createTime;

    private String content;

}
