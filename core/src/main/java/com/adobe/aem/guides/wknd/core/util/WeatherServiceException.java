package com.adobe.aem.guides.wknd.core.util;

public class WeatherServiceException extends RuntimeException {

    public WeatherServiceException(String message) {
        super(message);
    }

    public WeatherServiceException(
            String message,
            Throwable cause) {

        super(message, cause);
    }
}
