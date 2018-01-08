package srx.awesome.code.security.browser.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import srx.awesome.code.security.browser.support.SimpleResponse;
import srx.awesome.code.security.core.properties.LoginType;
import srx.awesome.code.security.core.properties.SecurityProperties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

//SimpleUrlAuthenticationSuccessHandler
@Component("uauthFailureHandler")
public class UauthFailureHandler extends SimpleUrlAuthenticationFailureHandler{

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired//spring会有默认的注入
    private ObjectMapper objectMapper;

    @Autowired
    private SecurityProperties securityProperties;
    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        logger.info("Login Failure");
        if (LoginType.JSON.equals(securityProperties.getBrowser().getLoginType())) {
            //设置响应的内容格式
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(INTERNAL_SERVER_ERROR.value());
            //将authentication以Json字符串的格式返回
            response.getWriter().write(
                    objectMapper.writeValueAsString(
                            new SimpleResponse(exception.getMessage())));
        }else {
            super.onAuthenticationFailure(request,response, exception);
        }
    }
}
