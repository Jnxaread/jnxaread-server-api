package com.jnxaread.model;

import lombok.Data;

import java.util.Date;

/**
 * @author 未央
 * @create 2020-05-08 11:06
 */
@Data
public class FictionModel {
    private Integer id;

    private String category;

    private String author;

    private Date createTime;

    private String title;

    private String introduction;

    private String[] tags;

    private Integer chapterCount;

    private Integer wordCount;

    private Integer commentCount;

    private Long viewCount;

    private Date lastTime;

    private Integer lastNumber;

    private String lastChapter;

    private Integer maxNumber;
}
