package com.jnxaread.bean.model;

import lombok.Data;

import java.util.Date;

/**
 * @author 未央
 * @create 2020-03-15 14:15
 */
@Data
public class NoticeModel {

    private Integer id;

    private Date submitTime;

    private Date lastSubmitTime;

    private String label;

    private String title;

    private Integer viewCount;

    private String content;

    private String username;

    private String lastEdit;

}
