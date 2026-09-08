package com.adobe.aem.guides.wknd.core.handlers;

import com.adobe.aem.guides.wknd.core.util.ServiceResolverFactory;
import com.day.cq.replication.ReplicationAction;

import java.time.Instant;
import java.util.*;

import org.apache.sling.api.resource.*;
import org.osgi.service.component.annotations.*;
import org.osgi.service.event.*;
import org.slf4j.*;

@Component(service = EventHandler.class, property = {
        EventConstants.EVENT_TOPIC + "=" + ReplicationAction.EVENT_TOPIC,
}, immediate = true)
public final class ReplicationActivitySummaryHandler implements EventHandler {
    private static final Logger LOG = LoggerFactory.getLogger(ReplicationActivitySummaryHandler.class);
    @Reference
    private ServiceResolverFactory resolvers;

    @Override
    public void handleEvent(Event event) {
        ReplicationAction a = ReplicationAction.fromEvent(event);
        if (a == null) return;
        List<String> paths = Arrays.stream(a.getPaths()).filter(Objects::nonNull)
                .filter(p -> p.startsWith("/content/wknd/")).limit(200).toList();
        if (paths.isEmpty()) return;
        try (ResourceResolver rr = resolvers.get()) {
            Resource root = ResourceUtil.getOrCreateResource(rr, "/var/wknd/replication-summary",
                    "sling:Folder", "sling:Folder", false);
            for (String path : paths) {
                String site = site(path);
                Resource r = rr.getResource(root.getPath() + "/" + site);
                if (r == null) r = rr.create(root, site, Map.of("jcr:primaryType", "nt:unstructured"));
                ModifiableValueMap m = r.adaptTo(ModifiableValueMap.class);
                m.put("wknd:lastPath", path);
                m.put("wknd:lastAction", a.getType().name());
                m.put("wknd:lastUser", String.valueOf(a.getUserId()));
                m.put("wknd:lastEventAt", Instant.now().toString());
                m.put("wknd:eventCount", m.get("wknd:eventCount", 0L) + 1L);
            }
            rr.commit();
        } catch (Exception e) {
            LOG.error("Unable to update replication summary", e);
        }
    }

    private String site(String p) {
        String[] s = p.split("/");
        return s.length > 3 ? s[3].replaceAll("[^A-Za-z0-9_-]", "_") : "unknown";
    }
}
