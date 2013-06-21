package th.co.truemoney.product.api.controller;

import java.util.Collections;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import th.co.truemoney.serviceinventory.ewallet.domain.Activity;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

import static org.mockito.Mockito.when;

public class TestWalletActivityController extends BaseTestController {

	private static final String fakeAccessToken = "1111111111";
	private static final String getActivityListURL = String.format("/profile/activities/list/%s", fakeAccessToken);

	@Autowired
	WalletActivityController controller = new WalletActivityController();

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

}
