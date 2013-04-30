package th.co.truemoney.product.api.controller;

import static org.junit.Assert.assertEquals;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import th.co.truemoney.product.api.domain.ActivityViewItem;
import th.co.truemoney.product.api.domain.ProductResponse;
import th.co.truemoney.serviceinventory.ewallet.domain.Activity;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

public class TestMobileWalletActivityController extends BaseTestController {

	private static final String fakeAccessToken = "1111111111";
	private static final String getActivityListURL = String.format(
			"/profile/activities/list/%s", fakeAccessToken);

	private static final String imagesURL = "https://secure.truemoney-dev.com/m/tmn_webview/logo_activity_type";
	
	@Autowired
	MobileWalletActivityController controller;
	
	@Before
	public void setUp() throws Exception {
		controller = new MobileWalletActivityController();
	}

	@SuppressWarnings("unchecked")
	@Test
	public void getActivityListSuccess() throws Exception {

		when(this.activityServiceMock.getActivities(fakeAccessToken)).thenReturn(Collections.<Activity> emptyList());
	}

	@Test
	public void getActivityListFail() throws Exception {
		when(this.activityServiceMock.getActivities(fakeAccessToken))
				.thenThrow(new ServiceInventoryException(400, "", "", ""));
		this.verifyFailed(this.doGET(getActivityListURL));
	}
	

	
	
	@SuppressWarnings("unused")
	private List<Activity> createActivityStub(String type, String action) throws ParseException{
		List<Activity> actList = new ArrayList<Activity>();
		Activity act = new Activity();
		SimpleDateFormat dt1 = new SimpleDateFormat("dd-mm-yy");
		Date date = dt1.parse("10-03-13");
		act.setReportID(111111111L);
		act.setTransactionDate(date);
		act.setType(type);
		act.setAction(action);
		
		actList.add(act);
		return actList;
	}
	
}
