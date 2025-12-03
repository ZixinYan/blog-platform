package com.zixin.blogplatform.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.zixin.blogplatform.config.handler.StringListTypeHandler;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@TableName("blog")
public class Blog {
    @TableId(type = IdType.AUTO)
    private Long blogId;

    private String blogTitle;

    private String blogSubUrl;

    private String blogCoverImage;

    private Integer blogCategoryId;

    private String blogCategoryName;

    @TableField(value = "blog_tags", typeHandler = StringListTypeHandler.class)
    private List<String> blogTags;

    private Byte blogStatus;

    private Byte enableComment;

    @TableField(value = "blog_views")
    private long viewed;

    @TableLogic(value = "0", delval = "1")
    private Byte isDeleted;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    private String blogContent;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    @Version
    private int version;
}