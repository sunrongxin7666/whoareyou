package srx.awesome.code.security.web.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import srx.awesome.code.security.web.filter.TimeFilter;
import srx.awesome.code.security.web.interceptor.TimeInterceptor;

import java.util.ArrayList;
import java.util.List;

//@Configuration//配置文件
public class WebConfig extends WebMvcConfigurerAdapter{//为了让拦截器生效 需要WebMvcConfigurerAdapter

    @Autowired
    private TimeInterceptor interceptor;

    //如果不使用@Componet 而将filter生效
    @Bean
    public FilterRegistrationBean timFilter(){
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();

        TimeFilter timeFilter = new TimeFilter();

        registrationBean.setFilter(timeFilter);//注册filter

        List<String> urls = new ArrayList<>();
        urls.add("/*");//配置生效的路径

        registrationBean.setUrlPatterns(urls);

        return registrationBean;
    }

    //注册拦截器
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //super.addInterceptors(registry);
        registry.addInterceptor(interceptor);
    }

    @Override//异步配置
    public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
        super.configureAsyncSupport(configurer);
        //注册异步的拦截器
        //configurer.registerCallableInterceptors();
        //configurer.registerDeferredResultInterceptors();
        //超时时间
        //configurer.setDefaultTimeout();
        //设置线程池
        //configurer.setTaskExecutor();
    }
}
