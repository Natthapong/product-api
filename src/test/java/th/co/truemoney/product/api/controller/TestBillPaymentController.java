package th.co.truemoney.product.api.controller;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import th.co.truemoney.serviceinventory.bill.domain.BillPaymentDraft;
import th.co.truemoney.serviceinventory.bill.domain.Bill;
import th.co.truemoney.serviceinventory.bill.domain.BillPaySourceOfFund;
import th.co.truemoney.serviceinventory.bill.domain.BillPaymentTransaction;
import th.co.truemoney.serviceinventory.bill.domain.BillPaymentConfirmationInfo;
import th.co.truemoney.serviceinventory.bill.domain.ServiceFee;
import th.co.truemoney.serviceinventory.ewallet.domain.OTP;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

public class TestBillPaymentController extends BaseTestController {

	private static final String fakeAccessToken = "111111111111";

	private static final String fakeBarcode = "|01234567890";

	private static final String fakeBillID = "99999999999";

	private static final String fakeBillPaymentID = fakeAccessToken;

	private static final String getBarcodeDetailURL = String.format("/payment/bill/barcode/%s/%s", fakeBarcode, fakeAccessToken);

	private static final String createBillPaymentURL = String.format("/payment/bill/create/%s", fakeAccessToken);

	private static final String confirmBillPaymentURL = String.format("/payment/bill/%s/confirm/%s", fakeBillID, fakeAccessToken);

	private static final String getBillPaymentStatusURL = String.format("/payment/bill/%s/status/%s", fakeBillPaymentID, fakeAccessToken);

	private static final String getBillPaymentDetailURL = String.format("/payment/bill/%s/detail/%s", fakeBillPaymentID, fakeAccessToken);

	Bill testBillInfo = createStubbedBillPaymentInfo();

	@Test
	public void getBillInformationSuccess() throws Exception {
		when(
			billPaymentServiceMock.getBillInformation(
				anyString(),
				anyString()
			)
		).thenReturn(testBillInfo);

		this.verifySuccess(this.doGET(getBarcodeDetailURL));
	}

	@Test
	public void getBillInformationFail() throws Exception {
		when(
			billPaymentServiceMock.getBillInformation(
				anyString(),
				anyString()
			)
		).thenThrow(new ServiceInventoryException(400, "", "", "TMN-PRODUCT"));

		this.verifyFailed(this.doGET(getBarcodeDetailURL));
	}

	@Test
	public void createBillPaymentSuccess() throws Exception {
		BillPaymentDraft bill = new BillPaymentDraft();

		when(
			billPaymentServiceMock.createBill(
				any(Bill.class),
				anyString()
			)
		).thenReturn(bill);

		when(
			billPaymentServiceMock.sendOTP(
				anyString(),
				anyString()
			)
		).thenReturn(new OTP());

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("target", "tmvh");
		params.put("amount", new Double(1000.00));
		params.put("ref1", "");
		params.put("ref2", "");
		params.put("serviceFeeAmount", new Double(10.00));

		this.verifySuccess(this.doPOST(createBillPaymentURL, params));
	}

	@Test
	public void createBillPaymentFail() throws Exception {
		when(
			billPaymentServiceMock.createBill(
				any(Bill.class),
				anyString()
			)
		).thenThrow(new ServiceInventoryException(400, "", "", "TMN-PRODUCT"));

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("target", "tmvh");
		params.put("amount", new Double(1000.00));
		params.put("ref1", "");
		params.put("ref2", "");
		params.put("serviceFeeAmount", new Double(10.00));

		this.verifyFailed(this.doPOST(createBillPaymentURL, params));
	}

	@Test
	public void confirmBillPaymentSuccess() throws Exception {
		when(
			billPaymentServiceMock.confirmBill(
				anyString(),
				any(OTP.class),
				anyString()
			)
		).thenReturn(BillPaymentDraft.Status.CREATED);

		this.verifySuccess(this.doPUT(confirmBillPaymentURL, new HashMap<String, Object>()));
	}

	@Test
	public void confirmBillPaymentFail() throws Exception {
		when(
			billPaymentServiceMock.confirmBill(
				anyString(),
				any(OTP.class),
				anyString()
			)
		).thenThrow(new ServiceInventoryException(400, "", "", "TMN-PRODUCT"));

		this.verifyFailed(this.doPUT(confirmBillPaymentURL, new HashMap<String, Object>()));
	}

	@Test
	public void getBillPaymentStatusSuccess() throws Exception {
		when(
			billPaymentServiceMock.getBillPaymentStatus(
				anyString(),
				anyString()
			)
		).thenReturn(BillPaymentTransaction.Status.PROCESSING);

		this.verifySuccess(this.doGET(getBillPaymentStatusURL));
	}

	@Test
	public void getBillPaymentStatusFail() throws Exception {
		when(
			billPaymentServiceMock.getBillPaymentStatus(
				anyString(),
				anyString()
			)
		).thenThrow(new ServiceInventoryException(400, "", "", "TMN-PRODUCT"));

		this.verifyFailed(this.doGET(getBillPaymentStatusURL));
	}

	@Test
	public void getBillPaymentDetailSuccess() throws Exception {
		ServiceFee sFee = new ServiceFee();
		sFee.setFee(new BigDecimal(1000.00));

		Bill billInfo = new Bill();
		billInfo.setServiceFee(sFee);

		BillPaymentDraft bill = new BillPaymentDraft();
		bill.setBillInfo(billInfo);

		BillPaymentTransaction bpay = new BillPaymentTransaction();
		bpay.setConfirmationInfo(new BillPaymentConfirmationInfo());
		bpay.setDraftTransaction(bill);

		when(
			billPaymentServiceMock.getBillPaymentResult(
				anyString(),
				anyString()
			)
		).thenReturn(bpay);

		this.verifySuccess(this.doGET(getBillPaymentDetailURL));
	}

	@Test
	public void getBillPaymentDetailFail() throws Exception {
		when(
			billPaymentServiceMock.getBillPaymentResult(
				anyString(),
				anyString()
			)
		).thenThrow(new ServiceInventoryException(400, "", "", "TMN-PRODUCT"));

		this.verifyFailed(this.doGET(getBillPaymentDetailURL));
	}

	private Bill createStubbedBillPaymentInfo() {
		Bill billPaymentInfo = new Bill();
		billPaymentInfo.setTarget("tcg");
		billPaymentInfo.setLogoURL("https://secure.truemoney-dev.com/m/tmn_webview/images/logo_bill/tmvh@2x.png");
		billPaymentInfo.setTitleTH("ค่าใช้บริการบริษัทในกลุ่มทรู");
		billPaymentInfo.setTitleEN("Convergence Postpay");

		billPaymentInfo.setRef1TitleTH("โทรศัพท์พื้นฐาน");
		billPaymentInfo.setRef1TitleEN("Fix Line");
		billPaymentInfo.setRef1("010004552");

		billPaymentInfo.setRef2TitleTH("รหัสลูกค้า");
		billPaymentInfo.setRef2TitleEN("Customer ID");
		billPaymentInfo.setRef2("010520120200015601");

		billPaymentInfo.setAmount(new BigDecimal("10000"));

		ServiceFee serviceFee = new ServiceFee();
		serviceFee.setFee(new BigDecimal("1000"));
		serviceFee.setFeeType("THB");
		serviceFee.setTotalFee(new BigDecimal("1000"));
		serviceFee.setMinFeeAmount(new BigDecimal("100"));
		serviceFee.setMaxFeeAmount(new BigDecimal("2500"));
		billPaymentInfo.setServiceFee(serviceFee);

		BillPaySourceOfFund[] sourceOfFundFees = new BillPaySourceOfFund[1];
		BillPaySourceOfFund sourceOfFundFee = new BillPaySourceOfFund();
		sourceOfFundFee.setSourceType("EW");
		sourceOfFundFee.setFee(new BigDecimal("1000"));
		sourceOfFundFee.setFeeType("THB");
		sourceOfFundFee.setTotalFee(new BigDecimal("1000"));
		sourceOfFundFee.setMinFeeAmount(new BigDecimal("100"));
		sourceOfFundFee.setMaxFeeAmount(new BigDecimal("2500"));
		sourceOfFundFees[0] = sourceOfFundFee;
		billPaymentInfo.setSourceOfFundFees(sourceOfFundFees);

		return billPaymentInfo;
	}

}
