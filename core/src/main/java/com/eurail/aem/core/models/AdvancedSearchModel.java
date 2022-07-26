package com.eurail.aem.core.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class AdvancedSearchModel {
 
    @Inject
    @Named("accordions/.")
    public List<AdvancedSearch> rowList;
 
    public boolean isConfigured() {
        return rowList != null && !rowList.isEmpty();
    }

	public List<AdvancedSearch> getRowList() {
		return rowList;
	}
    
}
