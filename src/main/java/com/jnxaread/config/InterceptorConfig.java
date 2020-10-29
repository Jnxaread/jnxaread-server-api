package com.jnxaread.config;

import com.jnxaread.interceptor.AccessInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

/**
 * @author 未央
 * @create 2020-06-19 18:46
 */
@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    @Resource
    private AccessInterceptor accessInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(accessInterceptor).addPathPatterns("/**");
    }
}
