package com.adobe.aem.guides.wknd.core.models;

import com.adobe.aem.guides.wknd.core.pojo.RelatedArticle;
import com.adobe.aem.guides.wknd.core.services.RelatedArticleService;
import com.day.cq.wcm.api.Page;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;

@Model(adaptables = SlingHttpServletRequest.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class RelatedArticleModel {

    @ScriptVariable
    private Page currentPage;

    @ValueMapValue
    private String title;

    @ValueMapValue
    private Integer limit;

    @ValueMapValue
    private String tag;

    @OSGiService
    private RelatedArticleService relatedArticleService;

    private List<RelatedArticle> relatedArticles;

    @PostConstruct
    protected void init() {
        if (currentPage == null) {
            relatedArticles = Collections.emptyList();
            return;
        }
        Resource contentResource = currentPage.getContentResource();
        if (contentResource == null) {
            relatedArticles = Collections.emptyList();
            return;
        }
        if (tag == null) {
            relatedArticles = Collections.emptyList();
            return;
        }
        int articleLimit = limit != null && limit > 0 ? limit : 5;
        relatedArticles = relatedArticleService.findRelatedArticles(currentPage.getPath(), tag, articleLimit);
    }

    public String getTitle() {
        return title != null && !title.isEmpty() ? title : "Related Articles";
    }

    public List<RelatedArticle> getRelatedArticles() {
        return relatedArticles;
    }

    public boolean isEmpty() {
        return relatedArticles == null || relatedArticles.isEmpty();
    }
}

