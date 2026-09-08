package com.adobe.aem.guides.wknd.core.util;

import java.util.Map;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(service = ServiceResolverFactory.class)
public final class ServiceResolverFactory {
    public static final String SUBSERVICE = "background-processing";
    @Reference private ResourceResolverFactory resolverFactory;

    public ResourceResolver get() throws LoginException {
        return resolverFactory.getServiceResourceResolver(
                Map.of(ResourceResolverFactory.SUBSERVICE, SUBSERVICE));
    }
}
