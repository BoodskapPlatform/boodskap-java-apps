package io.boodskap.iot.simulator.impl;

import java.util.Map;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.JSONObject;

import io.boodskap.iot.simulator.DomainConfig;
import io.boodskap.iot.simulator.DomainConfigs;
import io.boodskap.iot.simulator.SimulatorConfig;
import io.boodskap.iot.simulator.Transmitter;
import io.boodskap.iot.simulator.TransmitterHandler;
import io.boodskap.iot.simulator.TransmitterHandler.Type;

public abstract class AbstractTransmitter implements Transmitter {
	
	protected TransmitterHandler handler;
	protected SimulatorConfig sCfg;
	protected DomainConfig dCfg;

	public AbstractTransmitter() {
	}

	@Override
	public void init(SimulatorConfig cfg, TransmitterHandler handler) throws Exception{
		
		try {

			this.handler = handler;
			this.sCfg = cfg;
			this.dCfg = DomainConfigs.get().getConfiguration(cfg.getDomainConfig());
			
		} catch (Exception e) {
			handler.message(Type.TRANSMITTER, ExceptionUtils.getStackTrace(e));
			handler.message(Type.TRANSMITTER, "Failied initializing.");
		}
	}

	@Override
	public void dispose() {
		handler.message(Type.TRANSMITTER, String.format("%s Disposed.", getClass().getSimpleName()));
	}
	
	@Override
	public final void send(int messageId, Map<String, Object> json) {
		JSONObject jobj = new JSONObject(json);
		send(messageId, jobj.toString());
	}

}
