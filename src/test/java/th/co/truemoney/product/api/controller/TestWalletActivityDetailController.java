package th.co.truemoney.product.api.controller;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.validation.constraints.AssertFalse;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import th.co.truemoney.product.api.domain.ProductResponse;
import th.co.truemoney.product.api.domain.WalletActivity.TYPE;
import th.co.truemoney.serviceinventory.ewallet.domain.ActivityDetail;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

public class TestWalletActivityDetailController extends
		BaseTestController {

	@Autowired
	WalletActivityController controller;

	private static final String fakeAccessTokenID = "111111";

	private String getActivityDetailURL(String reportID) {
		return String.format("/profile/activities/%s/detail/%s", reportID ,fakeAccessTokenID);
	}

	@Test
	public void getActivityDetailSuccess() throws Exception {
		ActivityDetail activity = new ActivityDetail();
		activity.setType("topup_mobile");
		when(
				this.activityServiceMock.getActivityDetail(1L,fakeAccessTokenID)
		).thenReturn(activity);

		this.verifySuccess(this.doGET(getActivityDetailURL("1")));
	}

	@Test
	public void getActivityDetailFail() throws Exception {
		when(this.activityServiceMock.getActivityDetail(2L,fakeAccessTokenID))
				.thenThrow(new ServiceInventoryException(400,"","",""));
		this.verifyFailed(this.doGET(getActivityDetailURL("2")));
	}

	@Test
	public void getActivityPersonalMessageSuccess() throws Exception
	{
		ActivityDetail activity = new ActivityDetail();
		activity.setType(TYPE.TRANSFER.name());
		activity.setPersonalMessage("Hello World");
		
		when(this.activityServiceMock.getActivityDetail(3L, fakeAccessTokenID)).thenReturn(activity);
		
		ProductResponse resp = controller.getActivityDetails(String.valueOf(3L), fakeAccessTokenID);
		Map<String,Object> respData = resp.getData();
		assertTrue(respData.toString().toString().contains("Hello World"));
	}
	
	@Test
	public void getActivityPersonalMessageFail() throws Exception
	{
		ActivityDetail activity = new ActivityDetail();
		activity.setType(TYPE.TOPUP_MOBILE.name());
		activity.setPersonalMessage("Hello World");
		
		when(this.activityServiceMock.getActivityDetail(3L, fakeAccessTokenID)).thenReturn(activity);
		
		ProductResponse resp = controller.getActivityDetails(String.valueOf(3L), fakeAccessTokenID);
		Map<String,Object> respData = resp.getData();
		assertNull(respData.get("personalMessage"));
		
		
	}	
	
	@Test
	public void getTopupTMHActivityDetails() throws Exception{

		Date txnDate = new SimpleDateFormat("yyyy/MM/dd HH:mm").parse("2013/02/10 15:35");

		ActivityDetail detail = new ActivityDetail();
		detail.setType(TYPE.TOPUP_MOBILE.name());
		detail.setAction("tmhtopup_c");
		detail.setRef1("0897654333");
		detail.setAmount(new BigDecimal(23455.50));
		detail.setServiceFee(new BigDecimal(1234.50));
		detail.setTransactionDate(txnDate);
		detail.setTransactionID("1234567890");

		when(this.activityServiceMock.getActivityDetail(3L, fakeAccessTokenID)).thenReturn(detail);

		ProductResponse resp = controller.getActivityDetails(String.valueOf(3L), fakeAccessTokenID);
		Map<String, Object> data = resp.getData();

		assertEquals("{logoURL=http://localhost:8080/images/logo_bill/tmvh@2x.png, titleTh=, titleEn=}", data.get("section1").toString());
		assertEquals("{column1={cell1={titleTh=หมายเลขโทรศัพท์, titleEn=mobile number, value=089-765-4333}}}", data.get("section2").toString());
		assertEquals("{column1={cell2={titleTh=รวมเงินที่ชำระ, titleEn=total amount, value=24,690.00}, cell1={titleTh=จำนวนเงิน, titleEn=amount, value=23,455.50}}, column2={cell1={titleTh=ค่าธรรมเนียม, titleEn=total fee, value=1,234.50}}}", data.get("section3").toString());
		assertEquals("{column1={cell1={titleTh=วันที่-เวลา, titleEn=transaction date, value=10/02/13 15:35}}, column2={cell1={titleTh=เลขที่อ้างอิง, titleEn=transaction ID, value=1234567890}}}", data.get("section4").toString());
		
	}
	
	@Test
	public void getTopupTMVActivityDetails() throws Exception{

		Date txnDate = new SimpleDateFormat("yyyy/MM/dd HH:mm").parse("2013/02/10 15:35");

		ActivityDetail detail = new ActivityDetail();
		detail.setType(TYPE.TOPUP_MOBILE.name());
		detail.setAction("tmvtopup_c");
		detail.setRef1("0897654333");
		detail.setAmount(new BigDecimal(23455.50));
		detail.setServiceFee(new BigDecimal(1234.50));
		detail.setTransactionDate(txnDate);
		detail.setTransactionID("1234567890");

		when(this.activityServiceMock.getActivityDetail(3L, fakeAccessTokenID)).thenReturn(detail);

		ProductResponse resp = controller.getActivityDetails(String.valueOf(3L), fakeAccessTokenID);
		Map<String, Object> data = resp.getData();
		
		assertEquals("{logoURL=http://localhost:8080/images/logo_bill/trmv@2x.png, titleTh=, titleEn=}", data.get("section1").toString());
		assertEquals("{column1={cell1={titleTh=หมายเลขโทรศัพท์, titleEn=mobile number, value=089-765-4333}}}", data.get("section2").toString());
		assertEquals("{column1={cell2={titleTh=รวมเงินที่ชำระ, titleEn=total amount, value=24,690.00}, cell1={titleTh=จำนวนเงิน, titleEn=amount, value=23,455.50}}, column2={cell1={titleTh=ค่าธรรมเนียม, titleEn=total fee, value=1,234.50}}}", data.get("section3").toString());
		assertEquals("{column1={cell1={titleTh=วันที่-เวลา, titleEn=transaction date, value=10/02/13 15:35}}, column2={cell1={titleTh=เลขที่อ้างอิง, titleEn=transaction ID, value=1234567890}}}", data.get("section4").toString());
	}

	
	@Test
	public void getBillPayActivityDetails() throws Exception {
		Date txnDate = new SimpleDateFormat("yyyy/MM/dd HH:mm").parse("2013/02/10 15:35");

		ActivityDetail detail = new ActivityDetail();
		detail.setType(TYPE.BILLPAY.name());
		detail.setAction("ti_c");
		detail.setRef1("864895245");
		detail.setRef2("923178945372901");
		detail.setAmount(new BigDecimal(23455.50));
		detail.setServiceFee(new BigDecimal(1234.50));
		detail.setTransactionDate(txnDate);
		detail.setTransactionID("1234567890");

		when(this.activityServiceMock.getActivityDetail(4L, fakeAccessTokenID)).thenReturn(detail);

		ProductResponse resp = controller.getActivityDetails(String.valueOf(4L), fakeAccessTokenID);
		Map<String, Object> data = resp.getData();

		assertEquals("{logoURL=http://localhost:8080/images/logo_bill/ti@2x.png, titleTh=, titleEn=}", data.get("section1").toString());
		assertEquals("{column1={cell2={titleTh=เลขที่ใบแจ้งค่าบริการ, titleEn=invoice number, value=923178945372901}, cell1={titleTh=รหัสลูกค้า/หมายเลขโทรศัพท์, titleEn=Telephone Number/Customer Number, value=864895245}}}", data.get("section2").toString());
		assertEquals("{column1={cell2={titleTh=รวมเงินที่ชำระ, titleEn=total amount, value=24,690.00}, cell1={titleTh=จำนวนเงิน, titleEn=amount, value=23,455.50}}, column2={cell1={titleTh=ค่าธรรมเนียม, titleEn=total fee, value=1,234.50}}}", data.get("section3").toString());
		assertEquals("{column1={cell1={titleTh=วันที่-เวลา, titleEn=transaction date, value=10/02/13 15:35}}, column2={cell1={titleTh=เลขที่อ้างอิง, titleEn=transaction ID, value=1234567890}}}", data.get("section4").toString());
	}
	
	@Test
	public void getBillPayTMVHActivityDetails() throws Exception {
		Date txnDate = new SimpleDateFormat("yyyy/MM/dd HH:mm").parse("2013/02/10 15:35");

		ActivityDetail detail = new ActivityDetail();
		detail.setType(TYPE.BILLPAY.name());
		detail.setAction("tmvh_c");
		detail.setRef1("0891234567");
		detail.setRef2("");
		detail.setAmount(new BigDecimal(23455.50));
		detail.setServiceFee(new BigDecimal(1234.50));
		detail.setTransactionDate(txnDate);
		detail.setTransactionID("1234567890");

		when(this.activityServiceMock.getActivityDetail(4L, fakeAccessTokenID)).thenReturn(detail);

		ProductResponse resp = controller.getActivityDetails(String.valueOf(4L), fakeAccessTokenID);
		Map<String, Object> data = resp.getData();

		assertEquals("{logoURL=http://localhost:8080/images/logo_bill/tmvh@2x.png, titleTh=, titleEn=}", data.get("section1").toString());
		assertEquals("{column1={cell1={titleTh=รหัสลูกค้า/หมายเลขโทรศัพท์, titleEn=Mobile Number, value=089-123-4567}}}", data.get("section2").toString());
		assertEquals("{column1={cell2={titleTh=รวมเงินที่ชำระ, titleEn=total amount, value=24,690.00}, cell1={titleTh=จำนวนเงิน, titleEn=amount, value=23,455.50}}, column2={cell1={titleTh=ค่าธรรมเนียม, titleEn=total fee, value=1,234.50}}}", data.get("section3").toString());
		assertEquals("{column1={cell1={titleTh=วันที่-เวลา, titleEn=transaction date, value=10/02/13 15:35}}, column2={cell1={titleTh=เลขที่อ้างอิง, titleEn=transaction ID, value=1234567890}}}", data.get("section4").toString());
	}
	
	@Test
	public void getBillPayTRActivityDetails() throws Exception {
		Date txnDate = new SimpleDateFormat("yyyy/MM/dd HH:mm").parse("2013/02/10 15:35");

		ActivityDetail detail = new ActivityDetail();
		detail.setType(TYPE.BILLPAY.name());
		detail.setAction("tr_c");
		detail.setRef1("020000000");
		detail.setRef2("");
		detail.setAmount(new BigDecimal(23455.50));
		detail.setServiceFee(new BigDecimal(1234.50));
		detail.setTransactionDate(txnDate);
		detail.setTransactionID("1234567890");

		when(this.activityServiceMock.getActivityDetail(4L, fakeAccessTokenID)).thenReturn(detail);

		ProductResponse resp = controller.getActivityDetails(String.valueOf(4L), fakeAccessTokenID);
		Map<String, Object> data = resp.getData();
		
		assertEquals("{logoURL=http://localhost:8080/images/logo_bill/tr@2x.png, titleTh=, titleEn=}", data.get("section1").toString());
		assertEquals("{column1={cell1={titleTh=เลขที่อ้างอิง 1/หมายเลขโทรศัพท์, titleEn=Telephone Number, value=02-000-0000}}}", data.get("section2").toString());
		assertEquals("{column1={cell2={titleTh=รวมเงินที่ชำระ, titleEn=total amount, value=24,690.00}, cell1={titleTh=จำนวนเงิน, titleEn=amount, value=23,455.50}}, column2={cell1={titleTh=ค่าธรรมเนียม, titleEn=total fee, value=1,234.50}}}", data.get("section3").toString());
		assertEquals("{column1={cell1={titleTh=วันที่-เวลา, titleEn=transaction date, value=10/02/13 15:35}}, column2={cell1={titleTh=เลขที่อ้างอิง, titleEn=transaction ID, value=1234567890}}}", data.get("section4").toString());
	}
	
	@Test
	public void getBillPayActivityDetailsRef2Empty() throws Exception {
		Date txnDate = new SimpleDateFormat("yyyy/MM/dd HH:mm").parse("2013/02/10 15:35");

		ActivityDetail detail = new ActivityDetail();
		detail.setType(TYPE.BILLPAY.name());
		detail.setAction("ti_c");
		detail.setRef1("864895245");
		detail.setRef2("");
		detail.setAmount(new BigDecimal(23455.50));
		detail.setServiceFee(new BigDecimal(1234.50));
		detail.setTransactionDate(txnDate);
		detail.setTransactionID("1234567890");

		when(this.activityServiceMock.getActivityDetail(4L, fakeAccessTokenID)).thenReturn(detail);

		ProductResponse resp = controller.getActivityDetails(String.valueOf(4L), fakeAccessTokenID);
		Map<String, Object> data = resp.getData();
		
		assertEquals("{logoURL=http://localhost:8080/images/logo_bill/ti@2x.png, titleTh=, titleEn=}", data.get("section1").toString());
		assertEquals("{column1={cell1={titleTh=รหัสลูกค้า/หมายเลขโทรศัพท์, titleEn=Telephone Number/Customer Number, value=864895245}}}", data.get("section2").toString());
		assertEquals("{column1={cell2={titleTh=รวมเงินที่ชำระ, titleEn=total amount, value=24,690.00}, cell1={titleTh=จำนวนเงิน, titleEn=amount, value=23,455.50}}, column2={cell1={titleTh=ค่าธรรมเนียม, titleEn=total fee, value=1,234.50}}}", data.get("section3").toString());
		assertEquals("{column1={cell1={titleTh=วันที่-เวลา, titleEn=transaction date, value=10/02/13 15:35}}, column2={cell1={titleTh=เลขที่อ้างอิง, titleEn=transaction ID, value=1234567890}}}", data.get("section4").toString());
	}
	
	@Test
	public void getBillPayActivityDetailsRef2Dash() throws Exception {
		Date txnDate = new SimpleDateFormat("yyyy/MM/dd HH:mm").parse("2013/02/10 15:35");

		ActivityDetail detail = new ActivityDetail();
		detail.setType(TYPE.BILLPAY.name());
		detail.setAction("ti_c");
		detail.setRef1("864895245");
		detail.setRef2(" - ");
		detail.setAmount(new BigDecimal(23455.50));
		detail.setServiceFee(new BigDecimal(1234.50));
		detail.setTransactionDate(txnDate);
		detail.setTransactionID("1234567890");

		when(this.activityServiceMock.getActivityDetail(4L, fakeAccessTokenID)).thenReturn(detail);

		ProductResponse resp = controller.getActivityDetails(String.valueOf(4L), fakeAccessTokenID);
		Map<String, Object> data = resp.getData();
		
		assertEquals("{logoURL=http://localhost:8080/images/logo_bill/ti@2x.png, titleTh=, titleEn=}", data.get("section1").toString());
		assertEquals("{column1={cell1={titleTh=รหัสลูกค้า/หมายเลขโทรศัพท์, titleEn=Telephone Number/Customer Number, value=864895245}}}", data.get("section2").toString());
		assertEquals("{column1={cell2={titleTh=รวมเงินที่ชำระ, titleEn=total amount, value=24,690.00}, cell1={titleTh=จำนวนเงิน, titleEn=amount, value=23,455.50}}, column2={cell1={titleTh=ค่าธรรมเนียม, titleEn=total fee, value=1,234.50}}}", data.get("section3").toString());
		assertEquals("{column1={cell1={titleTh=วันที่-เวลา, titleEn=transaction date, value=10/02/13 15:35}}, column2={cell1={titleTh=เลขที่อ้างอิง, titleEn=transaction ID, value=1234567890}}}", data.get("section4").toString());
	}
	
	@Test
	public void getBonusActivityDetails() throws Exception {
		Date txnDate = new SimpleDateFormat("yyyy/MM/dd HH:mm").parse("2013/02/10 15:35");

		ActivityDetail detail = new ActivityDetail();
		detail.setType(TYPE.BONUS.name());
		detail.setAction("promo_direct_debit");
		detail.setRef1("add_money");
		detail.setAmount(new BigDecimal(23455.50));
		detail.setTransactionDate(txnDate);
		detail.setTransactionID("1234567890");

		when(this.activityServiceMock.getActivityDetail(5L, fakeAccessTokenID)).thenReturn(detail);

		ProductResponse resp = controller.getActivityDetails(String.valueOf(5L), fakeAccessTokenID);
		Map<String, Object> data = resp.getData();
		
		assertEquals("{titleTh=คืนค่าธรรมเนียม, titleEn=kickback}", data.get("section1").toString());
		assertEquals("{column1={cell1={titleTh=ทำรายการ, titleEn=activity, value=เติมเงินด้วยบัญชีธนาคาร}}}", data.get("section2").toString());
		assertEquals("{column1={cell1={titleTh=ยอดเงินเข้า Wallet, titleEn=total amount, value=23,455.50}}}", data.get("section3").toString());
		assertEquals("{column1={cell1={titleTh=วันที่-เวลา, titleEn=transaction date, value=10/02/13 15:35}}, column2={cell1={titleTh=เลขที่อ้างอิง, titleEn=transaction ID, value=1234567890}}}", data.get("section4").toString());
	}
	
	@Test
	public void getAddMoneyActivityDetailsByIOSApp() throws Exception {
		Date txnDate = new SimpleDateFormat("yyyy/MM/dd HH:mm").parse("2013/02/10 15:35");

		ActivityDetail detail = new ActivityDetail();
		detail.setType(TYPE.ADD_MONEY.name());
		detail.setAction("debit");
		detail.setRef1("KTB");
		detail.setRef2("***7412");
		detail.setAmount(new BigDecimal(23455.50));
		detail.setServiceFee(new BigDecimal(1234.50));
		detail.setTransactionDate(txnDate);
		detail.setTransactionID("1234567890");
		detail.setChannel(new Long(40));

		when(this.activityServiceMock.getActivityDetail(6L, fakeAccessTokenID)).thenReturn(detail);

		ProductResponse resp = controller.getActivityDetails(String.valueOf(6L), fakeAccessTokenID);
		Map<String, Object> data = resp.getData();
		
		assertEquals("{logoURL=http://localhost:8080/images/logo_bank/ktb@2x.png, titleTh=บัญชีธนาคาร, titleEn=bank account}", data.get("section1").toString());
		assertEquals("{column1={cell2={titleTh=หมายเลขบัญชี, titleEn=account number, value=***7412}, cell1={titleTh=ธนาคาร, titleEn=bank name, value=ธนาคารกรุงไทย}}}", data.get("section2").toString());
		assertEquals("{column1={cell1={titleTh=ยอดเงินเข้า Wallet, titleEn=total amount, value=24,690.00}}}", data.get("section3").toString());
		assertEquals("{column1={cell1={titleTh=วันที่-เวลา, titleEn=transaction date, value=10/02/13 15:35}}, column2={cell1={titleTh=เลขที่อ้างอิง, titleEn=transaction ID, value=1234567890}}}", data.get("section4").toString());
	}
	
	@Test
	public void getAddMoneyActivityDetailsByTMCC() throws Exception {
		Date txnDate = new SimpleDateFormat("yyyy/MM/dd HH:mm").parse("2013/02/10 15:35");

		ActivityDetail detail = new ActivityDetail();
		detail.setType(TYPE.ADD_MONEY.name());
		detail.setAction("tmcc");
		detail.setRef1("12345678901234");
		detail.setRef2("");
		detail.setAmount(new BigDecimal(139.50));
		detail.setSourceOfFundFee(new BigDecimal(10.50));
		detail.setServiceFee(new BigDecimal(0));
		detail.setTransactionDate(txnDate);
		detail.setTransactionID("1234567890");
		detail.setChannel(new Long("40"));

		when(this.activityServiceMock.getActivityDetail(6L, fakeAccessTokenID)).thenReturn(detail);

		ProductResponse resp = controller.getActivityDetails(String.valueOf(6L), fakeAccessTokenID);
		Map<String, Object> data = resp.getData();
		
		assertEquals("{titleTh=บัตรเงินสดทรูมันนี่, titleEn=True Money Cash Card}", data.get("section1").toString());
		assertEquals("{column1={cell1={titleTh=serial บัตรเงินสด, titleEn=serial number, value=12345678901234}}}", data.get("section2").toString());
		assertEquals("{column1={cell2={titleTh=ยอดเงินเข้า Wallet, titleEn=amount, value=139.50}, cell1={titleTh=จำนวนเงิน, titleEn=total amount, value=150.00}}, column2={cell1={titleTh=ค่าธรรมเนียม, titleEn=total fee, value=10.50}}}", data.get("section3").toString());
		assertEquals("{column1={cell1={titleTh=วันที่-เวลา, titleEn=transaction date, value=10/02/13 15:35}}, column2={cell1={titleTh=เลขที่อ้างอิง, titleEn=transaction ID, value=1234567890}}}", data.get("section4").toString());
	
	}
	
	@Test
	public void getAddMoneyActivityDetailsByKiosk() throws Exception {
		Date txnDate = new SimpleDateFormat("yyyy/MM/dd HH:mm").parse("2013/02/10 15:35");

		ActivityDetail detail = new ActivityDetail();
		detail.setType(TYPE.ADD_MONEY.name());
		detail.setAction("cash");
		detail.setRef1("12345678901234");
		detail.setRef2("");
		detail.setAmount(new BigDecimal(2000.00));
		detail.setSourceOfFundFee(new BigDecimal(0));
		detail.setServiceFee(new BigDecimal(0));
		detail.setTransactionDate(txnDate);
		detail.setTransactionID("1234567890");
		detail.setChannel(new Long("33"));

		when(this.activityServiceMock.getActivityDetail(6L, fakeAccessTokenID)).thenReturn(detail);

		ProductResponse resp = controller.getActivityDetails(String.valueOf(6L), fakeAccessTokenID);
		Map<String, Object> data = resp.getData();

		assertEquals("{titleTh=True Money Kiosk, titleEn=True Money Kiosk}", data.get("section1").toString());
		assertEquals("{column1={cell1={titleTh=ยอดเงินเข้า Wallet, titleEn=total amount, value=2,000.00}}}", data.get("section2").toString());
		assertEquals("{column1={cell1={titleTh=วันที่-เวลา, titleEn=transaction date, value=10/02/13 15:35}}, column2={cell1={titleTh=เลขที่อ้างอิง, titleEn=transaction ID, value=1234567890}}}", data.get("section3").toString());
		assertFalse(data.containsKey("section4"));
		
	}

	@Test
	public void getAddMoneyActivityDetailsByCPFreshMart() throws Exception {
		Date txnDate = new SimpleDateFormat("yyyy/MM/dd HH:mm").parse("2013/02/10 15:35");

		ActivityDetail detail = new ActivityDetail();
		detail.setType(TYPE.ADD_MONEY.name());
		detail.setAction("cash");
		detail.setRef1("12345678901234");
		detail.setRef2("");
		detail.setAmount(new BigDecimal(2000.00));
		detail.setSourceOfFundFee(new BigDecimal(0));
		detail.setServiceFee(new BigDecimal(0));
		detail.setTransactionDate(txnDate);
		detail.setTransactionID("1234567890");
		detail.setChannel(new Long("38"));

		when(this.activityServiceMock.getActivityDetail(6L, fakeAccessTokenID)).thenReturn(detail);

		ProductResponse resp = controller.getActivityDetails(String.valueOf(6L), fakeAccessTokenID);
		Map<String, Object> data = resp.getData();

		assertEquals("{titleTh=CP Fresh Mart, titleEn=CP Fresh Mart}", data.get("section1").toString());
		assertEquals("{column1={cell1={titleTh=หมายเลขสาขา, titleEn=หมายเลขสาขา, value=12345678901234}}}", data.get("section2").toString());
		assertEquals("{column1={cell1={titleTh=ยอดเงินเข้า Wallet, titleEn=total amount, value=2,000.00}}}", data.get("section3").toString());
		assertEquals("{column1={cell1={titleTh=วันที่-เวลา, titleEn=transaction date, value=10/02/13 15:35}}, column2={cell1={titleTh=เลขที่อ้างอิง, titleEn=transaction ID, value=1234567890}}}", data.get("section4").toString());
		
	}
	
	@Test
	public void getAddMoneyActivityDetailsByTMX() throws Exception {
		Date txnDate = new SimpleDateFormat("yyyy/MM/dd HH:mm").parse("2013/02/10 15:35");

		ActivityDetail detail = new ActivityDetail();
		detail.setType(TYPE.ADD_MONEY.name());
		detail.setAction("cash");
		detail.setRef1("12345678901234");
		detail.setRef2("");
		detail.setAmount(new BigDecimal(2000.00));
		detail.setSourceOfFundFee(new BigDecimal(0));
		detail.setServiceFee(new BigDecimal(0));
		detail.setTransactionDate(txnDate);
		detail.setTransactionID("1234567890");
		detail.setChannel(new Long("39"));

		when(this.activityServiceMock.getActivityDetail(6L, fakeAccessTokenID)).thenReturn(detail);

		ProductResponse resp = controller.getActivityDetails(String.valueOf(6L), fakeAccessTokenID);
		Map<String, Object> data = resp.getData();

		assertEquals("{titleTh=True Money Express, titleEn=True Money Express}", data.get("section1").toString());
		assertEquals("{column1={cell1={titleTh=หมายเลขร้านค้า, titleEn=หมายเลขร้านค้า, value=12345678901234}}}", data.get("section2").toString());
		assertEquals("{column1={cell1={titleTh=ยอดเงินเข้า Wallet, titleEn=total amount, value=2,000.00}}}", data.get("section3").toString());
		assertEquals("{column1={cell1={titleTh=วันที่-เวลา, titleEn=transaction date, value=10/02/13 15:35}}, column2={cell1={titleTh=เลขที่อ้างอิง, titleEn=transaction ID, value=1234567890}}}", data.get("section4").toString());
		
	}
	
	@Test
	public void getAddMoneyActivityDetailsByATM() throws Exception {
		Date txnDate = new SimpleDateFormat("yyyy/MM/dd HH:mm").parse("2013/02/10 15:35");

		ActivityDetail detail = new ActivityDetail();
		detail.setType(TYPE.ADD_MONEY.name());
		detail.setAction("cash");
		detail.setRef1("12345678901234");
		detail.setRef2("");
		detail.setAmount(new BigDecimal(2000.00));
		detail.setSourceOfFundFee(new BigDecimal(0));
		detail.setServiceFee(new BigDecimal(0));
		detail.setTransactionDate(txnDate);
		detail.setTransactionID("1234567890");
		detail.setChannel(new Long("42"));

		when(this.activityServiceMock.getActivityDetail(6L, fakeAccessTokenID)).thenReturn(detail);

		ProductResponse resp = controller.getActivityDetails(String.valueOf(6L), fakeAccessTokenID);
		Map<String, Object> data = resp.getData();

		assertEquals("{titleTh=เอทีเอ็ม, titleEn=ATM}", data.get("section1").toString());
		assertEquals("{column1={cell1={titleTh=ธนาคาร, titleEn=bank name, value=null}}}", data.get("section2").toString());
		assertEquals("{column1={cell1={titleTh=ยอดเงินเข้า Wallet, titleEn=total amount, value=2,000.00}}}", data.get("section3").toString());
		assertEquals("{column1={cell1={titleTh=วันที่-เวลา, titleEn=transaction date, value=10/02/13 15:35}}, column2={cell1={titleTh=เลขที่อ้างอิง, titleEn=transaction ID, value=1234567890}}}", data.get("section4").toString());
		
	}
	
	@Test
	public void getAddMoneyActivityDetailsByIBanking() throws Exception {
		Date txnDate = new SimpleDateFormat("yyyy/MM/dd HH:mm").parse("2013/02/10 15:35");

		ActivityDetail detail = new ActivityDetail();
		detail.setType(TYPE.ADD_MONEY.name());
		detail.setAction("cash");
		detail.setRef1("12345678901234");
		detail.setRef2("");
		detail.setAmount(new BigDecimal(2000.00));
		detail.setSourceOfFundFee(new BigDecimal(0));
		detail.setServiceFee(new BigDecimal(0));
		detail.setTransactionDate(txnDate);
		detail.setTransactionID("1234567890");
		detail.setChannel(new Long("43"));

		when(this.activityServiceMock.getActivityDetail(6L, fakeAccessTokenID)).thenReturn(detail);

		ProductResponse resp = controller.getActivityDetails(String.valueOf(6L), fakeAccessTokenID);
		Map<String, Object> data = resp.getData();

		assertEquals("{titleTh=ไอแบงก์กิ้ง, titleEn=iBanking}", data.get("section1").toString());
		assertEquals("{column1={cell1={titleTh=ธนาคาร, titleEn=bank name, value=null}}}", data.get("section2").toString());
		assertEquals("{column1={cell1={titleTh=ยอดเงินเข้า Wallet, titleEn=total amount, value=2,000.00}}}", data.get("section3").toString());
		assertEquals("{column1={cell1={titleTh=วันที่-เวลา, titleEn=transaction date, value=10/02/13 15:35}}, column2={cell1={titleTh=เลขที่อ้างอิง, titleEn=transaction ID, value=1234567890}}}", data.get("section4").toString());
		
	}
	
	@Test
	public void getAddMoneyActivityDetailsByTRM() throws Exception {
		Date txnDate = new SimpleDateFormat("yyyy/MM/dd HH:mm").parse("2013/02/10 15:35");

		ActivityDetail detail = new ActivityDetail();
		detail.setType(TYPE.ADD_MONEY.name());
		detail.setAction("cash");
		detail.setRef1("12345678901234");
		detail.setRef2("");
		detail.setAmount(new BigDecimal(2000.00));
		detail.setSourceOfFundFee(new BigDecimal(0));
		detail.setServiceFee(new BigDecimal(0));
		detail.setTransactionDate(txnDate);
		detail.setTransactionID("1234567890");
		detail.setChannel(new Long("44"));

		when(this.activityServiceMock.getActivityDetail(6L, fakeAccessTokenID)).thenReturn(detail);

		ProductResponse resp = controller.getActivityDetails(String.valueOf(6L), fakeAccessTokenID);
		Map<String, Object> data = resp.getData();

		assertEquals("{titleTh=True Shop, titleEn=True Shop}", data.get("section1").toString());
		assertEquals("{column1={cell1={titleTh=ยอดเงินเข้า Wallet, titleEn=total amount, value=2,000.00}}}", data.get("section2").toString());
		assertEquals("{column1={cell1={titleTh=วันที่-เวลา, titleEn=transaction date, value=10/02/13 15:35}}, column2={cell1={titleTh=เลขที่อ้างอิง, titleEn=transaction ID, value=1234567890}}}", data.get("section3").toString());
		assertFalse(data.containsKey("section4"));
		
	}
	
	@Test
	public void getTransferDebtorActivityDetails() throws Exception {
		Date txnDate = new SimpleDateFormat("yyyy/MM/dd HH:mm").parse("2013/02/10 15:35");

		ActivityDetail detail = new ActivityDetail();
		detail.setType(TYPE.TRANSFER.name());
		detail.setAction("debtor");
		detail.setRef1("0853828482");
		detail.setRef2("ทวี คุณบิดา");
		detail.setAmount(new BigDecimal(23455.50));
		detail.setTransactionDate(txnDate);
		detail.setTransactionID("1234567890");

		when(this.activityServiceMock.getActivityDetail(7L, fakeAccessTokenID)).thenReturn(detail);

		ProductResponse resp = controller.getActivityDetails(String.valueOf(7L), fakeAccessTokenID);
		Map<String, Object> data = resp.getData();

		assertEquals("{titleTh=ส่งเงิน, titleEn=debtor}", data.get("section1").toString());
		assertEquals("{column1={cell2={titleTh=ชื่อผู้รับ, titleEn=account owner, value=ทวี คุณบิดา}, cell1={titleTh=หมายเลขผู้รับ, titleEn=account number, value=085-382-8482}}}", data.get("section2").toString());
		assertEquals("{column1={cell1={titleTh=จำนวนเงิน, titleEn=amount, value=23,455.50}}}", data.get("section3").toString());
		assertEquals("{column1={cell1={titleTh=วันที่-เวลา, titleEn=transaction date, value=10/02/13 15:35}}, column2={cell1={titleTh=เลขที่อ้างอิง, titleEn=transaction ID, value=1234567890}}}", data.get("section4").toString());
	}
	
	@Test
	public void getTransferCreditorActivityDetails() throws Exception {
		Date txnDate = new SimpleDateFormat("yyyy/MM/dd HH:mm").parse("2013/02/10 15:35");

		ActivityDetail detail = new ActivityDetail();
		detail.setType(TYPE.TRANSFER.name());
		detail.setAction("creditor");
		detail.setRef1("0853828482");
		detail.setRef2("ทวี คุณบิดา");
		detail.setAmount(new BigDecimal(23455.50));
		detail.setTransactionDate(txnDate);
		detail.setTransactionID("1234567890");

		when(this.activityServiceMock.getActivityDetail(7L, fakeAccessTokenID)).thenReturn(detail);

		ProductResponse resp = controller.getActivityDetails(String.valueOf(7L), fakeAccessTokenID);
		Map<String, Object> data = resp.getData();
		
		assertEquals("{titleTh=รับเงิน, titleEn=creditor}", data.get("section1").toString());
		assertEquals("{column1={cell2={titleTh=ชื่อผู้ส่ง, titleEn=account owner, value=ทวี คุณบิดา}, cell1={titleTh=หมายเลขผู้ส่ง, titleEn=account number, value=085-382-8482}}}", data.get("section2").toString());
		assertEquals("{column1={cell1={titleTh=จำนวนเงิน, titleEn=amount, value=23,455.50}}}", data.get("section3").toString());
		assertEquals("{column1={cell1={titleTh=วันที่-เวลา, titleEn=transaction date, value=10/02/13 15:35}}, column2={cell1={titleTh=เลขที่อ้างอิง, titleEn=transaction ID, value=1234567890}}}", data.get("section4").toString());
	}
	
}
