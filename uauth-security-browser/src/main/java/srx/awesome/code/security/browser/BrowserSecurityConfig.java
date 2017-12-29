package srx.awesome.code.security.browser;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import srx.awesome.code.security.core.SecurityProperties;

@Configuration
public class BrowserSecurityConfig extends WebSecurityConfigurerAdapter{//安全配置类

    @Autowired
    private SecurityProperties securityProperties;

    @Autowired
    private AuthenticationSuccessHandler uauthSuccessHandler;

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.formLogin()//表单认证
                .loginPage("/authentication/require") //登录页面
                .loginProcessingUrl("/authentication/form")//处理登录请求的url
                .successHandler(uauthSuccessHandler)
                .and()
                .authorizeRequests()//授权设置
                .antMatchers("/authentication/require",
                        securityProperties.getBrowser().getLoginPage()).permitAll()
                .anyRequest()//任何请求
                .authenticated()//都需要认证后授权
                .and()
                .csrf()//关闭跨站攻击防护
                .disable();
    }
}
