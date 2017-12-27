package srx.awesome.code.security.core;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties( prefix = "uauth.security")//读取所有以此开头的属性
public class SecurityProperties {
    public BrowserProperties getBrowser() {
        return browser;
    }

    public void setBrowser(BrowserProperties browser) {
        this.browser = browser;
    }

    private BrowserProperties browser = new BrowserProperties();
}
