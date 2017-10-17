package io.boodskap.iot.simulator;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import io.boodskap.iot.simulator.TransmitterHandler.Type;
import io.boodskap.iot.simulator.ext.StaticQueue;

public class GroovyScriptJob implements Job {

	public GroovyScriptJob() {
	}

	@Override
	public void execute(JobExecutionContext ctx) throws JobExecutionException {
		
		TransmitterHandler handler = null;

		try {
			
			SimulatorConfig sCfg = (SimulatorConfig) ctx.getJobDetail().getJobDataMap().get("scfg");
			
			DomainConfig dCfg = DomainConfigs.get().getConfiguration(sCfg.getDomainConfig());
			
			
			handler = GroovyScheduler.get().handler(sCfg.getId());
			
			handler.message(Type.CLIENT, "Executing...");
			
			Map<String, Object> map = new HashMap<>();
			
			map.put("boodskap", GroovyScheduler.get().transmitter(sCfg.getId()));
			map.put("Q", StaticQueue.get(dCfg.getDomainKey(), sCfg.getDeviceId()));
			
		    final Binding binding = new Binding(map); // allow parameters in the script
		    final GroovyShell shell = new GroovyShell(binding); // create shall
	    	shell.evaluate(sCfg.getCode());
			
		} catch (Exception e) {
			handler.message(Type.CLIENT, ExceptionUtils.getStackTrace(e));
		}finally {
			handler.message(Type.CLIENT, "Done.");
		}
		
	}

}
