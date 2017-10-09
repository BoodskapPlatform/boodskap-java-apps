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

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;

/**
 * HTTP Sender implementation
 * Publish messages to Boodskap IoT Platform through HTTP Push
 * 
 * @author Jegan Vincent
 *
 * @see MqttSender
 * @see UDPSender
 */
public class HttpSender extends AbstractSender {
	
	protected final String httpUrl;

	public HttpSender(String httpUrl, String domainKey, String apiKey, String deviceId, String deviceModel, String firmwareVersion, MessageHandler handler) {
		super(domainKey, apiKey, deviceId, deviceModel, firmwareVersion, handler);
		this.httpUrl = httpUrl;
	}

	@Override
	public void publish(int messageId, Map<String, Object> json) throws Exception {
		
		JSONObject jobj = new JSONObject(json);
		
		System.out.println("Posting http message");
		
		HttpResponse<String> res = Unirest.post(httpUrl + "/push/raw/{dkey}/{akey}/{did}/{dmdl}/{fwver}/{mid}")
				  .header("accept", "application/json")
				  .routeParam("dkey", domainKey)
				  .routeParam("akey", apiKey)
				  .routeParam("did", deviceId)
				  .routeParam("dmdl", deviceModel)
				  .routeParam("fwver", firmwareVersion)
				  .routeParam("mid", String.valueOf(messageId))
				  .queryString("type", "JSON")
				  .body(jobj.toString())
				  .asString();
		
		if(res.getStatus() < 200 && res.getStatus() > 299) {
			throw new Exception(res.getStatusText());
		}
	}

	@Override
	protected void acknowledge(long corrId, boolean acked) throws Exception {
	}

	@Override
	public void open() throws Exception {
	}

	@Override
	public boolean isConnected() {
		return true;
	}

	@Override
	public void close() throws Exception {
	}

}
