package io.boodskap.iot.simulator.impl;

import java.util.Map;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.codehaus.jettison.json.JSONObject;

import io.boodskap.iot.MessageHandler;
import io.boodskap.iot.MqttSender;
import io.boodskap.iot.ThreadContext;
import io.boodskap.iot.simulator.SimulatorConfig;
import io.boodskap.iot.simulator.TransmitterHandler;
import io.boodskap.iot.simulator.TransmitterHandler.Type;

public class MqttTransmitter extends AbstractTransmitter implements  MessageHandler{
	
	private MqttSender sender;

	@Override
	public void init(SimulatorConfig cfg, TransmitterHandler handler)  throws Exception {
		
		try {
			
			handler.message(Type.TRANSMITTER, String.format("%s Initializing...", getClass().getSimpleName()));
			
			super.init(cfg, handler);
			final String url = String.format("tcp://%s:%d", dCfg.getMqttHost(), dCfg.getMqttPort());
			sender = new MqttSender(url, 180000, dCfg.getDomainKey(), dCfg.getApiKey(), cfg.getDeviceId(), cfg.getDeviceModel(), cfg.getFirmwareVersion(), this);
			sender.open();
			handler.message(Type.TRANSMITTER, "Initialized");
			
		} catch (Exception e) {
			handler.message(Type.TRANSMITTER, ExceptionUtils.getStackTrace(e));
		}

	}

	@Override
	public void dispose() {
		try {
			if(null != sender) {
				sender.close();
			}
		} catch (Exception e) {
			handler.message(Type.CLIENT, ExceptionUtils.getStackTrace(e));
		} finally {
			super.dispose();
		}
	}

	@Override
	public void send(int messageId, String json) {
		try {
			handler.message(Type.CLIENT, json);
			Map<String, Object> map = ThreadContext.jsonToMap(json);
			sender.sendMessage(messageId, map, 0, false);
		} catch (Exception e) {
			handler.message(Type.CLIENT, ExceptionUtils.getStackTrace(e));
		}
	}

	@Override
	public boolean handleMessage(String domainKey, String apiKey, String deviceId, String deviceModel, String firmwareVersion, long corrId, int messageId, JSONObject data) {
		try {
			
			JSONObject message = new JSONObject();
			JSONObject header = new JSONObject();
			
			header.put("mid", messageId);
			header.put("corrid", corrId);
			
			message.put("header", header);
			message.put("data", data);
			
			handler.message(Type.SERVER, message.toString());
			
			return true;
		} catch (Exception e) {
			handler.message(Type.SERVER, ExceptionUtils.getStackTrace(e));
		}
		
		return false;
	}

}
