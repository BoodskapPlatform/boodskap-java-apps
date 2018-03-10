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

/**
 * A utility class to publich Message packets to the Boodskap platform
 * 
 * @author Jegan Vincent
 *
 */
public class MessagePublisher<T extends BaseConfig> {
	
	protected final T config;
	
	protected AbstractPublisher publisher;

	public MessagePublisher(T c, String deviceModel, String firmwareVersion, MessageHandler handler) {
		
		this.config = c;
		
		switch(config.getPublisher()) {
		case HTTP:
			publisher = new HttpSender(c.getHttpUrl(), c.getDomainKey(), c.getApiKey(), c.getDeviceId(), deviceModel, firmwareVersion, handler);
			break;
		case MQTT:
			publisher = new MqttSender(c.getMqttUrl(), c.getHeartbeat(), c.getDomainKey(), c.getApiKey(), c.getDeviceId(), deviceModel, firmwareVersion, handler);
			break;
		case UDP:
			publisher = new UDPSender(c.getUdpHost(), c.getUdpPort(), c.getHeartbeat(),  c.getDomainKey(), c.getApiKey(), c.getDeviceId(), deviceModel, firmwareVersion, handler);
			break;
		}
	}
	
	public void open() throws Exception {
		publisher.open();
	}
	
	public boolean isConnected() {
		return publisher.isConnected();
	}
	
	public void close() throws Exception {
		publisher.close();
	}
	
	public T getConfig() {
		return config;
	}
	
	public void publish(int messageId, Map<String, Object> json) throws Exception {
		publisher.publish(messageId, json);
	}
	

}
