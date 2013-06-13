package th.co.truemoney.product.api.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;

public class TestBillOnlineUtil {
	
	private BillReferenceUtil billReferenceUtil;
	
	@Before
	public void setup(){
		billReferenceUtil = new BillReferenceUtil();
	}
	
	@Test
	public void successGetBillOnlineProperty(){
		assertEquals(true, billReferenceUtil.isOnlineInquiry("mea"));
	}
	
	@Test
	public void failGetBillOnlineProperty(){
		assertFalse(billReferenceUtil.isOnlineInquiry("xxx"));
	}
	
	@Test
	public void getTMVHBillInfoPropertySuccess(){
		Map<String, String> resp = billReferenceUtil.getBillInfoResponse("tmvh");
		assertEquals("เบอร์โทรศัพท์ทรูมูฟ เอช",resp.get("ref1TitleTh"));
		assertEquals("เบอร์โทรศัพท์ทรูมูฟ เอช",resp.get("ref1TitleEn"));
		assertEquals("",resp.get("ref2TitleEn"));
		assertEquals("",resp.get("ref2TitleTh"));
		assertEquals("mobile",resp.get("ref1Type"));
		assertEquals("none",resp.get("ref2Type"));
		assertEquals(Boolean.TRUE,resp.get("isInquiryOnline"));
		assertTrue(resp.containsKey("minAmount"));
		assertTrue(resp.containsKey("maxAmount"));
	}
	
	@Test
	public void getTRMVBillInfoPropertySuccess(){
		Map<String, String> resp = billReferenceUtil.getBillInfoResponse("trmv");
		assertEquals("เบอร์โทรศัพท์ทรูมูฟ",resp.get("ref1TitleTh"));
		assertEquals("เบอร์โทรศัพท์ทรูมูฟ",resp.get("ref1TitleEn"));
		assertEquals("",resp.get("ref2TitleEn"));
		assertEquals("",resp.get("ref2TitleTh"));
		assertEquals("mobile",resp.get("ref1Type"));
		assertEquals(Boolean.TRUE,resp.get("isInquiryOnline"));
		assertTrue(resp.containsKey("minAmount"));
		assertTrue(resp.containsKey("maxAmount"));
	}
	
	@Test
	public void getTRBillInfoPropertySuccess(){
		Map<String, String> resp = billReferenceUtil.getBillInfoResponse("tr");
		assertEquals("เบอร์โทรศัพท์บ้าน หรือรหัสลูกค้า 12 หลัก",resp.get("ref1TitleTh"));
		assertEquals("เบอร์โทรศัพท์บ้าน หรือรหัสลูกค้า 12 หลัก",resp.get("ref1TitleEn"));
		assertEquals("",resp.get("ref2TitleEn"));
		assertEquals("",resp.get("ref2TitleTh"));
		assertEquals("none",resp.get("ref1Type"));
		assertEquals("none",resp.get("ref2Type"));
		assertEquals(Boolean.TRUE,resp.get("isInquiryOnline"));
		assertTrue(resp.containsKey("minAmount"));
		assertTrue(resp.containsKey("maxAmount"));
	}
	
	@Test
	public void getTIBillInfoPropertySuccess(){
		Map<String, String> resp = billReferenceUtil.getBillInfoResponse("ti");
		assertEquals("เบอร์โทรศัพท์บ้าน หรือรหัสลูกค้า 12 หลัก",resp.get("ref1TitleTh"));
		assertEquals("เบอร์โทรศัพท์บ้าน หรือรหัสลูกค้า 12 หลัก",resp.get("ref1TitleEn"));
		assertEquals("",resp.get("ref2TitleEn"));
		assertEquals("",resp.get("ref2TitleTh"));
		assertEquals("none",resp.get("ref1Type"));
		assertEquals("none",resp.get("ref2Type"));
		assertEquals(true,resp.get("isInquiryOnline"));
		assertTrue(resp.containsKey("minAmount"));
		assertTrue(resp.containsKey("maxAmount"));
	}
	
	@Test
	public void getTICBillInfoPropertySuccess(){
		Map<String, String> resp = billReferenceUtil.getBillInfoResponse("tic");
		assertEquals("เบอร์โทรศัพท์บ้าน หรือรหัสลูกค้า 12 หลัก",resp.get("ref1TitleTh"));
		assertEquals("เบอร์โทรศัพท์บ้าน หรือรหัสลูกค้า 12 หลัก",resp.get("ref1TitleEn"));
		assertEquals("",resp.get("ref2TitleEn"));
		assertEquals("",resp.get("ref2TitleTh"));
		assertEquals("none",resp.get("ref1Type"));
		assertEquals("none",resp.get("ref2Type"));
		assertEquals(Boolean.TRUE,resp.get("isInquiryOnline"));
		assertTrue(resp.containsKey("minAmount"));
		assertTrue(resp.containsKey("maxAmount"));
	}
	
	@Test
	public void getTLPBillInfoPropertySuccess(){
		Map<String, String> resp = billReferenceUtil.getBillInfoResponse("tlp");
		assertEquals("เบอร์โทรศัพท์บ้าน หรือรหัสลูกค้า 12 หลัก",resp.get("ref1TitleTh"));
		assertEquals("เบอร์โทรศัพท์บ้าน หรือรหัสลูกค้า 12 หลัก",resp.get("ref1TitleEn"));
		assertEquals("",resp.get("ref2TitleEn"));
		assertEquals("",resp.get("ref2TitleTh"));
		assertEquals("none",resp.get("ref1Type"));
		assertEquals(Boolean.TRUE,resp.get("isInquiryOnline"));
		assertTrue(resp.containsKey("minAmount"));
		assertTrue(resp.containsKey("maxAmount"));
	}
	
	@Test
	public void getTCGBillInfoPropertySuccess(){
		Map<String, String> resp = billReferenceUtil.getBillInfoResponse("tcg");
		assertEquals("เบอร์โทรศัพท์บ้าน หรือรหัสลูกค้า 12 หลัก",resp.get("ref1TitleTh"));
		assertEquals("เบอร์โทรศัพท์บ้าน หรือรหัสลูกค้า 12 หลัก",resp.get("ref1TitleEn"));
		assertEquals("",resp.get("ref2TitleEn"));
		assertEquals("",resp.get("ref2TitleTh"));
		assertEquals("none",resp.get("ref1Type"));
		assertEquals("none",resp.get("ref2Type"));
		assertEquals(Boolean.FALSE,resp.get("isInquiryOnline"));
		assertTrue(resp.containsKey("minAmount"));
		assertTrue(resp.containsKey("maxAmount"));
	}
	
	@Test
	public void getCATVBillInfoPropertySuccess(){
		Map<String, String> resp = billReferenceUtil.getBillInfoResponse("catv");
		assertEquals("หมายเลขสมาชิกทรูวิชั่นส์",resp.get("ref1TitleTh"));
		assertEquals("หมายเลขสมาชิกทรูวิชั่นส์",resp.get("ref1TitleEn"));
		assertEquals("เลขที่ใบแจ้งค่าบริการ",resp.get("ref2TitleEn"));
		assertEquals("เลขที่ใบแจ้งค่าบริการ",resp.get("ref2TitleTh"));
		assertEquals("none",resp.get("ref1Type"));
		assertEquals("none",resp.get("ref2Type"));
		assertEquals(Boolean.FALSE, resp.get("isInquiryOnline"));
		assertTrue(resp.containsKey("minAmount"));
		assertTrue(resp.containsKey("maxAmount"));
	}
	
	@Test
	public void getDSTVBillInfoPropertySuccess(){
		Map<String, String> resp = billReferenceUtil.getBillInfoResponse("dstv");
		assertEquals("หมายเลขสมาชิกทรูวิชั่นส์",resp.get("ref1TitleTh"));
		assertEquals("หมายเลขสมาชิกทรูวิชั่นส์",resp.get("ref1TitleEn"));
		assertEquals("เลขที่ใบแจ้งค่าบริการ",resp.get("ref2TitleEn"));
		assertEquals("เลขที่ใบแจ้งค่าบริการ",resp.get("ref2TitleTh"));
		assertEquals("none",resp.get("ref1Type"));
		assertEquals("none",resp.get("ref2Type"));
		assertEquals(Boolean.FALSE,resp.get("isInquiryOnline"));
		assertTrue(resp.containsKey("minAmount"));
		assertTrue(resp.containsKey("maxAmount"));
	}
	
	@Test
	public void getMEABillInfoPropertySuccess(){
		Map<String, String> resp = billReferenceUtil.getBillInfoResponse("mea");
		assertEquals("บัญชีแสดงสัญญาเลขที่",resp.get("ref1TitleTh"));
		assertEquals("บัญชีแสดงสัญญาเลขที่",resp.get("ref1TitleEn"));
		assertEquals("เลขที่ใบแจ้งหนี้",resp.get("ref2TitleEn"));
		assertEquals("เลขที่ใบแจ้งหนี้",resp.get("ref2TitleTh"));
		assertEquals("none",resp.get("ref1Type"));
		assertEquals("none",resp.get("ref2Type"));
		assertEquals(Boolean.TRUE,resp.get("isInquiryOnline"));
		assertTrue(resp.containsKey("minAmount"));
		assertTrue(resp.containsKey("maxAmount"));
	}
	
	@Test
	public void getWATERBillInfoPropertySuccess(){
		Map<String, String> resp = billReferenceUtil.getBillInfoResponse("water");
		assertEquals("รหัสสาขา + ทะเบียนผู้ใช้น้ำ",resp.get("ref1TitleTh"));
		assertEquals("รหัสสาขา + ทะเบียนผู้ใช้น้ำ",resp.get("ref1TitleEn"));
		assertEquals("เลขที่ใบแจ้งหนี้",resp.get("ref2TitleEn"));
		assertEquals("เลขที่ใบแจ้งหนี้",resp.get("ref2TitleTh"));
		assertEquals("none",resp.get("ref1Type"));
		assertEquals("none",resp.get("ref2Type"));
		assertEquals(Boolean.FALSE,resp.get("isInquiryOnline"));
		assertTrue(resp.containsKey("minAmount"));
		assertTrue(resp.containsKey("maxAmount"));
	}
}
