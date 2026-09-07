package com.adobe.aem.guides.wknd.core.jobs;

import org.apache.sling.event.jobs.Job;
import org.apache.sling.event.jobs.JobManager;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

@Component(service = AssetJobProducer.class)
public class AssetJobProducer {

    private static final Logger LOG = LoggerFactory.getLogger(AssetJobProducer.class);

    // The 'topic' is the queue's name. Producer and consumer must agree on it.
    public static final String TOPIC = "mysite/form/process";

    @Reference
    private JobManager jobManager;

    /** Called by other code to request background processing of one asset. */
    public void submit(String assetPath) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("assetPath", assetPath);
        Job job = jobManager.addJob(TOPIC, payload);
        LOG.info("Submitted asset job {} for {}", job.getId(), assetPath);
    }

}
