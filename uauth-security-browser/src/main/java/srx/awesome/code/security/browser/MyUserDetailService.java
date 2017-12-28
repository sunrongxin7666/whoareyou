package srx.awesome.code.security.browser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class MyUserDetailService implements UserDetailsService{
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        //根据用户名来查找用户

        logger.info("login user name:" + s);
        // 根据查找的用户信息，判断账户是都可用
        // 密码+salt的数据摘要
        String pwd = passwordEncoder.encode("123456" );
        logger.info("login pwd:" + pwd);
        User user = new User(s, pwd, //username & pwd
                        true, //enable
                        true, //account not expired
                        true, //credentials not expired
                        true, // account not locked
                        AuthorityUtils.commaSeparatedStringToAuthorityList("admin"));// 授权
        return user;
    }
}
