package io.boodskap.iot.simulator;

import java.util.LinkedHashMap;
import java.util.Map;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.impl.StdSchedulerFactory;

import io.boodskap.iot.simulator.impl.HttpTransmitter;
import io.boodskap.iot.simulator.impl.MqttTransmitter;
import io.boodskap.iot.simulator.impl.UDPTransmitter;

public class GroovyScheduler {

	private static final GroovyScheduler instance = new GroovyScheduler();

	private final Map<String, TriggerKey> triggers = new LinkedHashMap<>();
	private final Map<String, Transmitter> transmitters = new LinkedHashMap<>();
	private final Map<String, TransmitterHandler> handlers = new LinkedHashMap<>();
	
	private Scheduler scheduler;

	private GroovyScheduler() {
	}

	public static final GroovyScheduler get() {
		return instance;
	}

	public void start() throws SchedulerException {
		if (null == scheduler) {
			scheduler = new StdSchedulerFactory().getScheduler();
			scheduler.start();
		}
	}

	public void stop() throws SchedulerException {
		if (null != scheduler) {
			scheduler.shutdown(false);
			scheduler = null;
		}
	}
	
	public boolean isActive(String id) {
		return triggers.containsKey(id);
	}
	
	public Transmitter transmitter(String id) {
		return transmitters.get(id);
	}
	
	public TransmitterHandler handler(String id) {
		return handlers.get(id);
	}
	
	public boolean unschedule(String id) throws SchedulerException {
		
		TriggerKey tk = triggers.remove(id);
		
		Transmitter t = transmitters.remove(id);
		if(null != t) {
			t.dispose();
		}
		
		handlers.remove(id);
		
		if(null != tk) {
			return scheduler.unscheduleJob(tk);
		}

		return false;
	}

	public Trigger schedule(DomainConfig dc, SimulatorConfig sc, TransmitterHandler handler) throws Exception {
		
		unschedule(sc.getId());
		
		Trigger trigger;
		Transmitter t;
		
		try {
			
			switch(sc.getProtocol()) {
			case UDP:
				t = new UDPTransmitter();
				break;
			case MQTT:
				t = new MqttTransmitter();
				break;
			case HTTP:
			case COAP:
			default:
				t = new HttpTransmitter();
				break;
			}
			
			t.init(sc, handler);
			transmitters.put(sc.getId(), t);
			handlers.put(sc.getId(), handler);
			
			trigger = TriggerBuilder
					.newTrigger()
					.withIdentity(sc.getId(), dc.getDomainKey())
					.withSchedule(CronScheduleBuilder.cronSchedule(sc.getCronSchedule())).build();

			JobDetail job = JobBuilder.newJob(GroovyScriptJob.class).build();
			job.getJobDataMap().put("scfg", sc);

			scheduler.scheduleJob(job, trigger);

			triggers.put(sc.getId(), trigger.getKey());
			
			return trigger;

		} catch (Exception e) {
			unschedule(sc.getId());
			throw e;
		}
		
	}

}
