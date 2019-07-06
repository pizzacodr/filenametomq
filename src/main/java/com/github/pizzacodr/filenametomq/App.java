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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class App {
	
	private static Path path;
	private static ConnectionFactory factory;
	
	public static void main(String[] args) throws IOException, InterruptedException, TimeoutException {
		
		ConfigFile cfg = ConfigFactory.create(ConfigFile.class, System.getProperties());
		Logger logger = LoggerFactory.getLogger(App.class);

		WatchService watchService = startWatchingDir(cfg.watchDir(), logger);

		WatchKey watchKey = path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);

		setupConnectionFactory(cfg.hostname(), logger);

		try (Connection connection = factory.newConnection(); Channel channel = connection.createChannel()) {

			channel.queueDeclare(cfg.queueName(), false, false, false, null);
			logger.info("Queue Name: " + cfg.queueName());

			while ((watchKey = watchService.take()) != null) {
				for (WatchEvent<?> event : watchKey.pollEvents()) {
					logger.info("Event kind:" + event.kind() + ". File affected: " + event.context());

					String message = event.context().toString();
					channel.basicPublish("", cfg.queueName(), null, message.getBytes());
					logger.info("Sent '" + message + "'");
				}
				watchKey.reset();
			}
		}
	}

	private static void setupConnectionFactory(String hostname, Logger logger) {
		factory = new ConnectionFactory();
		factory.setHost(hostname);
		
		logger.info("Hostname: " + hostname);
	}

	private static WatchService startWatchingDir(String watchDir, Logger logger) throws IOException {
		WatchService watchService = FileSystems.getDefault().newWatchService();
		path = Paths.get(watchDir);

		logger.info("Directory being watched: " + watchDir);
		return watchService;
	}
}