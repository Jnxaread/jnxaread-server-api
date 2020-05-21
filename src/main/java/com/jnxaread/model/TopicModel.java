package com.jnxaread.model;

import lombok.Data;

import java.util.Date;

/**
 * 响应实体类模型
 *
 * @author 未央
 * @create 2020-03-15 13:34
 */
@Data
public class TopicModel {
    private Integer id;

    private Date createTime;

    private String label;

    private String title;

    private String content;

    private Integer replyCount;

    private Integer viewCount;

    private String username;

    //最后回复者
    private String lastReply;

    //最后回复时间
    private Date lastSubmit;
}
