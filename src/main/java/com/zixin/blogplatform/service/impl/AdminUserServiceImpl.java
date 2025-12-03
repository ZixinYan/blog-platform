package com.zixin.blogplatform.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zixin.blogplatform.dao.AdminUserMapper;
import com.zixin.blogplatform.entity.AdminUser;
import com.zixin.blogplatform.service.AdminUserService;
import com.zixin.blogplatform.util.MD5Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 后台用户业务实现
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AdminUserServiceImpl extends ServiceImpl<AdminUserMapper, AdminUser> implements AdminUserService {

    private final AdminUserMapper adminUserMapper;

    @Override
    public AdminUser login(String userName, String password) {
        String passwordMd5 = MD5Util.MD5Encode(password, "UTF-8");
        AdminUser adminUser = adminUserMapper.login(userName, passwordMd5);
        log.info("Admin login attempt userName={}, success={}", userName, adminUser != null);
        return adminUser;
    }

    @Override
    public AdminUser getUserDetailById(Long loginUserId) {
        return adminUserMapper.selectByPrimaryKey(loginUserId);
    }

    @Override
    public Boolean updatePassword(Long loginUserId, String originalPassword, String newPassword) {
        AdminUser adminUser = adminUserMapper.selectByPrimaryKey(loginUserId);
        // 当前用户非空才可以进行更改
        if (adminUser != null) {
            String originalPasswordMd5 = MD5Util.MD5Encode(originalPassword, "UTF-8");
            String newPasswordMd5 = MD5Util.MD5Encode(newPassword, "UTF-8");
            // 比较原密码是否正确
            if (originalPasswordMd5.equals(adminUser.getLoginPassword())) {
                // 设置新密码并修改
                adminUser.setLoginPassword(newPasswordMd5);
                if (adminUserMapper.updateByPrimaryKeySelective(adminUser) > 0) {
                    log.info("Admin password updated, id={}", loginUserId);
                    // 修改成功则返回 true
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public Boolean updateName(Long loginUserId, String loginUserName, String nickName) {
        AdminUser adminUser = adminUserMapper.selectByPrimaryKey(loginUserId);
        // 当前用户非空才可以进行更改
        if (adminUser != null) {
            // 修改信息
            adminUser.setLoginUserName(loginUserName);
            adminUser.setNickName(nickName);
            if (adminUserMapper.updateByPrimaryKeySelective(adminUser) > 0) {
                log.info("Admin profile updated, id={}", loginUserId);
                // 修改成功则返回 true
                return true;
            }
        }
        return false;
    }
}
