package io.boodskap.iot.simulator.ext;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class StaticQueue {
	
	private static final Map<String, StaticQueue> instances = new HashMap<>();
	
	private final Map<String, Boolean[]> configs = new HashMap<>();
	private final Map<String, Queue<Object>> queues = new HashMap<>();
	private final Map<String, List<Object>> values = new HashMap<>();

	private StaticQueue() {
	}

	public static final StaticQueue get(String domainKey, String deviceId) {
		
		String id = String.format("%s.%s", domainKey, deviceId);
		
		StaticQueue q = instances.get(id);
		
		if(null == q) {
			q = new StaticQueue();
			instances.put(id, q);
		}
		
		return q;
	}
	
	public boolean has(String queue) {
		return configs.containsKey(queue);
	}
	
	public void init(String queue) {
		init(queue, false, false);
	}
	
	public void init(String queue, boolean refill, boolean reverse) {
		Queue<Object> q = new LinkedBlockingQueue<>();
		queues.put(queue, q);
		configs.put(queue, new Boolean[] {refill, reverse});
	}
	
	public void push(String queue, Object... values) {
		push(queue, Arrays.asList(values));
	}
	
	public void push(String queue, List<Object> values) {
		
		getQueue(queue).addAll(values);
		
		if(isRefill(queue)) {
			this.values.put(queue, values);
		}
	}
	
	public Object pop(String queue) {
		Object val = getQueue(queue).poll();
		if(isRefill(queue)) {
			refill(queue);
		}
		return val;
	}
	
	public boolean isEmpty(String queue) {
		return getQueue(queue).isEmpty();
	}

	protected Queue<Object> getQueue(String queue){
		return queues.get(queue);
	}
	
	protected boolean isRefill(String queue) {
		return configs.get(queue)[0];
	}
	
	protected boolean isReverse(String queue) {
		return configs.get(queue)[1];
	}
	
	protected void refill(String queue) {
		
		Queue<Object> q = getQueue(queue);
		
		List<Object> vals = values.get(queue);
		
		if(isReverse(queue)) {
			Collections.reverse(vals);
		}
		
		q.addAll(vals);
	}
	
}
