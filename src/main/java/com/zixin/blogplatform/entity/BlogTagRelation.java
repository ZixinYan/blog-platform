package com.zixin.blogplatform.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("blog_tag_relation")
public class BlogTagRelation {
    @TableId(type = IdType.AUTO)
    private Long relationId;

    private Long blogId;

    private Integer tagId;

    private Date createTime;
}