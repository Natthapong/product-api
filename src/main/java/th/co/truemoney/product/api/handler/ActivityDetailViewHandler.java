package th.co.truemoney.product.api.handler;

import java.util.Map;

import th.co.truemoney.serviceinventory.ewallet.domain.ActivityDetail;

public interface ActivityDetailViewHandler {
	
	public void handle(ActivityDetail activity);

	public Map<String, String> buildSection1();
	
}
