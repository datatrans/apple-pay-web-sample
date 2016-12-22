package ch.datatrans.applepay.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;

import javax.validation.constraints.NotNull;

/**
 * @author dominik.mengelt@gmail.com
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AuthorizeRequest {

    @NotNull
    private JsonNode paymentData;

    @NotNull
    private JsonNode paymentMethod;

    public JsonNode getPaymentData() {
        return paymentData;
    }

    public JsonNode getPaymentMethod() {
        return paymentMethod;
    }
}
