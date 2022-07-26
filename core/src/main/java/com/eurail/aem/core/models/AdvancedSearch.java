package com.eurail.aem.core.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class AdvancedSearch {
 
    @Inject
    private String title;
 
    @Inject
    @Named("list/.")
    private List<AdvancedSearchCheckbox> columnCheckboxList;
 
    public String getTitle() {
        return title;
    }

	public List<AdvancedSearchCheckbox> getColumnCheckboxList() {
		return columnCheckboxList;
	}
    
}
