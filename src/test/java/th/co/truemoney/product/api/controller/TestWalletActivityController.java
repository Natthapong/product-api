package th.co.truemoney.product.api.controller;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import th.co.truemoney.product.api.manager.OnlineResourceManager;
import th.co.truemoney.serviceinventory.ewallet.domain.Activity;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

public class TestWalletActivityController extends BaseTestController {

	private static final String fakeAccessToken = "1111111111";
	private static final String getActivityListURL = String.format("/profile/activities/list/%s", fakeAccessToken);

	@Autowired
	private WalletActivityController controller = new WalletActivityController();

	@Autowired
	private OnlineResourceManager onlineResourceManager;
	
	@Test
	public void getActivityListSuccess() throws Exception {

		when(this.activityServiceMock.getActivities(fakeAccessToken)).thenReturn(Collections.<Activity> emptyList());
		this.verifySuccess(doGET(getActivityListURL));
	}

	@Test
	public void getActivityListFail() throws Exception {
		when(this.activityServiceMock.getActivities(fakeAccessToken)).thenThrow(new ServiceInventoryException(400, "", "", ""));
		this.verifyFailed(this.doGET(getActivityListURL));
	}
	
	@Test
	public void transferActivityTypeLogo() {
		Activity transfer = new Activity();
		transfer.setType("transfer");
		transfer.setAction("debtor");
		
		String logoURL = onlineResourceManager.getActivityTypeLogoURL(transfer);
		assertTrue(logoURL.endsWith("transfer_debtor.png"));
		
		transfer.setAction("creditor");
		logoURL = onlineResourceManager.getActivityTypeLogoURL(transfer);
		assertTrue(logoURL.endsWith("transfer_creditor.png"));
	}
}
