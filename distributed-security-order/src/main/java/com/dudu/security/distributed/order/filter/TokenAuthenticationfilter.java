package com.dudu.security.distributed.order.filter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dudu.security.distributed.order.common.EncryptUtil;
import com.dudu.security.distributed.order.model.UserDTO;
import com.fasterxml.jackson.annotation.JsonAlias;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Description:
 * @author:zhaomeng
 * @date:2021/10/24 12:16 下午
 */
@Component
public class TokenAuthenticationfilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest,
                                    HttpServletResponse httpServletResponse,
                                    FilterChain filterChain) throws ServletException, IOException {
        //解析请求头的token
        String jsonToken = httpServletRequest.getHeader("json-token");
        if (jsonToken != null ){
            String json = EncryptUtil.decodeUTF8StringBase64(jsonToken);
            JSONObject jsonObject = JSON.parseObject(json);
            //用户身份信息
            UserDTO userDTO = new UserDTO();
            String principal = jsonObject.getString("principal");
            userDTO.setUsername(principal);
            //用户权限
            JSONArray authoritiesArray = jsonObject.getJSONArray("authorities");
            String[] authorities = authoritiesArray.toArray(new String[authoritiesArray.size()]);
        //放在spring sevurity认识的类中
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDTO,
                    null,//权限凭证
                    AuthorityUtils.createAuthorityList(authorities));
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));
        //将authenticationToken放在上下文中
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }
        //放行
        filterChain.doFilter(httpServletRequest,httpServletResponse);
    }
}
