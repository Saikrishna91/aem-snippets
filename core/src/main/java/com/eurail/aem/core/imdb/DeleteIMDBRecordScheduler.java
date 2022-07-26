package com.eurail.aem.core.imdb;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.commons.scheduler.ScheduleOptions;
import org.apache.sling.commons.scheduler.Scheduler;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(immediate = true, service = DeleteIMDBRecordScheduler.class)
@Designate(ocd = DeleteIMDBRecordConfiguration.class)
public class DeleteIMDBRecordScheduler implements Runnable {
	
	@Reference
	private ResourceResolverFactory rrFactory;
	
	@Reference
	private Scheduler scheduler;
	private int schedulerID;
	private Session session;
	private final Logger LOG = LoggerFactory.getLogger(this.getClass());

	@Activate
	protected void activate(DeleteIMDBRecordConfiguration config) {
		schedulerID = config.schedulerName().hashCode();
	}

	@Modified
	protected void modified(DeleteIMDBRecordConfiguration config) {
		removeScheduler();
		schedulerID = config.schedulerName().hashCode(); // update schedulerID
		addScheduler(config);
	}

	@Deactivate
	protected void deactivate(DeleteIMDBRecordConfiguration config) {
		removeScheduler();
	}

	/**
	 * Remove a scheduler based on the scheduler ID
	 */
	private void removeScheduler() {
		LOG.debug("Removing Scheduler Job '{}'", schedulerID);
		scheduler.unschedule(String.valueOf(schedulerID));
	}

	/**
	 * Add a scheduler based on the scheduler ID
	 */
	private void addScheduler(DeleteIMDBRecordConfiguration config) {
		if (config.serviceEnabled()) {
			ScheduleOptions sopts = scheduler.EXPR(config.schedulerExpression());
			sopts.name(String.valueOf(schedulerID));
			sopts.canRunConcurrently(false);
			scheduler.schedule(this, sopts);
			LOG.debug("Scheduler added succesfully");
		} else {
			LOG.debug("OSGIR6SchedulerExample is Disabled, no scheduler job created");
		}
	}

	@Override
	public void run() {
		LOG.debug("Inside DeleteIMDBRecordScheduler run Method");
		try {
			Map<String, Object> param = new HashMap<String, Object>();
			param.put(ResourceResolverFactory.SUBSERVICE, Constants.SUB_SERVICE);
			ResourceResolver resourceResolver = rrFactory.getServiceResourceResolver(param);
			Resource damResource = resourceResolver.getResource(Constants.DAM_PATH);
			Session session = resourceResolver.adaptTo(Session.class);
			if(!Objects.isNull(damResource) && damResource.hasChildren()) {
				Iterator<Resource> childrens = damResource.listChildren();
				while(childrens.hasNext()) {
					Resource resource = childrens.next();
					session.removeItem(resource.getPath());
					session.save();
					session.refresh(true);
				}
			}
		} catch (LoginException | RepositoryException e) {
			LOG.error("Error Occured during imdbsystemuser session: {}", e.getMessage());
		} finally {
			if(session != null && session.isLive()) {
				session.logout();
			}
		}
	}

}
