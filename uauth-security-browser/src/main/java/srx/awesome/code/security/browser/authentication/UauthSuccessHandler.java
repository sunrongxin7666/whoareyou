package srx.awesome.code.security.browser.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component("uauthSuccessHandler")
public class UauthSuccessHandler implements AuthenticationSuccessHandler{

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired//spring会有默认的Bean
    private ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        logger.info("Login Success");
        //设置响应的内容格式
        response.setContentType("application/json;charset=UTF-8");
        //将authentication以Json字符串的格式返回
        response.getWriter().write(objectMapper.writeValueAsString(authentication));
    }
}
