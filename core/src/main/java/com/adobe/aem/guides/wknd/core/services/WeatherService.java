package com.adobe.aem.guides.wknd.core.services;

import com.adobe.aem.guides.wknd.core.pojo.WeatherData;

public interface WeatherService {
    WeatherData getLocationWeather();
}
