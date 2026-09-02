package com.adobe.aem.guides.wknd.core.servlets;

import com.drew.lang.annotations.NotNull;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletPaths;
import org.osgi.service.component.annotations.Component;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;

@Component(service = Servlet.class)
@SlingServletPaths(value = "/bin/wknd/newPathBasedServlet")
public class PathBasedExampleServlet extends SlingSafeMethodsServlet {

    protected void doGet(@NotNull SlingHttpServletRequest request,
                         @NotNull SlingHttpServletResponse response) throws ServletException,
            IOException {
        String path = "/content/wknd/language-masters/en/faqs";
        Resource resource = request.getResourceResolver().resolve(path);
        if (resource != null) {
            ValueMap valueMap = resource.getValueMap();
            response.getWriter().println(valueMap);
        } else {
            response.sendError(404, "Resource not found");
        }
    }

}
