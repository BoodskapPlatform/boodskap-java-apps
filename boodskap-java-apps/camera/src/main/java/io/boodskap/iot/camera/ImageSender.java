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
package io.boodskap.iot.camera;

import java.util.HashMap;
import java.util.Map;

import io.boodskap.iot.AbstractPublisher;
import io.boodskap.iot.HttpSender;
import io.boodskap.iot.MessageHandler;
import io.boodskap.iot.MqttSender;
import io.boodskap.iot.UDPSender;

/**
 * A utility class to publich Camera packets to the Boodskap platform
 * 
 * @author Jegan Vincent
 *
 */
public class ImageSender {
	
	public static final int MSG_ID_IMAGE = 500000000;
	public static final int MSG_ID_VIDEO = 500000001;
	
	protected final CameraConfig config;
	
	protected AbstractPublisher publisher;

	public ImageSender(CameraConfig c, MessageHandler handler) {
		
		this.config = c;
		
		switch(config.getPublisher()) {
		case HTTP:
			publisher = new HttpSender(c.getHttpUrl(), c.getDomainKey(), c.getApiKey(), c.getDeviceId(), c.getDeviceModel(), c.getFirmwareVersion(), handler);
			break;
		case MQTT:
			publisher = new MqttSender(c.getMqttUrl(), c.getHeartbeat(), c.getDomainKey(), c.getApiKey(), c.getDeviceId(), c.getDeviceModel(), c.getFirmwareVersion(), handler);
			break;
		case UDP:
			publisher = new UDPSender(c.getUdpHost(), c.getUdpPort(), c.getHeartbeat(),  c.getDomainKey(), c.getApiKey(), c.getDeviceId(), c.getDeviceModel(), c.getFirmwareVersion(), handler);
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
	
	public void reconfig() {
		
	}
	
	public void sendPicture(String cameraId, byte[] data) throws Exception {
		
		switch(config.getPublisher()) {
		case HTTP:
		case UDP:
		default:
			Map<String, Object> json = new HashMap<>();
			json.put("data", data);
			json.put("format", config.getImageFormat().name().toLowerCase());
			json.put("cameraid", cameraId);
			publisher.publish(MSG_ID_IMAGE, json);
			break;
		case MQTT:
			
			((MqttSender)publisher).sendPicture(cameraId, true, config.getImageFormat().name().toLowerCase(), data, 0, false);
			
			break;
		}
		
	}

	public void sendVideo(String cameraId, byte[] data) throws Exception {
		
		switch(config.getPublisher()) {
		case HTTP:
		case UDP:
		default:
			Map<String, Object> json = new HashMap<>();
			json.put("data", data);
			json.put("format", config.getImageFormat().name().toLowerCase());
			json.put("cameraid", cameraId);
			publisher.publish(MSG_ID_VIDEO, json);
			break;
		case MQTT:
			
			((MqttSender)publisher).sendVideo(cameraId, true, config.getStreamFormat(), data, 0, false);
			
			break;
		}
		
	}

}
