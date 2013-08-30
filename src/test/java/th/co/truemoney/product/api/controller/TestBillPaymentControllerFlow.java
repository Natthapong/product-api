package th.co.truemoney.product.api.controller;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import th.co.truemoney.serviceinventory.bill.domain.Bill;
import th.co.truemoney.serviceinventory.bill.domain.BillPaymentDraft;
import th.co.truemoney.serviceinventory.bill.domain.BillPaymentTransaction;
import th.co.truemoney.serviceinventory.bill.domain.InquiryOutstandingBillType;
import th.co.truemoney.serviceinventory.bill.domain.ServiceFeeInfo;
import th.co.truemoney.serviceinventory.ewallet.domain.DraftTransaction.Status;
import th.co.truemoney.serviceinventory.ewallet.domain.OTP;

public class TestBillPaymentControllerFlow extends BaseTestController {

    @Test
    public void payFavoriteBill() throws Exception {
        String inputAmount = "200.00";

        // making bill amount differ from input amount to check if we use the right amount to pay
        BigDecimal billAmount = new BigDecimal(300.00);
        BigDecimal topupAmount = new BigDecimal(inputAmount);
        BigDecimal serviceFee = new BigDecimal(10.00);

        String token = "a-123-456-789";
        String ref1 = "0894432213";
        String ref2 = "";
        String billCode = "tmvh";
        String transactionID = "id_from_verification";

        Bill bill = new Bill("111", billCode, ref1, ref2, billAmount);
        bill.setServiceFee(new ServiceFeeInfo("THB", serviceFee));

        BillPaymentDraft otpConfirmedDraft =
                new BillPaymentDraft("222",
                                     bill,
                                     topupAmount,
                                     transactionID,
                                     Status.OTP_CONFIRMED);

        String returnedBillID = otpConfirmedDraft.getID(); // note: from draft ID
        String billPaymentID = returnedBillID;

        when (
            billPaymentServiceMock.retrieveBillInformationWithUserFavorite(
                    eq(billCode),
                    eq(ref1),
                    eq(ref2),
                    eq(topupAmount),
                    eq(InquiryOutstandingBillType.OFFLINE),
                    eq(token))
        ).thenReturn(bill);

        when (
            billPaymentServiceMock.verifyPaymentAbility(
                    eq(bill.getID()),
                    eq(topupAmount),
                    eq(token))
        ).thenReturn(otpConfirmedDraft);

        when (
            billPaymentServiceMock.getBillPaymentDraftDetail(
                    eq(otpConfirmedDraft.getID()),
                    eq(token))
        ).thenReturn(otpConfirmedDraft);

        when (
            billPaymentServiceMock.performPayment(
                    eq(otpConfirmedDraft.getID()),
                    eq(token))
        ).thenReturn(BillPaymentTransaction.Status.VERIFIED);

        when (
            billPaymentServiceMock.getBillPaymentStatus(
                    eq(billPaymentID),
                    eq(token))
        ).thenReturn(BillPaymentTransaction.Status.PROCESSING);

        when (
            billPaymentServiceMock.getBillPaymentResult(
                    eq(billPaymentID),
                    eq(token))
        ).thenReturn(new BillPaymentTransaction(otpConfirmedDraft));

        // step 1. get bill information using bill code and verify payment with a given amount
        Map<String, String> req1 = new HashMap<String, String>();
        req1.put("billCode", billCode);
        req1.put("amount", inputAmount);
        req1.put("ref1", ref1);
        verifySuccess(
                this.doPOST(String.format("/bill-payment/bill/%s/verify/%s", bill.getID(), token), req1)
                    ).andExpect(jsonPath("$..billID").value(returnedBillID)
                    ).andExpect(jsonPath("$..billPaymentID").value(transactionID)
                    ).andExpect(jsonPath("$..billPaymentStatus").value("OTP_CONFIRMED"));

        // step 2. perform payment confirmation
        Map<String, String> req2 = new HashMap<String, String>();
        req2.put("checksum", "12345");
        verifySuccess(
                this.doPUT(String.format("/bill-payment/%s/confirm/%s", returnedBillID, token), req2)
                    ).andExpect(jsonPath("$..billPaymentStatus").value("VERIFIED")
                    ).andExpect(jsonPath("$..billPaymentID").value(returnedBillID));
        // verify OTP method not touched
        verify(
                this.transactionAuthenServiceMock, never()
        ).verifyOTP(anyString(), any(OTP.class), anyString());

        // step 3. get payment status (async mode)
        verifySuccess(
                this.doGET(String.format("/bill-payment/%s/status/%s", billPaymentID, token))
                    ).andExpect(jsonPath("$..billPaymentStatus").value("PROCESSING"));

        // step 4. when step 3. return status as SUCCESS then get payment details
        verifySuccess(
                this.doGET(String.format("/bill-payment/%s/details/%s", billPaymentID, token))
                    ).andExpect(jsonPath("$..amount").value(inputAmount)
                    ).andExpect(jsonPath("$..totalFee").value("10")
                    ).andExpect(jsonPath("$..totalAmount").value("210.00")
                    ).andExpect(jsonPath("$..billNameEn").value("tmvh")
                    ).andExpect(jsonPath("$..billNameTh").value("ทรูมูฟ เอช"));
    }

}
