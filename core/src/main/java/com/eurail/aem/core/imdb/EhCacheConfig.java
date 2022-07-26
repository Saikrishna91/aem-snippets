package com.eurail.aem.core.imdb;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "EhCache Configuration", description = "EhCache Configuration")
public @interface EhCacheConfig {
	
	@AttributeDefinition(name = "MaxElementCount", description = "Max Element Count", type = AttributeType.STRING)
	public String maxElementsCount() default "18000";
	
	@AttributeDefinition(name = "TimeToIdle", description = "Time To Idle in Seconds", type = AttributeType.STRING)
	public String timeToIdle() default "1800";
	
	@AttributeDefinition(name = "TimeToLive", description = "Time To Live in Seconds", type = AttributeType.STRING)
	public String timeToLive() default "1800";

}
