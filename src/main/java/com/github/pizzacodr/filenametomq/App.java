package com.github.pizzacodr.filenametomq;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

public class App 
{
    public static void main( String[] args ) throws IOException, InterruptedException
    {
        //constantly watch a directory
    	WatchService watchService = FileSystems.getDefault().newWatchService();
    	Path path = Paths.get("/home/yuri/watchDir");
    	WatchKey watchKey = path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);
    	
    	int i = 0;
    	while ((watchKey = watchService.take()) != null) {
            for (WatchEvent<?> event : watchKey.pollEvents()) {
            	i++;
                System.out.println(
                  "Number: " + i + " vent kind:" + event.kind() 
                    + ". File affected: " + event.context() + ".");
            }
            watchKey.reset();
        }
    	//read in any files names that are placed in there
    	
    	//insert each filename into a message queue
    	
    }
}
