package com.zixin.blogplatform.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
@TableName("blog_comment")
public class BlogComment {

    @TableId(type = IdType.AUTO)
    private Long commentId;

    private Long blogId;

    private String commentator;

    private String email;

    private String websiteUrl;

    private String commentBody;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date commentCreateTime;

    private String commentatorIp;

    private Long parentCommentId;

    // 0 - parent comment, 1 - reply
    private Long commentStatus;

    @TableLogic(value = "0", delval = "1")
    private Byte isDeleted;

    public void setCommentator(String commentator) {
        this.commentator = commentator == null ? null : commentator.trim();
    }

    public void setEmail(String email) {
        this.email = email == null ? null : email.trim();
    }

    public void setWebsiteUrl(String websiteUrl) {
        this.websiteUrl = websiteUrl == null ? null : websiteUrl.trim();
    }

    public void setCommentBody(String commentBody) {
        this.commentBody = commentBody == null ? null : commentBody.trim();
    }

    public void setCommentatorIp(String commentatorIp) {
        this.commentatorIp = commentatorIp == null ? null : commentatorIp.trim();
    }




}