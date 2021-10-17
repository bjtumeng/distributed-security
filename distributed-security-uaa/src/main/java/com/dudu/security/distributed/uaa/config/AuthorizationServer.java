package com.dudu.security.distributed.uaa.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.InMemoryAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.JdbcAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;


import javax.sql.DataSource;
import java.util.Arrays;

/**
 * @Description:配置OAuth2.0授权服务器
 * 需要重写父类AuthorizationServerConfigurerAdapter三个方法
 * @author:zhaomeng
 * @date:2021/10/17 11:42 上午
 */
@Configuration
@EnableAuthorizationServer //代表是授权服务
public class AuthorizationServer extends AuthorizationServerConfigurerAdapter {
    @Autowired
    private TokenStore tokenStore;
    @Autowired
    private ClientDetailsService clientDetailsService;
    @Autowired
    private AuthorizationCodeServices authorizationCodeServices;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtAccessTokenConverter accessTokenConverter;

    /**
     * 校验客户端服务是否合法，即能否访问授权服务
     * @param clients
     * @throws Exception
     */


    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
    // 使用in‐memory存储
       clients.inMemory()
               // client_id：(必须的)用来标识客户的Id
              .withClient("c1")
                    //客户端密码  相当于校验客户端的id和密码
              .secret(new BCryptPasswordEncoder().encode("secret"))
                 //资源列表
              .resourceIds("res1")
                    //此客户端可以使用的授权类型，默认为空
                    //该client允许的授权类型 authorization_code,password,refresh_token,implicit,client_credentials
              .authorizedGrantTypes("authorization_code",
                          "password","client_credentials","implicit","refresh_token")
                      //允许的授权范围 .autoApprove(false)
              .scopes("all")
                      //false：跳转到授权页面  true：直接发令牌
              .autoApprove(false)
                    //加上验证回调地址
              .redirectUris("http://www.baidu.com");
   }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     *2.使用数据库方式
     */
    @Bean
    public ClientDetailsService clientDetailsService(DataSource dataSource) {
        ClientDetailsService clientDetailsService = new JdbcClientDetailsService(dataSource);
        ((JdbcClientDetailsService) clientDetailsService).setPasswordEncoder(passwordEncoder());
        return clientDetailsService;
    }

    /**
     * 用来配置令牌(token)的访问端点（令牌url地址）和令牌服务(发放令牌的规则)。
     * @param endpoints
     * @throws Exception
     */
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints
                //密码模式需要
                .authenticationManager(authenticationManager)
                //授权码模式需要
                .authorizationCodeServices(authorizationCodeServices)
                //令牌管理服务
                .tokenServices(tokenService())
                .allowedTokenEndpointRequestMethods(HttpMethod.POST);
    }

    /**
     * 令牌管理服务
     * @return
     */
    @Bean
    public AuthorizationServerTokenServices tokenService() {
        DefaultTokenServices service=new DefaultTokenServices();
        service.setClientDetailsService(clientDetailsService);//客户端详情服务
        service.setSupportRefreshToken(true); //是否刷新令牌
        service.setTokenStore(tokenStore);
        //设置令牌增强，针对于jwt令牌
        TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
        tokenEnhancerChain.setTokenEnhancers(Arrays.asList(accessTokenConverter));
        service.setTokenEnhancer(tokenEnhancerChain);
        service.setAccessTokenValiditySeconds(7200); // 令牌默认有效期2小时
        service.setRefreshTokenValiditySeconds(259200); // 刷新令牌默认有效期3天
        return service;
    }

    //数据库存取授权码
    @Bean
    public AuthorizationCodeServices authorizationCodeServices(DataSource dataSource) {
        return new JdbcAuthorizationCodeServices(dataSource);
    }

    //内存存储授权码
//    @Bean
//    public AuthorizationCodeServices authorizationCodeServices() {
//        return new InMemoryAuthorizationCodeServices();
//    }

    /**
     * 用来配置令牌端点的安全约束（例如：谁可以访问）
     * @param security
     * @throws Exception
     */
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security
                //tokenkey这个endpoint当使用JwtToken且使用非对称加密时，
                //谁可以获取公钥，这里配置的是所有
                //url:  /oauth/token_key
                .tokenKeyAccess("permitAll()")
                //checkToken这个endpoint完全公开
                //url:  /oauth/check_token
                .checkTokenAccess("permitAll()")
                //允许表单认证
                .allowFormAuthenticationForClients();
    }
}
