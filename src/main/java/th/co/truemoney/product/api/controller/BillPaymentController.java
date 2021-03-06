package th.co.truemoney.product.api.controller;

import java.math.BigDecimal;
import java.security.InvalidParameterException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import th.co.truemoney.product.api.domain.BillResponse;
import th.co.truemoney.product.api.domain.ProductResponse;
import th.co.truemoney.product.api.manager.BillConfigurationManager;
import th.co.truemoney.product.api.manager.MessageManager;
import th.co.truemoney.product.api.util.Utils;
import th.co.truemoney.product.api.util.ValidateUtil;
import th.co.truemoney.serviceinventory.authen.TransactionAuthenService;
import th.co.truemoney.serviceinventory.bill.BillPaymentService;
import th.co.truemoney.serviceinventory.bill.domain.Bill;
import th.co.truemoney.serviceinventory.bill.domain.BillPaymentDraft;
import th.co.truemoney.serviceinventory.bill.domain.BillPaymentTransaction;
import th.co.truemoney.serviceinventory.bill.domain.InquiryOutstandingBillType;
import th.co.truemoney.serviceinventory.ewallet.TmnProfileService;
import th.co.truemoney.serviceinventory.ewallet.domain.DraftTransaction.Status;
import th.co.truemoney.serviceinventory.ewallet.domain.OTP;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

/**
 * Transaction States:
 *   1. Bill
 *   2. BillPaymentDraft
 * 	 3. BillPayTransaction
 *   4. BillPaymentConfirmation
 *
 *   BillPaymentTransaction.ID == BillPaymentDraft.ID
 */

@Controller
@RequestMapping(value = "/bill-payment")
public class BillPaymentController extends BaseController {

    @Autowired
    private BillPaymentService billPaymentService;

    @Autowired
    private TransactionAuthenService authService;

    @Autowired
    private TmnProfileService profileService;

    @Autowired
    private MessageManager messageManager;
    
    @Autowired
    BillConfigurationManager billConfigurationManager;
    
    private Logger logger = Logger.getLogger(BillPaymentController.class);

    // get bill by barcode
    @RequestMapping(value = "/barcode/{barcode}/{accessTokenID}", method = RequestMethod.GET)
    public @ResponseBody
    ProductResponse getBillInformationFromBarcode(
            @PathVariable String barcode,
            @PathVariable String accessTokenID) {

        StopWatch timer = new StopWatch("getBillInformation ("+accessTokenID+")");
        timer.start();
        Bill bill;

        try{
            bill = billPaymentService.retrieveBillInformationWithBarcode(barcode, accessTokenID);
        }catch(ServiceInventoryException e){
            if(("TMN-SERVICE-INVENTORY".equals(e.getErrorNamespace()) && "1012".equals(e.getErrorCode()))){
                String targetTitle = getTargetTitle(Utils.removeSuffix(e.getData().get("target").toString()));
                e.setErrorCode("80000");
                e.setErrorNamespace("TMN-PRODUCT");
                Date dueDate = new Date((Long)e.getData().get("dueDate"));
                e.getData().put("dueDate",Utils.formatDate4Y(dueDate));
                e.getData().put("targetTitle",targetTitle);
                throw e;
            } else if(("TMN-SERVICE-INVENTORY".equals(e.getErrorNamespace()) && "1021".equals(e.getErrorCode()))){
                String targetTitle = getTargetTitle(Utils.removeSuffix(e.getData().get("target").toString()));
                e.setErrorCode("80001");
                e.setErrorNamespace("TMN-PRODUCT");
                Date dueDate = new Date((Long)e.getData().get("dueDate"));
                e.getData().put("dueDate",Utils.formatDate4Y(dueDate));
                e.getData().put("targetTitle",targetTitle);
                throw e;                
            } else{
                throw e;
            }
        }

        Map<String, Object> data = BillResponse.builder()
                                        .setBill(bill)
                                        .buildBillInfoResponse();
        timer.stop();
        logger.info(timer.shortSummary());

        return createResponse(data);
    }

  //get bill key-in
    @RequestMapping(value = "/key-in/bill/{accessTokenID}", method = RequestMethod.POST)
    public @ResponseBody
    ProductResponse getBillInformationFromKeyInBillCode(
            @PathVariable String accessTokenID, @RequestBody Map<String,String> request) {

        String target = request.get("target");
        String ref1 = request.get("ref1");
        String ref2 = request.containsKey("ref2") ? request.get("ref2") : "";
        String inquiryType = request.get("inquiry");
        String inputAmount = request.containsKey("amount") ? request.get("amount") : "0";

        if (ValidateUtil.isEmpty(target)) {
            throw new InvalidParameterException("60000");
        }

        BigDecimal amount = new BigDecimal(inputAmount.replace(",", ""));

        Bill bill;
        try{
            bill = billPaymentService.retrieveBillInformationWithKeyin(target, ref1, ref2, amount, InquiryOutstandingBillType.valueFromString(inquiryType), accessTokenID);
        }catch(ServiceInventoryException e){
            if("PCS.PCS-30024".equals(String.format("%s.%s", e.getErrorNamespace(), e.getErrorCode()))){
                if("tmvh".equals(Utils.removeSuffix(target)) || "trmv".equals(Utils.removeSuffix(target))){
                    throw new ServiceInventoryException(500,"70000","","TMN-PRODUCT");
                }
            }
            throw e;
        }

        Map<String, Object> data = BillResponse.builder()
                .setBill(bill)
                .buildBillInfoResponse();

        String formattedMobileNumber = Utils.formatTelephoneNumber(bill.getRef1());
        data.put("ref1", formattedMobileNumber);

        return createResponse(data);
    }

    // get bill favorite
    @RequestMapping(value = "/favorite/bill/{accessTokenID}", method = RequestMethod.POST)
    public @ResponseBody
    ProductResponse getBillInformationFromFavorite(@PathVariable String accessTokenID,@RequestBody Map<String, String> request){
        // get
        String target = request.get("target");
        String ref1 = request.get("ref1");
        String ref2 = request.containsKey("ref2") ? request.get("ref2") : "";
        String inquiryType = request.get("inquiry");
        String inputAmount = request.containsKey("amount") ? request.get("amount") : "0";

        if (ValidateUtil.isEmpty(target)) {
            throw new InvalidParameterException("60000");
        }

        BigDecimal amount = new BigDecimal(inputAmount.replace(",", ""));

        Bill bill = null;
        try {
            bill = billPaymentService.retrieveBillInformationWithUserFavorite(
            		target, ref1, ref2, amount, InquiryOutstandingBillType.valueFromString(inquiryType), accessTokenID);
        } catch(ServiceInventoryException e) {
        	String error = e.getErrorNamespace() + "." + e.getErrorCode();
            if("PCS.PCS-30024".equals(error)) {
            	String serviceCode = Utils.removeSuffix(target);
                if("tmvh".equals(serviceCode) || "trmv".equals(serviceCode)){
                    throw new ServiceInventoryException(500,"70000","","TMN-PRODUCT");
                } else if("mea".equals(serviceCode)) {
                    throw new ServiceInventoryException(500,"90000","","TMN-PRODUCT");
                }
            } else if(("TMN-SERVICE-INVENTORY.1012".equals(error)) 
            		|| ("TMN-SERVICE-INVENTORY.1020".equals(error))) {
                String serviceCode = e.getData().get("target").toString();
                Date dueDate = new Date((Long)e.getData().get("dueDate"));
                e.setErrorCode("80000");
                e.setErrorNamespace("TMN-PRODUCT");
                e.getData().put("dueDate",Utils.formatDate4Y(dueDate));
                e.getData().put("targetTitle", getTargetTitle(Utils.removeSuffix(serviceCode)));
                throw e;
            } else if ("MEA.C-02".equals(error)) {
            	String targetTitle = getTargetTitle(Utils.removeSuffix(target));
            	Map<String, Object> data = new HashMap<String, Object>();
            	data.put("targetTitle", targetTitle);

            	e.setData(data);
            	e.setErrorCode("80001");
            	e.setErrorNamespace("TMN-PRODUCT");
			}
            
            throw e;
        }

        Map<String, Object> data = BillResponse.builder()
                .setBill(bill)
                .buildBillInfoResponse();

        String formattedMobileNumber = Utils.formatTelephoneNumber(bill.getRef1());
        data.put("ref1", formattedMobileNumber);
        return createResponse(data);
    }

    //verify bill by barcode & key-in
    @RequestMapping(value = "/verify/{accessTokenID}", method = RequestMethod.POST)
    public @ResponseBody
    ProductResponse createBillPayment(
            @PathVariable String accessTokenID,
            @RequestBody Map<String, String> request) {

        StopWatch timer = new StopWatch("createBillPayment ("+accessTokenID+")");
        timer.start();

        String billID = (String)request.get("billID");
        BigDecimal inputAmount = new BigDecimal(request.get("amount").replace(",", ""));

        BillPaymentDraft paymentDraft = this.billPaymentService.verifyPaymentAbility(billID, inputAmount, accessTokenID);

        OTP otp = this.authService.requestOTP(paymentDraft.getID(), accessTokenID);

        Map<String, Object> data = BillResponse.builder()
                                        .setOTP(otp)
                                        .setPaymentDraft(paymentDraft)
                                        .buildBillCreateResponse();

        timer.stop();
        logger.info(timer.shortSummary());

        return createResponse(data);
    }

    // verify favorite bill only
    @RequestMapping(value = "/bill/{billInfoID}/verify/{accessTokenID}", method = RequestMethod.POST)
    public @ResponseBody
    ProductResponse verifyBillPayment(
            @PathVariable String billInfoID,
            @PathVariable String accessTokenID,
            @RequestBody Map<String, String> request) {

        StopWatch timer = new StopWatch("verifyBillPayment for favorite bill ("+accessTokenID+")");
        timer.start();
        String inputAmount = request.get("amount");

        if (ValidateUtil.isEmpty(inputAmount)) {
            throw new InvalidParameterException("60000");
        }

        BigDecimal amount = new BigDecimal(inputAmount.replace(",", ""));
        BillPaymentDraft paymentDraft = billPaymentService.verifyPaymentAbility(
                billInfoID, amount, accessTokenID);

        Map<String, Object> data = BillResponse.builder()
                                        .setPaymentDraft(paymentDraft)
                                        .buildBillFavoriteResponse();

        Bill bill = paymentDraft.getBillInfo();
        if(ValidateUtil.isMobileNumber(bill.getRef1())) {
            String formattedMobileNumber = Utils.formatMobileNumber(bill.getRef1());
            data.put("ref1", formattedMobileNumber);
        }else if(ValidateUtil.isTelNumber(bill.getRef1())){
            String formattedTelNumber = Utils.formatTelNumber(bill.getRef1());
            data.put("ref1", formattedTelNumber);
        }

        timer.stop();
        logger.info(timer.shortSummary());

        return createResponse(data);
    }

    //confirm all bills
    @RequestMapping(value = "/{draftID}/confirm/{accessTokenID}", method = RequestMethod.PUT)
    public @ResponseBody
    ProductResponse confirmBillPayment(
            @PathVariable String draftID,
            @PathVariable String accessTokenID,
            @RequestBody Map<String, String> request) {

        StopWatch timer = new StopWatch("confirmBillPayment ("+accessTokenID+")");
        timer.start();

        BillPaymentDraft draft = billPaymentService.getBillPaymentDraftDetail(draftID, accessTokenID);
        BillPaymentDraft.Status draftStatus = draft.getStatus();

        if (draftStatus != Status.OTP_CONFIRMED) {
            String otpStr = request.get("otpString");
            String otpRef = request.get("otpRefCode");
            String mobile = request.get("mobileNumber");

            OTP otp = new OTP(mobile, otpRef, otpStr);
            authService.verifyOTP(draftID, otp, accessTokenID);
        }

        BillPaymentTransaction.Status transactionStatus = billPaymentService.performPayment(draftID, accessTokenID);

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("billPaymentStatus", transactionStatus.getStatus());
        data.put("billPaymentID", draftID); //billPaymentID has the same value as draftID

        timer.stop();
        logger.info(timer.shortSummary());

        return createResponse(data);
    }

    // transaction status
    @RequestMapping(value = "/{billPaymentID}/status/{accessTokenID}", method = RequestMethod.GET)
    public @ResponseBody
    ProductResponse getBillPaymentStatus(
            @PathVariable String billPaymentID,
            @PathVariable String accessTokenID) {

        StopWatch timer = new StopWatch("getBillPaymentStatus ("+accessTokenID+")");
        timer.start();

        BillPaymentTransaction.Status sts = this.billPaymentService.getBillPaymentStatus(billPaymentID, accessTokenID);

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("billPaymentStatus", sts.getStatus());

        timer.stop();
        logger.info(timer.shortSummary());

        return createResponse(data);
    }

    //get payment result
    @RequestMapping(value = "/{billPaymentID}/details/{accessTokenID}", method = RequestMethod.GET)
    public @ResponseBody
    ProductResponse getBillPaymentDetail(
            @PathVariable String billPaymentID,
            @PathVariable String accessTokenID) {

        StopWatch timer = new StopWatch("getBillPaymentDetail ("+accessTokenID+")");
        timer.start();

        BillPaymentTransaction txn = this.billPaymentService.getBillPaymentResult(billPaymentID, accessTokenID);

        BigDecimal currentBalance = this.profileService.getEwalletBalance(accessTokenID);

        Map<String, Object> data = BillResponse.builder()
                                        .setPaymentTransaction(txn)
                                        .setWalletBalance(currentBalance)
                                        .buildBillPaymentDetailResponse();

        Bill bill = txn.getDraftTransaction().getBillInfo();
        String target = Utils.removeSuffix(bill.getTarget());
        
        if ("tmvh".equals(target) || "trmv".equals(target) || "rft".equals(target)) {
            // remark message that display at the bottom of receipt
            data.put("remarkEn", messageManager.getMessageEn("payment.bill.remark"));
            data.put("remarkTh", messageManager.getMessageTh("payment.bill.remark"));
        }

        if(ValidateUtil.isMobileNumber(bill.getRef1())){
            String formattedMobileNumber = Utils.formatMobileNumber(bill.getRef1());
            data.put("ref1", formattedMobileNumber);
        }else if(ValidateUtil.isTelNumber(bill.getRef1())){
            String formattedTelNumber = Utils.formatTelNumber(bill.getRef1());
            data.put("ref1", formattedTelNumber);
        }

        timer.stop();
        logger.info(timer.shortSummary());

        return createResponse(data);
    }
    
    //getKeyIn placeholder
    @RequestMapping(value = "/key-in/place-holder/{billCode}/{accessTokenID}", method = RequestMethod.GET)
    public @ResponseBody
    ProductResponse getKeyInInputPlaceHolders(
            @PathVariable String accessTokenID, 
            @PathVariable String billCode) {
        
    	Map<String, Object> data = new HashMap<String, Object>();
    	data.put("target", billCode);
    	data.putAll(billConfigurationManager.getBillInfoResponse(billCode));

        return createResponse(data);
    }
    
    private ProductResponse createResponse(Map<String, Object> data) {
        return this.responseFactory.createSuccessProductResonse(data);
    }

    public void setBillPaymentService(BillPaymentService billPaymentService) {
        this.billPaymentService = billPaymentService;
    }

    public void setAuthService(TransactionAuthenService authService) {
        this.authService = authService;
    }

    public void setProfileService(TmnProfileService profileService) {
        this.profileService = profileService;
    }

    public void setBillConfigurationManager(
			BillConfigurationManager billConfigurationManager) {
		this.billConfigurationManager = billConfigurationManager;
	}

	private String getTargetTitle(String target){
        String result = "";
        if("mea".equals(target)){
            result = "การไฟฟ้านครหลวง";
        }else if("water".equals(target)){
            result = "การประปานครหลวง";
        }else if("tli".equals(target)){
            result = "ไทยประกันชีวิต";
        }else if("dlt".equals(target)){
            result = "กรมการขนส่งทางบก";
        }
        return result;
    }

}

