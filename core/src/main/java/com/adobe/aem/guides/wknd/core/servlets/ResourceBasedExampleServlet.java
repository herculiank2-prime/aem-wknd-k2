package com.adobe.aem.guides.wknd.core.servlets;

import com.drew.lang.annotations.NotNull;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletResourceTypes;
import org.osgi.service.component.annotations.Component;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;

@Component(service = Servlet.class)
@SlingServletResourceTypes(resourceTypes = "wknd/components/image-list", methods = "GET", extensions = {"txt", "json"}, selectors = "variation")
public class ResourceBasedExampleServlet extends SlingSafeMethodsServlet {
    protected void doGet(@NotNull SlingHttpServletRequest request,
                         @NotNull SlingHttpServletResponse response) throws ServletException,
            IOException {
        Resource resource = request.getResource();
        if (resource != null) {
            ValueMap valueMap = resource.getValueMap();
            response.getWriter().println(valueMap);
        } else {
            response.sendError(404, "Resource not found");
        }
    }
}
