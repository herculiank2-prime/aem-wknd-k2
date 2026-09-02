package com.adobe.aem.guides.wknd.core.services;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "Weather Config", description = "Enter Weather configuration details here")
public @interface WeatherConfig {
    @AttributeDefinition(name = "Cache TTL", description = "Weather cache lifetime in seconds")
    long cacheTtlSeconds() default 300;

    @AttributeDefinition(name = "Connection Timeout", description = "HTTP connection timeout in milliseconds")
    int connectTimeoutMillis() default 1000;

    @AttributeDefinition(name = "Request Timeout", description = "HTTP request timeout in milliseconds")
    int requestTimeoutMillis() default 5000;

    @AttributeDefinition(name = "API URL")
    String apiUrl() default "https://api.open-meteo.com/v1/forecast";

    @AttributeDefinition(name = "Latitude")
    String latitude() default "";

    @AttributeDefinition(name = "Longitude")
    String longitude() default "";
}
