package th.co.truemoney.product.api.controller;

import java.util.Arrays;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import th.co.truemoney.product.api.handler.BillPayActivityDetailViewHandler;
import th.co.truemoney.product.api.manager.BillConfigurationManager;
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
				{ new BillTest("rft", "", "", "12345", "Account/Mobile Number", "รหัสลูกค้า/หมายเลขโทรศัพท์", "54321", "invoice number", "เลขที่ใบแจ้งค่าบริการ") },
				{ new BillTest("bla", "bla", "กรุงเทพประกันชีวิต", "12345", "Ref. No. 1", "เลขที่อ้างอิง 1", "54321", "Ref. No. 2", "เลขที่อ้างอิง 2") },
				{ new BillTest("tqm", "tqm", "ทีคิวเอ็ม ประกันภัย", "12345", "Ref. No. 1", "เลขที่อ้างอิง 1", "54321", "Ref. No. 2", "เลขที่อ้างอิง 2") },
				{ new BillTest("cigna", "cigna", "ซิกน่า ประกันภัย", "12345", "Customer Number", "รหัสลูกค้า", "54321", "Reference Number", "เลขที่อ้างอิง") },
				{ new BillTest("mitt", "mitt", "มิตรแท้ประกันภัย", "12345", "Ref. No. 1", "เลขที่ลูกค้า/รหัสผู้ให้บริการ", "54321", "Ref. No. 2", "กรมธรรม์เลขที่/สลักหลังเลขที่") },
				{ new BillTest("sg", "sg", "เอส 11 กรุ๊ป", "12345", "Customer Number", "รหัสลูกค้า", "54321", "Reference No", "หมายเลขอ้างอิง") },
				{ new BillTest("glc", "glc", "สินเชื่อบริษัท กรุ๊ปลีส", "12345", "Customer Number", "รหัสลูกค้า", "54321", "Reference No", "หมายเลขอ้างอิง") },
				{ new BillTest("gepc", "gepc", "สินเชื่อบุคคล อยุธยา แคปปิตอล เซอร์วิสเซส", "12345", "", "เลขที่อ้างอิง 1") },
				{ new BillTest("qc", "qc", "สินเชื่อเงินสดควิกแคช", "12345", "REF1", "หมายเลขสมาชิก") },
				{ new BillTest("tescopl", "tescopl", "สินเชื่อเทสโก้ เพอร์ซัลนอลโลน", "12345", "Account Number", "หมายเลขสมาชิก") },
				{ new BillTest("tesco", "tesco", "บัตรเครดิตเทสโก้ โลตัส", "12345", "Account Number", "หมายเลขบัตร") },
				{ new BillTest("centralpl", "centralpl", "เซ็นทรัล เอ็กซ์คลูซีฟ แคช", "12345", "Account Number", "หมายเลขบัญชี") },
				{ new BillTest("central", "central", "เซ็นทรัล เครดิตคาร์ด", "12345", "", "หมายเลขบัญชี") }, 
				{ new BillTest("robinson", "robinson", "ซิมเพิล วีซ่า คาร์ด", "12345", "Account Number", "หมายเลขบัญชี") },
				{ new BillTest("dream", "dream", "กรุงศรี ดรีมโลน", "12345", "Loan Number", "หมายเลขสมาชิก") },
				{ new BillTest("hp", "hp", "บัตรเครดิตโฮมโปร", "************1234", "Card Number", "หมายเลขบัตร") },
				{ new BillTest("cal", "cal", "เซ็นเตอร์ ออโต้ลิสซิ่ง", "1234567890", "", "รหัสลูกค้า", "0987654321", "", "เลขที่สัญญา") },
				{ new BillTest("ask", "ask", "เอเซียเสริมกิจลีสซิ่ง", "12345", "", "หมายเลขผู้เช่าซื้อ", "54321", "", "เลขที่สัญญา + ประเภทบริการ") },
				{ new BillTest("scal", "scal", "ซัมมิท แคปปิตอล ลีสซิ่ง", "1234567890", "Ref. No. 1", "สัญญาเลขที่", "0987654321", "Ref. No. 2", "เลขที่อ้างอิง") },
				{ new BillTest("cimbex", "cimbex", "เอ็กซ์ตร้าแคช ซีไอเอ็มบี ไทย", "12345", "", "เลขที่บัญชี") },
				{ new BillTest("cimbpe", "cimbpe", "เพอร์ซันนัลแคช ซีไอเอ็มบี ไทย", "12345", "", "เลขที่บัญชี", "54321", "Ref. 2", "วงเงินกู้") },
				{ new BillTest("tqmlife", "tqmlife", "ทีคิวเอ็ม ประกันชีวิต", "12345", "Ref. No. 1", "เลขที่อ้างอิง 1", "54321", "Ref. No. 2", "เลขที่อ้างอิง 2") },
				{ new BillTest("bkip", "bkip", "กรุงเทพประกันภัย", "12345", "Policy Number", "กรมธรรม์เลขที่", "54321", "", "เลขที่อ้างอิง 2") },
				{ new BillTest("ing", "ing", "ไอเอ็นจี ประกันชีวิต", "12345", "Ref. No. 1", "หมายเลขอ้างอิง 1", "54321", "Ref. No. 2", "หมายเลขอ้างอิง 2") },
				{ new BillTest("mti", "mti", "เมืองไทยประกันภัย", "12345", "Proposal ID", "เลขที่ใบคำขอ", "54321", "Ref. No. 2", "เลขที่ตัวแทน") },
				{ new BillTest("mthai", "mthai", "เมืองไทยประกันชีวิต", "12345", "Policy Number", "เลขที่กรมธรรม์") },
				{ new BillTest("hiway", "hiway", "ไฮเวย์", "12345", "Ref. No. 1", "เลขที่อ้างอิง", "54321", "Ref. No. 2", "สัญญาเลขที่") },
				{ new BillTest("tli", "tli", "ไทยประกันชีวิต", "12345", "Ref. No. 1/Cust. No.", "Ref. No. 1/Cust. No.", "54321", "Ref. No. 2", "Ref. No. 2") },
				{ new BillTest("dlt", "dlt", "ภาษีรถยนต์", "12345", "", "เลขใบแจ้งชำระค่าภาษี", "54321", "", "หมายเลขอ้างอิง") },
				{ new BillTest("hp", "hp", "บัตรเครดิตโฮมโปร", "************1234", "Card Number", "หมายเลขบัตร") },
				{ new BillTest("central", "central", "เซ็นทรัล เครดิตคาร์ด", "************1234", "", "หมายเลขบัญชี") },
				{ new BillTest("bblc", "bblc", "บัตรเครดิตธนาคารกรุงเทพ", "************1234", "Account Number", "หมายเลขบัตรเครดิต") },
				{ new BillTest("tisco", "tisco", "ธนาคารทิสโก้", "12345", "Ref. 1", "เลขที่อ้างอิง", "54321", "Ref. 2", "สัญญาเลขที่") },
				{ new BillTest("ghb", "ghb", "ธนาคารอาคารสงเคราะห์", "12345", "Account Number", "เลขที่บัญชีเงินกู้") },
				{ new BillTest("ktc", "ktc", "KTC", "************1234", "Account Number", "หมายเลขบัตรเครดิต/หมายเลขสมาชิก") }, 
				{ new BillTest("aeon", "aeon", "อิออน", "12345", "Ref. 1", "เลขที่อ้างอิง 1") },
				{ new BillTest("fc", "fc", "กรุงศรีเฟิร์สชอยส์", "************1234", "Account Number", "หมายเลขบัตรเครดิต") },
				{ new BillTest("kcc", "kcc", "บัตรเครดิตกรุงศรี", "************1234", "Account Number", "หมายเลขบัตรเครดิต") },
				{ new BillTest("pb", "pb", "บัตรเพาเวอร์บาย", "************1234", "Account Number", "หมายเลขบัตรเครดิต") },
				{ new BillTest("uob", "uob", "บัตรเครดิตธนาคารยูโอบี", "************1234", "Card Number", "หมายเลขบัตร") },
				{ new BillTest("bwc", "bwc", "เอ็ม พาวเวอร์", "12345", "Customer Number", "รหัสสาวจำหน่าย", "54321", "Bill Number", "เลขที่ใบส่งของ") },
				{ new BillTest("mistine", "mistine", "มิสทีน", "12345", "Customer Number", "รหัสสาวจำหน่าย", "54321", "Bill Number", "เลขที่ใบส่งของ") }
		};
		return Arrays.asList(data);
	}

	@Before
	public void setup() {
		handler = new BillPayActivityDetailViewHandler();
		handler.setOnlineReourceManager(new OnlineResourceManager());
		handler.setConfigurationManager(new BillConfigurationManager());
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
		return String.format(sec1FormatStr, bill.getAction(), bill.getTitleTh(), bill.getTitleEn());
	}
    
}

class BillTest {
	String action;
	String titleEn;
	String titleTh;
	String ref1;
	String ref1TitleEn;
	String ref1TitleTh;
	String ref2;
	String ref2TitleEn;
	String ref2TitleTh;
	
	public BillTest(String action, String titleEn, String titleTh, 
			String ref1, String ref1TitleEn, String ref1TitleTh, 
			String ref2, String ref2TitleEn, String ref2TitleTh) {
		super();
		this.action = action;
		this.titleEn = titleEn;
		this.titleTh = titleTh;
		this.ref1 = ref1;
		this.ref1TitleEn = ref1TitleEn;
		this.ref1TitleTh = ref1TitleTh;
		this.ref2 = ref2;
		this.ref2TitleEn = ref2TitleEn;
		this.ref2TitleTh = ref2TitleTh;
	}
	public BillTest(String action, String titleEn, String titleTh, 
			String ref1, String ref1TitleEn, String ref1TitleTh) {
		super();
		this.action = action;
		this.titleEn = titleEn;
		this.titleTh = titleTh;
		this.ref1 = ref1;
		this.ref1TitleEn = ref1TitleEn;
		this.ref1TitleTh = ref1TitleTh;
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
	public String getTitleEn() {
		return titleEn;
	}
	public void setTitleEn(String titleEn) {
		this.titleEn = titleEn;
	}
}
