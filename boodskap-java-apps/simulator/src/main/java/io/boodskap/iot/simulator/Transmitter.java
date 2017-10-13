package io.boodskap.iot.simulator;

import java.util.Map;

public interface Transmitter {
	
	public void init(SimulatorConfig cfg, TransmitterHandler handler)throws Exception;
	
	public void dispose();

	public void send(int messageId, String json);
	
	public void send(int messageId, Map<String, Object> json);
}
