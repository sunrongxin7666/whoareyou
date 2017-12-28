#Spring Security

## Spring Security
### 核心功能
- 认证
- 授权
- 攻击防护（防止身法伪造）

### 任务大纲
1. Spring Security基本原理
2. username+password的认证方式
3. phonenumber+message的认证

默认情况下，所有路径都会启动HTTP BASIC来认证用户

### 自定义认证配置

```
@Configuration
public class BrowserSecurityConfig extends WebSecurityConfigurerAdapter{
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.formLogin()//表单认证
                .and()
                .authorizeRequests()//授权设置
                .anyRequest()//任何请求
                .authenticated();//都需要认证后授权
    }
}
```

### 基本原理
![Alt text](./1514212455382.png)
基于过滤器：绿色的自定义可配的（执行顺序和是否生效），但是蓝色的异常过滤器和橙色的拦截器是不能配置。

访问-》安全拦截器拦截请求，发现未认证-》异常过滤器异常处理-》重定向-》认证界面-》通过认证-》跳转到原访问界面-》安全拦截器拦截请求，发现已认证-》允许访问；

### 自定义用户认证逻辑：
1. 如何获取用户信息的逻辑：UserDetailService

```java
public interface UserDetailsService {	
	/**
	 * Locates the user based on the username. In the actual implementation, the search
	 * may possibly be case sensitive, or case insensitive depending on how the
	 * implementation instance is configured. In this case, the <code>UserDetails</code>
	 * object that comes back may have a username that is of a different case than what
	 * was actually requested..
	 *
	 * @param username the username identifying the user whose data is required.
	 *
	 * @return a fully populated user record (never <code>null</code>)
	 *
	 * @throws UsernameNotFoundException if the user could not be found or the user has no
	 * GrantedAuthority
	 */
	UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;
}

```
配置实例

```java
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
```

2. 处理用户校验的逻辑：UserDetail

```java
public interface UserDetails extends Serializable {
	
	/**
	 * Returns the authorities granted to the user. Cannot return <code>null</code>.
	 *
	 * @return the authorities, sorted by natural key (never <code>null</code>)
	 */
	Collection<? extends GrantedAuthority> getAuthorities();

	/**
	 * Returns the password used to authenticate the user.
	 *
	 * @return the password
	 */
	String getPassword();

	/**
	 * Returns the username used to authenticate the user. Cannot return <code>null</code>
	 * .
	 *
	 * @return the username (never <code>null</code>)
	 */
	String getUsername();

	/**
	 * Indicates whether the user's account has expired. An expired account cannot be
	 * authenticated.
	 *
	 * @return <code>true</code> if the user's account is valid (ie non-expired),
	 * <code>false</code> if no longer valid (ie expired)
	 */
	boolean isAccountNonExpired();

	/**
	 * Indicates whether the user is locked or unlocked. A locked user cannot be
	 * authenticated.
	 *
	 * @return <code>true</code> if the user is not locked, <code>false</code> otherwise
	 */
	boolean isAccountNonLocked();

	/**
	 * Indicates whether the user's credentials (password) has expired. Expired
	 * credentials prevent authentication.
	 *
	 * @return <code>true</code> if the user's credentials are valid (ie non-expired),
	 * <code>false</code> if no longer valid (ie expired)
	 */
	boolean isCredentialsNonExpired();

	/**
	 * Indicates whether the user is enabled or disabled. A disabled user cannot be
	 * authenticated.
	 *
	 * @return <code>true</code> if the user is enabled, <code>false</code> otherwise
	 */
	boolean isEnabled();
}
```
User类时UserDetail的一种实现。
```
public class User implements UserDetails, CredentialsContainer 
```

3. 密码的保存和验证的方式：PasswordEncoder  

```java
/**
 * Service interface for encoding passwords.
 *
 * The preferred implementation is {@code BCryptPasswordEncoder}.
 *
 * @author Keith Donald
 */
public interface PasswordEncoder {

	/**
	 * Encode the raw password. Generally, a good encoding algorithm applies a SHA-1 or
	 * greater hash combined with an 8-byte or greater randomly generated salt.
	 */
	String encode(CharSequence rawPassword);

	/**
	 * Verify the encoded password obtained from storage matches the submitted raw
	 * password after it too is encoded. Returns true if the passwords match, false if
	 * they do not. The stored password itself is never decoded.
	 *
	 * @param rawPassword the raw password to encode and match
	 * @param encodedPassword the encoded password from storage to compare with
	 * @return true if the raw password, after encoding, matches the encoded password from
	 * storage
	 */
	boolean matches(CharSequence rawPassword, String encodedPassword);

}

```
BCryptPasswordEncoder是PasswordEncoder的一种实现，在BrowserSecurityConfig中加入其为Bean
```java
@Configuration
public class BrowserSecurityConfig extends WebSecurityConfigurerAdapter {
    //......
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    //.....
}
```

### 个性化用户认证流程
1. 自定义登录界面
2. 自定义登录成功处理
3. 自定义登录失败处理
