package io.boodskap.iot.simulator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

public class SimulatorConfigs {
	
	private static final SimulatorConfigs instance = new SimulatorConfigs();

	private List<SimulatorConfig> simulators = new ArrayList<>();
	
	private SimulatorConfigs() {
	}

	public static boolean exists() {
		File file = new File(App.CONFIG_PATH, "simulators.json");
		return file.exists();
	}

	public static SimulatorConfigs get() throws FileNotFoundException {
		
		if(exists() && instance.simulators.isEmpty()) {
			File file = new File(App.CONFIG_PATH, "simulators.json");
			JsonReader reader = new JsonReader(new FileReader(file));
			SimulatorConfigs cfg = new Gson().fromJson(reader, SimulatorConfigs.class);
			
			instance.simulators.clear();
			instance.simulators.addAll(cfg.simulators);
		}
		
		return instance;
	}
	
	public void save() throws IOException {
		File file = new File(App.CONFIG_PATH, "simulators.json");
		String json = new GsonBuilder().setPrettyPrinting().create().toJson(this);
		FileWriter writer = new FileWriter(file);
		writer.write(json);
		writer.flush();
		writer.close();
	}
	
	public boolean exists(String id) {
		
		for(SimulatorConfig sc : simulators) {
			if(sc.getId().equals(id)) {
				return true;
			}
		}
		
		return false;
	}
	
	public void save(SimulatorConfig cfg) throws IOException {
		
		if(!exists(cfg.getId())) {
			simulators.add(cfg);
		}
		
		save();
		
	}
	
	public void delete(SimulatorConfig cfg) throws IOException {
		simulators.remove(cfg);
		save();
	}

	public List<SimulatorConfig> getSimulators() {
		return simulators;
	}

}
