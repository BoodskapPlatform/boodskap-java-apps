package io.boodskap.iot.wb;

public class Session {
	
	private static final Session instance = new Session();
	
	private String domainKey = "myremothgs";
	private String apiKey = "MYR3M0THG5";

	private Session() {
	}
	
	public static final Session get() {
		return instance;
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

	public String getToken() {
		return String.format("%s:%s", domainKey, apiKey);
	}

}
