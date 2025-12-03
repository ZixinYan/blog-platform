package com.zixin.blogplatform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zixin.blogplatform.entity.AdminUser;

/**
 * 后台用户业务接口
 * 统一使用 MyBatis-Plus 的 IService 泛型接口，方便在 JDK17 下复用通用 CRUD 能力。
 */
public interface AdminUserService extends IService<AdminUser> {

    AdminUser login(String userName, String password);

    /**
     * 获取用户信息
     *
     * @param loginUserId 用户 id
     */
    AdminUser getUserDetailById(Long loginUserId);

    /**
     * 修改当前登录用户的密码
     */
    Boolean updatePassword(Long loginUserId, String originalPassword, String newPassword);

    /**
     * 修改当前登录用户的名称信息
     */
    Boolean updateName(Long loginUserId, String loginUserName, String nickName);

}
