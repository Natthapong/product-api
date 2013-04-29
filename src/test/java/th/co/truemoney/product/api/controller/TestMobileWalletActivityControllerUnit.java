package th.co.truemoney.product.api.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import th.co.truemoney.product.api.domain.ActivityViewItem;
import th.co.truemoney.product.api.domain.ProductResponse;
import th.co.truemoney.product.api.manager.MessageManager;
import th.co.truemoney.product.api.util.ProductResponseFactory;
import th.co.truemoney.serviceinventory.ewallet.ActivityService;
import th.co.truemoney.serviceinventory.ewallet.domain.Activity;

@RunWith(Parameterized.class)
public class TestMobileWalletActivityControllerUnit {
	
	protected ActivityService activityServiceMock;
	
	MobileWalletActivityController controller;
	
	ProductResponseFactory responseFactory;
	
	static SimpleDateFormat df = new SimpleDateFormat("dd-mm-yyyy");
	
	@Parameters
	public static Iterable<Object[]> data() throws ParseException {
		return Arrays.asList(new Object[][] {
				{ new Activity(1L, "topup_mobile"), new ActivityViewItem(String.valueOf(1L), "เติมเงินมือถือ") },
				{ new Activity(2L, "billpay"), new ActivityViewItem(String.valueOf(2L), "จ่ายบิล") }
		});
	}
	
	private Activity fInput;
	
	private ActivityViewItem fOutput;
	
	public TestMobileWalletActivityControllerUnit(Activity input, ActivityViewItem output) {
		this.fInput = input;
		this.fOutput = output;
	}
	
	@Before
	public void setup() {
		this.activityServiceMock = mock(ActivityService.class);
		this.responseFactory = new ProductResponseFactory();
		this.responseFactory.setMessageManager(mock(MessageManager.class));
		this.controller = new MobileWalletActivityController();
		this.controller.setActivityService(this.activityServiceMock);
		this.controller.setResponseFactory(responseFactory);
	}
	
	public void tearDown() {
		reset(this.activityServiceMock);
	}
	@Test
	public void getActivities() throws Exception {
		String accessToken = "_whatever_";
		
		List<Activity> list = new ArrayList<Activity>();
		list.add(this.fInput);
		
		when(this.activityServiceMock.getActivities(accessToken)).thenReturn(list);
		
		ProductResponse resp = this.controller.getActivityList(accessToken);
		
		Map<String, Object> data = resp.getData();
		List<ActivityViewItem> viewItemList = (List<ActivityViewItem>) data.get("activities");
		assertTrue(viewItemList.size() > 0);
		
		ActivityViewItem viewItem = viewItemList.get(0);
		
		assertEquals(fOutput.getReportID(), viewItem.getReportID());
	}
}
