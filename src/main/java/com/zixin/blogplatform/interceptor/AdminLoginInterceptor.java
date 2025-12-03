package com.zixin.blogplatform.interceptor;

import com.alibaba.fastjson.JSON;
import com.zixin.blogplatform.util.JwtUtil;
import com.zixin.blogplatform.util.R;
import com.zixin.blogplatform.util.ThreadLocalUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

/**
 * 后台系统身份验证拦截器
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AdminLoginInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object o) throws Exception {
        String auth = request.getHeader("Authorization");
        String uri = request.getRequestURI();
        log.info("AdminLoginInterceptor preHandle, uri={}, authHeaderPresent={}", uri, auth != null);
        if (auth == null || !auth.startsWith("Bearer ")) {
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(JSON.toJSONString(R.error(401, "请先登录")));
            response.getWriter().flush();
            return false;
        }
        String token = auth.substring(7);
        if (!jwtUtil.validateToken(token)) {
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(JSON.toJSONString(R.error(401, "无效或过期的令牌")));
            response.getWriter().flush();
            return false;
        }
        Map<String,Object> claims = jwtUtil.parseToken(token);
        ThreadLocalUtil.set(claims);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
    }
}
