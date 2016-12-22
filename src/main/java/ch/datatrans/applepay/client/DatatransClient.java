package ch.datatrans.applepay.client;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.StringWriter;
import java.util.Properties;

/**
 * @author dominik.mengelt@gmail.com
 */
@Component
public final class DatatransClient {

    private final VelocityEngine velocityEngine;
    private final RestTemplate restTemplate;
    private final DatatransConfig datatransConfig;


    @Autowired
    public DatatransClient(RestTemplate restTemplate, DatatransConfig datatransConfig) {
        this.restTemplate = restTemplate;
        this.datatransConfig = datatransConfig;
        velocityEngine = new VelocityEngine();
        Properties properties = new Properties();
        properties.setProperty("resource.loader", "file");
        properties.setProperty("file.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        velocityEngine.init(properties);
    }

    public String authorize(String paymentData) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_XML);
        HttpEntity<String> request = new HttpEntity<>(getRequestPayload(paymentData), headers);
        ResponseEntity<String> response = restTemplate.postForEntity(datatransConfig.getEndpoint(), request, String.class);
        return response.getBody();
    }

    private String getRequestPayload(String paymentData) {
        VelocityContext context = new VelocityContext();
        context.put("merchantId", datatransConfig.getMerchantId());
        context.put("sign", datatransConfig.getSign());
        context.put("token", paymentData);
        context.put("refno", System.currentTimeMillis());
        Template authorizationServiceTemplate = velocityEngine.getTemplate("velocity/authorizationService.vm");
        StringWriter writer = new StringWriter();
        authorizationServiceTemplate.merge(context, writer);
        return writer.toString();
    }

    @Component
    static final class DatatransConfig {

        private final String merchantId;
        private final String sign;
        private final String endpoint;

        DatatransConfig(@Value("${ch.datatrans.merchantId}") String merchantId,
                        @Value("${ch.datatrans.sign}") String sign,
                        @Value("${ch.datatrans.endpoint}") String endpoint) {

            this.merchantId = merchantId;
            this.sign = sign;
            this.endpoint = endpoint;
        }

        public String getMerchantId() {
            return merchantId;
        }

        public String getSign() {
            return sign;
        }

        public String getEndpoint() {
            return endpoint;
        }
    }

}
