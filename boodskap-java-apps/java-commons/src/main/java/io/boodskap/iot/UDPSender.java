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

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.codehaus.jettison.json.JSONObject;

/**
 * UDP Sender implementation
 * Publish messages to Boodskap IoT Platform through UDP Datagrams
 * 
 * @author Jegan Vincent
 *
 * @see MqttSender
 * @see HttpSender
 */
public class UDPSender extends AbstractSender implements Runnable  {
	
	public static int BUFFER_SIZE = 2048;
	
	protected final String udpHost;
	protected final int udpPort;
	
	private DatagramSocket socket;
	private ExecutorService exec = Executors.newSingleThreadExecutor();

	private Future<?> runner;

	public UDPSender(String udpHost, int udpPort, String domainKey, String apiKey, String deviceId, String deviceModel, String firmwareVersion, MessageHandler handler) {
		super(domainKey, apiKey, deviceId, deviceModel, firmwareVersion, handler);
		this.udpHost = udpHost;
		this.udpPort = udpPort;
	}

	@Override
	public void publish(int messageId, Map<String, Object> json) throws Exception {
		
		Map<String, Object> map = new HashMap<>();
		map.put(P_DOMAIN_KEY, domainKey);
		map.put(P_API_KEY, apiKey);
		map.put(P_MESSAGE_ID, messageId);
		map.put(P_DEVICE_ID, deviceId);
		map.put(P_DEVICE_MODEL, deviceModel);
		map.put(P_FIRMWARE_VERSION, firmwareVersion);
		
		JSONObject header = new JSONObject(map);
		JSONObject data = new JSONObject(json);
		JSONObject message = new JSONObject();
		
		message.put("header", header);
		message.put("data", data);
		
		byte[] payload = message.toString().getBytes();

		System.out.format("sending message size: %d\n", payload.length);
		
		DatagramPacket pkt = new DatagramPacket(payload, payload.length, new InetSocketAddress(udpHost, udpPort));
		socket.send(pkt);
	}

	@Override
	protected void acknowledge(long corrId) throws Exception {
		
		Map<String, Object> map = new HashMap<>();
		map.put(P_DOMAIN_KEY, domainKey);
		map.put(P_API_KEY, apiKey);
		map.put(P_MESSAGE_ID, 2);
		map.put(P_DEVICE_ID, deviceId);
		map.put(P_DEVICE_MODEL, deviceModel);
		map.put(P_FIRMWARE_VERSION, firmwareVersion);
		map.put(P_CORRELATION_ID, corrId);
		
		JSONObject header = new JSONObject(map);
		JSONObject data = new JSONObject();
		data.put(P_CORRELATION_ID, corrId);
		JSONObject message = new JSONObject();
		
		message.put("header", header);
		message.put("data", data);
		
		byte[] payload = message.toString().getBytes();
		DatagramPacket pkt = new DatagramPacket(payload, payload.length, new InetSocketAddress(udpHost, udpPort));
		socket.send(pkt);
	}

	/**
	 * Open connection with server channel
	 */
	public void open()throws Exception{
		if(!isConnected()) {
			socket = new DatagramSocket(0);
			runner = exec.submit(this);
		}
	}

	/**
	 * Check if you are connected
	 * @return <code>true if connected else false</code>
	 */
	public boolean isConnected() {
		return (null != socket && !socket.isClosed());
	}
	
	/**
	 * Closes the connection
	 * @throws Exception
	 */
	public void close() throws Exception{
		if(null != runner) {
			runner.cancel(true);
			runner = null;
		}
		if(null != socket && isConnected()) {
			socket.close();
			socket = null;
		}
	}

	@Override
	public void run() {
		
		byte[] data = new byte[BUFFER_SIZE];
		
		while(!Thread.currentThread().isInterrupted()) {
			try {
				
				DatagramPacket pkt = new DatagramPacket(data, data.length);
				socket.receive(pkt);
				
				processData(pkt.getData(), pkt.getOffset(), pkt.getLength());
				
			} catch (Exception e) {
				if(null != runner) {
					e.printStackTrace();
				}
			}
		}
	}
	

}
