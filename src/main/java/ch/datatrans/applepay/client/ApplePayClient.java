package ch.datatrans.applepay.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * @author dominik.mengelt@gmail.com
 */

@Component

public class ApplePayClient {

    private RestTemplate restTemplate;
    private ApplePaySession applePaySession;

    @Autowired
    public ApplePayClient(RestTemplate restTemplate, ApplePaySession applePaySession) {
        this.restTemplate = restTemplate;
        this.applePaySession = applePaySession;
    }

    public String createSession(String validationUrl, String origin) {
        applePaySession.setDomainName(origin.replace("https://", ""));
        return restTemplate.postForObject(validationUrl, applePaySession, String.class);
    }

    @Component
    static class ApplePaySession {

        private final String merchantIdentifier;
        private final String displayName;

        private String domainName;

        ApplePaySession(@Value("${ch.datatrans.applepay.merchantIdentifier}") String merchantIdentifier,
                        @Value("${ch.datatrans.applepay.displayName}") String displayName) {

            this.merchantIdentifier = merchantIdentifier;

            this.displayName = displayName;
        }

        public String getMerchantIdentifier() {
            return merchantIdentifier;
        }

        public String getDisplayName() {
            return displayName;
        }

        public String getDomainName() {
            return domainName;
        }

        public void setDomainName(String domainName) {
            this.domainName = domainName;
        }
    }
}
