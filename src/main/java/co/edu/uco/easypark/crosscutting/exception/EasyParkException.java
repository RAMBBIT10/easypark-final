package co.edu.uco.easypark.crosscutting.exception;

import org.springframework.http.HttpStatus;

public class EasyParkException extends RuntimeException {

    private final HttpStatus httpStatus;
    private final String messageKey;

    public EasyParkException(String messageKey, HttpStatus httpStatus) {
        super(messageKey);
        this.messageKey = messageKey;
        this.httpStatus = httpStatus;
    }

    public EasyParkException(String messageKey, HttpStatus httpStatus, Throwable cause) {
        super(messageKey, cause);
        this.messageKey = messageKey;
        this.httpStatus = httpStatus;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getMessageKey() {
        return messageKey;
    }
}