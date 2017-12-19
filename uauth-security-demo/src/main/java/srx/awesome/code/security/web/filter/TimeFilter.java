package srx.awesome.code.security.web.filter;

import org.springframework.stereotype.Component;

import javax.servlet.*;
import java.io.IOException;
import java.util.Date;

//@Component//生命为SpringBoot bean
public class TimeFilter implements Filter{
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("TimeFilter init");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        System.out.println("TimeFilter start!");
        long time = new Date().getTime();
        filterChain.doFilter(servletRequest,servletResponse);
        System.out.println("run time:"+(new Date().getTime()-time));
        System.out.println("TimeFilter Finish");
    }

    @Override
    public void destroy() {
        System.out.println("TimeFilter destroy");
    }
}
