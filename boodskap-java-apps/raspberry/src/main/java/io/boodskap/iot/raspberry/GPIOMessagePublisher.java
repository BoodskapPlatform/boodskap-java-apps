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
package io.boodskap.iot.raspberry;

import java.util.HashMap;
import java.util.Map;

import io.boodskap.iot.MessageHandler;
import io.boodskap.iot.MessagePublisher;

/**
 * A utility class to publich Message packets to the Boodskap platform
 * 
 * @author Jegan Vincent
 *
 */
public class GPIOMessagePublisher extends MessagePublisher<GPIOConfig>{
	
	public GPIOMessagePublisher(GPIOConfig c, String deviceModel, String firmwareVersion, MessageHandler handler) {
		super(c, deviceModel, firmwareVersion, handler);
	}

	public void send(GPIOMessage msg) throws Exception {
		
		Map<String, Object> json = new HashMap<>();
		
		for(GPIOPin p :  msg.getPins()) {
			json.put(p.getName(), !p.getState().isHigh());
		}
		
		publish(msg.getMessageId(), json);
		
	}


}
