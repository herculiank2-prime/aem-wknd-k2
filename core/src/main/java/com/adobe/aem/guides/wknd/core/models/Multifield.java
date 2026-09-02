package com.adobe.aem.guides.wknd.core.models;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.util.List;

@Model(adaptables = {SlingHttpServletRequest.class, Resource.class}, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class Multifield {

    @ValueMapValue
    private String alt;

    @ValueMapValue
    private String displayPopupTitle;

    @ChildResource
    private List<Contact> contact;

    @SlingObject
    private ResourceResolver resourceResolver;

    @ScriptVariable
    private Page currentPage;

    @Self
    private SlingHttpServletRequest request;

    private static final Logger LOGGER = LoggerFactory.getLogger(Multifield.class);

    public String getAlt() {
        return alt;
    }

    public String getDisplayPopupTitle() {
        return displayPopupTitle;
    }

    public List<Contact> getContact() {
        return contact;
    }

    private String value;

    public String getValue() {
        return value;
    }

    public Page getCurrentPage() {
        return currentPage;
    }

    String paramValue;

    public String getParamValue() {
        return paramValue;
    }

    @PostConstruct
    void init() {
        PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
        assert pageManager != null;
        //currentPage = pageManager.getContainingPage(currentResource);
        Page cPage = currentPage;
        List<RequestParameter> list = request.getRequestParameterList();
        String paramName = list.getFirst().getName();
        paramValue = request.getCookie("wcmmode").getValue();
        LOGGER.debug("Multifield Init Method : {}, Value {}", paramName, paramValue);
        if (paramValue.equals("campaign1")) {

        }
    }
}
