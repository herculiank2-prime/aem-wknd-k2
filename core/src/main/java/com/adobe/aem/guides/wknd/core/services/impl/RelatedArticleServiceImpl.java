package com.adobe.aem.guides.wknd.core.services.impl;

import com.adobe.aem.guides.wknd.core.pojo.RelatedArticle;
import com.adobe.aem.guides.wknd.core.services.RelatedArticleService;
import com.adobe.aem.guides.wknd.core.util.ServiceResolverFactory;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Session;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component(service = RelatedArticleService.class)
public class RelatedArticleServiceImpl implements RelatedArticleService {

    private static final Logger LOG = LoggerFactory.getLogger(RelatedArticleServiceImpl.class);
    private static final String ARTICLE_ROOT = "/content/wknd/us/en/adventures";
    private static final String ARTICLE_PAGE_TEMPLATE = "/conf/wknd/settings/wcm/templates/adventure-page-template";

    @Reference
    private QueryBuilder queryBuilder;

    @Reference
    private ServiceResolverFactory serviceResolverFactory;

    @Override
    public List<RelatedArticle> findRelatedArticles(String currentPagePath, String currentPageTag, int limit) {
        List<RelatedArticle> articles = new ArrayList<>();
        if (currentPagePath == null || currentPagePath.isEmpty()) {
            LOG.warn("Current page path is empty");
            return articles;
        }
        if (currentPageTag == null) {
            LOG.debug("No tags found for page {}", currentPagePath);
            return articles;
        }
        if (limit <= 0) {
            limit = 5;
        }
        try (ResourceResolver resourceResolver = serviceResolverFactory.get()) {
            Session session = resourceResolver.adaptTo(Session.class);
            if (session == null) {
                LOG.error("Could not adapt ResourceResolver to JCR Session");
                return articles;
            }
            Query query = createQuery(session, currentPagePath, currentPageTag, limit);
            SearchResult searchResult = query.getResult();
            for (Hit hit : searchResult.getHits()) {
                Resource pageResource = hit.getResource();
                if (pageResource == null) {
                    continue;
                }
                Resource contentResource = pageResource.getChild("jcr:content");
                if (contentResource == null) {
                    continue;
                }
                String title = contentResource.getValueMap().get("jcr:title", String.class);
                String description = contentResource.getValueMap().get("jcr:description", String.class);
                articles.add(new RelatedArticle(hit.getPath(), title, description));
            }
        } catch (Exception e) {
            LOG.error("Error while finding related articles for {}", currentPagePath, e);
        }
        return articles;
    }

    private Query createQuery(Session session, String currentPagePath, String currentPageTag, int limit) {
        Map<String, String> predicates = new HashMap<>();
        predicates.put("path", ARTICLE_ROOT);
        predicates.put("type", "cq:Page");
        predicates.put("1_property", "jcr:content/cq:template");
        predicates.put("1_property.value", ARTICLE_PAGE_TEMPLATE);
        predicates.put("2_property", "jcr:path");
        predicates.put("2_property.operation", "unequals");
        predicates.put("2_property.value", currentPagePath);
        predicates.put("3_property", "jcr:content/cq:tags");
        predicates.put("3_property.1_value", currentPageTag);
        predicates.put("orderby", "@jcr:content/jcr:lastModified");
        predicates.put("orderby.sort", "desc");
        predicates.put("p.offset", "0");
        predicates.put("p.limit", String.valueOf(limit));
        predicates.put("p.guessTotal", "true");
        PredicateGroup predicateGroup = PredicateGroup.create(predicates);
        return queryBuilder.createQuery(predicateGroup, session);
    }

}
