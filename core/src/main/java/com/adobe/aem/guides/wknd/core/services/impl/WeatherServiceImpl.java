package com.adobe.aem.guides.wknd.core.services.impl;

import com.adobe.aem.guides.wknd.core.pojo.WeatherData;
import com.adobe.aem.guides.wknd.core.services.WeatherConfig;
import com.adobe.aem.guides.wknd.core.services.WeatherService;
import com.adobe.aem.guides.wknd.core.pojo.OpenMeteoResponse;
import com.adobe.aem.guides.wknd.core.util.WeatherServiceException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Component(service = WeatherService.class, immediate = true)
@Designate(ocd = WeatherConfig.class)
public class WeatherServiceImpl implements WeatherService {

    private static final Logger LOG = LoggerFactory.getLogger(WeatherServiceImpl.class);

    private static final String LOCATION = "Bengaluru";

    private static final double LATITUDE = 12.97194;

    private static final double LONGITUDE = 77.59369;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private HttpClient httpClient;

    //private volatile CachedWeather cachedWeather;

    private long cacheTtlSeconds;

    private int connectTimeoutMillis;

    private int requestTimeoutMillis;

    private String apiUrl;

    private String latitude;

    private String longitude;

    @Activate
    protected void activate(WeatherConfig config) {
        this.cacheTtlSeconds = config.cacheTtlSeconds();
        this.connectTimeoutMillis = config.connectTimeoutMillis();
        this.requestTimeoutMillis = config.requestTimeoutMillis();
        this.apiUrl = config.apiUrl();
        this.latitude = config.latitude();
        this.longitude = config.longitude();
        this.httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofMillis(connectTimeoutMillis)).build();
        LOG.info("WeatherService activated. cacheTtl={}s, connectTimeout={}ms, requestTimeout={}ms",
                cacheTtlSeconds,
                connectTimeoutMillis,
                requestTimeoutMillis
        );
    }

    @Override
    public WeatherData getLocationWeather() {
        synchronized (this) {
            return fetchFromOpenMeteo();
        }
    }

    private WeatherData fetchFromOpenMeteo() {
        String url = apiUrl + "?latitude=" + latitude + "&longitude=" + longitude + "&current=is_day,temperature_2m" + "&forecast_days=1";
        LOG.debug("Calling Open-Meteo API URL: {}", url);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofMillis(requestTimeoutMillis))
                .header("Accept", "application/json")
                .GET()
                .build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            int statusCode = response.statusCode();
            if (statusCode < 200 || statusCode >= 300) {
                LOG.error("Open-Meteo returned HTTP status {}", statusCode);
                throw new WeatherServiceException("Open-Meteo returned HTTP " + statusCode);
            }
            OpenMeteoResponse apiResponse = objectMapper.readValue(response.body(), OpenMeteoResponse.class);
            validateResponse(apiResponse);
            OpenMeteoResponse.Current current = apiResponse.getCurrent();
            return new WeatherData(LOCATION, current.getTemperature2m(), current.getIsDay(), current.getTime());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LOG.error("Thread interrupted while calling Open-Meteo", e);
            throw new WeatherServiceException("Weather API request was interrupted", e);
        } catch (IOException e) {
            LOG.error("Error calling Open-Meteo", e);
            throw new WeatherServiceException("Unable to retrieve weather information", e
            );
        }
    }

    private void validateResponse(OpenMeteoResponse response) {
        if (response == null) {
            throw new WeatherServiceException("Open-Meteo returned an empty response");
        }
        if (response.getCurrent() == null) {
            throw new WeatherServiceException("Open-Meteo response does not contain current weather");
        }
    }

    /*@ObjectClassDefinition(name = "Weather Service Configuration", description = "Configuration for Open-Meteo weather integration")
    public @interface Config {
        @AttributeDefinition(name = "Cache TTL", description = "Weather cache lifetime in seconds")
        long cacheTtlSeconds() default 300;

        @AttributeDefinition(name = "Connection Timeout", description = "HTTP connection timeout in milliseconds")
        int connectTimeoutMillis() default 1000;

        @AttributeDefinition(name = "Request Timeout", description = "HTTP request timeout in milliseconds")
        int requestTimeoutMillis() default 5000;

        @AttributeDefinition(name = "API URL")
        String apiUrl() default "https://api.open-meteo.com/v1/forecast";
    }*/

    /*private final class CachedWeather {
        private final WeatherData weatherData;
        private final Instant cachedAt;

        private CachedWeather(WeatherData weatherData, Instant cachedAt) {
            this.weatherData = weatherData;
            this.cachedAt = cachedAt;
        }

        private boolean isExpired() {
            return Instant.now().isAfter(cachedAt.plusSeconds(cacheTtlSeconds));
        }
    }*/
}
