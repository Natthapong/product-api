package th.co.truemoney.product.api.config;

import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
 
/**
 * Loads properties from a file called ${APP_ENV}.properties
 * or default.properties if APP_ENV is not set.
 */
@Configuration
@PropertySource("classpath:.properties")
public class PropertyPlaceholderConfig {
 
  @Bean
  public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
    return new PropertySourcesPlaceholderConfigurer();
  }
}