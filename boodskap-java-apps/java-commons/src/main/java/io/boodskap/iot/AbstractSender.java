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

import java.util.Map;

import org.codehaus.jettison.json.JSONObject;

/**
 * A abstract implementation transceive packets from and to Boodkskap platform
 * @author Jegan Vincent
 * 
 * @see MqttSender
 * @see UDPSender
 * @see HttpSender
 *
 */
public abstract class AbstractSender {

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

	protected String domainKey;
	protected String apiKey;
	protected final String deviceId;
	protected final String deviceModel;
	protected final String firmwareVersion;
	protected final MessageHandler handler;
	
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
	protected AbstractSender(String domainKey, String apiKey, String deviceId, String deviceModel, String firmwareVersion, MessageHandler handler) {
		this.domainKey = domainKey;
		this.apiKey = apiKey;
		this.deviceId = deviceId;
		this.deviceModel = deviceModel;
		this.firmwareVersion = firmwareVersion;
		this.handler = handler;
	}
	
	/**
	 * Send a well formatted Boodskap message
	 * @param messageId <pre>Message ID defined in Boodskap Platform</pre>
	 * @param json <pre>A Map containing message fields</pre>
	 * @throws Exception
	 */
	public abstract void publish(int messageId, Map<String, Object> json) throws Exception;
	
	/**
	 * Acknowledges the received packet
	 * @param corrId
	 * @param acked <code>true for success else false</code>
	 * @throws Exception
	 */
	protected abstract void acknowledge(long corrId, boolean acked)throws Exception;
	
	/**
	 * Open connection with server channel
	 */
	public abstract void open()throws Exception;

	/**
	 * Check if you are connected
	 * @return <code>true if connected else false</code>
	 */
	public abstract boolean isConnected();
	
	/**
	 * Closes the connection
	 * @throws Exception
	 */
	public abstract void close() throws Exception;
	
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

	/**
	 * Processed the data received, auto acks with the platform
	 * @param raw
	 * @param offset
	 * @param length
	 */
	protected void processData(byte[] raw, int offset, int length) {
		
		
		try {
			
			JSONObject json = new JSONObject(new String(raw, offset, length));
			System.out.format("Command Received: %s\n", json);
			
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
	
}
