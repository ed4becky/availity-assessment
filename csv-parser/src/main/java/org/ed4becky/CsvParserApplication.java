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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
		Collections.sort(list, new UserComparator());
		List<User> removal = new ArrayList<>();
		List<User> newList = new ArrayList<>();

		User prev = list.get(0);
		for (int i = 1; i < list.size(); i++) {
			User curr = list.get(i);
			if (prev.getId().equals(curr.getId())
					&& prev.getInsuranceCompany().equals(curr.getInsuranceCompany())) {
				if (prev.getVersion() < curr.getVersion()) {
					removal.add(prev);
					log.info("remove " + prev + " and save " + curr);
					prev = curr;
				} else {
					removal.add(curr);
					log.info("remove " + curr + " and save " + prev);
				}
			} else
				prev = curr;
		}
		if (removal.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				User curr = list.get(i);
				boolean isDupe = false;
				for (int j = 0; j < removal.size(); j++) {
					User r = removal.get(j);
					if (curr.getId().equals(r.getId())
							&& curr.getInsuranceCompany().equals(r.getInsuranceCompany())
							&& curr.getVersion() == r.getVersion()) {
						isDupe = true;
						break;
					}
				}
				if (!isDupe)
					newList.add(curr);
			}
		} else
			newList = list;

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
