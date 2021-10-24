package com.dudu.security.distributed.gateway.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;

/**
 * @Description:网关服务是资源服务，挂接几个微服务，需要配置几个微服务
 * 主要配置的内容就是定义一些匹配规则，描述某个接入客户端需要
 * 什么样的权限才能访问某个微服务
 * @author:zhaomeng
 * @date:2021/10/17 8:31 下午
 */
@Configuration
public class ResouceServerConfig{
    //可以访问的资源
    public static final String RESOURCE_ID = "res1";
    @Autowired
    private TokenStore tokenStore;

    @Configuration
    @EnableResourceServer //标记资源服务
    public class UAAResouceServerConfig extends ResourceServerConfigurerAdapter {
        @Override
        public void configure(ResourceServerSecurityConfigurer resources){
            resources.tokenStore(tokenStore)
                     .resourceId(RESOURCE_ID)
                     .stateless(true);
        }

        //放行所有的请求
        @Override
        public void configure(HttpSecurity http) throws Exception {
            http.authorizeRequests()
                .antMatchers("/uaa/**").permitAll();
        }
    }

    @Configuration
    @EnableResourceServer //标记资源服务
    public class OrderResouceServerConfig extends ResourceServerConfigurerAdapter {
        @Override
        public void configure(ResourceServerSecurityConfigurer resources){
            resources.tokenStore(tokenStore)
                    .resourceId(RESOURCE_ID)
                    .stateless(true);
        }

        //放行所有的请求
        @Override
        public void configure(HttpSecurity http) throws Exception {
            http.authorizeRequests()
                    .antMatchers("/order/**").access("#oauth2.hasScope('ROLE_API')");
        }
    }

}

