package com.tensquare.user.compoment;

import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import util.JwtUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class JwtInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //执行权限的校验
        System.out.println("拦截器已经执行。。。。");
        //1）从请求头中取Authorization，判断头是否存在
        String authorization = request.getHeader("Authorization");
        //	2）如果头不存在，认证失败。
        if (authorization == null || "".equals(authorization)) {
            throw new AuthorizationException();
        }
        //	3）如果有头，是否是以“Bearer ”开头。
        if (!authorization.startsWith("Bearer ")) {
        //	4）如果不是认证失败
            throw new AuthorizationException();
        }
        //	5）如果是以“Bearer ”开头
        //	6）取token
        String token = authorization.substring(7);
        //	7）对token进行校验，判断当前用户是否有角色信息。
        Claims claims = jwtUtil.parseJWT(token);
        String roles = (String) claims.get("roles");
        if (roles == null || "".equals(roles)) {
            throw new AuthorizationException();
        }
        //	8）校验通过，把认证结果解析出来，放到Request对象中。
        request.setAttribute("claims", claims);
        //如果返回true，放行。返回false，拦截
        return true;
    }
}
