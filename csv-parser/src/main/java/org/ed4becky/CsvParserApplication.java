package org.ed4becky;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import com.opencsv.CSVReader;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.HeaderColumnNameTranslateMappingStrategy;

import org.ed4becky.User;

@SpringBootApplication
public class CsvParserApplication implements CommandLineRunner {
	private static final String HEADER = "\"User Id\",\"First Name\",\"Last Name\",\"Version\",\"Insurance Company\"\n";
	private static final Logger log = LoggerFactory.getLogger(CsvParserApplication.class);
	private int retCode = 0;

	@Autowired
	private ApplicationContext appContext;

	public static void main(String[] args) throws IOException {
		SpringApplication.run(CsvParserApplication.class, args);
	}

	@Override
	public void run(String... args) throws IOException {
		log.info("EXECUTING : command line runner");
		String filename = "";

		if (args.length == 2 && args[0].equals("-f")) {
			filename = args[1];
			retCode = processFile(filename);
		} else if (args.length == 1) {
			filename = args[0];
			processFile(filename);
		} else {
			log.error("Filename required");
			retCode = -1;
		}
		SpringApplication.exit(appContext, () -> retCode);

	}

	int processFile(String filename) throws IOException {
		// Hashmap to map CSV data to Bean attributes.
		Map<String, String> mapping = new HashMap<String, String>();
		mapping.put("User Id", "id");
		mapping.put("First Name", "firstName");
		mapping.put("Last Name", "lastName");
		mapping.put("Version", "version");
		mapping.put("Insurance Company", "insuranceCompany");

		HeaderColumnNameTranslateMappingStrategy<User> strategy = new HeaderColumnNameTranslateMappingStrategy<User>();
		strategy.setType(User.class);
		strategy.setColumnMapping(mapping);

		// Create csvtobean and csvreader object
		CSVReader csvReader = null;
		try {
			csvReader = new CSVReader(new FileReader(filename));
		} catch (FileNotFoundException e) {
			log.error(e.getLocalizedMessage());
		}
		CsvToBean<User> csvToBean = new CsvToBean<User>();

		// call the parse method of CsvToBean
		// pass strategy, csvReader to parse method
		csvToBean.setCsvReader(csvReader);
		csvToBean.setMappingStrategy(strategy);

		List<User> list = csvToBean.parse();
		for (User e : list) {
			System.out.println(e);
		}
		if (csvReader != null)
			csvReader.close();

		Map<String, List<User>> companyMap = processList(list);
		Iterator<List<User>> it = companyMap.values().iterator();

		while (it.hasNext()) {
			writeFiles(it.next());
		}
		return 0;
	}

	void writeFiles(List<User> list) throws IOException {
		List<User> newList = dedupeAndSort(list);
		String filename = newList.get(0)
				.getInsuranceCompany()
				.replace(' ', '_')
				.replace(',', '_')
				.replace('\'', '_')
				.toLowerCase();

		log.info(filename);
		FileWriter myWriter = null;;
		try {
			Files.createDirectories(Paths.get("out"));

			myWriter = new FileWriter("out/" + filename + ".csv");
			myWriter.write(HEADER);
			Iterator<User> it = newList.iterator();
			while(it.hasNext()) {
				String line = it.next().getLine();
				myWriter.write(line);
			}
		} catch(Exception e) {
			log.error(e.getLocalizedMessage());
		} finally {
    	  if(myWriter != null) {
		  	myWriter.close();
		  }
		}
	}

	private List<User> dedupeAndSort(List<User> list) {
		Map<Integer, User> users = new HashMap<>();
		
		for (int i = 0; i < list.size(); i++) {
			User listUser = list.get(i);
			User setUser = users.get(listUser.hashCode());
			if(setUser != null ) {
				if(listUser.getVersion() > setUser.getVersion()) {
					users.put(listUser.hashCode(), listUser);
				}
			} else {
				users.put(listUser.hashCode(), listUser);
			}
		}
		List<User> newList = new ArrayList<>(users.values());
		Collections.sort(newList, new UserComparator());

		return newList;
	}

	Map<String, List<User>> processList(List<User> list) {
		Map<String, List<User>> companyMap = new HashMap<String, List<User>>();
		Iterator<User> it = list.iterator();
		while (it.hasNext()) {
			User user = it.next();
			List<User> tmpList = companyMap.get(user.getInsuranceCompany());
			if (tmpList == null) {
				tmpList = new ArrayList<User>();
				companyMap.put(user.getInsuranceCompany(), tmpList);
			}
			tmpList.add(user);
		}
		return companyMap;
	}
}
