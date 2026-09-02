package com.adobe.aem.guides.wknd.core.servlets;

import com.adobe.aem.guides.wknd.core.pojo.WeatherData;
import com.adobe.aem.guides.wknd.core.services.WeatherService;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletResourceTypes;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.Servlet;
import java.io.IOException;

@Component(service = Servlet.class)
@SlingServletResourceTypes(resourceTypes = "cq:Page", extensions = "json", selectors = "temperature")
public class WeatherServlet extends SlingSafeMethodsServlet {

    @Reference
    private WeatherService weatherService;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        WeatherData weather = weatherService.getLocationWeather();
        double temp = weather.getTemperature();
        response.getWriter().print(temp);
        // Serialize response
    }
}
