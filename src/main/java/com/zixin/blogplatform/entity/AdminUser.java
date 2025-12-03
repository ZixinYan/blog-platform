package com.zixin.blogplatform.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

@Data
@TableName("admin_user")
public class AdminUser {

    @TableId(value = "admin_user_id", type = IdType.AUTO)
    private Integer adminUserId;

    @TableField("login_user_name")
    private String loginUserName;

    @TableField("login_password")
    private String loginPassword;

    private String nickName;        // 这个字段名一致
    private Integer locked;         // 这个字段名一致

    // 添加 MyBatis-Plus 的逻辑删除字段
    @TableLogic(value = "0", delval = "1")
    private Integer isDeleted;

    // 添加时间字段（可选）
    @TableField(fill = FieldFill.INSERT)
    private java.util.Date createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private java.util.Date updateTime;
}