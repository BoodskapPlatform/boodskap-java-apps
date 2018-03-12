package io.boodskap.iot.wb;

import javax.swing.SwingWorker;

import io.boodskap.iot.api.RetreiveDomainRuleApi;
import io.boodskap.iot.api.impl.ApiException;
import io.boodskap.iot.api.model.DomainRule;

public class PlatformCodeLoader extends SwingWorker<Object, String> {
	
	private final TreeMenu menu;
	
	public PlatformCodeLoader(TreeMenu menu) {
		this.menu = menu;
	}

	@Override
	protected Object doInBackground() throws Exception {
		
		try {
			
			switch(menu.getType()) {
			case DOMAIN_RULE:
				return fetchDomainRule();
			case MESSAGE_RULE:
				break;
			case NAMED_RULE:
				break;
			case SCHEDULED_RULE:
				break;
			case GROOVY_CLASS:
				break;
			default:
				return null;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}

	private Object fetchDomainRule() throws ApiException {
		RetreiveDomainRuleApi api = new RetreiveDomainRuleApi();
		DomainRule rule = api.getDomainRule(Session.get().getToken());
		return rule;
	}

}
