package srx.awesome.code.security.core;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(SecurityProperties.class)//让属性读取器生效
public class SecurityCoreConfig {
}
