//package com.adobe.aem.guides.wknd.core.handlers;
//
//import com.adobe.aem.guides.wknd.core.util.ServiceResolverFactory;
//import com.day.cq.replication.ReplicationAction;
//
//import java.io.StringReader;
//import java.time.Instant;
//import java.util.*;
//
//import org.apache.sling.api.resource.*;
//import org.osgi.service.component.annotations.*;
//import org.osgi.service.event.*;
//import org.slf4j.*;
//
//import javax.json.*;
//
//@Component(service = EventHandler.class, property = {
//        EventConstants.EVENT_TOPIC + "=aem/platform/http_request",
//}, immediate = true)
//public final class ReplicationActivitySummaryHandler implements EventHandler {
//    private static final Logger LOG = LoggerFactory.getLogger(ReplicationActivitySummaryHandler.class);
//    @Reference
//    private ServiceResolverFactory resolvers;
//
//    @Override
//    public void handleEvent(Event event) {
//        LOG.debug("ReplicationActivitySummaryHandler handleEvent");
//            String data = (String) event.getProperty("data");
//
//            try (JsonReader reader = Json.createReader(new StringReader(data))) {
//                JsonObject object = reader.readObject();
//                JsonObject bodyObject = object.getJsonObject("aem:http_request").getJsonObject("body");
//                List<String> paths = bodyObject.getJsonArray("path")
//                        .getValuesAs(JsonString.class)
//                        .stream()
//                        .map(JsonString::getString)
//                        .toList();
//
////            Map<String, Object> data = (Map<String, Object>) event.getProperty("data");
////            Map<String, Object> httpRequest =
////                    (Map<String, Object>) data.get("aem:http_request");
////            Map<String, Object> body =
////                    (Map<String, Object>) httpRequest.get("body");
////            List<String> paths = (List<String>) body.get("path");
//
//                LOG.debug("ReplicationActivitySummaryHandler ReplicationAction {}", paths);
//                if (paths.isEmpty())
//                    return;
//                try (ResourceResolver rr = resolvers.get()) {
//                    Resource root = ResourceUtil.getOrCreateResource(rr, "/var/wknd/replication-summary",
//                            "sling:Folder", "sling:Folder", false);
//                    for (String path : paths) {
//                        String site = site(path);
//                        Resource r = rr.getResource(root.getPath() + "/" + site);
//                        if (r == null) r = rr.create(root, site, Map.of("jcr:primaryType", "nt:unstructured"));
//                        ModifiableValueMap m = r.adaptTo(ModifiableValueMap.class);
//                        m.put("wknd%3AlastPath", path);
//                        //m.put("wknd:lastAction", a.getType().name());
//                        //m.put("wknd:lastUser", String.valueOf(a.getUserId()));
//                        m.put("wknd%3AlastEventAt", Instant.now().toString());
//                        m.put("wknd%3AeventCount\n", m.get("wknd:eventCount", 0L) + 1L);
//                    }
//                    rr.commit();
//                } catch (LoginException | PersistenceException e) {
//                    LOG.error("Unable to update replication summary", e);
//                }
//
//        } catch (RuntimeException e) {
//            LOG.error("Runtime exception while casting event to JSONObject", e);
//        }
//    }
//
//    private String site(String p) {
//        String[] s = p.split("/");
//        return s.length > 3 ? s[3].replaceAll("[^A-Za-z0-9_-]", "_") : "unknown";
//    }
//}
