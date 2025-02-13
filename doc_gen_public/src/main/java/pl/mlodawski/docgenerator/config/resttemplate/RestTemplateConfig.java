package pl.mlodawski.docgenerator.config.resttemplate;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

/*
 @Author Michał Młodawski
 */
/**
 * Configuration class for creating and customizing a {@code RestTemplate} bean.
 *
 * The {@code RestTemplate} is a synchronous client to perform HTTP requests,
 * exposing methods for different types of HTTP operations, such as GET, POST,
 * PUT, DELETE, etc. It simplifies interaction with HTTP servers and integrates
 * well with the Spring Framework.
 *
 * This class defines a {@link Bean} for {@code RestTemplate}, which can be injected
 * into other components within the Spring container for making HTTP requests.
 */
@Configuration
public class RestTemplateConfig {

    /**
     * Creates and configures a ClientHttpRequestFactory with custom timeout settings.
     *
     * @return configured ClientHttpRequestFactory instance
     */
    @Bean
    public ClientHttpRequestFactory clientHttpRequestFactory() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(60000);  // 60 seconds in milliseconds
        factory.setReadTimeout(60000);     // 60 seconds in milliseconds
        return factory;
    }

    /**
     * Creates and configures a RestTemplate bean with custom request factory.
     *
     * @param factory the configured ClientHttpRequestFactory
     * @return configured RestTemplate instance
     */
    @Bean
    public RestTemplate restTemplate(ClientHttpRequestFactory factory) {
        return new RestTemplate(factory);
    }
}


/*
"C:\Program Files\Eclipse Adoptium\jdk-21.0.5.11-hotspot\bin\keytool" -import -trustcacerts -keystore "C:\Program Files\Eclipse Adoptium\jdk-21.0.5.11-hotspot\lib\security\cacerts" -storepass changeit -noprompt -alias custom_cert -file "C:\Users\Michal\Desktop\server-1.oroshi.net.crt"
 */