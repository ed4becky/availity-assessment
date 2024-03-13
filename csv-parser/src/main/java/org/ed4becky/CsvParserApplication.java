package org.ed4becky;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CsvParserApplication implements CommandLineRunner {
	private static final Logger log = LoggerFactory.getLogger(CsvParserApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(CsvParserApplication.class, args);
	}

	@Override
	public void run(String... args) {
		log.info("EXECUTING : command line runner");

		for (int i = 0; i < args.length; ++i) {
			log.info("args[{}]: {}", i, args[i]);
		}
	}

}
