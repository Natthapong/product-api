package th.co.truemoney.product.api.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Ignore;
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
		Map<String,Object> data = (Map<String,Object>)respData.get("personalMessage");
		assertEquals(data.get("value"),"Hello World");
		
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

		assertTrue(data.containsKey("section1"));
		Map<String, String> section1 = (Map<String, String>) data.get("section1");
		assertTrue(section1.containsKey("logoURL"));
		assertTrue(section1.containsKey("titleTh"));
		assertTrue(section1.containsKey("titleEn"));
		assertEquals("http://localhost:8080/m/tmn_webview/images/logo_bill/tmvh@2x.png", section1.get("logoURL"));
		assertEquals("", section1.get("titleTh"));
		assertEquals("", section1.get("titleEn"));

		assertTrue(data.containsKey("section2"));
		Map<String, Object> section2 = (Map<String, Object>) data.get("section2");
		assertTrue(section2.containsKey("column1"));
		assertFalse(section2.containsKey("column2"));
		Map<String, Object> column21 = (Map<String, Object>) section2.get("column1");
		assertTrue(column21.containsKey("cell1"));
		assertFalse(column21.containsKey("cell2"));
		Map<String, String> cell1 = (Map<String, String>) column21.get("cell1");
		assertTrue(cell1.containsKey("titleTh"));
		assertTrue(cell1.containsKey("titleEn"));
		assertTrue(cell1.containsKey("value"));
		assertEquals("หมายเลขโทรศัพท์", cell1.get("titleTh"));
		assertEquals("mobile number", cell1.get("titleEn"));
		assertEquals("089-765-4333", cell1.get("value"));

		assertTrue(data.containsKey("section3"));
		Map<String, Object> section3 = (Map<String, Object>) data.get("section3");
		assertTrue(section3.containsKey("column1"));
		assertTrue(section3.containsKey("column2"));
		Map<String, Object> column31 = (Map<String, Object>) section3.get("column1");
		Map<String, Object> column32 = (Map<String, Object>) section3.get("column2");
		assertTrue(column31.containsKey("cell1"));
		assertTrue(column31.containsKey("cell2"));
		assertTrue(column32.containsKey("cell1"));
		assertFalse(column32.containsKey("cell2"));
		Map<String, String> cell311 = (Map<String, String>) column31.get("cell1");
		Map<String, String> cell312 = (Map<String, String>) column31.get("cell2");
		Map<String, String> cell321 = (Map<String, String>) column32.get("cell1");
		assertTrue(cell311.containsKey("titleTh"));
		assertTrue(cell311.containsKey("titleEn"));
		assertTrue(cell311.containsKey("value"));
		assertEquals("จำนวนเงิน", cell311.get("titleTh"));
		assertEquals("amount", cell311.get("titleEn"));
		assertEquals("23,455.50", cell311.get("value"));
		assertTrue(cell312.containsKey("titleTh"));
		assertTrue(cell312.containsKey("titleEn"));
		assertTrue(cell312.containsKey("value"));
		assertEquals("รวมเงินที่ชำระ", cell312.get("titleTh"));
		assertEquals("total amount", cell312.get("titleEn"));
		assertEquals("24,690.00", cell312.get("value"));
		assertTrue(cell321.containsKey("titleTh"));
		assertTrue(cell321.containsKey("titleEn"));
		assertTrue(cell321.containsKey("value"));
		assertEquals("ค่าธรรมเนียม", cell321.get("titleTh"));
		assertEquals("total fee", cell321.get("titleEn"));
		assertEquals("1,234.50", cell321.get("value"));

		assertTrue(data.containsKey("section4"));
		Map<String, Object> section4 = (Map<String, Object>) data.get("section4");
		assertTrue(section4.containsKey("column1"));
		assertTrue(section4.containsKey("column2"));
		Map<String, Object> column41 = (Map<String, Object>) section4.get("column1");
		Map<String, Object> column42 = (Map<String, Object>) section4.get("column2");
		assertTrue(column41.containsKey("cell1"));
		assertFalse(column41.containsKey("cell2"));
		assertTrue(column42.containsKey("cell1"));
		assertFalse(column42.containsKey("cell2"));
		Map<String, String> cell411 = (Map<String, String>) column41.get("cell1");
		Map<String, String> cell421 = (Map<String, String>) column42.get("cell1");
		assertEquals("วันที่-เวลา", cell411.get("titleTh"));
		assertEquals("transaction date", cell411.get("titleEn"));
		assertEquals("10/02/13 15:35", cell411.get("value"));
		assertEquals("เลขที่อ้างอิง", cell421.get("titleTh"));
		assertEquals("transaction ID", cell421.get("titleEn"));
		assertEquals("1234567890", cell421.get("value"));
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

		assertTrue(data.containsKey("section1"));
		Map<String, String> section1 = (Map<String, String>) data.get("section1");
		assertTrue(section1.containsKey("logoURL"));
		assertTrue(section1.containsKey("titleTh"));
		assertTrue(section1.containsKey("titleEn"));
		assertEquals("http://localhost:8080/m/tmn_webview/images/logo_bill/trmv@2x.png", section1.get("logoURL"));
		assertEquals("", section1.get("titleTh"));
		assertEquals("", section1.get("titleEn"));

		assertTrue(data.containsKey("section2"));
		Map<String, Object> section2 = (Map<String, Object>) data.get("section2");
		assertTrue(section2.containsKey("column1"));
		assertFalse(section2.containsKey("column2"));
		Map<String, Object> column21 = (Map<String, Object>) section2.get("column1");
		assertTrue(column21.containsKey("cell1"));
		assertFalse(column21.containsKey("cell2"));
		Map<String, String> cell1 = (Map<String, String>) column21.get("cell1");
		assertTrue(cell1.containsKey("titleTh"));
		assertTrue(cell1.containsKey("titleEn"));
		assertTrue(cell1.containsKey("value"));
		assertEquals("หมายเลขโทรศัพท์", cell1.get("titleTh"));
		assertEquals("mobile number", cell1.get("titleEn"));
		assertEquals("089-765-4333", cell1.get("value"));

		assertTrue(data.containsKey("section3"));
		Map<String, Object> section3 = (Map<String, Object>) data.get("section3");
		assertTrue(section3.containsKey("column1"));
		assertTrue(section3.containsKey("column2"));
		Map<String, Object> column31 = (Map<String, Object>) section3.get("column1");
		Map<String, Object> column32 = (Map<String, Object>) section3.get("column2");
		assertTrue(column31.containsKey("cell1"));
		assertTrue(column31.containsKey("cell2"));
		assertTrue(column32.containsKey("cell1"));
		assertFalse(column32.containsKey("cell2"));
		Map<String, String> cell311 = (Map<String, String>) column31.get("cell1");
		Map<String, String> cell312 = (Map<String, String>) column31.get("cell2");
		Map<String, String> cell321 = (Map<String, String>) column32.get("cell1");
		assertTrue(cell311.containsKey("titleTh"));
		assertTrue(cell311.containsKey("titleEn"));
		assertTrue(cell311.containsKey("value"));
		assertEquals("จำนวนเงิน", cell311.get("titleTh"));
		assertEquals("amount", cell311.get("titleEn"));
		assertEquals("23,455.50", cell311.get("value"));
		assertTrue(cell312.containsKey("titleTh"));
		assertTrue(cell312.containsKey("titleEn"));
		assertTrue(cell312.containsKey("value"));
		assertEquals("รวมเงินที่ชำระ", cell312.get("titleTh"));
		assertEquals("total amount", cell312.get("titleEn"));
		assertEquals("24,690.00", cell312.get("value"));
		assertTrue(cell321.containsKey("titleTh"));
		assertTrue(cell321.containsKey("titleEn"));
		assertTrue(cell321.containsKey("value"));
		assertEquals("ค่าธรรมเนียม", cell321.get("titleTh"));
		assertEquals("total fee", cell321.get("titleEn"));
		assertEquals("1,234.50", cell321.get("value"));

		assertTrue(data.containsKey("section4"));
		Map<String, Object> section4 = (Map<String, Object>) data.get("section4");
		assertTrue(section4.containsKey("column1"));
		assertTrue(section4.containsKey("column2"));
		Map<String, Object> column41 = (Map<String, Object>) section4.get("column1");
		Map<String, Object> column42 = (Map<String, Object>) section4.get("column2");
		assertTrue(column41.containsKey("cell1"));
		assertFalse(column41.containsKey("cell2"));
		assertTrue(column42.containsKey("cell1"));
		assertFalse(column42.containsKey("cell2"));
		Map<String, String> cell411 = (Map<String, String>) column41.get("cell1");
		Map<String, String> cell421 = (Map<String, String>) column42.get("cell1");
		assertEquals("วันที่-เวลา", cell411.get("titleTh"));
		assertEquals("transaction date", cell411.get("titleEn"));
		assertEquals("10/02/13 15:35", cell411.get("value"));
		assertEquals("เลขที่อ้างอิง", cell421.get("titleTh"));
		assertEquals("transaction ID", cell421.get("titleEn"));
		assertEquals("1234567890", cell421.get("value"));
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

		assertTrue(data.containsKey("section1"));
		Map<String, String> section1 = (Map<String, String>) data.get("section1");
		assertTrue(section1.containsKey("logoURL"));
		assertTrue(section1.containsKey("titleTh"));
		assertTrue(section1.containsKey("titleEn"));
		assertEquals("http://localhost:8080/m/tmn_webview/images/logo_bill/ti@2x.png", section1.get("logoURL"));
		assertEquals("", section1.get("titleTh"));
		assertEquals("", section1.get("titleEn"));

		assertTrue(data.containsKey("section2"));
		Map<String, Object> section2 = (Map<String, Object>) data.get("section2");
		assertTrue(section2.containsKey("column1"));
		assertFalse(section2.containsKey("column2"));
		Map<String, Object> column21 = (Map<String, Object>) section2.get("column1");
		assertTrue(column21.containsKey("cell1"));
		assertTrue(column21.containsKey("cell2"));
		Map<String, String> cell1 = (Map<String, String>) column21.get("cell1");
		Map<String, String> cell2 = (Map<String, String>) column21.get("cell2");
		assertTrue(cell1.containsKey("titleTh"));
		assertTrue(cell1.containsKey("titleEn"));
		assertTrue(cell1.containsKey("value"));
		assertEquals("รหัสลูกค้า/หมายเลขโทรศัพท์", cell1.get("titleTh"));
		assertEquals("Account/Mobile Number", cell1.get("titleEn"));
		assertEquals("864895245", cell1.get("value"));
		assertTrue(cell2.containsKey("titleTh"));
		assertTrue(cell2.containsKey("titleEn"));
		assertTrue(cell2.containsKey("value"));
		assertEquals("เลขที่ใบแจ้งค่าบริการ", cell2.get("titleTh"));
		assertEquals("invoice number", cell2.get("titleEn"));
		assertEquals("923178945372901", cell2.get("value"));

		assertTrue(data.containsKey("section3"));
		Map<String, Object> section3 = (Map<String, Object>) data.get("section3");
		assertTrue(section3.containsKey("column1"));
		assertTrue(section3.containsKey("column2"));
		Map<String, Object> column31 = (Map<String, Object>) section3.get("column1");
		Map<String, Object> column32 = (Map<String, Object>) section3.get("column2");
		assertTrue(column31.containsKey("cell1"));
		assertTrue(column31.containsKey("cell2"));
		assertTrue(column32.containsKey("cell1"));
		assertFalse(column32.containsKey("cell2"));
		Map<String, String> cell311 = (Map<String, String>) column31.get("cell1");
		Map<String, String> cell312 = (Map<String, String>) column31.get("cell2");
		Map<String, String> cell321 = (Map<String, String>) column32.get("cell1");
		assertTrue(cell311.containsKey("titleTh"));
		assertTrue(cell311.containsKey("titleEn"));
		assertTrue(cell311.containsKey("value"));
		assertEquals("จำนวนเงิน", cell311.get("titleTh"));
		assertEquals("amount", cell311.get("titleEn"));
		assertEquals("23,455.50", cell311.get("value"));
		assertTrue(cell312.containsKey("titleTh"));
		assertTrue(cell312.containsKey("titleEn"));
		assertTrue(cell312.containsKey("value"));
		assertEquals("รวมเงินที่ชำระ", cell312.get("titleTh"));
		assertEquals("total amount", cell312.get("titleEn"));
		assertEquals("24,690.00", cell312.get("value"));
		assertTrue(cell321.containsKey("titleTh"));
		assertTrue(cell321.containsKey("titleEn"));
		assertTrue(cell321.containsKey("value"));
		assertEquals("ค่าธรรมเนียม", cell321.get("titleTh"));
		assertEquals("total fee", cell321.get("titleEn"));
		assertEquals("1,234.50", cell321.get("value"));

		assertTrue(data.containsKey("section4"));
		Map<String, Object> section4 = (Map<String, Object>) data.get("section4");
		assertTrue(section4.containsKey("column1"));
		assertTrue(section4.containsKey("column2"));
		Map<String, Object> column41 = (Map<String, Object>) section4.get("column1");
		Map<String, Object> column42 = (Map<String, Object>) section4.get("column2");
		assertTrue(column41.containsKey("cell1"));
		assertFalse(column41.containsKey("cell2"));
		assertTrue(column42.containsKey("cell1"));
		assertFalse(column42.containsKey("cell2"));
		Map<String, String> cell411 = (Map<String, String>) column41.get("cell1");
		Map<String, String> cell421 = (Map<String, String>) column42.get("cell1");
		assertEquals("วันที่-เวลา", cell411.get("titleTh"));
		assertEquals("transaction date", cell411.get("titleEn"));
		assertEquals("10/02/13 15:35", cell411.get("value"));
		assertEquals("เลขที่อ้างอิง", cell421.get("titleTh"));
		assertEquals("transaction ID", cell421.get("titleEn"));
		assertEquals("1234567890", cell421.get("value"));
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

		assertTrue(data.containsKey("section1"));
		Map<String, String> section1 = (Map<String, String>) data.get("section1");
		assertTrue(section1.containsKey("logoURL"));
		assertTrue(section1.containsKey("titleTh"));
		assertTrue(section1.containsKey("titleEn"));
		assertEquals("http://localhost:8080/m/tmn_webview/images/logo_bill/tmvh@2x.png", section1.get("logoURL"));
		assertEquals("", section1.get("titleTh"));
		assertEquals("", section1.get("titleEn"));

		assertTrue(data.containsKey("section2"));
		Map<String, Object> section2 = (Map<String, Object>) data.get("section2");
		assertTrue(section2.containsKey("column1"));
		assertFalse(section2.containsKey("column2"));
		Map<String, Object> column21 = (Map<String, Object>) section2.get("column1");
		assertTrue(column21.containsKey("cell1"));
		assertFalse(column21.containsKey("cell2"));
		Map<String, String> cell1 = (Map<String, String>) column21.get("cell1");
		assertTrue(cell1.containsKey("titleTh"));
		assertTrue(cell1.containsKey("titleEn"));
		assertTrue(cell1.containsKey("value"));
		assertEquals("รหัสลูกค้า/หมายเลขโทรศัพท์", cell1.get("titleTh"));
		assertEquals("Account/Mobile Number", cell1.get("titleEn"));
		assertEquals("089-123-4567", cell1.get("value"));

		assertTrue(data.containsKey("section3"));
		Map<String, Object> section3 = (Map<String, Object>) data.get("section3");
		assertTrue(section3.containsKey("column1"));
		assertTrue(section3.containsKey("column2"));
		Map<String, Object> column31 = (Map<String, Object>) section3.get("column1");
		Map<String, Object> column32 = (Map<String, Object>) section3.get("column2");
		assertTrue(column31.containsKey("cell1"));
		assertTrue(column31.containsKey("cell2"));
		assertTrue(column32.containsKey("cell1"));
		assertFalse(column32.containsKey("cell2"));
		Map<String, String> cell311 = (Map<String, String>) column31.get("cell1");
		Map<String, String> cell312 = (Map<String, String>) column31.get("cell2");
		Map<String, String> cell321 = (Map<String, String>) column32.get("cell1");
		assertTrue(cell311.containsKey("titleTh"));
		assertTrue(cell311.containsKey("titleEn"));
		assertTrue(cell311.containsKey("value"));
		assertEquals("จำนวนเงิน", cell311.get("titleTh"));
		assertEquals("amount", cell311.get("titleEn"));
		assertEquals("23,455.50", cell311.get("value"));
		assertTrue(cell312.containsKey("titleTh"));
		assertTrue(cell312.containsKey("titleEn"));
		assertTrue(cell312.containsKey("value"));
		assertEquals("รวมเงินที่ชำระ", cell312.get("titleTh"));
		assertEquals("total amount", cell312.get("titleEn"));
		assertEquals("24,690.00", cell312.get("value"));
		assertTrue(cell321.containsKey("titleTh"));
		assertTrue(cell321.containsKey("titleEn"));
		assertTrue(cell321.containsKey("value"));
		assertEquals("ค่าธรรมเนียม", cell321.get("titleTh"));
		assertEquals("total fee", cell321.get("titleEn"));
		assertEquals("1,234.50", cell321.get("value"));

		assertTrue(data.containsKey("section4"));
		Map<String, Object> section4 = (Map<String, Object>) data.get("section4");
		assertTrue(section4.containsKey("column1"));
		assertTrue(section4.containsKey("column2"));
		Map<String, Object> column41 = (Map<String, Object>) section4.get("column1");
		Map<String, Object> column42 = (Map<String, Object>) section4.get("column2");
		assertTrue(column41.containsKey("cell1"));
		assertFalse(column41.containsKey("cell2"));
		assertTrue(column42.containsKey("cell1"));
		assertFalse(column42.containsKey("cell2"));
		Map<String, String> cell411 = (Map<String, String>) column41.get("cell1");
		Map<String, String> cell421 = (Map<String, String>) column42.get("cell1");
		assertEquals("วันที่-เวลา", cell411.get("titleTh"));
		assertEquals("transaction date", cell411.get("titleEn"));
		assertEquals("10/02/13 15:35", cell411.get("value"));
		assertEquals("เลขที่อ้างอิง", cell421.get("titleTh"));
		assertEquals("transaction ID", cell421.get("titleEn"));
		assertEquals("1234567890", cell421.get("value"));
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

		assertTrue(data.containsKey("section1"));
		Map<String, String> section1 = (Map<String, String>) data.get("section1");
		assertTrue(section1.containsKey("logoURL"));
		assertTrue(section1.containsKey("titleTh"));
		assertTrue(section1.containsKey("titleEn"));
		assertEquals("http://localhost:8080/m/tmn_webview/images/logo_bill/tr@2x.png", section1.get("logoURL"));
		assertEquals("", section1.get("titleTh"));
		assertEquals("", section1.get("titleEn"));

		assertTrue(data.containsKey("section2"));
		Map<String, Object> section2 = (Map<String, Object>) data.get("section2");
		assertTrue(section2.containsKey("column1"));
		assertFalse(section2.containsKey("column2"));
		Map<String, Object> column21 = (Map<String, Object>) section2.get("column1");
		assertTrue(column21.containsKey("cell1"));
		assertFalse(column21.containsKey("cell2"));
		Map<String, String> cell1 = (Map<String, String>) column21.get("cell1");
		assertTrue(cell1.containsKey("titleTh"));
		assertTrue(cell1.containsKey("titleEn"));
		assertTrue(cell1.containsKey("value"));
		assertEquals("เลขที่อ้างอิง 1/หมายเลขโทรศัพท์", cell1.get("titleTh"));
		assertEquals("Account/Mobile Number", cell1.get("titleEn"));
		assertEquals("02-000-0000", cell1.get("value"));

		assertTrue(data.containsKey("section3"));
		Map<String, Object> section3 = (Map<String, Object>) data.get("section3");
		assertTrue(section3.containsKey("column1"));
		assertTrue(section3.containsKey("column2"));
		Map<String, Object> column31 = (Map<String, Object>) section3.get("column1");
		Map<String, Object> column32 = (Map<String, Object>) section3.get("column2");
		assertTrue(column31.containsKey("cell1"));
		assertTrue(column31.containsKey("cell2"));
		assertTrue(column32.containsKey("cell1"));
		assertFalse(column32.containsKey("cell2"));
		Map<String, String> cell311 = (Map<String, String>) column31.get("cell1");
		Map<String, String> cell312 = (Map<String, String>) column31.get("cell2");
		Map<String, String> cell321 = (Map<String, String>) column32.get("cell1");
		assertTrue(cell311.containsKey("titleTh"));
		assertTrue(cell311.containsKey("titleEn"));
		assertTrue(cell311.containsKey("value"));
		assertEquals("จำนวนเงิน", cell311.get("titleTh"));
		assertEquals("amount", cell311.get("titleEn"));
		assertEquals("23,455.50", cell311.get("value"));
		assertTrue(cell312.containsKey("titleTh"));
		assertTrue(cell312.containsKey("titleEn"));
		assertTrue(cell312.containsKey("value"));
		assertEquals("รวมเงินที่ชำระ", cell312.get("titleTh"));
		assertEquals("total amount", cell312.get("titleEn"));
		assertEquals("24,690.00", cell312.get("value"));
		assertTrue(cell321.containsKey("titleTh"));
		assertTrue(cell321.containsKey("titleEn"));
		assertTrue(cell321.containsKey("value"));
		assertEquals("ค่าธรรมเนียม", cell321.get("titleTh"));
		assertEquals("total fee", cell321.get("titleEn"));
		assertEquals("1,234.50", cell321.get("value"));

		assertTrue(data.containsKey("section4"));
		Map<String, Object> section4 = (Map<String, Object>) data.get("section4");
		assertTrue(section4.containsKey("column1"));
		assertTrue(section4.containsKey("column2"));
		Map<String, Object> column41 = (Map<String, Object>) section4.get("column1");
		Map<String, Object> column42 = (Map<String, Object>) section4.get("column2");
		assertTrue(column41.containsKey("cell1"));
		assertFalse(column41.containsKey("cell2"));
		assertTrue(column42.containsKey("cell1"));
		assertFalse(column42.containsKey("cell2"));
		Map<String, String> cell411 = (Map<String, String>) column41.get("cell1");
		Map<String, String> cell421 = (Map<String, String>) column42.get("cell1");
		assertEquals("วันที่-เวลา", cell411.get("titleTh"));
		assertEquals("transaction date", cell411.get("titleEn"));
		assertEquals("10/02/13 15:35", cell411.get("value"));
		assertEquals("เลขที่อ้างอิง", cell421.get("titleTh"));
		assertEquals("transaction ID", cell421.get("titleEn"));
		assertEquals("1234567890", cell421.get("value"));
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

		assertTrue(data.containsKey("section1"));
		Map<String, String> section1 = (Map<String, String>) data.get("section1");
		assertTrue(section1.containsKey("logoURL"));
		assertTrue(section1.containsKey("titleTh"));
		assertTrue(section1.containsKey("titleEn"));
		assertEquals("http://localhost:8080/m/tmn_webview/images/logo_bill/ti@2x.png", section1.get("logoURL"));
		assertEquals("", section1.get("titleTh"));
		assertEquals("", section1.get("titleEn"));

		assertTrue(data.containsKey("section2"));
		Map<String, Object> section2 = (Map<String, Object>) data.get("section2");
		assertTrue(section2.containsKey("column1"));
		assertFalse(section2.containsKey("column2"));
		Map<String, Object> column21 = (Map<String, Object>) section2.get("column1");
		assertTrue(column21.containsKey("cell1"));
		assertFalse(column21.containsKey("cell2"));
		Map<String, String> cell1 = (Map<String, String>) column21.get("cell1");
		Map<String, String> cell2 = (Map<String, String>) column21.get("cell2");
		assertTrue(cell1.containsKey("titleTh"));
		assertTrue(cell1.containsKey("titleEn"));
		assertTrue(cell1.containsKey("value"));
		assertEquals("รหัสลูกค้า/หมายเลขโทรศัพท์", cell1.get("titleTh"));
		assertEquals("Account/Mobile Number", cell1.get("titleEn"));
		assertEquals("864895245", cell1.get("value"));
		

		assertTrue(data.containsKey("section3"));
		Map<String, Object> section3 = (Map<String, Object>) data.get("section3");
		assertTrue(section3.containsKey("column1"));
		assertTrue(section3.containsKey("column2"));
		Map<String, Object> column31 = (Map<String, Object>) section3.get("column1");
		Map<String, Object> column32 = (Map<String, Object>) section3.get("column2");
		assertTrue(column31.containsKey("cell1"));
		assertTrue(column31.containsKey("cell2"));
		assertTrue(column32.containsKey("cell1"));
		assertFalse(column32.containsKey("cell2"));
		Map<String, String> cell311 = (Map<String, String>) column31.get("cell1");
		Map<String, String> cell312 = (Map<String, String>) column31.get("cell2");
		Map<String, String> cell321 = (Map<String, String>) column32.get("cell1");
		assertTrue(cell311.containsKey("titleTh"));
		assertTrue(cell311.containsKey("titleEn"));
		assertTrue(cell311.containsKey("value"));
		assertEquals("จำนวนเงิน", cell311.get("titleTh"));
		assertEquals("amount", cell311.get("titleEn"));
		assertEquals("23,455.50", cell311.get("value"));
		assertTrue(cell312.containsKey("titleTh"));
		assertTrue(cell312.containsKey("titleEn"));
		assertTrue(cell312.containsKey("value"));
		assertEquals("รวมเงินที่ชำระ", cell312.get("titleTh"));
		assertEquals("total amount", cell312.get("titleEn"));
		assertEquals("24,690.00", cell312.get("value"));
		assertTrue(cell321.containsKey("titleTh"));
		assertTrue(cell321.containsKey("titleEn"));
		assertTrue(cell321.containsKey("value"));
		assertEquals("ค่าธรรมเนียม", cell321.get("titleTh"));
		assertEquals("total fee", cell321.get("titleEn"));
		assertEquals("1,234.50", cell321.get("value"));

		assertTrue(data.containsKey("section4"));
		Map<String, Object> section4 = (Map<String, Object>) data.get("section4");
		assertTrue(section4.containsKey("column1"));
		assertTrue(section4.containsKey("column2"));
		Map<String, Object> column41 = (Map<String, Object>) section4.get("column1");
		Map<String, Object> column42 = (Map<String, Object>) section4.get("column2");
		assertTrue(column41.containsKey("cell1"));
		assertFalse(column41.containsKey("cell2"));
		assertTrue(column42.containsKey("cell1"));
		assertFalse(column42.containsKey("cell2"));
		Map<String, String> cell411 = (Map<String, String>) column41.get("cell1");
		Map<String, String> cell421 = (Map<String, String>) column42.get("cell1");
		assertEquals("วันที่-เวลา", cell411.get("titleTh"));
		assertEquals("transaction date", cell411.get("titleEn"));
		assertEquals("10/02/13 15:35", cell411.get("value"));
		assertEquals("เลขที่อ้างอิง", cell421.get("titleTh"));
		assertEquals("transaction ID", cell421.get("titleEn"));
		assertEquals("1234567890", cell421.get("value"));
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

		assertTrue(data.containsKey("section1"));
		Map<String, String> section1 = (Map<String, String>) data.get("section1");
		assertTrue(section1.containsKey("logoURL"));
		assertTrue(section1.containsKey("titleTh"));
		assertTrue(section1.containsKey("titleEn"));
		assertEquals("http://localhost:8080/m/tmn_webview/images/logo_bill/ti@2x.png", section1.get("logoURL"));
		assertEquals("", section1.get("titleTh"));
		assertEquals("", section1.get("titleEn"));

		assertTrue(data.containsKey("section2"));
		Map<String, Object> section2 = (Map<String, Object>) data.get("section2");
		assertTrue(section2.containsKey("column1"));
		assertFalse(section2.containsKey("column2"));
		Map<String, Object> column21 = (Map<String, Object>) section2.get("column1");
		assertTrue(column21.containsKey("cell1"));
		assertFalse(column21.containsKey("cell2"));
		Map<String, String> cell1 = (Map<String, String>) column21.get("cell1");
		Map<String, String> cell2 = (Map<String, String>) column21.get("cell2");
		assertTrue(cell1.containsKey("titleTh"));
		assertTrue(cell1.containsKey("titleEn"));
		assertTrue(cell1.containsKey("value"));
		assertEquals("รหัสลูกค้า/หมายเลขโทรศัพท์", cell1.get("titleTh"));
		assertEquals("Account/Mobile Number", cell1.get("titleEn"));
		assertEquals("864895245", cell1.get("value"));
		

		assertTrue(data.containsKey("section3"));
		Map<String, Object> section3 = (Map<String, Object>) data.get("section3");
		assertTrue(section3.containsKey("column1"));
		assertTrue(section3.containsKey("column2"));
		Map<String, Object> column31 = (Map<String, Object>) section3.get("column1");
		Map<String, Object> column32 = (Map<String, Object>) section3.get("column2");
		assertTrue(column31.containsKey("cell1"));
		assertTrue(column31.containsKey("cell2"));
		assertTrue(column32.containsKey("cell1"));
		assertFalse(column32.containsKey("cell2"));
		Map<String, String> cell311 = (Map<String, String>) column31.get("cell1");
		Map<String, String> cell312 = (Map<String, String>) column31.get("cell2");
		Map<String, String> cell321 = (Map<String, String>) column32.get("cell1");
		assertTrue(cell311.containsKey("titleTh"));
		assertTrue(cell311.containsKey("titleEn"));
		assertTrue(cell311.containsKey("value"));
		assertEquals("จำนวนเงิน", cell311.get("titleTh"));
		assertEquals("amount", cell311.get("titleEn"));
		assertEquals("23,455.50", cell311.get("value"));
		assertTrue(cell312.containsKey("titleTh"));
		assertTrue(cell312.containsKey("titleEn"));
		assertTrue(cell312.containsKey("value"));
		assertEquals("รวมเงินที่ชำระ", cell312.get("titleTh"));
		assertEquals("total amount", cell312.get("titleEn"));
		assertEquals("24,690.00", cell312.get("value"));
		assertTrue(cell321.containsKey("titleTh"));
		assertTrue(cell321.containsKey("titleEn"));
		assertTrue(cell321.containsKey("value"));
		assertEquals("ค่าธรรมเนียม", cell321.get("titleTh"));
		assertEquals("total fee", cell321.get("titleEn"));
		assertEquals("1,234.50", cell321.get("value"));

		assertTrue(data.containsKey("section4"));
		Map<String, Object> section4 = (Map<String, Object>) data.get("section4");
		assertTrue(section4.containsKey("column1"));
		assertTrue(section4.containsKey("column2"));
		Map<String, Object> column41 = (Map<String, Object>) section4.get("column1");
		Map<String, Object> column42 = (Map<String, Object>) section4.get("column2");
		assertTrue(column41.containsKey("cell1"));
		assertFalse(column41.containsKey("cell2"));
		assertTrue(column42.containsKey("cell1"));
		assertFalse(column42.containsKey("cell2"));
		Map<String, String> cell411 = (Map<String, String>) column41.get("cell1");
		Map<String, String> cell421 = (Map<String, String>) column42.get("cell1");
		assertEquals("วันที่-เวลา", cell411.get("titleTh"));
		assertEquals("transaction date", cell411.get("titleEn"));
		assertEquals("10/02/13 15:35", cell411.get("value"));
		assertEquals("เลขที่อ้างอิง", cell421.get("titleTh"));
		assertEquals("transaction ID", cell421.get("titleEn"));
		assertEquals("1234567890", cell421.get("value"));
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

		assertTrue(data.containsKey("section1"));
		Map<String, String> section1 = (Map<String, String>) data.get("section1");
		assertFalse(section1.containsKey("logoURL"));
		assertTrue(section1.containsKey("titleTh"));
		assertTrue(section1.containsKey("titleEn"));
		assertEquals("คืนค่าธรรมเนียม", section1.get("titleTh"));
		assertEquals("kickback", section1.get("titleEn"));

		assertTrue(data.containsKey("section2"));
		Map<String, Object> section2 = (Map<String, Object>) data.get("section2");
		assertTrue(section2.containsKey("column1"));
		assertFalse(section2.containsKey("column2"));
		Map<String, Object> column21 = (Map<String, Object>) section2.get("column1");
		assertTrue(column21.containsKey("cell1"));
		assertFalse(column21.containsKey("cell2"));
		Map<String, String> cell1 = (Map<String, String>) column21.get("cell1");
		Map<String, String> cell2 = (Map<String, String>) column21.get("cell2");
		assertTrue(cell1.containsKey("titleTh"));
		assertTrue(cell1.containsKey("titleEn"));
		assertTrue(cell1.containsKey("value"));
		assertEquals("ทำรายการ", cell1.get("titleTh"));
		assertEquals("activity", cell1.get("titleEn"));
		assertEquals("เติมเงินด้วยบัญชีธนาคาร", cell1.get("value"));

		assertTrue(data.containsKey("section3"));
		Map<String, Object> section3 = (Map<String, Object>) data.get("section3");
		assertTrue(section3.containsKey("column1"));
		assertFalse(section3.containsKey("column2"));
		Map<String, Object> column31 = (Map<String, Object>) section3.get("column1");
		assertTrue(column31.containsKey("cell1"));
		assertFalse(column31.containsKey("cell2"));
		Map<String, String> cell311 = (Map<String, String>) column31.get("cell1");
		assertTrue(cell311.containsKey("titleTh"));
		assertTrue(cell311.containsKey("titleEn"));
		assertTrue(cell311.containsKey("value"));
		assertEquals("ยอดเงินเข้า Wallet", cell311.get("titleTh"));
		assertEquals("total amount", cell311.get("titleEn"));
		assertEquals("23,455.50", cell311.get("value"));

		assertTrue(data.containsKey("section4"));
		Map<String, Object> section4 = (Map<String, Object>) data.get("section4");
		assertTrue(section4.containsKey("column1"));
		assertTrue(section4.containsKey("column2"));
		Map<String, Object> column41 = (Map<String, Object>) section4.get("column1");
		Map<String, Object> column42 = (Map<String, Object>) section4.get("column2");
		assertTrue(column41.containsKey("cell1"));
		assertFalse(column41.containsKey("cell2"));
		assertTrue(column42.containsKey("cell1"));
		assertFalse(column42.containsKey("cell2"));
		Map<String, String> cell411 = (Map<String, String>) column41.get("cell1");
		Map<String, String> cell421 = (Map<String, String>) column42.get("cell1");
		assertEquals("วันที่-เวลา", cell411.get("titleTh"));
		assertEquals("transaction date", cell411.get("titleEn"));
		assertEquals("10/02/13 15:35", cell411.get("value"));
		assertEquals("เลขที่อ้างอิง", cell421.get("titleTh"));
		assertEquals("transaction ID", cell421.get("titleEn"));
		assertEquals("1234567890", cell421.get("value"));
	}

	@Test
	public void getAddMoneyActivityDetails() throws Exception {
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

		when(this.activityServiceMock.getActivityDetail(6L, fakeAccessTokenID)).thenReturn(detail);

		ProductResponse resp = controller.getActivityDetails(String.valueOf(6L), fakeAccessTokenID);
		Map<String, Object> data = resp.getData();

		assertTrue(data.containsKey("section1"));
		Map<String, String> section1 = (Map<String, String>) data.get("section1");
		assertTrue(section1.containsKey("logoURL"));
		assertTrue(section1.containsKey("titleTh"));
		assertTrue(section1.containsKey("titleEn"));
		assertEquals("บัญชีธนาคาร", section1.get("titleTh"));
		assertEquals("bank account", section1.get("titleEn"));

		assertTrue(data.containsKey("section2"));
		Map<String, Object> section2 = (Map<String, Object>) data.get("section2");
		assertTrue(section2.containsKey("column1"));
		assertFalse(section2.containsKey("column2"));
		Map<String, Object> column21 = (Map<String, Object>) section2.get("column1");
		assertTrue(column21.containsKey("cell1"));
		assertTrue(column21.containsKey("cell2"));
		Map<String, String> cell1 = (Map<String, String>) column21.get("cell1");
		Map<String, String> cell2 = (Map<String, String>) column21.get("cell2");
		assertTrue(cell1.containsKey("titleTh"));
		assertTrue(cell1.containsKey("titleEn"));
		assertTrue(cell1.containsKey("value"));
		assertEquals("ธนาคาร", cell1.get("titleTh"));
		assertEquals("bank name", cell1.get("titleEn"));
		assertEquals("ธนาคารกรุงไทย", cell1.get("value"));
		assertTrue(cell2.containsKey("titleTh"));
		assertTrue(cell2.containsKey("titleEn"));
		assertTrue(cell2.containsKey("value"));
		assertEquals("หมายเลขบัญชี", cell2.get("titleTh"));
		assertEquals("account number", cell2.get("titleEn"));
		assertEquals("***7412", cell2.get("value"));

		assertTrue(data.containsKey("section3"));
		Map<String, Object> section3 = (Map<String, Object>) data.get("section3");
		assertTrue(section3.containsKey("column1"));
		assertFalse(section3.containsKey("column2"));
		Map<String, Object> column31 = (Map<String, Object>) section3.get("column1");
		Map<String, Object> column32 = (Map<String, Object>) section3.get("column2");
		assertTrue(column31.containsKey("cell1"));
		assertFalse(column31.containsKey("cell2"));
		Map<String, String> cell311 = (Map<String, String>) column31.get("cell1");
		Map<String, String> cell312 = (Map<String, String>) column31.get("cell2");
		assertTrue(cell311.containsKey("titleTh"));
		assertTrue(cell311.containsKey("titleEn"));
		assertTrue(cell311.containsKey("value"));
		assertEquals("ยอดเงินเข้า Wallet", cell311.get("titleTh"));
		assertEquals("total amount", cell311.get("titleEn"));
		assertEquals("24,690.00", cell311.get("value"));

		assertTrue(data.containsKey("section4"));
		Map<String, Object> section4 = (Map<String, Object>) data.get("section4");
		assertTrue(section4.containsKey("column1"));
		assertTrue(section4.containsKey("column2"));
		Map<String, Object> column41 = (Map<String, Object>) section4.get("column1");
		Map<String, Object> column42 = (Map<String, Object>) section4.get("column2");
		assertTrue(column41.containsKey("cell1"));
		assertFalse(column41.containsKey("cell2"));
		assertTrue(column42.containsKey("cell1"));
		assertFalse(column42.containsKey("cell2"));
		Map<String, String> cell411 = (Map<String, String>) column41.get("cell1");
		Map<String, String> cell421 = (Map<String, String>) column42.get("cell1");
		assertEquals("วันที่-เวลา", cell411.get("titleTh"));
		assertEquals("transaction date", cell411.get("titleEn"));
		assertEquals("10/02/13 15:35", cell411.get("value"));
		assertEquals("เลขที่อ้างอิง", cell421.get("titleTh"));
		assertEquals("transaction ID", cell421.get("titleEn"));
		assertEquals("1234567890", cell421.get("value"));
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

		assertTrue(data.containsKey("section1"));
		Map<String, String> section1 = (Map<String, String>) data.get("section1");
		assertFalse(section1.containsKey("logoURL"));
		assertTrue(section1.containsKey("titleTh"));
		assertTrue(section1.containsKey("titleEn"));
		assertEquals("ส่งเงิน", section1.get("titleTh"));
		assertEquals("debtor", section1.get("titleEn"));

		assertTrue(data.containsKey("section2"));
		Map<String, Object> section2 = (Map<String, Object>) data.get("section2");
		assertTrue(section2.containsKey("column1"));
		assertFalse(section2.containsKey("column2"));
		Map<String, Object> column21 = (Map<String, Object>) section2.get("column1");
		assertTrue(column21.containsKey("cell1"));
		assertTrue(column21.containsKey("cell2"));
		Map<String, String> cell1 = (Map<String, String>) column21.get("cell1");
		Map<String, String> cell2 = (Map<String, String>) column21.get("cell2");
		assertTrue(cell1.containsKey("titleTh"));
		assertTrue(cell1.containsKey("titleEn"));
		assertTrue(cell1.containsKey("value"));
		assertEquals("หมายเลขผู้รับ", cell1.get("titleTh"));
		assertEquals("account number", cell1.get("titleEn"));
		assertEquals("085-382-8482", cell1.get("value"));
		assertTrue(cell2.containsKey("titleTh"));
		assertTrue(cell2.containsKey("titleEn"));
		assertTrue(cell2.containsKey("value"));
		assertEquals("ชื่อผู้รับ", cell2.get("titleTh"));
		assertEquals("account owner", cell2.get("titleEn"));
		assertEquals("ทวี คุณบิดา", cell2.get("value"));

		assertTrue(data.containsKey("section3"));
		Map<String, Object> section3 = (Map<String, Object>) data.get("section3");
		assertTrue(section3.containsKey("column1"));
		assertFalse(section3.containsKey("column2"));
		Map<String, Object> column31 = (Map<String, Object>) section3.get("column1");
		assertTrue(column31.containsKey("cell1"));
		assertFalse(column31.containsKey("cell2"));
		Map<String, String> cell311 = (Map<String, String>) column31.get("cell1");
		assertTrue(cell311.containsKey("titleTh"));
		assertTrue(cell311.containsKey("titleEn"));
		assertTrue(cell311.containsKey("value"));
		assertEquals("จำนวนเงิน", cell311.get("titleTh"));
		assertEquals("amount", cell311.get("titleEn"));
		assertEquals("23,455.50", cell311.get("value"));

		assertTrue(data.containsKey("section4"));
		Map<String, Object> section4 = (Map<String, Object>) data.get("section4");
		assertTrue(section4.containsKey("column1"));
		assertTrue(section4.containsKey("column2"));
		Map<String, Object> column41 = (Map<String, Object>) section4.get("column1");
		Map<String, Object> column42 = (Map<String, Object>) section4.get("column2");
		assertTrue(column41.containsKey("cell1"));
		assertFalse(column41.containsKey("cell2"));
		assertTrue(column42.containsKey("cell1"));
		assertFalse(column42.containsKey("cell2"));
		Map<String, String> cell411 = (Map<String, String>) column41.get("cell1");
		Map<String, String> cell421 = (Map<String, String>) column42.get("cell1");
		assertEquals("วันที่-เวลา", cell411.get("titleTh"));
		assertEquals("transaction date", cell411.get("titleEn"));
		assertEquals("10/02/13 15:35", cell411.get("value"));
		assertEquals("เลขที่อ้างอิง", cell421.get("titleTh"));
		assertEquals("transaction ID", cell421.get("titleEn"));
		assertEquals("1234567890", cell421.get("value"));
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

		assertTrue(data.containsKey("section1"));
		Map<String, String> section1 = (Map<String, String>) data.get("section1");
		assertFalse(section1.containsKey("logoURL"));
		assertTrue(section1.containsKey("titleTh"));
		assertTrue(section1.containsKey("titleEn"));
		assertEquals("รับเงิน", section1.get("titleTh"));
		assertEquals("creditor", section1.get("titleEn"));

		assertTrue(data.containsKey("section2"));
		Map<String, Object> section2 = (Map<String, Object>) data.get("section2");
		assertTrue(section2.containsKey("column1"));
		assertFalse(section2.containsKey("column2"));
		Map<String, Object> column21 = (Map<String, Object>) section2.get("column1");
		assertTrue(column21.containsKey("cell1"));
		assertTrue(column21.containsKey("cell2"));
		Map<String, String> cell1 = (Map<String, String>) column21.get("cell1");
		Map<String, String> cell2 = (Map<String, String>) column21.get("cell2");
		assertTrue(cell1.containsKey("titleTh"));
		assertTrue(cell1.containsKey("titleEn"));
		assertTrue(cell1.containsKey("value"));
		assertEquals("หมายเลขผู้ส่ง", cell1.get("titleTh"));
		assertEquals("account number", cell1.get("titleEn"));
		assertEquals("085-382-8482", cell1.get("value"));
		assertTrue(cell2.containsKey("titleTh"));
		assertTrue(cell2.containsKey("titleEn"));
		assertTrue(cell2.containsKey("value"));
		assertEquals("ชื่อผู้ส่ง", cell2.get("titleTh"));
		assertEquals("account owner", cell2.get("titleEn"));
		assertEquals("ทวี คุณบิดา", cell2.get("value"));

		assertTrue(data.containsKey("section3"));
		Map<String, Object> section3 = (Map<String, Object>) data.get("section3");
		assertTrue(section3.containsKey("column1"));
		assertFalse(section3.containsKey("column2"));
		Map<String, Object> column31 = (Map<String, Object>) section3.get("column1");
		assertTrue(column31.containsKey("cell1"));
		assertFalse(column31.containsKey("cell2"));
		Map<String, String> cell311 = (Map<String, String>) column31.get("cell1");
		assertTrue(cell311.containsKey("titleTh"));
		assertTrue(cell311.containsKey("titleEn"));
		assertTrue(cell311.containsKey("value"));
		assertEquals("จำนวนเงิน", cell311.get("titleTh"));
		assertEquals("amount", cell311.get("titleEn"));
		assertEquals("23,455.50", cell311.get("value"));

		assertTrue(data.containsKey("section4"));
		Map<String, Object> section4 = (Map<String, Object>) data.get("section4");
		assertTrue(section4.containsKey("column1"));
		assertTrue(section4.containsKey("column2"));
		Map<String, Object> column41 = (Map<String, Object>) section4.get("column1");
		Map<String, Object> column42 = (Map<String, Object>) section4.get("column2");
		assertTrue(column41.containsKey("cell1"));
		assertFalse(column41.containsKey("cell2"));
		assertTrue(column42.containsKey("cell1"));
		assertFalse(column42.containsKey("cell2"));
		Map<String, String> cell411 = (Map<String, String>) column41.get("cell1");
		Map<String, String> cell421 = (Map<String, String>) column42.get("cell1");
		assertEquals("วันที่-เวลา", cell411.get("titleTh"));
		assertEquals("transaction date", cell411.get("titleEn"));
		assertEquals("10/02/13 15:35", cell411.get("value"));
		assertEquals("เลขที่อ้างอิง", cell421.get("titleTh"));
		assertEquals("transaction ID", cell421.get("titleEn"));
		assertEquals("1234567890", cell421.get("value"));
	}
}
