package io.boodskap.iot.simulator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

public class DomainConfigs {
	
	private static final DomainConfigs instance = new DomainConfigs();

	private List<DomainConfig> configurations = new ArrayList<>();
	private String activeConfiguration;
	
	protected DomainConfigs() {
	}
	
	public static boolean exists() {
		File file = new File(SimulatorApplication.CONFIG_PATH, "domains.json");
		return file.exists();
	}

	public static DomainConfigs get() throws FileNotFoundException {
		
		if(instance.configurations.isEmpty() && exists()) {
			
			File file = new File(SimulatorApplication.CONFIG_PATH, "domains.json");
			JsonReader reader = new JsonReader(new FileReader(file));
			DomainConfigs cfg =  new Gson().fromJson(reader, DomainConfigs.class);
			
			instance.configurations.clear();
			
			for(DomainConfig dc : cfg.configurations) {
				instance.configurations.add(dc);
			}
			
			instance.activeConfiguration = cfg.activeConfiguration;
		}
		
		return instance;
	}
	
	public void save(DomainConfig dcfg) throws IOException {
		
		if(null == getConfiguration(dcfg.getId())) {
			configurations.add(dcfg);
		}
		
		if(StringUtils.isBlank(activeConfiguration)) {
			this.activeConfiguration = dcfg.getId();
		}

		save();
	}
	
	public void save() throws IOException {
		File file = new File(SimulatorApplication.CONFIG_PATH, "domains.json");
		String json = new GsonBuilder().setPrettyPrinting().create().toJson(this);
		FileWriter writer = new FileWriter(file);
		writer.write(json);
		writer.flush();
		writer.close();
	}

	public List<DomainConfig> getConfigurations() {
		return configurations;
	}
	
	public DomainConfig getConfiguration(String id) {
		for(DomainConfig dc : configurations) {
			if(dc.getId().equals(id)) return dc;
		}
		return null;
	}

	public String getActiveConfiguration() {
		return activeConfiguration;
	}

	public void setActiveConfiguration(String activeConfiguration) throws IOException {
		this.activeConfiguration = activeConfiguration;
		save();
	}

}
