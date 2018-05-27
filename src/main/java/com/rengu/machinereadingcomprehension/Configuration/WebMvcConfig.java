package com.rengu.machinereadingcomprehension.Configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 允许跨于请求的类型
        registry.addMapping("/**").allowedMethods("PUT", "PATCH", "DELETE", "GET", "POST");
    }
}
