package com.example.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * 跨域配置
 */
@Configuration
public class CorsConfig {


     @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedOrigin("*"); // 2 设置访问源地址
        corsConfiguration.addAllowedHeader("*"); // 3 设置访问源请求头
        corsConfiguration.addAllowedMethod("*"); // 4 设置访问源请求方法
        source.registerCorsConfiguration("/**", corsConfiguration); // 5 对接口配置跨域设置
        return new CorsFilter(source);
    }
}
