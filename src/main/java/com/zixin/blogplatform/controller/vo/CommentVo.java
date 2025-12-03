package com.zixin.blogplatform.controller.vo;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class CommentVo {
    private Long id;
    private String content;
    private Long blogId;
    private Long parentId;
    private Date createTime;
    private List<CommentVo> replies;  // 用于存储子评论
}
