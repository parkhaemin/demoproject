package com.example.demo.config;

import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistration;

public interface WebMvcConfigurer extends org.springframework.web.servlet.config.annotation.WebMvcConfigurer {
    void addResourceHandlers(ResourceHandlerRegistration registry);
}
