package com.adobe.aem.guides.wknd.core.services;

import com.adobe.aem.guides.wknd.core.pojo.RelatedArticle;

import java.util.List;

public interface RelatedArticleService {

    List<RelatedArticle> findRelatedArticles(String currentPagePath, String currentPageTag, int limit);
}
