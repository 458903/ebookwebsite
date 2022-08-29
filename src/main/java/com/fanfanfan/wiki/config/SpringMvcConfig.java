package com.fanfanfan.wiki.config;
import com.fanfanfan.wiki.interceptor.LogInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

@Configuration
public class SpringMvcConfig implements WebMvcConfigurer {

    @Resource
    LogInterceptor loginInterceptor;


    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(//不拦截的请求
                        "/test/**",
                        "/redis/**",
                        "/user/login",
                        "/category/all",
                        "/ebook/list",
                        "/doc/all/**",
                        "/doc/vote/**",
                        "/doc/find-content/**",
                        "/ebook-snapshot/**"
                );

    //  registry.addInterceptor(actionInterceptor)
      //          .addPathPatterns(
        //                "/*/save",
          //              "/*/delete/**",
            //            "/*/reset-password");

    }
}
