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

![Alt text](./1514377048979.png)
1. 自定义登录界面
 ![处理不同类型的请求](./1514377870857.png)
2. 自定义登录成功处理
AuthenticationSuccessHandle

``` java
/**
 * Strategy used to handle a successful user authentication.
 * <p>
 * Implementations can do whatever they want but typical behaviour would be to control the
 * navigation to the subsequent destination (using a redirect or a forward). For example,
 * after a user has logged in by submitting a login form, the application needs to decide
 * where they should be redirected to afterwards (see
 * {@link AbstractAuthenticationProcessingFilter} and subclasses). Other logic may also be
 * included if required.
 *
 * @author Luke Taylor
 * @since 3.0
 */
public interface AuthenticationSuccessHandler {

	/**
	 * Called when a user has been successfully authenticated.
	 *
	 * @param request the request which caused the successful authentication
	 * @param response the response
	 * @param authentication the <tt>Authentication</tt> object which was created during
	 * the authentication process.
	 */
	void onAuthenticationSuccess(HttpServletRequest request,
			HttpServletResponse response, Authentication authentication)
			throws IOException, ServletException;

}
```

```java
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
```

3. 自定义登录失败处理

```java
@Component("uauthFailureHandler")
public class UauthFailureHandler implements AuthenticationFailureHandler{

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired//spring会有默认的注入
    private ObjectMapper objectMapper;
    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        logger.info("Login Failure");
        //设置响应的内容格式
        response.setContentType("application/json;charset=UTF-8");
        //将authentication以Json字符串的格式返回
        response.getWriter().write(objectMapper.writeValueAsString(exception));
    }
}
```

Spring默认的成功和失败的处理器。
SimpleUrlAuthenticationFailureHandler
SavedRequestAwareAuthenticationSuccessHandler

### 源码分析
1. 认证流程说明；
![Alt text](./1514892525283.png)
![Alt text](./1514892706787.png)

提交表单-》
#### 1.UsernamePasswordAuthenticationFilter.class 
![Alt text](./1514893133447.png)
构建 UsernamePasswordAuthenticationToken 
![Alt text](./1514893159884.png)
其父类是Authentication接口的实现，其中有认证信息

![构造器](./1514893199514.png)
UsernamePasswordAuthenticationToken 的父类之中传入权限
![Alt text](./1514893239121.png)

UsernamePasswordAuthenticationToken 支持自定义属性
![Alt text](./1514893421959.png)

然后跳到AuthenticationManager。
![Alt text](./1514893531085.png)
传入的参数就是UsernamePasswordAuthenticationToken 

#### 2. AuthenticationManager
具体的实现为ProviderManager，其authenticate方法
![Alt text](./1514893624674.png)

在循环中，拿到所有AuthenticationProvider接口的实现，其中有真正认证的逻辑，以满足不同的认证流程。
其中provider.supports就是返回是否支持当前的认证流程
![Alt text](./1514893795068.png)

如果支持则执行认证逻辑：

```
result = provider.authenticate(authentication);

if (result != null) {
	copyDetails(authentication, result);
	break;
}
```

比如用户名口令认证的provider是

```
public class DaoAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider
```

主要的认证逻辑在抽象类中，
其中authenticate方法里，首先获得用户信息UserDetails，然后再认证
![Alt text](./1514894298444.png)

DaoAuthenticationProvider类中实现了retrieveUser方法

```
	protected final UserDetails retrieveUser(String username,
			UsernamePasswordAuthenticationToken authentication)
			throws AuthenticationException {
		UserDetails loadedUser;

		try {
			loadedUser = this.getUserDetailsService().loadUserByUsername(username);
		}
		catch (UsernameNotFoundException notFound) {
			if (authentication.getCredentials() != null) {
				String presentedPassword = authentication.getCredentials().toString();
				passwordEncoder.isPasswordValid(userNotFoundEncodedPassword,
						presentedPassword, null);
			}
			throw notFound;
		}
		catch (Exception repositoryProblem) {
			throw new InternalAuthenticationServiceException(
					repositoryProblem.getMessage(), repositoryProblem);
		}

		if (loadedUser == null) {
			throw new InternalAuthenticationServiceException(
					"UserDetailsService returned null, which is an interface contract violation");
		}
		return loadedUser;
	}
```
![Alt text](./1514894413362.png)
这里调用前文中定义的UserDetailsService来处用户认证信息，最终获得UserDetails。 

AbstractUserDetailsAuthenticationProvider在获得UserDetails后，进行预处理
```
preAuthenticationChecks.check(user);
additionalAuthenticationChecks(user,
	(UsernamePasswordAuthenticationToken) authentication);
```
![Alt text](./1514894957057.png)

预处理中

2. 认证结果如何在多个请求间共享；
3. 获取认证用户信息；


图形验证码
1. 生成图形验证码的接口
根据随机数生成图片；
将随机数保存在Session中；
将生成的图片写到接口的响应中；
2. 在认证流程中加入图形验证码
3. 重构


