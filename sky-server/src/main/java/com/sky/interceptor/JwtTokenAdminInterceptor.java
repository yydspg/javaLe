package com.sky.interceptor;

import com.sky.constant.JwtClaimsConstant;
import com.sky.context.BaseContext;
import com.sky.properties.JwtProperties;
import com.sky.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * jwt令牌校验的拦截器
 *
 */
@Slf4j
@Component
public class JwtTokenAdminInterceptor implements HandlerInterceptor{
    @Autowired
    private JwtProperties jwtProperties;

    /**
     * verify jwt ,return true means discharging and false means blocking
     * @param request current HTTP request
     * @param response current HTTP response
     * @param handler chosen handler to execute, for type and/or instance evaluation
     * @return
     * @throws Exception
     */
    public boolean preHandle(HttpServletRequest request,HttpServletResponse response,Object handler) throws Exception{
        //判断拦截到的是controller 还是other
        if(!(handler instanceof HandlerMethod)){
            //拦截到不是动态method
            return true;
        }
        //1.从request 获取令牌
        String token = request.getHeader(jwtProperties.getAdminTokenName());

        //2.校验令牌
        try {
            log.info("jwt verify {}!!!",token);
            Claims claims = JwtUtil.parseJWT(jwtProperties.getAdminSecretKey(),token);
            long empId = Long.valueOf(claims.get(JwtClaimsConstant.EMP_ID).toString());
            log.info("current user id :{}",empId);
            //3.设置ThreadLocal 的值!!!
            BaseContext.setCurrentId(empId);
            //discharged
            return true;
        } catch (NumberFormatException e) {
            response.setStatus(401);
            return false;
        }
    }
}