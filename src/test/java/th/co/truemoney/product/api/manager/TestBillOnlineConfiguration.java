package th.co.truemoney.product.api.manager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import th.co.truemoney.product.api.manager.BillConfigurationManager;

public class TestBillOnlineConfiguration {
	
	private BillConfigurationManager configurationManager;
	
	@Before
	public void setup() {
		configurationManager = new BillConfigurationManager();
	}
	
	@Test
	public void successGetBillOnlineProperty(){
		assertEquals(true, configurationManager.isOnlineInquiry("mea"));
	}
	
	@Test
	public void failGetBillOnlineProperty(){
		assertFalse(configurationManager.isOnlineInquiry("xxx"));
	}
	
	@Test
	public void getTMVHBillInfoPropertySuccess(){
		Map<String, Object> resp = configurationManager.getBillInfoResponse("tmvh");
		assertEquals("เบอร์โทรศัพท์ทรูมูฟ เอช",resp.get("ref1TitleTh"));
		assertEquals("Mobile Number",resp.get("ref1TitleEn"));
		assertFalse(resp.containsKey("ref2TitleEn"));
		assertFalse(resp.containsKey("ref2TitleTh"));
		assertEquals("mobile",resp.get("ref1Type"));
		assertEquals("none",resp.get("ref2Type"));
		assertEquals(Boolean.TRUE,resp.get("isInquiryOnline"));
		assertTrue(resp.containsKey("minAmount"));
		assertTrue(resp.containsKey("maxAmount"));
	}
	
	@Test
	public void getTRMVBillInfoPropertySuccess(){
		Map<String, Object> resp = configurationManager.getBillInfoResponse("trmv");
		assertEquals("เบอร์โทรศัพท์ทรูมูฟ",resp.get("ref1TitleTh"));
		assertEquals("Mobile Number",resp.get("ref1TitleEn"));
		assertFalse(resp.containsKey("ref2TitleEn"));
		assertFalse(resp.containsKey("ref2TitleTh"));
		assertEquals("mobile",resp.get("ref1Type"));
		assertEquals(Boolean.TRUE,resp.get("isInquiryOnline"));
		assertTrue(resp.containsKey("minAmount"));
		assertTrue(resp.containsKey("maxAmount"));
	}
	
	@Test
	public void getTRBillInfoPropertySuccess(){
		Map<String, Object> resp = configurationManager.getBillInfoResponse("tr");
		assertEquals("เบอร์โทรศัพท์บ้าน",resp.get("ref1TitleTh"));
		assertEquals("Telephone Number",resp.get("ref1TitleEn"));
		assertFalse(resp.containsKey("ref2TitleEn"));
		assertFalse(resp.containsKey("ref2TitleTh"));
		assertEquals("none",resp.get("ref1Type"));
		assertEquals("none",resp.get("ref2Type"));
		assertEquals(Boolean.TRUE,resp.get("isInquiryOnline"));
		assertTrue(resp.containsKey("minAmount"));
		assertTrue(resp.containsKey("maxAmount"));
	}
	
	@Test
	public void getTIBillInfoPropertySuccess(){
		Map<String, Object> resp = configurationManager.getBillInfoResponse("ti");
		assertEquals("เบอร์โทรศัพท์บ้าน หรือรหัสลูกค้า 12 หลัก",resp.get("ref1TitleTh"));
		assertEquals("Telephone Number/Customer Number",resp.get("ref1TitleEn"));
		assertFalse(resp.containsKey("ref2TitleEn"));
		assertFalse(resp.containsKey("ref2TitleTh"));
		assertEquals("none",resp.get("ref1Type"));
		assertEquals("none",resp.get("ref2Type"));
		assertEquals(true,resp.get("isInquiryOnline"));
		assertTrue(resp.containsKey("minAmount"));
		assertTrue(resp.containsKey("maxAmount"));
	}
	
	@Test
	public void getTICBillInfoPropertySuccess(){
		Map<String, Object> resp = configurationManager.getBillInfoResponse("tic");
		assertEquals("เบอร์โทรศัพท์บ้าน หรือรหัสลูกค้า 12 หลัก",resp.get("ref1TitleTh"));
		assertEquals("Telephone Number/Customer Number",resp.get("ref1TitleEn"));
		assertFalse(resp.containsKey("ref2TitleEn"));
		assertFalse(resp.containsKey("ref2TitleTh"));
		assertEquals("none",resp.get("ref1Type"));
		assertEquals("none",resp.get("ref2Type"));
		assertEquals(Boolean.TRUE,resp.get("isInquiryOnline"));
		assertTrue(resp.containsKey("minAmount"));
		assertTrue(resp.containsKey("maxAmount"));
	}
	
	@Test
	public void getTLPBillInfoPropertySuccess(){
		Map<String, Object> resp = configurationManager.getBillInfoResponse("tlp");
		assertEquals("รหัสลูกค้า/หมายเลขโทรศัพท์",resp.get("ref1TitleTh"));
		assertEquals("Account/Mobile Number",resp.get("ref1TitleEn"));
		assertFalse(resp.containsKey("ref2TitleEn"));
		assertFalse(resp.containsKey("ref2TitleTh"));
		assertEquals("none",resp.get("ref1Type"));
		assertEquals(Boolean.TRUE,resp.get("isInquiryOnline"));
		assertTrue(resp.containsKey("minAmount"));
		assertTrue(resp.containsKey("maxAmount"));
	}
	
	@Test
	public void getTCGBillInfoPropertySuccess(){
		Map<String, Object> resp = configurationManager.getBillInfoResponse("tcg");
		assertEquals("เบอร์โทรศัพท์บ้าน หรือรหัสลูกค้า 12 หลัก",resp.get("ref1TitleTh"));
		assertEquals("Telephone Number/Customer Number",resp.get("ref1TitleEn"));
		assertFalse(resp.containsKey("ref2TitleEn"));
		assertFalse(resp.containsKey("ref2TitleTh"));
		assertEquals("none",resp.get("ref1Type"));
		assertEquals("none",resp.get("ref2Type"));
		assertEquals(Boolean.FALSE,resp.get("isInquiryOnline"));
		assertTrue(resp.containsKey("minAmount"));
		assertTrue(resp.containsKey("maxAmount"));
	}
	
	@Test
	public void getCATVBillInfoPropertySuccess(){
		Map<String, Object> resp = configurationManager.getBillInfoResponse("catv");
		assertEquals("หมายเลขสมาชิกทรูวิชั่นส์",resp.get("ref1TitleTh"));
		assertEquals("Customer Number",resp.get("ref1TitleEn"));
		assertEquals("invoice number",resp.get("ref2TitleEn"));
		assertEquals("เลขที่ใบแจ้งค่าบริการ",resp.get("ref2TitleTh"));
		assertEquals("none",resp.get("ref1Type"));
		assertEquals("none",resp.get("ref2Type"));
		assertEquals(Boolean.FALSE, resp.get("isInquiryOnline"));
		assertTrue(resp.containsKey("minAmount"));
		assertTrue(resp.containsKey("maxAmount"));
	}
	
	@Test
	public void getDSTVBillInfoPropertySuccess(){
		Map<String, Object> resp = configurationManager.getBillInfoResponse("dstv");
		assertEquals("หมายเลขสมาชิกทรูวิชั่นส์",resp.get("ref1TitleTh"));
		assertEquals("Customer Number",resp.get("ref1TitleEn"));
		assertEquals("invoice number",resp.get("ref2TitleEn"));
		assertEquals("เลขที่ใบแจ้งค่าบริการ",resp.get("ref2TitleTh"));
		assertEquals("none",resp.get("ref1Type"));
		assertEquals("none",resp.get("ref2Type"));
		assertEquals(Boolean.FALSE,resp.get("isInquiryOnline"));
		assertTrue(resp.containsKey("minAmount"));
		assertTrue(resp.containsKey("maxAmount"));
	}
	
	@Test
	public void getMEABillInfoPropertySuccess(){
		Map<String, Object> resp = configurationManager.getBillInfoResponse("mea");
		assertEquals("บัญชีแสดงสัญญาเลขที่",resp.get("ref1TitleTh"));
		assertEquals("Account Number",resp.get("ref1TitleEn"));
		assertEquals("Ref. 2",resp.get("ref2TitleEn"));
		assertEquals("เลขที่ใบแจ้งหนี้",resp.get("ref2TitleTh"));
		assertEquals("none",resp.get("ref1Type"));
		assertEquals("none",resp.get("ref2Type"));
		assertEquals(Boolean.TRUE,resp.get("isInquiryOnline"));
		assertTrue(resp.containsKey("minAmount"));
		assertTrue(resp.containsKey("maxAmount"));
	}
	
	@Test
	public void getWATERBillInfoPropertySuccess(){
		Map<String, Object> resp = configurationManager.getBillInfoResponse("water");
		assertEquals("รหัสสาขา + ทะเบียนผู้ใช้น้ำ",resp.get("ref1TitleTh"));
		assertEquals("Account Number",resp.get("ref1TitleEn"));
		assertEquals("Ref. 2",resp.get("ref2TitleEn"));
		assertEquals("เลขที่ใบแจ้งหนี้",resp.get("ref2TitleTh"));
		assertEquals("none",resp.get("ref1Type"));
		assertEquals("none",resp.get("ref2Type"));
		assertEquals(Boolean.FALSE,resp.get("isInquiryOnline"));
		assertTrue(resp.containsKey("minAmount"));
		assertTrue(resp.containsKey("maxAmount"));
	}
	
}
