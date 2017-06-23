package uk.gov.ons.ctp.common.error;


import org.springframework.validation.Errors;

public class InvalidRequestException extends Exception {

    private Errors errors;
    private String sourceMessage = "Invalid Request ";

    public InvalidRequestException(String message, Errors errors) {
        super(message);
        this.errors = errors;
    }

    public Errors getErrors() {
        return errors;
    }

    public String getSourceMessage() {
        return sourceMessage;
    }
}