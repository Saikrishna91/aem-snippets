package com.eurail.aem.core.imdb;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "DeleteIMDB Scheduler", description = "DeleteIMDB Scheduler Configuration")
public @interface DeleteIMDBRecordConfiguration {

	@AttributeDefinition(name = "Scheduler name", description = "Scheduler name", type = AttributeType.STRING)
	public String schedulerName() default "DeleteIMDB Scheduler Name";

	@AttributeDefinition(name = "Concurrent", description = "Schedule task concurrently", type = AttributeType.BOOLEAN)
	boolean schedulerConcurrent() default true;

	@AttributeDefinition(name = "Enabled", description = "Enable Scheduler", type = AttributeType.BOOLEAN)
	boolean serviceEnabled() default true;

	@AttributeDefinition(name = "Expression", description = "Cron-job expression. Default: run every hour.", type = AttributeType.STRING)
	String schedulerExpression() default "0 0 0/1 1/1 * ? *";
	
}
