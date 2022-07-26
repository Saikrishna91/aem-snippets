package com.eurail.aem.core.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
 
import javax.inject.Inject;
 
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class AdvancedSearchCheckbox {
 
    @Inject
    private String name;
    
    @Inject
    private String id;
    
    @Inject
    private String label;

	public String getName() {
		return name;
	}

	public String getId() {
		return id;
	}

	public String getLabel() {
		return label;
	}
    
}