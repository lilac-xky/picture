package com.lilac.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.web.http.HeaderHttpSessionIdResolver;
import org.springframework.session.web.http.HttpSessionIdResolver;

/**
 * @author lilac
 */
@Configuration
public class HttpSessionConfig {
    /**
     * 开启通过 Header 解析 SessionId
     * 支持名为 "x-auth-token" 的请求头
     */
    @Bean
    public HttpSessionIdResolver httpSessionIdResolver() {
        return HeaderHttpSessionIdResolver.xAuthToken();
    }
}