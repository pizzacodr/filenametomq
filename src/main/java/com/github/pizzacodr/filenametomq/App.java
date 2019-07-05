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
	public static void main(String[] args) throws IOException, InterruptedException, TimeoutException {
		
		ConfigFile cfg = ConfigFactory.create(ConfigFile.class, System.getProperties());
		System.out.println(cfg.loggingFormat());
		System.setProperty("java.util.logging.SimpleFormatter.format", "[%1$tF %1$tT] [%4$-7s] %5$s %n");
		Logger logger = LoggerFactory.getLogger(App.class);

		WatchService watchService = FileSystems.getDefault().newWatchService();
		Path path = Paths.get(cfg.watchDir());

		logger.info("Directory being watched: " + cfg.watchDir());

		WatchKey watchKey = path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);

		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost(cfg.hostname());

		logger.info("Hostname: " + cfg.hostname());

		try (Connection connection = factory.newConnection(); Channel channel = connection.createChannel()) {

			channel.queueDeclare(cfg.queueName(), false, false, false, null);

			logger.info("Queue Name: " + cfg.queueName());

			int i = 0;
			while ((watchKey = watchService.take()) != null) {
				for (WatchEvent<?> event : watchKey.pollEvents()) {
					i++;
					logger.info("Number: " + i + " Event kind:" + event.kind() + ". File affected: " + event.context()
							+ ".");

					String message = event.context().toString();
					channel.basicPublish("", cfg.queueName(), null, message.getBytes());
					logger.info(" [x] Sent '" + message + "'");
				}
				watchKey.reset();
			}
		}
	}
}