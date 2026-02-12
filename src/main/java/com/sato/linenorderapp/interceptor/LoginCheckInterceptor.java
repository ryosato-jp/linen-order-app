package com.sato.linenorderapp.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class LoginCheckInterceptor implements HandlerInterceptor {
	
	@Override
	public boolean preHandle(HttpServletRequest request,
			                 HttpServletResponse response,
			                 Object handler) throws Exception{
		
		String uri = request.getRequestURI();
		String ctx = request.getContextPath();
		
		//念のため、ログイン画面・エラーは素通り（excludeの保険）
		if(uri.equals(ctx + "/login") || uri.startsWith(ctx + "/erroer")) {
			return true;
			
		}
		
		HttpSession session = request.getSession(false);
		Object v = (session == null) ? null : session.getAttribute("facilityId");
		
		Long facilityId = null;
		if (v instanceof Long) {
			facilityId = (Long) v;
		} else if(v instanceof Integer) {
			facilityId = ((Integer)v).longValue();
		} else if(v instanceof String) {
			try {
				facilityId = Long.parseLong((String) v);
			} catch(NumberFormatException ignore) {}	
		}
		
		//未ログインならログイン画面へ
		if(facilityId == null) {
			response.sendRedirect(ctx + "/login");
			return false; //ここで処理を止める
		}
		
		return true; //OKならControllerへ進む
	}
}