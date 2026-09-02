package com.adobe.aem.guides.wknd.core.servlets;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletResourceTypes;
import org.osgi.service.component.annotations.Component;

import javax.servlet.Servlet;
import java.io.IOException;

@Component(service = Servlet.class)
@SlingServletResourceTypes(resourceTypes = "wknd/components/helloworld", selectors = "info", extensions = {"txt", "json"})
public class ResourceBasedServlet extends SlingSafeMethodsServlet {
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        Resource res = request.getResource();
        if (res != null) {
           response.getOutputStream().println(res.getValueMap().toString());
        } else {
            response.sendError(404, "Error !! Given path does not exist in AEM");
        }
    }
}
