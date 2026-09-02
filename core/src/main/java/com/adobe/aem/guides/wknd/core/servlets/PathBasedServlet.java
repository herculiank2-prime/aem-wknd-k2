package com.adobe.aem.guides.wknd.core.servlets;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletPaths;
import org.apache.sling.servlets.annotations.SlingServletPathsStrict;
import org.osgi.service.component.annotations.Component;

import javax.servlet.Servlet;
import java.io.IOException;

@Component(service = Servlet.class)
@SlingServletPathsStrict(paths = "/bin/wknd/pathBasedServlet", extensions = "json", methods = "GET")
public class PathBasedServlet extends SlingAllMethodsServlet {

    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        if(request.getRequestParameter("path") != null && StringUtils.isNotEmpty(request.getRequestParameter("path").getString())) {
            String path = request.getRequestParameter("path").getString();
            Resource resource = request.getResourceResolver().getResource(path);
            if(resource != null) {
                ValueMap val = resource.getValueMap();
                response.getOutputStream().println(val.toString());
            } else {
                response.sendError(404, "Error !! Given path does not exist in AEM");
            }

        } else {
            response.sendError(400, "Error !! Path request parameter not sent or is empty.");
        }

    }
}
