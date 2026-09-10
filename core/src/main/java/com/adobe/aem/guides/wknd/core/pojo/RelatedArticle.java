package com.adobe.aem.guides.wknd.core.pojo;

public class RelatedArticle {

    private final String path;
    private final String title;
    private final String description;

    public RelatedArticle(String path, String title, String description) {
        this.path = path;
        this.title = title;
        this.description = description;
    }

    public String getPath() {
        return path;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}
