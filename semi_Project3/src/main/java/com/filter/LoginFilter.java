package com.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.member.SessionInfo;

// 회원 로그인 여부 체크 필터

@WebFilter("/*")
public class LoginFilter implements Filter {

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		
		// 로그인 검사
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse resp = (HttpServletResponse) response;
		String cp = req.getContextPath();
		
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo)session.getAttribute("member");
		
		// 로그인이 되어 있지 않고 로그인이 필요한 요청인 경우
		if(info == null && isExcludeUri(req) == false) {
			if(isAjaxRequest(req)) {
				// AJAX 요청인 경우 403 에러 코드 전송
				resp.sendError(403);
			} else {
				// 로그인 전주소를 세션에 저장하면 로그인 후 전 주소로 이동
				String uri = req.getRequestURI();
				String qs = req.getQueryString(); // get 방식 파라미터
				if(qs != null) {
					uri += "?" + qs;
				}
				session.setAttribute("preLoginURI", uri);
				
				// 로그인 페이지로
				resp.sendRedirect(cp + "/member/login.do");
			}
			return;
		}
		
		// 다음 필터 또는 마지막 필터이면 end-pointer(서블릿 등)로 이동
		chain.doFilter(request, response);
		
		
	}

	
	@Override
	public void destroy() {
	}
	// 클라이언트 요청이 AJAX 요청인지 확인(AJAX는 헤더에 AJAX에 true로 설정해서 요청하도록 코딩함)
	protected boolean isAjaxRequest(HttpServletRequest req) {
		String h = req.getHeader("AJAX");
		
		return h != null && h.equals("true");
	}
	
	// 로그인 체크가 필요하지 않는 주소인지 여부
	protected boolean isExcludeUri(HttpServletRequest req) {
		String uri = req.getRequestURI();
		String cp = req.getContextPath();
		uri = uri.substring(cp.length()); // cp를 뗌(cp 다음부터)
		
		String []uris = {
				"/index.jsp", "/main.do",
				"/member/login.do", "/member/login_ok.do",
				"/member/member.do", "/member/member_ok.do",
				"/notice/list.do",
				"/resource/**"
		};
		
		if(uri.length() <= 1) {
			return true;
		}
		
		for(String s : uris) {
			if(s.lastIndexOf("**") != -1 ) {
				s = s.substring(0, s.lastIndexOf("**"));
				if(uri.indexOf(s) == 0) {
					return true;
				}
			} else if (uri.equals(s)) {
				return true;
			}
		}
		
		return false;
	}

}
