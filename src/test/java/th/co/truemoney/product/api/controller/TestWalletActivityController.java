package th.co.truemoney.product.api.controller;

import static org.mockito.Mockito.when;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import th.co.truemoney.serviceinventory.ewallet.domain.Activity;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

public class TestWalletActivityController extends BaseTestController {

	private static final String fakeAccessToken = "1111111111";
	private static final String getActivityListURL = String.format(
			"/profile/activities/list/%s", fakeAccessToken);

	@Autowired
	WalletActivityController controller = new WalletActivityController();

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
