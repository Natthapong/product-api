package th.co.truemoney.product.api.handler;

import th.co.truemoney.product.api.manager.OnlineResourceManager;
import th.co.truemoney.serviceinventory.ewallet.domain.ActivityDetail;

public abstract class GeneralActivityDetailViewHandler implements ActivityDetailViewHandler {
	
	protected OnlineResourceManager onlineResourceManager = new OnlineResourceManager();
	
	protected ActivityDetail activity;
	
	@Override
	public void handle(ActivityDetail activity) {
		this.activity = activity;
	}

}
