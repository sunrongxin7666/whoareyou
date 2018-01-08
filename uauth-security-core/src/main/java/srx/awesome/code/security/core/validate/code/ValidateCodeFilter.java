package srx.awesome.code.security.core.validate.code;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.social.connect.web.HttpSessionSessionStrategy;
import org.springframework.social.connect.web.SessionStrategy;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;

public class ValidateCodeFilter
        extends OncePerRequestFilter{//Spring 中只执行一次的过滤器


    public void setFailureHandler(AuthenticationFailureHandler failureHandler) {
        this.failureHandler = failureHandler;
    }

    private AuthenticationFailureHandler failureHandler;

    private SessionStrategy sessionStrategy = new HttpSessionSessionStrategy();


    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {

        if(StringUtils.equals("/authentication/form",httpServletRequest.getRequestURI())
                &&StringUtils.endsWithIgnoreCase("post",httpServletRequest.getMethod())){
            try{
                validate(new ServletWebRequest(httpServletRequest));
            } catch (ValidateCodeException e){
                failureHandler.onAuthenticationFailure(httpServletRequest,httpServletResponse,e);
                return;
            }
        }
        //如果不是登录界面的post请求，就直接执行后面的过滤器
        filterChain.doFilter(httpServletRequest,httpServletResponse);
    }

    /**
     * 如果验证不通过，就会抛出异常
     * @param request
     * @throws ServletRequestBindingException
     */
    private void validate(ServletWebRequest request) throws ServletRequestBindingException {
        ImageCode codeInSession = (ImageCode) sessionStrategy.getAttribute(request,ValidateCodeController.SESSION_KEY_IMAGE_CODE);

        String codeInRequest = ServletRequestUtils.getStringParameter(request.getRequest(), "imageCode");

        if(codeInSession == null){
            throw new ValidateCodeException("validateCode is null");
        }

        if(StringUtils.isEmpty(codeInRequest)){
            throw new ValidateCodeException("validateCode不存在");
        }

        if(codeInSession.isExpried()){
            sessionStrategy.removeAttribute(request,ValidateCodeController.SESSION_KEY_IMAGE_CODE);
            throw new ValidateCodeException("验证码过期");
        }

        if(!StringUtils.equals(codeInRequest,codeInSession.getCode())){
            throw new ValidateCodeException("验证码不正确");
        }

        sessionStrategy.removeAttribute(request,ValidateCodeController.SESSION_KEY_IMAGE_CODE);
    }
}
