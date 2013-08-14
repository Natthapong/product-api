package th.co.truemoney.product.api.handler;

import java.util.Map;

import th.co.truemoney.serviceinventory.ewallet.domain.ActivityDetail;

public interface ActivityDetailViewHandler {
	
	void handle(ActivityDetail activity);

	Map<String, String> buildSection1();

	Map<String, Object> buildSection2();

	Map<String, Object> buildSection3();

	Map<String, Object> buildSection4();
	
}
