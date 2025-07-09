package com.admin.school.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/")
                .setCachePeriod(3600)
                .resourceChain(true);
        
        // Add specific handlers for different file types
        registry.addResourceHandler("/uploads/images/**")
                .addResourceLocations("file:uploads/images/")
                .setCachePeriod(3600);
        
        registry.addResourceHandler("/uploads/videos/**")
                .addResourceLocations("file:uploads/videos/")
                .setCachePeriod(3600);
        
        registry.addResourceHandler("/uploads/audio/**")
                .addResourceLocations("file:uploads/audio/")
                .setCachePeriod(3600);
    }
} 