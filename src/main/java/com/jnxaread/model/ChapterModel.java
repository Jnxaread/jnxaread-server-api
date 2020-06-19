package com.jnxaread.model;

import lombok.Data;

import java.util.Date;

/**
 * @author 未央
 * @create 2020-05-22 19:04
 */
@Data
public class ChapterModel {
    private Integer id;

    private Integer fictionId;

    private String author;

    private Date createTime;

    private Integer number;

    private String title;

    private Integer wordCount;

    private Integer commentCount;

    private Integer viewCount;

    private Integer restricted;

    private String content;
}
