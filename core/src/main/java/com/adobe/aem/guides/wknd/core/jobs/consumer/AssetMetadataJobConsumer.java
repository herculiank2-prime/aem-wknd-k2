package com.adobe.aem.guides.wknd.core.jobs.consumer;

import com.adobe.aem.guides.wknd.core.jobs.AssetJobProducer;
import com.day.cq.dam.api.Asset;
import org.apache.sling.api.resource.*;
import org.apache.sling.event.jobs.Job;
import org.apache.sling.event.jobs.consumer.JobConsumer;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Map;

@Component(service = JobConsumer.class, property = {JobConsumer.PROPERTY_TOPICS + "=" + AssetJobProducer.TOPIC})
public class AssetMetadataJobConsumer implements JobConsumer {

    private static final Logger LOG =
            LoggerFactory.getLogger(AssetMetadataJobConsumer.class);

    // This label must match the service-user mapping (shown in Step 2).
    private static final String SUBSERVICE = "asset-writer";

    @Reference
    private ResourceResolverFactory resolverFactory;

    @Override
    public JobResult process(Job job) {
        String assetPath = job.getProperty("assetPath", String.class);
        Map<String, Object> authInfo = Collections.singletonMap(
                ResourceResolverFactory.SUBSERVICE, (Object) SUBSERVICE);
        // try-with-resources guarantees the resolver is always closed.
        try (ResourceResolver resolver = resolverFactory.getServiceResourceResolver(authInfo)) {
            Asset asset = resolver.getResource(assetPath) != null
                    ? resolver.getResource(assetPath).adaptTo(Asset.class)
                    : null;
            if (asset == null) {
                LOG.error("Asset not found: {} — giving up.", assetPath);
                return JobResult.CANCEL;     // bad input, never going to work
            }
            // Example: write a metadata value, then save.
            Resource metadata = resolver.getResource(asset.getPath() + "/jcr:content/metadata");
            ModifiableValueMap properties = metadata.adaptTo(ModifiableValueMap.class);
            properties.put("mysite-processingStatus", "PROCESSED");
            resolver.commit();              // persist the change
            LOG.info("Processed asset {}", assetPath);
            return JobResult.OK;
        } catch (LoginException e) {
            LOG.error("Service user not configured correctly.", e);
            return JobResult.FAILED;        // config issue — retry after fix
        } catch (Exception e) {
            LOG.warn("Temporary failure for {}, will retry.", assetPath, e);
            return JobResult.FAILED;        // retry
        }
    }
}


