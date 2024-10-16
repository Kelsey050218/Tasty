package com.cs183.tasty.filter;

import com.cs183.tasty.entity.pojo.LoginUser;
import com.cs183.tasty.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

import static com.cs183.tasty.constant.RedisConstants.LOGIN_USER_KEY;

@Component
//OncePerRequestFilter特点是在处理单个HTTP请求时确保过滤器的 doFilterInternal 方法只被调用一次
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        //在请求头中获取token
        String token = request.getHeader("authorization");

        //此处需要判断token是否为空
        if (!StringUtils.hasText(token)){
            //没有token放行 此时的SecurityContextHolder没有用户信息 会被后面的过滤器拦截
            filterChain.doFilter(request,response);
            return;
        }

        //解析token获取用户id
        String userId;
        try {
            Claims claims = JwtUtil.parseJWT(token);
            userId = claims.getSubject();
        } catch (Exception e) {
            //解析失败
            throw new RuntimeException("token非法");
        }
        //在redis中获取用户信息
        LoginUser user = (LoginUser) redisTemplate.opsForValue().get(LOGIN_USER_KEY + userId);

        //此处需要判断loginUser是否为空
        if (Objects.isNull(user)){
            throw new RuntimeException("用户未登录");
        }

        //实现Jwt的自动续期


        //将获取到的用户信息存入SecurityContextHolder 参数（用户信息，，权限信息）
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        //放行
        filterChain.doFilter(request,response);
    }
}