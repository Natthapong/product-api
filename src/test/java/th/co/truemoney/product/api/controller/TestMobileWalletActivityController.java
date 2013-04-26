package th.co.truemoney.product.api.controller;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import th.co.truemoney.serviceinventory.ewallet.domain.Activity;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

public class TestMobileWalletActivityController extends BaseTestController {

	private static final String fakeAccessToken = "1111111111";
	private static final String getActivityListURL = String.format(
			"/profile/activities/list/%s", fakeAccessToken);
	private MobileWalletActivityController controller;

	@Before
	public void setUp() throws Exception {
		controller = new MobileWalletActivityController();
	}

	@SuppressWarnings("unchecked")
	@Test
	public void getActivityListSuccess() throws Exception {
		when(this.activityServiceMock.getActivities(fakeAccessToken)).thenReturn(Collections.EMPTY_LIST);
		this.verifySuccess(this.doGET(getActivityListURL));
	}

	@Test
	public void getActivityListFail() throws Exception {
		when(this.activityServiceMock.getActivities(fakeAccessToken))
				.thenThrow(new ServiceInventoryException(400, "", "", ""));
		this.verifyFailed(this.doGET(getActivityListURL));
	}
	
	@Test
	public void test(){
		List<Activity> mockActivities = new ArrayList<Activity>();
		mockActivities.add(new Activity());
		
		when(this.activityServiceMock.getActivities(fakeAccessToken)).thenReturn(mockActivities);
		
		List<Activity> activities = this.activityServiceMock.getActivities(fakeAccessToken);
		assertEquals(1, activities.size());
	}
	
}
