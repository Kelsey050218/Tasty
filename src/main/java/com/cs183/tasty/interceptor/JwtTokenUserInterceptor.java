//package com.cs183.tasty.interceptor;
//
//import com.cs183.tasty.context.BaseContext;
//import com.cs183.tasty.constant.jwtClaimsConstant;
//import com.cs183.tasty.entity.pojo.LoginUser;
//import com.cs183.tasty.properties.JwtProperties;
//import com.cs183.tasty.utils.JwtUtil;
//import io.jsonwebtoken.Claims;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//import org.springframework.web.method.HandlerMethod;
//import org.springframework.web.servlet.HandlerInterceptor;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.util.Map;
//
//@Component
//@Slf4j
//public class JwtTokenUserInterceptor implements HandlerInterceptor {
//    @Autowired
//    private JwtProperties jwtProperties;
//
//    /**
//     * 校验jwt
//     *
//     * @param request
//     * @param response
//     * @param handler
//     * @return
//     * @throws Exception
//     */
//    @Override
//    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//        //判断当前拦截到的是Controller的方法还是其他资源
//        if (!(handler instanceof HandlerMethod)) {
//            //当前拦截到的不是动态方法，直接放行
//            return true;
//        }
//        //1、从请求头中获取令牌
//        String token = request.getHeader(jwtProperties.getUserTokenName());
//
//        //2、校验令牌
//        try {
//            log.info("jwt校验:{}", token);
//            Map<String,Object> claims = JwtUtil.parseJWT(jwtProperties.getUserSecretKey(), token);
//            LoginUser loginUser = (LoginUser) claims.get(jwtClaimsConstant.USER_INFO);
//            Long userId = loginUser.getUser().getUserId();
//            log.info("当前用户的id：{}", userId);
//            BaseContext.setCurrentId(userId);
//            //3、通过，放行
//            return true;
//        } catch (Exception ex) {
//            //4、不通过，响应401状态码
//            response.setStatus(401);
//            return false;
//        }
//    }
//}
//
//
//
