package io.boodskap.iot.simulator.impl;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.JSONObject;

import io.boodskap.iot.simulator.SimulatorConfig;
import io.boodskap.iot.simulator.TransmitterHandler;
import io.boodskap.iot.simulator.TransmitterHandler.Type;

public class UDPTransmitter extends AbstractTransmitter {
	
	protected DatagramSocket socket;
	
	private ExecutorService exec = Executors.newSingleThreadExecutor();

	public UDPTransmitter() {
	}

	@Override
	public void init(SimulatorConfig cfg, TransmitterHandler handler)  throws Exception {

		handler.message(Type.TRANSMITTER, String.format("%s Initializing...", getClass().getSimpleName()));
		
		super.init(cfg, handler);
		
		socket = new DatagramSocket();
		exec.submit(new Runnable() {
			@Override
			public void run() {
				try {
					while(!Thread.currentThread().isInterrupted()) {
						byte[] buf = new byte[2048];
						DatagramPacket pkt = new DatagramPacket(buf, buf.length);
						socket.receive(pkt);
						String value = new String(pkt.getData(), pkt.getOffset(), pkt.getLength());
						handler.message(Type.SERVER, value);
					}
				} catch (Exception e) {
					if(!socket.isClosed()) {
						handler.message(Type.CLIENT, ExceptionUtils.getStackTrace(e));
					}
				}
			}
		});
		
		handler.message(Type.TRANSMITTER, "Initialized");
		
	}

	@Override
	public void dispose() {
		try {
			exec.shutdownNow();
			if(null != socket) {
				socket.close();
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
			
			JSONObject message = new JSONObject();
			JSONObject header = new JSONObject();
			JSONObject data = new JSONObject(json);
			
			header.put("key", dCfg.getDomainKey());
			header.put("api", dCfg.getApiKey());
			header.put("did", sCfg.getDeviceId());
			header.put("dmdl", sCfg.getDeviceModel());
			header.put("fwver", sCfg.getFirmwareVersion());
			header.put("mid", messageId);
			
			message.put("header", header);
			message.put("data", data);
			
			String value = message.toString();
			
			handler.message(Type.CLIENT, value);
			
			byte[] payload = value.getBytes();
			
			DatagramPacket pkt = new DatagramPacket(payload, payload.length, new InetSocketAddress(dCfg.getUdpHost(), dCfg.getUdpPort()));

			socket.send(pkt);
			
		} catch (Exception e) {
			handler.message(Type.CLIENT, ExceptionUtils.getStackTrace(e));
		}
		
	}

}
