package io.boodskap.iot.simulator.impl;

import org.apache.commons.lang3.exception.ExceptionUtils;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.request.body.RequestBodyEntity;

import io.boodskap.iot.simulator.SimulatorConfig;
import io.boodskap.iot.simulator.TransmitterHandler;
import io.boodskap.iot.simulator.TransmitterHandler.Type;

public class HttpTransmitter extends AbstractTransmitter{
	
	public HttpTransmitter() {
	}

	@Override
	public void send(int messageId, String json) {
		
		try {
			
			handler.message(Type.CLIENT, json);
			
			RequestBodyEntity res = Unirest.post( dCfg.getApiUrl() + "/push/raw/{dkey}/{akey}/{did}/{dmdl}/{fwver}/{mid}")
					  .header("accept", "application/json")
					  .routeParam("dkey", dCfg.getDomainKey())
					  .routeParam("akey", dCfg.getApiKey())
					  .routeParam("did", sCfg.getDeviceId())
					  .routeParam("dmdl", sCfg.getDeviceModel())
					  .routeParam("fwver", sCfg.getFirmwareVersion())
					  .routeParam("mid", String.valueOf(messageId))
					  .queryString("type", "JSON")
					  .body(json)
					;
			
			handler.message(Type.CLIENT, res.asString().getStatusText());
					
		} catch (Exception e) {
			handler.message(Type.CLIENT, ExceptionUtils.getStackTrace(e));
		}
		
	}

	@Override
	public void init(SimulatorConfig cfg, TransmitterHandler handler)  throws Exception {

		handler.message(Type.TRANSMITTER, String.format("%s Initializing...", getClass().getSimpleName()));
		
		super.init(cfg, handler);
		
		handler.message(Type.TRANSMITTER, "Initialized");
		
	}

	@Override
	public void dispose() {
		super.dispose();
	}

}
