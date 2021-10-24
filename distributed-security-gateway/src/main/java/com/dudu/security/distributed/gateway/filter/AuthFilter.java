package com.dudu.security.distributed.gateway.filter;

import com.alibaba.fastjson.JSON;
import com.dudu.security.distributed.gateway.common.EncryptUtil;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;

import java.util.*;


/**
 * @Description:
 * 目的是让下游微服务能够很方便的获取到当前的登录用户信息(明文token)
 * @author:zhaomeng
 * @date:2021/10/24 11:26 上午
 */
public class AuthFilter extends ZuulFilter {
    @Override
    public String filterType() {
        return "pre";
    }

    //最优先，数值越小越优先
    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run(){
       //1.获取当前用户身份信息和权限信息
        //1.1 security的方法
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        //1.2 如果不是使用OAuth2.0协议，则返回null
        if (!(authentication instanceof OAuth2Authentication)){
            return null;
        }
        OAuth2Authentication oAuth2Authentication = (OAuth2Authentication) authentication;
        Authentication userAuthentication = oAuth2Authentication.getUserAuthentication();
        //1.3  用户身份
        String name = userAuthentication.getName();
        //1.4  获取当前用户权限信息
        List<String>  authorities =new ArrayList<>();
        userAuthentication.getAuthorities().forEach(item ->authorities.add(item.getAuthority()));
        //2.把身份信息和权限信息放在json中，加入到http的header中
        OAuth2Request oAuth2Request = oAuth2Authentication.getOAuth2Request();
        Map<String, String> requestParameters = oAuth2Request.getRequestParameters();
        Map<String,Object> jsonToken = new HashMap<>(requestParameters);
        if (userAuthentication != null){
            jsonToken.put("principal",name);
            jsonToken.put("authorities",authorities);
        }
        //2.1 zuul的方法
        RequestContext currentContext = RequestContext.getCurrentContext();
        currentContext.addZuulRequestHeader("json-token",
                EncryptUtil.encodeUTF8StringBase64(JSON.toJSONString(jsonToken)));
        return null;
    }
}
