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
	private static final ConfigFile CFG = ConfigFactory.create(ConfigFile.class, System.getProperties());
	private static final Logger LOGGER = LoggerFactory.getLogger(App.class);
	
	public static void main(String[] args) throws IOException, InterruptedException, TimeoutException {

		WatchService watchService = startWatchingDir();

		WatchKey watchKey = path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);

		setupConnectionFactory();

		try (Connection connection = factory.newConnection(); Channel channel = connection.createChannel()) {

			channel.queueDeclare(CFG.queueName(), false, false, false, null);
			LOGGER.info("Queue Name: " + CFG.queueName());

			while ((watchKey = watchService.take()) != null) {
				for (WatchEvent<?> event : watchKey.pollEvents()) {
					LOGGER.info("Event kind:" + event.kind() + ". File affected: " + event.context());

					String message = event.context().toString();
					channel.basicPublish("", CFG.queueName(), null, message.getBytes());
					LOGGER.info("Sent '" + message + "'");
				}
				watchKey.reset();
			}
		}
	}

	private static void setupConnectionFactory() {
		factory = new ConnectionFactory();
		factory.setHost(CFG.hostname());
		LOGGER.info("Hostname: " + CFG.hostname());
	}

	private static WatchService startWatchingDir() throws IOException {
		WatchService watchService = FileSystems.getDefault().newWatchService();
		path = Paths.get(CFG.watchDir());

		LOGGER.info("Directory being watched: " + CFG.watchDir());
		return watchService;
	}
}