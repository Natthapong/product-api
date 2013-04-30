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
				{ new Activity(1L, "topup_mobile", df.parse("10-03-2013"), "d.tmvhtopup", "0812345678"), new ActivityViewItem(String.valueOf(1L), "เติมเงินมือถือ", "10/03/13", "TrueMove H", "081-234-5678") },
				{ new Activity(1L, "topup_mobile", df.parse("10-03-2013"), "d.tmvtopup", "0812345678"), new ActivityViewItem(String.valueOf(1L), "เติมเงินมือถือ", "10/03/13",  "TrueMove", "081-234-5678") },
				{ new Activity(2L, "billpay", df.parse("10-03-2013"), "d.catv", "111111111"), new ActivityViewItem(String.valueOf(2L), "จ่ายบิล", "10/03/13", "True Vision", "111111111") },
				{ new Activity(2L, "billpay", df.parse("10-03-2013"), "d.dstv", "111111111"), new ActivityViewItem(String.valueOf(2L), "จ่ายบิล", "10/03/13", "True Vision", "111111111") },
				{ new Activity(2L, "billpay", df.parse("10-03-2013"), "d.tr", "111111111"), new ActivityViewItem(String.valueOf(2L), "จ่ายบิล", "10/03/13", "โทรศัพท์บ้านทรู", "111111111") },
				{ new Activity(2L, "billpay", df.parse("10-03-2013"), "d.tmvh", "111111111"), new ActivityViewItem(String.valueOf(2L), "จ่ายบิล", "10/03/13", "TrueMove H", "111111111") },
				{ new Activity(2L, "billpay", df.parse("10-03-2013"), "d.trmv", "111111111"), new ActivityViewItem(String.valueOf(2L), "จ่ายบิล", "10/03/13", "TrueMove", "111111111") },
				{ new Activity(2L, "billpay", df.parse("10-03-2013"), "d.ti", "111111111"), new ActivityViewItem(String.valueOf(2L), "จ่ายบิล", "10/03/13", "True Online", "111111111") },
				{ new Activity(2L, "billpay", df.parse("10-03-2013"), "d.tic", "111111111"), new ActivityViewItem(String.valueOf(2L), "จ่ายบิล", "10/03/13", "True 006", "111111111") },
				{ new Activity(2L, "billpay", df.parse("10-03-2013"), "d.tlp", "111111111"), new ActivityViewItem(String.valueOf(2L), "จ่ายบิล", "10/03/13", "Truelifeplus", "111111111") },
				{ new Activity(2L, "billpay", df.parse("10-03-2013"), "d.tcg", "111111111"), new ActivityViewItem(String.valueOf(2L), "จ่ายบิล", "10/03/13", "บิลกลุ่มทรู", "111111111") },
				{ new Activity(2L, "bonus", df.parse("10-03-2013"), "promo_direct_debit", "add_money"), new ActivityViewItem(String.valueOf(2L), "โปรโมชั่น", "10/03/13", "คืนค่าธรรมเนียม", "เติมเงินด้วยบัญชีธนาคาร") },
				{ new Activity(2L, "add_money", df.parse("10-03-2013"), "debit", "KTB"), new ActivityViewItem(String.valueOf(2L), "เติมเงิน Wallet", "10/03/13", "บัญชีธนาคาร", "ธนาคารกรุงไทย") },
				{ new Activity(2L, "add_money", df.parse("10-03-2013"), "debit", "SCB"), new ActivityViewItem(String.valueOf(2L), "เติมเงิน Wallet", "10/03/13", "บัญชีธนาคาร", "ธนาคารไทยพาณิชย์") },
				{ new Activity(2L, "add_money", df.parse("10-03-2013"), "debit", "BBL"), new ActivityViewItem(String.valueOf(2L), "เติมเงิน Wallet", "10/03/13", "บัญชีธนาคาร", "ธนาคารกรุงเทพ") },
				{ new Activity(2L, "add_money", df.parse("10-03-2013"), "debit", "BAY"), new ActivityViewItem(String.valueOf(2L), "เติมเงิน Wallet", "10/03/13", "บัญชีธนาคาร", "ธนาคารกรุงศรีอยุธยา") },
				{ new Activity(2L, "transfer", df.parse("10-03-2013"), "debtor", "0812345678"), new ActivityViewItem(String.valueOf(2L), "โอนเงินระหว่าง Wallet", "10/03/13", "ส่งเงิน", "081-234-5678") },
				{ new Activity(2L, "transfer", df.parse("10-03-2013"), "creditor", "0812345678"), new ActivityViewItem(String.valueOf(2L), "โอนเงิน Wallet", "10/03/13", "รับเงิน", "081-234-5678") }
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
	
	@SuppressWarnings("unchecked")
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
