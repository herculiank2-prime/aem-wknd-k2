package com.adobe.aem.guides.wknd.core.schedulers;

import com.adobe.aem.guides.wknd.core.jobs.AssetJobProducer;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(service = Runnable.class, immediate = true, property = {"scheduler.name=first-scheduler", "scheduler.expression=5 0/2 * * * ?"})
public class FirstScheduler implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(FirstScheduler.class);

    @Reference
    AssetJobProducer assetJobProducer;

    @Override
    public void run() {
        LOG.debug("FirstScheduler ran");
        assetJobProducer.submit("/content/dam/wknd/en/site/wknd-logo-dk.png");
    }
}
