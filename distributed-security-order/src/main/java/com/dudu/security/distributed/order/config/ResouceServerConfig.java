package com.dudu.security.distributed.order.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.RemoteTokenServices;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;

/**
 * @Description:资源服务
 * @author:zhaomeng
 * @date:2021/10/17 8:31 下午
 */
@Configuration
@EnableResourceServer //标记资源服务
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class ResouceServerConfig extends ResourceServerConfigurerAdapter {
    //可以访问的资源，与uaa系统匹配
    public static final String RESOURCE_ID = "res1";
    @Autowired
    private TokenStore tokenStore;

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) {
        resources.resourceId(RESOURCE_ID)
                 .tokenStore(tokenStore)
//                 .tokenServices(tokenService()) 不使用jwt需要放开注释
                 .stateless(true);
    }
    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
            .antMatchers("/**")
                //与uaa系统匹配，否则不可访问
            .access("#oauth2.hasScope('all')")
            .and().csrf().disable()
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    /**
     *     //资源服务令牌解析服务，不使用jwt技术
     *     @Bean
     *     public ResourceServerTokenServices tokenService() {
     * //使用远程服务请求授权服务器校验token,
     * // 必须指定校验token 的url、client_id，client_secret
     *         RemoteTokenServices service=new RemoteTokenServices();
     *         //调用授权服务校验令牌
     *         service.setCheckTokenEndpointUrl("http://localhost:53020/uaa/oauth/check_token");
     *         service.setClientId("c1");
     *         service.setClientSecret("secret");
     *         return service;
     *     }
     * @return
     */



}

