package com.adobe.aem.guides.wknd.core.pojo;

public final class WeatherData {

    private final String location;
    private final double temperature;
    private final int isDay;
    private final String retrievedAt;

    public WeatherData(
            String location,
            double temperature,
            int isDay,
            String retrievedAt) {

        this.location = location;
        this.temperature = temperature;
        this.isDay = isDay;
        this.retrievedAt = retrievedAt;
    }

    public String getLocation() {
        return location;
    }

    public double getTemperature() {
        return temperature;
    }

    public int getIsDay() {
        return isDay;
    }

    public String getRetrievedAt() {
        return retrievedAt;
    }

    public String getDayNight() {
        return isDay == 1 ? "Day" : "Night";
    }
}
