package th.co.truemoney.product.api.handler;

import java.util.Map;

import th.co.truemoney.serviceinventory.ewallet.domain.ActivityDetail;

public interface ActivityDetailViewHandler {
	
	public void handle(ActivityDetail activity);

	public Map<String, String> buildSection1();

	public Map<String, Object> buildSection2();

	public Map<String, Object> buildSection3();

	public Map<String, Object> buildSection4();
	
}
