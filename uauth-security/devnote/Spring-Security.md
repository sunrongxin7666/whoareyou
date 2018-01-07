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
#### 1. 认证流程说明；
![Alt text](./1514892525283.png)
![Alt text](./1514892706787.png)

提交表单-》UsernamePasswordAuthenticationFilter拦截该请求

```
public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;

		if (!requiresAuthentication(request, response)) {
			chain.doFilter(request, response);

			return;
		}

		if (logger.isDebugEnabled()) {
			logger.debug("Request is to process authentication");
		}

		Authentication authResult;

		try {
			authResult = attemptAuthentication(request, response);
			if (authResult == null) {
				// return immediately as subclass has indicated that it hasn't completed
				// authentication
				return;
			}
			sessionStrategy.onAuthentication(authResult, request, response);
		}
		catch (InternalAuthenticationServiceException failed) {
			logger.error(
					"An internal error occurred while trying to authenticate the user.",
					failed);
			unsuccessfulAuthentication(request, response, failed);

			return;
		}
		catch (AuthenticationException failed) {
			// Authentication failed
			unsuccessfulAuthentication(request, response, failed);

			return;
		}

		// Authentication success
		if (continueChainBeforeSuccessfulAuthentication) {
			chain.doFilter(request, response);
		}

		successfulAuthentication(request, response, chain, authResult);
	}
```


##### 1.UsernamePasswordAuthenticationFilter.class 
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

##### 2. AuthenticationManager
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
##### 3. AbstractUserDetailsAuthenticationProvider
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
```
private class DefaultPreAuthenticationChecks implements UserDetailsChecker {
		public void check(UserDetails user) {
			if (!user.isAccountNonLocked()) {
				logger.debug("User account is locked");

				throw new LockedException(messages.getMessage(
						"AbstractUserDetailsAuthenticationProvider.locked",
						"User account is locked"));
			}

			if (!user.isEnabled()) {
				logger.debug("User account is disabled");

				throw new DisabledException(messages.getMessage(
						"AbstractUserDetailsAuthenticationProvider.disabled",
						"User is disabled"));
			}

			if (!user.isAccountNonExpired()) {
				logger.debug("User account is expired");

				throw new AccountExpiredException(messages.getMessage(
						"AbstractUserDetailsAuthenticationProvider.expired",
						"User account has expired"));
			}
		}
	}
```
检查用户是否被锁定；
检查用户是否被删除；
检查用户有效性是否过期；

预先检查之后，再进行附加检查，
`additionalAuthenticationChecks`
主要的任务是使用passwordEncoder验证密码的信息摘要
```
protected void additionalAuthenticationChecks(UserDetails userDetails,
			UsernamePasswordAuthenticationToken authentication)
			throws AuthenticationException {
		Object salt = null;

		if (this.saltSource != null) {
			salt = this.saltSource.getSalt(userDetails);
		}

		if (authentication.getCredentials() == null) {
			logger.debug("Authentication failed: no credentials provided");

			throw new BadCredentialsException(messages.getMessage(
					"AbstractUserDetailsAuthenticationProvider.badCredentials",
					"Bad credentials"));
		}

		String presentedPassword = authentication.getCredentials().toString();

		if (!passwordEncoder.isPasswordValid(userDetails.getPassword(),
				presentedPassword, salt)) {
			logger.debug("Authentication failed: password does not match stored value");

			throw new BadCredentialsException(messages.getMessage(
					"AbstractUserDetailsAuthenticationProvider.badCredentials",
					"Bad credentials"));
		}
	}
```

通过预先检查和附加检查之后，AbstractUserDetailsAuthenticationProvider的authenticate方法会继续执行后续检查`postAuthenticationChecks.check(user);`
即校验最后一个bool，用户的身份凭据是否过期
```
	private class DefaultPostAuthenticationChecks implements UserDetailsChecker {
		public void check(UserDetails user) {
			if (!user.isCredentialsNonExpired()) {
				logger.debug("User account credentials have expired");

				throw new CredentialsExpiredException(messages.getMessage(
						"AbstractUserDetailsAuthenticationProvider.credentialsExpired",
						"User credentials have expired"));
			}
		}
	}
```
都通过之后，生成用户认证成功信息。

```
return createSuccessAuthentication(principalToReturn, authentication, user);
```

##### 4. UsernamePasswordAuthenticationToken 
再次生成UsernamePasswordAuthenticationToken ，不过这次是有授权信息的
```
	protected Authentication createSuccessAuthentication(Object principal,
			Authentication authentication, UserDetails user) {
		// Ensure we return the original credentials the user supplied,
		// so subsequent attempts are successful even with encoded passwords.
		// Also ensure we return the original getDetails(), so that future
		// authentication events after cache expiry contain the details
		UsernamePasswordAuthenticationToken result = new UsernamePasswordAuthenticationToken(
				principal, authentication.getCredentials(),
				authoritiesMapper.mapAuthorities(user.getAuthorities()));
		result.setDetails(authentication.getDetails());

		return result;
	}
```

调用四个参数的构造器，将认证信息设为true。
```
	public UsernamePasswordAuthenticationToken(Object principal, Object credentials,
			Collection<? extends GrantedAuthority> authorities) {
		super(authorities);
		this.principal = principal; //用户
		this.credentials = credentials; //凭证(密码)
		super.setAuthenticated(true); // must use super, as we override
	}
```
这时候UserDetail中已经有了用户的授权信息。

##### 5. Authentication 
生成Authentication 之后，认证信息将返回给`UsernamePasswordAuthenticationFilter`，其父类`AbstractAuthenticationProcessingFilter`中`successfulAuthentication(request, response, chain, authResult);`方法中调用`successHandler.onAuthenticationSuccess(request, response, authResult);`使用定义的认证成功处理器来处理该事件，同理，如果认证失败调用`unsuccessfulAuthentication(request, response, failed);`


#### 2. 认证结果如何在多个请求间共享
Session. 何时将什么对象放入Session
![Alt text](./1514983385576.png)
![Alt text](./1514983471985.png)

`AbstractAuthenticationProcessingFilter`中`successfulAuthentication`方法中调用`successHandler.onAuthenticationSuccess`之前
将认证结果放入**SecurityContext**，在交给**SecurityContextHolder**

```
SecurityContextHolder.getContext().setAuthentication(authResult);
```
SecurityContextImpl是SecurityContext的实现，其中封装了Authentication对象，并重写了其Equals和HashCode方法
![Alt text](./1514983685712.png)

而SecurityContextHolder是一种ThreadLocal，用于在线程内部隔离变量，SecurityContext就放在其中。

因为处理请求和返回响应都在同一个线程中完成的，所以请求时放入认证结果，在同一线程的后续其他程序中，也能被读出，包括响应过程。
![Alt text](./1514984078763.png)

`SecurityContextPersistenceFilter`在过滤器的最前端，请求到来时，其在会尝试在Session中尝试SecurityContext，如果不能找到SecurityContext，则说明没有认证，交个后面的过滤器来处理；当返回响应时，如果SecurityContextHolder中包含SecurityContext，其会SecurityContext放入Session中。


```
public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;

		if (request.getAttribute(FILTER_APPLIED) != null) {
			// ensure that filter is only applied once per request
			chain.doFilter(request, response);
			return;
		}

		final boolean debug = logger.isDebugEnabled();

		request.setAttribute(FILTER_APPLIED, Boolean.TRUE);

		if (forceEagerSessionCreation) {
			HttpSession session = request.getSession();

			if (debug && session.isNew()) {
				logger.debug("Eagerly created session: " + session.getId());
			}
		}

		HttpRequestResponseHolder holder = new HttpRequestResponseHolder(request,
				response);
		SecurityContext contextBeforeChainExecution = repo.loadContext(holder);

		try {
			SecurityContextHolder.setContext(contextBeforeChainExecution);//session中取出SecurityContext，可能null

			chain.doFilter(holder.getRequest(), holder.getResponse());//进行后续过滤器的处理

		}
		finally { //执行后，SecurityContextHolder取出SecurityContext,放入Session 
			SecurityContext contextAfterChainExecution = SecurityContextHolder
					.getContext();
			// Crucial removal of SecurityContextHolder contents - do this before anything
			// else.
			SecurityContextHolder.clearContext();
			repo.saveContext(contextAfterChainExecution, holder.getRequest(),
					holder.getResponse());
			request.removeAttribute(FILTER_APPLIED);

			if (debug) {
				logger.debug("SecurityContextHolder now cleared, as request processing completed");
			}
		}
	}
```

3. 获取认证用户信息；

```
    @GetMapping("/me")
    public Object getCurrentUser(){
        return SecurityContextHolder.getContext().getAuthentication();
    }
```

![Alt text](./1514985497159.png)
![Alt text](./1514986340828.png)





图形验证码
1. 生成图形验证码的接口
根据随机数生成图片；
将随机数保存在Session中；
将生成的图片写到接口的响应中；
2. 在认证流程中加入图形验证码
3. 重构


