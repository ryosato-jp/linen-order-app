package com.sato.linenorderapp.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.sato.linenorderapp.interceptor.LoginCheckInterceptor;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
	
	private final LoginCheckInterceptor loginCheckInterceptor;
	
	public WebMvcConfig(LoginCheckInterceptor loginCheckInterceptor) {
		this.loginCheckInterceptor = loginCheckInterceptor;
	}
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		
		registry.addInterceptor(loginCheckInterceptor)
		        .addPathPatterns("/**") //全部対象
		        .excludePathPatterns(
		        		"/login",           //ログイン画面（GET）
		        		"/error",           //Springのエラー画面
		        		"/css/**",
		        		"/js/**",
		        		"/images/**",
		        		"/favicon.info"
		        	);
	}
}
