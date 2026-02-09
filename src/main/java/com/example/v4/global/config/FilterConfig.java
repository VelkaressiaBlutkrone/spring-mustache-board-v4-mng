package com.example.v4.global.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Configuration;

import com.example.v4.global.filter.FirstFilter;
import com.example.v4.global.filter.LoginFilter;

@Configuration
public class FilterConfig {

    // @Bean
    public FilterRegistrationBean<FirstFilter> firstFilterRegistration(FirstFilter firstFilter) {
        FilterRegistrationBean<FirstFilter> registration = new FilterRegistrationBean<>(firstFilter);
        registration.addUrlPatterns("/*");
        registration.setOrder(1);
        return registration;
    }

    // @Bean
    public FilterRegistrationBean<LoginFilter> loginFilterRegistration(LoginFilter loginFilter) {
        FilterRegistrationBean<LoginFilter> registration = new FilterRegistrationBean<>(loginFilter);
        registration.addUrlPatterns("/*");
        registration.setOrder(2);
        return registration;
    }
}
