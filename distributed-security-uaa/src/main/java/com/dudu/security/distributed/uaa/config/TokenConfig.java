package com.dudu.security.distributed.uaa.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

/**
 * @Description:
 * @author:zhaomeng
 * @date:2021/10/17 12:01 下午
 */
@Configuration
public class TokenConfig {
    /**
     *  //令牌存储策略-基于内存
     *     @Bean
     *     public TokenStore tokenStore() {
     *         //内存方式，生成普通令牌
     *         return new InMemoryTokenStore();
     *     }
     */
    /**
     * //令牌存储策略-基于jwt令牌
     */
    private String SIGNING_KEY = "uaa123";
    @Bean
    public TokenStore tokenStore() {
        return new JwtTokenStore(accessTokenConverter());
    }
    @Bean
    public JwtAccessTokenConverter accessTokenConverter() {
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        converter.setSigningKey(SIGNING_KEY); //对称秘钥，资源服务器使用该秘钥来验证
        return converter;
    }
}

