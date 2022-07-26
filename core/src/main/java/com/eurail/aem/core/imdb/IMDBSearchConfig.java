package com.eurail.aem.core.imdb;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "EURail - IMDBSearch Configuration", description = "EURail - IMDBSearch Configuration")
public @interface IMDBSearchConfig {

	@AttributeDefinition(name = "searchAPIUrl", description = "IMDB Search API URL", type = AttributeType.STRING)
	String searchAPIUrl() default "https://imdb-api.com/en/API/SearchMovie/";
	
	@AttributeDefinition(name = "advancedSearchAPIUrl", description = "IMDB Advanced Search API URL", type = AttributeType.STRING)
	String advancedSearchAPIUrl() default "https://imdb-api.com/API/AdvancedSearch/";
	
	@AttributeDefinition(name = "youtubeAPIUrl", description = "Youtube API URL", type = AttributeType.STRING)
	String youtubeAPIUrl() default "https://imdb-api.com/en/API/YouTubeTrailer/";

	@AttributeDefinition(name = "searchAPIKey", description = "IMDB Search API Key", type = AttributeType.STRING)
	String searchAPIKey() default "k_nzhgln68";
	
	@AttributeDefinition(name = "FileBased Cache", description = "Enable for Caching Search in File", type = AttributeType.BOOLEAN)
	boolean fileBasedCache() default true;

}