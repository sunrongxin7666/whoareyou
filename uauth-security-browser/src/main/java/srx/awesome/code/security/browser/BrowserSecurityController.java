package srx.awesome.code.security.browser;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import srx.awesome.code.security.browser.support.SimpleResponse;
import srx.awesome.code.security.core.SecurityProperties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
public class BrowserSecurityController {

    private RequestCache cache = new HttpSessionRequestCache();
    private Logger logger = LoggerFactory.getLogger(getClass());
    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Autowired
    private SecurityProperties securityProperties;

    /**
     * 当需要身份认证时，跳转到这里
     * 根据不同的请求模式，才有不同的方法处理
     * @param request 当前请求
     * @param response 当前的响应
     * @return 响应结果
     */
    @RequestMapping("/authentication/require")
    @ResponseStatus(code = HttpStatus.UNAUTHORIZED)//绑定返回的状态码
    public SimpleResponse requireAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws IOException {
        //获得引发本次跳转的请求
        SavedRequest savedRequest = cache.getRequest(request,response);
        if(savedRequest != null) {//如果存在已发跳转的请求
            String redirectUrl = savedRequest.getRedirectUrl();
            logger.info("本次跳转由此路径请求引发："+redirectUrl);

            //如果是页面访问，则跳转到登录页面
            if(StringUtils.endsWithIgnoreCase(redirectUrl,".html")){
                redirectStrategy.sendRedirect(request,response,securityProperties.getBrowser().getLoginPage());
            }
        }
        //如果不是页面的请求，则返回未认证；
        return new SimpleResponse("该访问需要身份认证，请先登录");
    }
}
