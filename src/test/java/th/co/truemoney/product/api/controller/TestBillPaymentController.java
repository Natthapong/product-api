package th.co.truemoney.product.api.controller;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.mockito.Mockito;

import th.co.truemoney.serviceinventory.bill.domain.Bill;
import th.co.truemoney.serviceinventory.bill.domain.BillPaymentConfirmationInfo;
import th.co.truemoney.serviceinventory.bill.domain.BillPaymentDraft;
import th.co.truemoney.serviceinventory.bill.domain.BillPaymentTransaction;
import th.co.truemoney.serviceinventory.bill.domain.ServiceFeeInfo;
import th.co.truemoney.serviceinventory.bill.domain.SourceOfFund;
import th.co.truemoney.serviceinventory.ewallet.domain.DraftTransaction;
import th.co.truemoney.serviceinventory.ewallet.domain.DraftTransaction.Status;
import th.co.truemoney.serviceinventory.ewallet.domain.OTP;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public class TestBillPaymentController extends BaseTestController {

        private static final String fakeAccessToken = "111111111111";

        private static final String fakeBarcode = "|01234567890";

        private static final String fakeBillID = "99999999999";

        private static final String fakeBillPaymentID = fakeAccessToken;

        private static final String getBarcodeDetailURL = String.format("/bill-payment/barcode/%s/%s", fakeBarcode, fakeAccessToken);

        private static final String createBillPaymentURL = String.format("/bill-payment/create/%s", fakeAccessToken);

        private static final String confirmBillPaymentURL = String.format("/bill-payment/%s/confirm/%s", fakeBillID, fakeAccessToken);

        private static final String getBillPaymentStatusURL = String.format("/bill-payment/%s/status/%s", fakeBillPaymentID, fakeAccessToken);

        private static final String getBillPaymentDetailURL = String.format("/bill-payment/%s/details/%s", fakeBillPaymentID, fakeAccessToken);
        
        private static final String getBillPaymentFavoriteInfoURL = String.format("/bill-payment/favorite/verify/%s", fakeAccessToken);
        
        private static final String getKeyInBillListURL = "/bill-payment/key-in/list";

        private static String getKeyInBillPaymentInfoURL(String billCode){ 
        	return String.format("/bill-payment/info/%s/%s", billCode, fakeAccessToken);
        }

        private static final String getKeyInBillPaymentURL = String.format("/bill-payment/key-in/%s", fakeAccessToken);

        Bill testBillInfo = createStubbedBillInfo("tcg");

        @Test
        public void getBillInformationSuccess() throws Exception {
                when(
                        billPaymentServiceMock.retrieveBillInformationWithBarcode(
                                anyString(),
                                anyString()
                        )
                ).thenReturn(testBillInfo);

                this.verifySuccess(this.doGET(getBarcodeDetailURL))
                .andExpect(jsonPath("$.data.titleTh").value(""))
                .andExpect(jsonPath("$.data.titleEn").value(""))
                .andExpect(jsonPath("$..isFavoritable").doesNotExist());
        }

        @Test
        public void getBillInformationFail() throws Exception {
                when(
                        billPaymentServiceMock.retrieveBillInformationWithBarcode(
                                anyString(),
                                anyString()
                        )
                ).thenThrow(new ServiceInventoryException(400, "", "", "TMN-PRODUCT"));

                this.verifyFailed(this.doGET(getBarcodeDetailURL));
        }

        @Test
        public void createBillPaymentSuccess() throws Exception {
        		Bill billInfo = new Bill();
        		billInfo.setAmount(BigDecimal.TEN);
        		billInfo.setServiceFee(new ServiceFeeInfo("THB", BigDecimal.ONE));

        		BillPaymentDraft bill = new BillPaymentDraft();
        		bill.setAmount(BigDecimal.TEN);
                bill.setBillInfo(billInfo);

                when(
                        billPaymentServiceMock.verifyPaymentAbility(
                                anyString(),
                                any(BigDecimal.class),
                                anyString()
                        )
                ).thenReturn(bill);

                when(
                        transactionAuthenServiceMock.requestOTP(
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
                        billPaymentServiceMock.verifyPaymentAbility(
                                anyString(),
                                any(BigDecimal.class),
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
        	BillPaymentDraft otpSentDraft = new BillPaymentDraft(null, null, null, null, BillPaymentDraft.Status.OTP_SENT);
    		when(
    			this.billPaymentServiceMock.getBillPaymentDraftDetail(anyString(), anyString())
    		).thenReturn(otpSentDraft);
    				
    		when(
    			this.transactionAuthenServiceMock.verifyOTP(anyString(), any(OTP.class), anyString())
    		).thenReturn(DraftTransaction.Status.OTP_CONFIRMED);
    		
    		when(
    			this.billPaymentServiceMock.performPayment(anyString(), anyString())
    		).thenReturn(BillPaymentTransaction.Status.PROCESSING);
    		
    		this.verifySuccess(this.doPUT(confirmBillPaymentURL, new HashMap<String, Object>()));

            Mockito.verify(billPaymentServiceMock).performPayment(anyString(), anyString());
        }

        @Test
        public void confirmBillPaymentFail() throws Exception {
        	BillPaymentDraft otpSentDraft = new BillPaymentDraft(null, null, null, null, BillPaymentDraft.Status.OTP_SENT);
    		when(
    			this.billPaymentServiceMock.getBillPaymentDraftDetail(anyString(), anyString())
    		).thenReturn(otpSentDraft);
    				
    		when(
    			this.transactionAuthenServiceMock.verifyOTP(anyString(), any(OTP.class), anyString())
    		).thenThrow(new ServiceInventoryException(400, "", "", "TMN-PRODUCT"));

            this.verifyFailed(this.doPUT(confirmBillPaymentURL, new HashMap<String, Object>()));

            Mockito.verify(billPaymentServiceMock, Mockito.never()).performPayment(anyString(), anyString());
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
                ServiceFeeInfo sFee = new ServiceFeeInfo();
                sFee.setFeeRate(new BigDecimal(1000.00));
                sFee.setFeeRateType("THB");

                Bill billInfo = new Bill();
                billInfo.setServiceFee(sFee);
                billInfo.setFavoritable(true);

                BillPaymentDraft bill = new BillPaymentDraft();
                bill.setBillInfo(billInfo);
                bill.setAmount(new BigDecimal("100.00"));


                BillPaymentTransaction bpay = new BillPaymentTransaction();
                bpay.setConfirmationInfo(new BillPaymentConfirmationInfo());
                bpay.setDraftTransaction(bill);

                when(
                        billPaymentServiceMock.getBillPaymentResult(
                                anyString(),
                                anyString()
                        )
                ).thenReturn(bpay);
                when(
                		profileServiceMock.getEwalletBalance(
                				anyString()
                		)
                ).thenReturn(new BigDecimal(100.00));

                this.verifySuccess(this.doGET(getBillPaymentDetailURL))
                .andExpect(jsonPath("$..isFavoritable").value("true"));
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
        
        @Test
        public void verifyAndGetFavoriteBillInfoSuccess() throws Exception{
        	when( billPaymentServiceMock.retrieveBillInformationWithBillCode(
        			anyString(), anyString(), any(BigDecimal.class), anyString()))
        			.thenReturn(createStubbedBillInfo("tcg"));
        	
        	 when(
                     billPaymentServiceMock.verifyPaymentAbility(
                             anyString(),
                             any(BigDecimal.class),
                             anyString()
                     )
             ).thenReturn(createBillPaymentDraftStubbed());
        	
        	Map<String, String> req = new HashMap<String, String>();
        	req.put("billCode", "tcg");
        	req.put("ref1", "010004552");
        	req.put("amount", "10000");
        	
        	this.verifySuccess(this.doPOST(getBillPaymentFavoriteInfoURL, req))
        	.andExpect(jsonPath("$.data").exists())
        	.andExpect(jsonPath("$..ref1").value("010004552"));
        }
        
        @Test
        public void verifyAndGetFavoriteBillInfoFail() throws Exception{
        	when( billPaymentServiceMock.retrieveBillInformationWithBillCode(
        			anyString(), anyString(), any(BigDecimal.class), anyString()))
        			.thenThrow(new ServiceInventoryException(400, "", "", ""));
        	
        	
        	
        	Map<String, String> req = new HashMap<String, String>();
        	req.put("billCode", "tcg");
        	req.put("ref1", "010004552");
        	req.put("amount", "10000");
        	
        	this.verifyFailed(this.doPOST(getBillPaymentFavoriteInfoURL, req));
        }
        
        @Test
        public void getKeyInBillInformationListSuccess() throws Exception {
        	//this.verifySuccess()
        	this.doGET(getKeyInBillListURL);
        }
        
        @Test
        public void getKeyInBillInformationTCGSuccess() throws Exception {
                when(
                        billPaymentServiceMock.retrieveBillInformationWithKeyin(anyString(), anyString())
                ).thenReturn(testBillInfo);

                this.verifySuccess(this.doGET(getKeyInBillPaymentInfoURL("tcg")))
                .andExpect(jsonPath("$..ref1TitleTh").exists())
                .andExpect(jsonPath("$..ref1TitleEn").exists())
                .andExpect(jsonPath("$..ref2TitleTh").doesNotExist())
                .andExpect(jsonPath("$..ref2TitleEn").doesNotExist())
                .andExpect(jsonPath("$..minAmount").exists())
                .andExpect(jsonPath("$..maxAmount").exists())
                .andExpect(jsonPath("$..ref1Type").value("none"))
                .andExpect(jsonPath("$..ref2Type").value("none"))
                .andExpect(jsonPath("$..target").value("tcg"));
        }
        
        @Test
        public void getKeyInBillInformationCATVSuccess() throws Exception {
                when(
                        billPaymentServiceMock.retrieveBillInformationWithKeyin(anyString(), anyString())
                ).thenReturn(createStubbedBillInfo("catv"));

                this.verifySuccess(this.doGET(getKeyInBillPaymentInfoURL("catv")))
                .andExpect(jsonPath("$..ref1TitleTh").exists())
                .andExpect(jsonPath("$..ref1TitleEn").exists())
                .andExpect(jsonPath("$..ref2TitleTh").exists())
                .andExpect(jsonPath("$..ref2TitleEn").exists())
                .andExpect(jsonPath("$..minAmount").exists())
                .andExpect(jsonPath("$..maxAmount").exists())
                .andExpect(jsonPath("$..ref1Type").value("none"))
                .andExpect(jsonPath("$..ref2Type").value("none"))
                .andExpect(jsonPath("$..target").value("catv"));
        }
        
        @Test
        public void getKeyInBillInformationFail() throws Exception {
                when(
                        billPaymentServiceMock.retrieveBillInformationWithKeyin(
                                anyString(),
                                anyString()
                        )
                ).thenThrow(new ServiceInventoryException(400, "", "", "TMN-PRODUCT"));

                this.verifyFailed(this.doGET(getKeyInBillPaymentInfoURL("tmvh_c")));
        }

        @Test
        public void getKeyInBillPaymentTCGSuccess() throws Exception {
        	Map<String, String> req = new HashMap<String, String>();
        	req.put("ref1", "1234567890");
        	req.put("target", "tcg");
        	req.put("amount", "100.00");
        	
            when(
                    billPaymentServiceMock.retrieveBillInformationWithBillCode(
                    		anyString(), anyString(), any(BigDecimal.class), anyString())
            ).thenReturn(testBillInfo);

            this.verifySuccess(this.doPOST(getKeyInBillPaymentURL, req));
        }
        
        @Test
        public void getKeyInBillPaymentCATVSuccess() throws Exception {
        	Map<String, String> req = new HashMap<String, String>();
        	req.put("ref1", "1234567890");
        	req.put("ref2", "1234567890");
        	req.put("target", "catv");
        	req.put("amount", "100.00");
        	
            when(
                    billPaymentServiceMock.updateBillInformation(
                    		anyString(), anyString(), anyString(), any(BigDecimal.class), anyString())
            ).thenReturn(createStubbedBillInfo("catv"));

            this.verifySuccess(this.doPOST(getKeyInBillPaymentURL,req));
        }
        
        @Test
        public void getKeyInBillPaymentFail() throws Exception {
        	Map<String, String> req = new HashMap<String, String>();
        	req.put("ref1", "1234567890");
        	req.put("ref2", "1234567890");
        	req.put("target", "catv");
        	req.put("amount", "100.00");
                when(
                        billPaymentServiceMock.updateBillInformation(
                        		anyString(), anyString(), anyString(), any(BigDecimal.class), anyString())
                ).thenThrow(new ServiceInventoryException(400, "", "", "TMN-PRODUCT"));

                this.verifyFailed(this.doPOST(getKeyInBillPaymentURL, req));
        }
                
        private Bill createStubbedBillInfo(String target) {
                Bill billInfo = new Bill();
                billInfo.setTarget(target);
                billInfo.setLogoURL("https://secure.truemoney-dev.com/m/tmn_webview/images/logo_bill/tcg@2x.png");
                billInfo.setTitleTH("ค่าใช้บริการบริษัทในกลุ่มทรู");
                billInfo.setTitleEN("Convergence Postpay");

                billInfo.setRef1TitleTH("โทรศัพท์พื้นฐาน");
                billInfo.setRef1TitleEN("Fix Line");
                billInfo.setRef1("010004552");

                billInfo.setRef2TitleTH("รหัสลูกค้า");
                billInfo.setRef2TitleEN("Customer ID");
                billInfo.setRef2("010520120200015601");

                billInfo.setAmount(new BigDecimal("10000"));

                billInfo.setFavoritable(false);

                ServiceFeeInfo serviceFee = new ServiceFeeInfo();
                serviceFee.setFeeRate(new BigDecimal("1000"));
                serviceFee.setFeeRateType("THB");
                billInfo.setServiceFee(serviceFee);

                SourceOfFund[] sourceOfFundFees = new SourceOfFund[1];
                SourceOfFund sourceOfFundFee = new SourceOfFund();
                sourceOfFundFee.setSourceType("EW");
                sourceOfFundFee.setFeeRate(new BigDecimal("1000"));
                sourceOfFundFee.setFeeRateType("THB");
                sourceOfFundFee.setMinFeeAmount(new BigDecimal("100"));
                sourceOfFundFee.setMaxFeeAmount(new BigDecimal("2500"));
                sourceOfFundFees[0] = sourceOfFundFee;
                billInfo.setSourceOfFundFees(sourceOfFundFees);

                return billInfo;
        }

        private BillPaymentDraft createBillPaymentDraftStubbed(){
	    	Bill bill = createStubbedBillInfo("tcg");
    		return new BillPaymentDraft("1111111111", bill, new BigDecimal(11000), "123567890", Status.OTP_CONFIRMED);
    	}

}