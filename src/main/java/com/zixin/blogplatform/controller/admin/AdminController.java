package com.zixin.blogplatform.controller.admin;

import cn.hutool.captcha.ShearCaptcha;
import com.zixin.blogplatform.config.exception.BizCodeEnum;
import com.zixin.blogplatform.controller.vo.UserVo;
import com.zixin.blogplatform.entity.AdminUser;
import com.zixin.blogplatform.service.AdminUserService;
import com.zixin.blogplatform.util.MD5Util;
import com.zixin.blogplatform.util.JwtUtil;
import com.zixin.blogplatform.util.R;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminUserService adminUserService;
    private final JwtUtil jwtUtil;
    /**
       * 管理员登录
     */
    @PostMapping(value = "/login")
    public R login(@RequestParam("userName") String userName, @RequestParam("password") String password, @RequestParam("verifyCode") String verifyCode, HttpSession session) {
        if (!StringUtils.hasText(verifyCode)) {return R.error(BizCodeEnum.SMS_CODE_ERROR.getCode(),BizCodeEnum.SMS_CODE_ERROR.getMsg());}
        if (!StringUtils.hasText(userName) || !StringUtils.hasText(password)) {return R.error(BizCodeEnum.LOGIN_ACCOUNT_PASSWORD_INVALID.getCode(),BizCodeEnum.LOGIN_ACCOUNT_PASSWORD_INVALID.getMsg());}
        ShearCaptcha shearCaptcha = (ShearCaptcha) session.getAttribute("verifyCode");
        if (shearCaptcha == null || !shearCaptcha.verify(verifyCode)) {
            return R.error(BizCodeEnum.SMS_CODE_ERROR.getCode(),BizCodeEnum.SMS_CODE_ERROR.getMsg());
        }
        AdminUser adminUser = adminUserService.login(userName, password);
        if (adminUser == null) {
            return R.error(BizCodeEnum.LOGIN_ACCOUNT_PASSWORD_INVALID.getCode(),BizCodeEnum.LOGIN_ACCOUNT_PASSWORD_INVALID.getMsg());
        }

        UserVo userVo = new UserVo();
        userVo.setUserId(adminUser.getAdminUserId());
        userVo.setUsername(adminUser.getLoginUserName());
        userVo.setNickname(adminUser.getNickName());

        String token = jwtUtil.generateToken(userVo);
        return R.ok().setData(token);
    }

    /**
     * 管理员注册
     */
    @PostMapping("/register")
    public R register(@RequestParam("userName") String userName,
                      @RequestParam("password") String password,
                      @RequestParam(value = "nickName", required = false) String nickName) {
        // 基本参数校验
        if (!StringUtils.hasText(userName) || !StringUtils.hasText(password)) {
            return R.error(BizCodeEnum.LOGIN_ACCOUNT_PASSWORD_INVALID.getCode(),
                    BizCodeEnum.LOGIN_ACCOUNT_PASSWORD_INVALID.getMsg());
        }

        // 检查用户名是否已存在
        boolean exists = adminUserService.lambdaQuery()
                .eq(AdminUser::getLoginUserName, userName)
                .oneOpt()
                .isPresent();
        if (exists) {
            return R.error(BizCodeEnum.LOGIN_ACCOUNT_PASSWORD_INVALID.getCode(), "用户名已存在");
        }

        // 构造新管理员用户，使用 MD5 加密密码
        AdminUser adminUser = new AdminUser();
        adminUser.setLoginUserName(userName);
        adminUser.setLoginPassword(MD5Util.MD5Encode(password, "UTF-8"));
        adminUser.setNickName(StringUtils.hasText(nickName) ? nickName : userName);
        adminUser.setLocked(0);

        boolean saved = adminUserService.save(adminUser);
        return saved ? R.ok() : R.error();
    }
}
