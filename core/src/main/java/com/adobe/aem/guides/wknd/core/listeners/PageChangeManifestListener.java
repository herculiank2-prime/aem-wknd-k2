package com.adobe.aem.guides.wknd.core.listeners;

import com.adobe.aem.guides.wknd.core.util.ServiceResolverFactory;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.api.resource.observation.ResourceChange;
import org.apache.sling.api.resource.observation.ResourceChangeListener;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.HexFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component(service = ResourceChangeListener.class, property = {
        ResourceChangeListener.PATHS + "=/content/wknd/us/en",
        ResourceChangeListener.CHANGES + "=ADDED",
        ResourceChangeListener.CHANGES + "=CHANGED",
        ResourceChangeListener.CHANGES + "=REMOVED",
        ResourceChangeListener.PROPERTY_NAMES_HINT + "=jcr:title",
        ResourceChangeListener.PROPERTY_NAMES_HINT + "=jcr:description",
        ResourceChangeListener.PROPERTY_NAMES_HINT + "=cq:lastModified"
})
public final class PageChangeManifestListener implements ResourceChangeListener {

    private static final Logger LOG = LoggerFactory.getLogger(PageChangeManifestListener.class);

    @Reference
    private ServiceResolverFactory resolvers;

    @Override
    public void onChange(List<ResourceChange> changes) {
        Map<String, Map<String, String>> pages = new LinkedHashMap<>();
        for (ResourceChange change : changes) {
            Map<String, String> values = new LinkedHashMap<>();
            values.put(change.getUserId(), change.getType().toString());
            pages.put(change.getPath(), values);
        }
        //changes.stream().filter(c -> relevant(c.getPath())).forEach(c -> pages.put(pagePath(c.getPath()), c.getType()));
        pages.remove(null);
        if (pages.isEmpty())
            return;
        try (ResourceResolver rr = resolvers.get()) {
            Resource root = ResourceUtil.getOrCreateResource(rr, "/var/wknd/change-manifest",
                    "sling:Folder", "sling:Folder", false);
            for (var e : pages.entrySet()) {
                String name = sha256(e.getKey());
                Resource marker = rr.getResource(root.getPath() + "/" + name);
                if (marker == null)
                    marker = rr.create(root, name, Map.of("jcr:primaryType", "nt:unstructured"));
                ModifiableValueMap m = marker.adaptTo(ModifiableValueMap.class);
                m.put("wknd-path", e.getKey());
                for (var j : e.getValue().entrySet()) {
                    m.put("wknd-changeType", j.getValue());
                    m.put("wknd-changedBy", j.getKey());
                }
                m.put("wknd-changedAt", Instant.now().toString());
            }
            rr.commit();
        } catch (Exception e) {
            LOG.error("Unable to update page change manifest", e);
        }
    }

    private boolean relevant(String p) {
        return p != null && !p.contains("/rep:policy") && !p.contains("/cq:responsive");
    }

    private String pagePath(String p) {
        int i = p.indexOf("/jcr:content");
        return i > 0 ? p.substring(0, i) : null;
    }

    private String sha256(String value) throws Exception {
        return HexFormat.of().formatHex(
                MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8)));
    }
}
