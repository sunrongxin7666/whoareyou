package srx.awesome.code.security.browser;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
public class BrowserSecurityConfig extends WebSecurityConfigurerAdapter{//安全配置类
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.formLogin()//表单认证
                .and()
                .authorizeRequests()//授权设置
                .anyRequest()//任何请求
                .authenticated();//都需要认证后授权
    }
}
