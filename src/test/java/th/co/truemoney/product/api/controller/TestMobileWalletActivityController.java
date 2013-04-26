package th.co.truemoney.product.api.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import th.co.truemoney.serviceinventory.ewallet.domain.Activity;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TestMobileWalletActivityController extends BaseTestController {

	private static final String fakeAccessToken = "1111111111";
	private static final String getActivityListURL = String.format(
			"/profile/activities/list/%s", fakeAccessToken);
	

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void getActivityListSuccess() throws Exception {
//		List<Activity> activityList = new ArrayList<Activity>();
		
		when(this.activityServiceMock.getActivities(fakeAccessToken)).thenReturn(Collections.EMPTY_LIST);
		this.verifySuccess(this.doGET(getActivityListURL));
	}

	@Test
	public void getActivityListFail() throws Exception {
		when(this.activityServiceMock.getActivities(fakeAccessToken))
				.thenThrow(new ServiceInventoryException(400, "", "", ""));
		this.verifyFailed(this.doGET(getActivityListURL));
	}
}
