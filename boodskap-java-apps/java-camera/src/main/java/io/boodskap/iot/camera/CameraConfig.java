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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;

import com.github.sarxos.webcam.WebcamResolution;

/**
 * Camera Application Configuration, stored in ${user.home}/.camera/config.json
 * 
 * @author Jegan Vincent
 *
 */
public class CameraConfig implements Serializable{
	
	private static final long serialVersionUID = 2838776972406711700L;

	public static enum Mode{
		STREAM,
		SNAP
	}
	
	public static enum Publisher{
		MQTT,
		UDP,
		HTTP
	}
	
	private String domainKey="";
	private String apiKey="";
	private String deviceId="";
	private String deviceModel="BSKP-PICAM";
	private String firmwareVersion="1.0.0";
	private Mode mode = Mode.SNAP;
	private Publisher publisher = Publisher.MQTT;
	private long interval = 15000;
	private int framePerMinite = 30;
	private String imageFormat = "jpeg";
	private String streamFormat = "mp4";
	private int imageWidth = 200;
	private int imageHeight = 200;
	private float imageQuality = 0.5F;
	private Map<String, WebcamResolution> cameraResolutions = new HashMap<String, WebcamResolution>();
	private List<WebcamResolution> availableResolutions = new ArrayList<WebcamResolution>();
	private List<String> availableFormats = new ArrayList<String>();
	private String mqttUrl = "tcp://mqtt.boodskap.io:1883";
	private String udpHost = "udp.boodskap.io";
	private int udpPort = 5555;
	private String httpUrl = "http://api.boodskap.io";

	public CameraConfig() {
	}

	public void validate() throws Exception {
		
		if(null == mqttUrl || mqttUrl.trim().equals("")) throw new IllegalArgumentException("mqttUrl required");
		if(null == udpHost || udpHost.trim().equals("")) throw new IllegalArgumentException("udpHost required");
		if(udpPort <= 0) throw new IllegalArgumentException("udpPort must be > 0");
		if(null == httpUrl || httpUrl.trim().equals("")) throw new IllegalArgumentException("httpUrl required");
		if(null == domainKey || domainKey.trim().equals("")) throw new IllegalArgumentException("domainKey required");
		if(null == apiKey || apiKey.trim().equals("")) throw new IllegalArgumentException("apiKey required");
		if(null == deviceId || deviceId.trim().equals("")) throw new IllegalArgumentException("deviceId required");
		if(null == deviceModel || deviceModel.trim().equals("deviceModel")) throw new IllegalArgumentException("deviceModel required");
		if(null == firmwareVersion || firmwareVersion.trim().equals("")) throw new IllegalArgumentException("firmwareVersion required");
		if(null == mode) throw new IllegalArgumentException("mode required");
		if(null == publisher) throw new IllegalArgumentException("publisher required");
		if(interval < 10) throw new IllegalArgumentException("interval must be >= 10");
		if(framePerMinite > 2500) throw new IllegalArgumentException("framePerMinite must be <= 2500");
		if(null == imageFormat || imageFormat.trim().equals("")) throw new IllegalArgumentException("imageFormat required");
		if(null == streamFormat || streamFormat.trim().equals("")) throw new IllegalArgumentException("streamFormat required");
		
		Iterator<ImageWriter> iter = ImageIO.getImageWritersBySuffix(imageFormat);
		if(!iter.hasNext()) throw new IllegalArgumentException(String.format("No image writer found for image format: %s", imageFormat));
		while(iter.hasNext()) {
			System.out.format("Writer for Image Format %s: %s\n", imageFormat, iter.next().getClass());
		}
	}

	public Mode getMode() {
		return mode;
	}

	public void setMode(Mode mode) {
		this.mode = mode;
	}

	public long getInterval() {
		return interval;
	}

	public void setInterval(long interval) {
		this.interval = interval;
	}

	public int getFramePerMinite() {
		return framePerMinite;
	}

	public void setFramePerMinite(int framePerMinite) {
		this.framePerMinite = framePerMinite;
	}

	public String getImageFormat() {
		return imageFormat;
	}

	public void setImageFormat(String imageFormat) {
		this.imageFormat = imageFormat;
	}

	public String getStreamFormat() {
		return streamFormat;
	}

	public void setStreamFormat(String streamFormat) {
		this.streamFormat = streamFormat;
	}

	public String getMqttUrl() {
		return mqttUrl;
	}

	public void setMqttUrl(String mqttUrl) {
		this.mqttUrl = mqttUrl;
	}

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

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getDeviceModel() {
		return deviceModel;
	}

	public void setDeviceModel(String deviceModel) {
		this.deviceModel = deviceModel;
	}

	public String getFirmwareVersion() {
		return firmwareVersion;
	}

	public void setFirmwareVersion(String firmwareVersion) {
		this.firmwareVersion = firmwareVersion;
	}

	public Map<String, WebcamResolution> getCameraResolutions() {
		return cameraResolutions;
	}

	public void setCameraResolutions(Map<String, WebcamResolution> cameraResolutions) {
		this.cameraResolutions = cameraResolutions;
	}

	public List<WebcamResolution> getAvailableResolutions() {
		return availableResolutions;
	}

	public void setAvailableResolutions(List<WebcamResolution> availableResolutions) {
		this.availableResolutions = availableResolutions;
	}

	public List<String> getAvailableFormats() {
		return availableFormats;
	}

	public void setAvailableFormats(List<String> availableFormats) {
		this.availableFormats = availableFormats;
	}

	public int getImageWidth() {
		return imageWidth;
	}

	public void setImageWidth(int imageWidth) {
		this.imageWidth = imageWidth;
	}

	public int getImageHeight() {
		return imageHeight;
	}

	public void setImageHeight(int imageHeight) {
		this.imageHeight = imageHeight;
	}

	public float getImageQuality() {
		return imageQuality;
	}

	public void setImageQuality(float imageQuality) {
		this.imageQuality = imageQuality;
	}

	public Publisher getPublisher() {
		return publisher;
	}

	public void setPublisher(Publisher publisher) {
		this.publisher = publisher;
	}

	public String getUdpHost() {
		return udpHost;
	}

	public void setUdpHost(String udpHost) {
		this.udpHost = udpHost;
	}

	public int getUdpPort() {
		return udpPort;
	}

	public void setUdpPort(int udpPort) {
		this.udpPort = udpPort;
	}

	public String getHttpUrl() {
		return httpUrl;
	}

	public void setHttpUrl(String httpUrl) {
		this.httpUrl = httpUrl;
	}

}
