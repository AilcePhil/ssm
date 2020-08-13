package com.powernode.filter;

import com.powernode.settings.pojo.TblUser;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class LoginFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        //向下转型
        servletRequest.setCharacterEncoding("UTF-8");
        servletResponse.setContentType("text/html;charset=UTF-8");
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        //获得路径
        String requestURI = request.getRequestURI();
        //获得当前登陆的用户
        TblUser user = (TblUser)request.getSession().getAttribute("USER");
        //放行满足条件的路径
        if (requestURI.contains("login") || requestURI.endsWith(".css") || requestURI.endsWith(".js") || requestURI.contains("font") || requestURI.endsWith(".JPG") ||requestURI.endsWith("message") || requestURI.endsWith("check")){
            chain.doFilter(request, response);
        }else {
            if (user != null){
                chain.doFilter(request, response);
            }else {
                //如果都不满足，则重定向到登陆页面
                response.sendRedirect(request.getContextPath() + "/login.html");
            }
        }

        /*HttpServletRequest request = (HttpServletRequest) ServletRequest;
        HttpServletResponse response = (HttpServletResponse) ServletResponse;
        //获得路径
        String requestURI = request.getRequestURI();
        //获得当前用户
        TblUser user = (TblUser) request.getSession().getAttribute("USER");

        //判断路径
        if (requestURI.contains("login") || requestURI.endsWith(".css") || requestURI.endsWith(".js") || requestURI.endsWith(".jpg") || requestURI.contains("font")){
            chain.doFilter(request,response);
        }else {
            if (user != null){
                chain.doFilter(request,response);
            }else {
                response.sendRedirect(request.getContextPath() + "/login.html");
            }
        }*/



    }



    @Override
    public void destroy() {

    }
}
