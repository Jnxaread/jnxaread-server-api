package com.jnxaread.model;

import lombok.Data;

import java.util.Date;

/**
 * @author 未央
 * @create 2020-03-15 13:59
 */
@Data
public class ReplyModel {

    private Date createTime;

    private Integer floor;

    private Integer quote;

    private String content;

    private String username;

}
