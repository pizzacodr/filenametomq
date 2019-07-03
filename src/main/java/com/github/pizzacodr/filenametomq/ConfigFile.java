package com.github.pizzacodr.filenametomq;

import org.aeonbits.owner.Config;
import org.aeonbits.owner.Config.Sources;

@Sources({ "file:${user.dir}/configFilenameToMQ.properties", 
    "file:${user.home}/configFilenameToMQ.properties"})

public interface ConfigFile extends Config {
	
	@DefaultValue("${user.home}/watchDir")
	String watchDir();
	
	@DefaultValue("localhost")
	String hostname();
	
	@DefaultValue("filename")
	String queueName();
}