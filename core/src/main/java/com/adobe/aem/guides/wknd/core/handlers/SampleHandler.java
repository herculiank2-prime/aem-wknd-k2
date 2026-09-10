package com.adobe.aem.guides.wknd.core.handlers;

import com.adobe.aem.guides.wknd.core.util.ServiceResolverFactory;
import com.day.cq.dam.api.DamEvent;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.*;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component(service = EventHandler.class, property = {EventConstants.EVENT_TOPIC + "=" + DamEvent.EVENT_TOPIC}, immediate = true)
public final class SampleHandler implements EventHandler {
    private static final Logger LOG = LoggerFactory.getLogger(SampleHandler.class);

    private static final String CONTENT_ROOT = "/content/wknd/us/en";

    private static final String SUMMARY_ROOT = "/var/wknd/replication-summary";

    private static final String EVENTS_NODE = "events";

    @Reference
    private ServiceResolverFactory resolvers;

    @Override
    public void handleEvent(Event event) {
        LOG.debug("SampleHandler received DAM event");
        try {
            DamEvent damEvent = DamEvent.fromEvent(event);
            if (damEvent == null) {
                LOG.debug("Unable to create DamEvent from event");
                return;
            }
            String path = damEvent.getAssetPath();
            /*
             * Only process events under our WKND content tree.
             */
            if (!StringUtils.startsWith(path, CONTENT_ROOT)) {
                LOG.debug("Ignoring event outside configured path: {}", path);
                return;
            }
            try (ResourceResolver rr = resolvers.get()) {
                Resource summaryRoot = ResourceUtil.getOrCreateResource(rr, SUMMARY_ROOT, 
                        "sling:Folder", "sling:Folder", false);
                String site = site(path);
                Resource siteResource = rr.getResource(SUMMARY_ROOT + "/" + site);
                if (siteResource == null) {
                    siteResource = rr.create(summaryRoot, site,
                            Map.of("jcr:primaryType", "sling:Folder"));
                }
                updateSummary(siteResource, damEvent, path);
                createEventHistory(rr, siteResource, damEvent, path);
                rr.commit();
                LOG.info("Recorded event for path={}", path);
            }
        } catch (Exception e) {
            LOG.error("Unable to update event summary", e);
        }
    }

    /**
     * Updates the latest-event information on the site node.
     */
    private void updateSummary(Resource siteResource, DamEvent damEvent, String path) {
        ModifiableValueMap properties = siteResource.adaptTo(ModifiableValueMap.class);
        if (properties == null) {
            LOG.warn("Unable to adapt {} to ModifiableValueMap", siteResource.getPath());
            return;
        }
        Long currentCount = properties.get("wknd-eventCount", Long.class);
        long newCount = currentCount == null ? 1L : currentCount + 1L;
        properties.put("wknd-lastPath", path);
        properties.put("wknd-lastAction", damEvent.getType().name());
        properties.put("wknd-lastUser", StringUtils.defaultString(damEvent.getUserId()));
        properties.put("wknd-lastEventAt", Instant.now().toString());
        properties.put("wknd-eventCount", newCount);
    }

    /**
     * Creates a unique node for every event.
     * <p>
     * This prevents a later event from overwriting
     * an earlier event.
     */
    private void createEventHistory(
            ResourceResolver rr,
            Resource siteResource,
            DamEvent damEvent,
            String path) throws PersistenceException {

        /*
         * Create:
         *
         * /var/wknd/replication-summary/us/events
         */
        Resource eventsResource = siteResource.getChild(EVENTS_NODE);

        if (eventsResource == null) {
            eventsResource = rr.create(siteResource, EVENTS_NODE, Map.of("jcr:primaryType", "sling:Folder"));
        }

        /*
         * Generate a unique event node name.
         *
         * UUID is important because multiple events can happen
         * within the same millisecond.
         */
        String eventNodeName = System.currentTimeMillis() + "-" + UUID.randomUUID();
        Map<String, Object> eventProperties = new HashMap<>();
        eventProperties.put("jcr:primaryType", "nt:unstructured");
        eventProperties.put("wknd-path", path);
        eventProperties.put("wknd-action", damEvent.getType().name());
        eventProperties.put("wknd-user", StringUtils.defaultString(damEvent.getUserId()));
        eventProperties.put("wknd-eventAt", Instant.now().toString());
        rr.create(eventsResource, eventNodeName, eventProperties);
    }

    /**
     * Extract site from:
     * <p>
     * /content/wknd/us/en/...
     * <p>
     * Result:
     * <p>
     * us
     */
    private String site(String path) {
        String relative = StringUtils.substringAfter(path, "/content/wknd/");
        if (StringUtils.isBlank(relative)) {
            return "unknown";
        }
        String site = StringUtils.substringBefore(relative, "/");
        if (StringUtils.isBlank(site)) {
            return "unknown";
        }
        return site.replaceAll("[^A-Za-z0-9_-]", "_");
    }
}
