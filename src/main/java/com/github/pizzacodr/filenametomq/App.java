package com.github.pizzacodr.filenametomq;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.concurrent.TimeoutException;

import org.aeonbits.owner.ConfigFactory;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class App 
{
    public static void main( String[] args ) throws IOException, InterruptedException, TimeoutException
    {
    	ConfigFile cfg = ConfigFactory.create(ConfigFile.class, System.getProperties());
    	
    	WatchService watchService = FileSystems.getDefault().newWatchService();
    	Path path = Paths.get(cfg.watchDir());
    	WatchKey watchKey = path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);
    	
    	int i = 0;
    	while ((watchKey = watchService.take()) != null) {
            for (WatchEvent<?> event : watchKey.pollEvents()) {
            	i++;
                System.out.println(
                  "Number: " + i + " Event kind:" + event.kind() 
                    + ". File affected: " + event.context() + ".");
                
                ConnectionFactory factory = new ConnectionFactory();
                factory.setHost(cfg.hostname());
                try (Connection connection = factory.newConnection();
                     Channel channel = connection.createChannel()) {

                	channel.queueDeclare(cfg.queueName(), false, false, false, null);
                	String message = event.context().toString();
                	channel.basicPublish("", cfg.queueName(), null, message.getBytes());
                	System.out.println(" [x] Sent '" + message + "'");

                }
            }
            watchKey.reset();
        }
    }
}