/*******************************************************************************
 * Copyright (C) 2017 Boodskap Inc
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package io.boodskap.iot;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A abstract implementation transceive packets from and to Boodkskap platform
 * @author Jegan Vincent
 * 
 * @see MqttSender
 * @see UDPSender
 * @see HttpSender
 *
 */
public abstract class AbstractPublisher {

	private static final Logger LOG = LoggerFactory.getLogger(AbstractPublisher.class);

	protected static final String P_DOMAIN_KEY = "key";
	protected static final String P_API_KEY = "api";
	protected static final String P_MESSAGE_ID = "mid";
	protected static final String P_DEVICE_ID = "did";
	protected static final String P_DEVICE_MODEL = "dmdl";
	protected static final String P_FIRMWARE_VERSION = "fwver";
	protected static final String P_CORRELATION_ID = "corrid";
	protected static final String P_ACK = "acked";
	
	public static final int MSG_PING = 1;
	public static final int MSG_ACK = 2;

	protected LinkedBlockingQueue<Map<String,Object>> acks = new LinkedBlockingQueue<>();
	private ExecutorService exec = Executors.newFixedThreadPool(1);
	private Future<?> pF;
	

	protected final long heartbeat;
	protected String domainKey;
	protected String apiKey;
	protected final String deviceId;
	protected final String deviceModel;
	protected final String firmwareVersion;
	protected final MessageHandler handler;
	
	private long lastAckedTime;
	private long lastSentTime = 0;
	
	
	/**
	 * 
	 * @param domainKey <code>Domain Key obtained from Boodskap platform</code>
	 * @param apiKey <code>API Key obtained from Boodskap platform</code>
	 * @param domainKey <code> You will get it from Boodskap Dashboard</code>
	 * @param apiKey <code> You will get it from Boodskap Dashboard</code>
	 * @param deviceId  <code> Your device id, ex: MyCamera, MyKitchenCamara, etc...</code>
	 * @param deviceModel <code> Ex: RaspCAM, ArduCAM, etc...</code>
	 * @param firmwareVersion <code> Ex: 1.0.0, 0.0.7, etc...</code>
	 * @param handler <code>Handler to handle messages from Boodskap platform</code>
	 */
	protected AbstractPublisher(String domainKey, String apiKey, String deviceId, String deviceModel, String firmwareVersion, long heartbeat, MessageHandler handler) {
		this.domainKey = domainKey;
		this.apiKey = apiKey;
		this.deviceId = deviceId;
		this.deviceModel = deviceModel;
		this.firmwareVersion = firmwareVersion;
		this.heartbeat = heartbeat;
		this.handler = handler;
		
		pF = exec.submit(pinger);
		
	}
	
	public final void open() throws Exception{
		doOpen();
		ping();
	}
	
	public final void close() throws Exception {
		
		try {
			doClose();
		}finally {
			
			if(null != pF) {
				pF.cancel(true);
				pF= null;
			}
		}
	}
	
	public final void ping() throws Exception {
		publish(MSG_PING, new HashMap<>());
	}
	
	public final void publish(int messageId, Map<String, Object> json) throws Exception{
		doPublish(messageId, json);
		lastSentTime = System.currentTimeMillis();
	}
	
	/**
	 * Send a well formatted Boodskap message
	 * @param messageId <pre>Message ID defined in Boodskap Platform</pre>
	 * @param json <pre>A Map containing message fields</pre>
	 * @throws Exception
	 */
	protected abstract void doPublish(int messageId, Map<String, Object> json) throws Exception;
	
	protected final void acknowledge(long corrId, boolean acked) throws MqttPersistenceException, JSONException, MqttException {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(P_CORRELATION_ID, corrId);
		map.put(P_ACK, acked ? 1 : 0);
		acks.offer(map);
	}

	
	/**
	 * Open connection with server channel
	 */
	protected abstract void doOpen()throws Exception;

	/**
	 * Check if you are connected
	 * @return <code>true if connected else false</code>
	 */
	public abstract boolean isConnected();
	
	/**
	 * Closes the connection
	 * @throws Exception
	 */
	protected abstract void doClose() throws Exception;
	
	public String getDomainKey() {
		return domainKey;
	}

	public void setDomainKey(String domainKey) {
		this.domainKey = domainKey;
	}

	public String getApiKey() {
		return apiKey;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}
	
	public long getLastAckedTime() {
		return lastAckedTime;
	}

	/**
	 * Processed the data received, auto acks with the platform
	 * @param raw
	 * @param offset
	 * @param length
	 */
	protected void processData(byte[] raw, int offset, int length) {
		
		
		try {

			lastAckedTime = System.currentTimeMillis();
			
			JSONObject json = new JSONObject(new String(raw, offset, length));
			LOG.info("Command Received: {}", json);
			
			JSONObject header = json.getJSONObject("header");
			JSONObject data = json.getJSONObject("data");
			
			long corrId = header.getLong(P_CORRELATION_ID);
			boolean acked = false;
			boolean shouldAck = true;
			
			try {
				
				String model = null;
				String fwver = null;
				int messageId = header.getInt(P_MESSAGE_ID);
				
				
				if(!header.isNull(P_DEVICE_MODEL)) {
					model = header.getString(P_DEVICE_MODEL);
				}
				
				if(!header.isNull(P_FIRMWARE_VERSION)) {
					fwver = header.getString(P_FIRMWARE_VERSION);
				}
				
				if(messageId > 100) { //Domain messages
					
					acked = handler.handleMessage(
							header.getString(P_DOMAIN_KEY), 
							header.getString(P_API_KEY), 
							header.getString(P_DEVICE_ID), 
							model, 
							fwver,
							corrId,
							messageId,
							data
							);
					
				}else { //Platform messages
					
					switch(messageId) {
					case MSG_PING:
						acked = true;
						break;
					case MSG_ACK:
						shouldAck = false;
						break;
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

			try {
				if(shouldAck) {
					acknowledge(corrId, acked);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private final Runnable pinger = new Runnable() {
		
		@Override
		public void run() {

			while(!Thread.currentThread().isInterrupted()) {
				try {
					
					while(!isConnected()) {
						Thread.sleep(2000);
					}
					
					Map<String, Object> adata = acks.poll(2, TimeUnit.SECONDS);
					
					if(null != adata) {
						
						publish(MSG_ACK, adata);
						
					}else {
						
						if((System.currentTimeMillis()-lastSentTime) >= heartbeat) {
							ping();
						}
						
					}
					
					
				} catch (Exception e) {
					if(null != pF) {
						e.printStackTrace();
					}
				}
			}
		}
	};

}

