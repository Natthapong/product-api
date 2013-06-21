package th.co.truemoney.product.api.controller;

import java.util.Arrays;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import th.co.truemoney.product.api.handler.BillPayActivityDetailViewHandler;
import th.co.truemoney.product.api.manager.OnlineResourceManager;
import th.co.truemoney.serviceinventory.ewallet.domain.ActivityDetail;

import static org.junit.Assert.assertEquals;

@RunWith(value=Parameterized.class)
public class TestBillPayActivityDetailViewHandler {

	BillPayActivityDetailViewHandler handler;
	
	BillTest bill;

	public TestBillPayActivityDetailViewHandler(BillTest bill) {
		this.bill = bill;
	}

	@Parameters
	public static Iterable<BillTest[]> data() {
		BillTest[][] data = new BillTest[][] {
				{ new BillTest("hp", "บัตรเครดิตโฮมโปร", "************1234", "Card Number", "หมายเลขบัตร", null, null, null) },
				{ new BillTest("central", "เซ็นทรัล เครดิตคาร์ด", "************1234", "", "เลขที่บัตรเครดิต (16หลัก)", null, null, null) },
				{ new BillTest("scal", "ซัมมิท แคปปิตอล ลีสซิ่ง", "1234567890", "Ref. No. 1", "สัญญาเลขที่", "0987654321", "Ref. No. 2", "เลขที่อ้างอิง") }
		};
		return Arrays.asList(data);
	}

	@Before
	public void setup() {
		handler = new BillPayActivityDetailViewHandler();
		handler.setOnlineReourceManager(new OnlineResourceManager());
	}
    
	@Test
	public void testBill() {
		ActivityDetail activity = new ActivityDetail();
		activity.setAction(bill.getAction());
		activity.setRef1(bill.getRef1());
		activity.setRef2(bill.getRef2());
		
		handler.handle(activity);
		
		//section1
		Map<String, String> sec1 = handler.buildSection1();
		assertEquals(section1String(), sec1.toString());
		
		//section2
		Map<String, Object> sec2 = handler.buildSection2();
		assertEquals(section2String(), sec2.toString());
		
		//section3
		Map<String, Object> sec3 = handler.buildSection3();
		assertEquals(section3String(), sec3.toString());
		
		//section4
		Map<String, Object> sec4 = handler.buildSection4();
		assertEquals(section4String(), sec4.toString());
	}

	private static final String sec1FormatStr = "{logoURL=null/m/tmn_webview/images/logo_bill/%s@2x.png, titleTh=%s, titleEn=%s}";
	private static final String sec2FormatStrRef1Ref2 = "{column1={cell2={titleTh=%s, titleEn=%s, value=%s}, cell1={titleTh=%s, titleEn=%s, value=%s}}}";
	private static final String sec2FormatStrRef1 = "{column1={cell1={titleTh=%s, titleEn=%s, value=%s}}}";
	private static final String sec3FormatStr = "{column1={cell2={titleTh=%s, titleEn=%s, value=%s}, cell1={titleTh=%s, titleEn=%s, value=%s}}, column2={cell1={titleTh=%s, titleEn=%s, value=%s}}}";
	private static final String sec4FormatStr = "{column1={cell1={titleTh=%s, titleEn=%s, value=%s}}, column2={cell1={titleTh=%s, titleEn=%s, value=%s}}}";
	
	private String section4String() {
		return String.format(sec4FormatStr, "วันที่-เวลา", "transaction date", "", "เลขที่อ้างอิง", "transaction ID", "null");
	}

	private String section3String() {
		return String.format(sec3FormatStr, "รวมเงินที่ชำระ", "total amount", "0.00", "จำนวนเงิน", "amount", "0.00", "ค่าธรรมเนียม", "total fee", "0.00");
	}
	
	private String section2String() {
		if (bill.getRef2() != null) {
			return String.format(sec2FormatStrRef1Ref2, bill.getRef2TitleTh(), bill.getRef2TitleEn(), bill.getRef2(), bill.getRef1TitleTh(), bill.getRef1TitleEn(), bill.getRef1());
		} else {
			return String.format(sec2FormatStrRef1, bill.getRef1TitleTh(), bill.getRef1TitleEn(), bill.getRef1());
		}
	}

	private String section1String() {
		return String.format(sec1FormatStr, bill.getAction(), bill.getTitleTh(), bill.getAction());
	}
    
}

class BillTest {
	String action;
	String titleTh;
	String ref1;
	String ref1TitleEn;
	String ref1TitleTh;
	String ref2;
	String ref2TitleEn;
	String ref2TitleTh;
	
	public BillTest(String action, String titleTh, String ref1,
			String ref1TitleEn, String ref1TitleTh, String ref2,
			String ref2TitleEn, String ref2TitleTh) {
		super();
		this.action = action;
		this.titleTh = titleTh;
		this.ref1 = ref1;
		this.ref1TitleEn = ref1TitleEn;
		this.ref1TitleTh = ref1TitleTh;
		this.ref2 = ref2;
		this.ref2TitleEn = ref2TitleEn;
		this.ref2TitleTh = ref2TitleTh;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getTitleTh() {
		return titleTh;
	}
	public void setTitleTh(String titleTh) {
		this.titleTh = titleTh;
	}
	public String getRef1() {
		return ref1;
	}
	public void setRef1(String ref1) {
		this.ref1 = ref1;
	}
	public String getRef1TitleEn() {
		return ref1TitleEn;
	}
	public void setRef1TitleEn(String ref1TitleEn) {
		this.ref1TitleEn = ref1TitleEn;
	}
	public String getRef1TitleTh() {
		return ref1TitleTh;
	}
	public void setRef1TitleTh(String ref1TitleTh) {
		this.ref1TitleTh = ref1TitleTh;
	}
	public String getRef2() {
		return ref2;
	}
	public void setRef2(String ref2) {
		this.ref2 = ref2;
	}
	public String getRef2TitleEn() {
		return ref2TitleEn;
	}
	public void setRef2TitleEn(String ref2TitleEn) {
		this.ref2TitleEn = ref2TitleEn;
	}
	public String getRef2TitleTh() {
		return ref2TitleTh;
	}
	public void setRef2TitleTh(String ref2TitleTh) {
		this.ref2TitleTh = ref2TitleTh;
	}
}
