package com.github.pizzacodr.filenametomq;

import org.aeonbits.owner.Config;
import org.aeonbits.owner.Config.Sources;
import static org.aeonbits.owner.Config.DisableableFeature.VARIABLE_EXPANSION;

@Sources({ "file:${user.dir}/configFilenameToMQ.properties", 
    "file:${user.home}/configFilenameToMQ.properties"})

public interface ConfigFile extends Config {
	
	@DefaultValue("${user.home}/watchDir")
	String watchDir();
	
	@DefaultValue("localhost")
	String hostname();
	
	@DefaultValue("filename")
	String queueName();
	
	@DisableFeature(VARIABLE_EXPANSION)
	@DefaultValue("[%1$tF %1$tT] [%4$-7s] %5$s %n")
	String loggingFormat();
}