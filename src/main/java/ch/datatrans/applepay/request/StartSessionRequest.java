package ch.datatrans.applepay.request;

import javax.validation.constraints.NotNull;

/**
 * @author dominik.mengelt@gmail.com
 */
public class StartSessionRequest {

    @NotNull
    private String validationUrl;

    public String getValidationUrl() {
        return validationUrl;
    }
}


