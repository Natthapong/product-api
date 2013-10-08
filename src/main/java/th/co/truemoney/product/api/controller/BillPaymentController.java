package th.co.truemoney.product.api.controller;

import java.math.BigDecimal;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import th.co.truemoney.product.api.manager.BillCategoryConfigurationManager;
import th.co.truemoney.product.api.manager.BillConfiguration;
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
	
	private static final String PRODUCT_NSPACE = "TMN-PRODUCT";

	private static final String PARAM_REF1 = "ref1";
    private static final String PARAM_REF2 = "ref2";
    private static final String PARAM_TARGET = "target";
    private static final String PARAM_AMOUNT = "amount";
    private static final String PARAM_BILL_ID = "billID";
    private static final String PARAM_DUE_DATE = "dueDate";
    private static final String PARAM_OTP_STR = "otpString";
    private static final String PARAM_REMARK_TH = "remarkTh";
    private static final String PARAM_REMARK_EN = "remarkEn";
    private static final String PARAM_OTP_REF = "otpRefCode";
    private static final String PARAM_INQUIRY_TYPE = "inquiry";
    private static final String PARAM_BARCODE_PREFIX = "barcode";
    private static final String PARAM_BILL_NAME_TH = "billNameTh";
    private static final String PARAM_BILL_NAME_EN = "billNameEn";
    private static final String PARAM_TARGET_TITLE = "targetTitle";
    private static final String PARAM_MOBILE_NUMBER = "mobileNumber";
    private static final String PARAM_BILL_PAYMENT_ID = "billPaymentID";
    private static final String PARAM_BILL_PAYMENT_STATUS = "billPaymentStatus";
    
    private static Map<String, String> targetNameMap = new HashMap<String, String>();
    static {
    	targetNameMap.put("tli", "ไทยประกันชีวิต");
    	targetNameMap.put("mea", "การไฟฟ้านครหลวง");
    	targetNameMap.put("dlt", "กรมการขนส่งทางบก");
    	targetNameMap.put("pea", "การไฟฟ้าส่วนภูมิภาค");
    	targetNameMap.put("water", "การประปานครหลวง");
    }
    
	private static final Map<String, String[]> billInfoErrorMapping = new HashMap<String, String[]>();
    static {
    	billInfoErrorMapping.put("PCS:PCS-30024", new String[]{ PRODUCT_NSPACE, "70000"});
    	billInfoErrorMapping.put("TMN-SERVICE-INVENTORY:1012", new String[]{ PRODUCT_NSPACE, "80000" });
    	billInfoErrorMapping.put("TMN-SERVICE-INVENTORY:1020", new String[]{ PRODUCT_NSPACE, "80000" });
    	billInfoErrorMapping.put("TMN-SERVICE-INVENTORY:1021", new String[]{ PRODUCT_NSPACE, "80001" });
    	billInfoErrorMapping.put("MEA:C-02", new String[]{ "TMN-PRODUCT", "80001"});
    }

	private Logger logger = Logger.getLogger(BillPaymentController.class);

	@Autowired
	private BillConfigurationManager billConfigurationManager;
	
	@Autowired
	private BillCategoryConfigurationManager billCategoryConfigurationManager;
	
    @Autowired
    private BillPaymentService billPaymentService;

    @Autowired
    private TransactionAuthenService authService;

    @Autowired
    private TmnProfileService profileService;

    @Autowired
    private MessageManager messageManager;
    
    /**
     * Get multi-barcode bill information
     * 
     * @param accessTokenID
     * @param barcodeList
     * @return
     */
    @RequestMapping(value = "/barcode/info/{accessTokenID}", method = RequestMethod.POST) 
    public @ResponseBody
    ProductResponse getBillInformationFromMultiBarcode(
    		@PathVariable String accessTokenID,
    		@RequestBody Map<String, String> request) {
    	
    	StopWatch timer = startTimer("getBillInformation ("+accessTokenID+")");
        
        List<String> barcodeList = getBarcodeParameterList(request);
        if (barcodeList.isEmpty()) {
        	throw new InvalidParameterException("5000");//TODO Fix this
        }
        
        Bill bill = getBillInformationFromBarcode(barcodeList, accessTokenID);
        
        Map<String, Object> data = BillResponse.builder()
                                        .setBill(bill)
                                        .buildBillInfoResponse();
        stopTimer(timer);

        return createResponse(data);
    }
    
    private List<String> getBarcodeParameterList(Map<String, String> request) {
    	List<String> barcodeList = new ArrayList<String>();
        Set<String> keys = request.keySet();
        for (String k : keys) {
        	// 'barcode1', 'barcode2', 'barcode3' is expected
        	if (k.startsWith(PARAM_BARCODE_PREFIX)) {
        		barcodeList.add(request.get(k));
        	}
        }
        return barcodeList;
    }
    
    /**
     * Get single barcode bill information
     * 
     * @param barcode
     * @param accessTokenID
     * @return
     */
    @RequestMapping(value = "/barcode/{barcode}/{accessTokenID}", method = RequestMethod.GET)
    public @ResponseBody
    ProductResponse getBillInformationFromBarcode(
            @PathVariable String barcode,
            @PathVariable String accessTokenID) {

        StopWatch timer = startTimer("getBillInformation ("+accessTokenID+")");
        
        List<String> barcodeList = Arrays.asList(barcode);
        Bill bill = getBillInformationFromBarcode(barcodeList, accessTokenID);

        Map<String, Object> data = BillResponse.builder()
                                        .setBill(bill)
                                        .buildBillInfoResponse();
        stopTimer(timer);

        return createResponse(data);
    }
        
    private Bill getBillInformationFromBarcode(List<String> barcodeList, String accessTokenID) {
    	try{
            return billPaymentService.retrieveBillInformationWithBarcode(barcodeList, accessTokenID);
        } catch(ServiceInventoryException e) {
        	String errorLookupKey = String.format("%s:%s", e.getErrorNamespace(), e.getErrorCode());
        	if (billInfoErrorMapping.containsKey(errorLookupKey)) {
        		String targetCode = Utils.removeSuffix(e.getData().get(PARAM_TARGET).toString());
        		e.getData().put(PARAM_TARGET_TITLE, getTargetTitle(targetCode));
        		Date dueDate = new Date((Long)e.getData().get(PARAM_DUE_DATE));
        		e.getData().put(PARAM_DUE_DATE,Utils.formatDate4Y(dueDate));
        		String[] newError = billInfoErrorMapping.get(errorLookupKey);
        		e.setErrorNamespace(newError[0]);
        		e.setErrorCode(newError[1]);
        	}
            throw e;
        }
    }
    
    private String getRequestParameter(Map<String, String> parameters, String parameterName, String defaultValue)  {
    	return parameters.containsKey(parameterName) ? parameters.get(parameterName) : defaultValue;
    }
    
    //get bill key-in
    @RequestMapping(value = "/key-in/bill/{accessTokenID}", method = RequestMethod.POST)
    public @ResponseBody
    ProductResponse getBillInformationFromKeyInBillCode(
            @PathVariable String accessTokenID, 
            @RequestBody Map<String,String> request) {

        String ref1Val = getRequestParameter(request, PARAM_REF1, "");
        String ref2Val = getRequestParameter(request, PARAM_REF2, "");
        String target = getRequestParameter(request, PARAM_TARGET, "");
        String inputAmount = getRequestParameter(request, PARAM_AMOUNT, "0");
        String inquiryType = getRequestParameter(request, PARAM_INQUIRY_TYPE, "");

        if (ValidateUtil.isEmpty(target)) {
            throw new InvalidParameterException("60000");
        }

        BigDecimal amount = new BigDecimal(inputAmount.replace(",", ""));
        InquiryOutstandingBillType type = InquiryOutstandingBillType.valueFromString(inquiryType);

        Bill bill;
        try{
            bill = billPaymentService.retrieveBillInformationWithKeyin(target, ref1Val, ref2Val, amount, type, accessTokenID);
        } catch(ServiceInventoryException e)  {
        	String errorLookupKey = String.format("%s:%s", e.getErrorNamespace(), e.getErrorCode());
            if (billInfoErrorMapping.containsKey(errorLookupKey)) {
            	String targetCode = Utils.removeSuffix(target);
            	String[] newError = billInfoErrorMapping.get(errorLookupKey);
            	if ("tmvh".equalsIgnoreCase(targetCode) || "trmv".equalsIgnoreCase(targetCode)) {
            		throw new ServiceInventoryException(500, newError[1],"", newError[0]);
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
    ProductResponse getBillInformationFromFavorite(
    		@PathVariable String accessTokenID,
    		@RequestBody Map<String, String> request) {
    	
    	String ref1Val = getRequestParameter(request, PARAM_REF1, "");
        String ref2Val = getRequestParameter(request, PARAM_REF2, "");
        String target = getRequestParameter(request, PARAM_TARGET, "");
        String inputAmount = getRequestParameter(request, PARAM_AMOUNT, "0");
        String inquiryType = getRequestParameter(request, PARAM_INQUIRY_TYPE, "");

        if (ValidateUtil.isEmpty(target)) {
            throw new InvalidParameterException("60000");
        }

        BigDecimal amount = new BigDecimal(inputAmount.replace(",", ""));
        InquiryOutstandingBillType type = InquiryOutstandingBillType.valueFromString(inquiryType);
        
        Bill bill = null;
        try {
            bill = billPaymentService.retrieveBillInformationWithUserFavorite(target, ref1Val, ref2Val, amount, type, accessTokenID);
        } catch(ServiceInventoryException e) {
        	String errorLookupKey = e.getErrorNamespace() + ":" + e.getErrorCode();
        	if("PCS:PCS-30024".equals(errorLookupKey)) {
            	String serviceCode = Utils.removeSuffix(target);
                if("tmvh".equals(serviceCode) || "trmv".equals(serviceCode)){
                    throw new ServiceInventoryException(500, "70000","", PRODUCT_NSPACE);
                } else if("mea".equals(serviceCode)) {
                    throw new ServiceInventoryException(500, "90000","", PRODUCT_NSPACE);
                }
        	} else if (billInfoErrorMapping.containsKey(errorLookupKey)) {
            	String[] newError = billInfoErrorMapping.get(errorLookupKey);
                e.setErrorCode(newError[1]);
                e.setErrorNamespace(newError[0]);
                
                if (e.getData().containsKey(PARAM_TARGET)) {
                	String targetCode = e.getData().get(PARAM_TARGET).toString();
                	e.getData().put(PARAM_TARGET_TITLE, getTargetTitle(Utils.removeSuffix(targetCode)));
                }
                
                if (e.getData().containsKey(PARAM_DUE_DATE)) {
                	Date dueDate = new Date((Long)e.getData().get(PARAM_DUE_DATE));
                	e.getData().put(PARAM_DUE_DATE,Utils.formatDate4Y(dueDate));
                }
            }
            throw e;
        }

        Map<String, Object> data = BillResponse.builder()
                .setBill(bill)
                .buildBillInfoResponse();

        String formattedMobileNumber = Utils.formatTelephoneNumber(bill.getRef1());
        data.put(PARAM_REF1, formattedMobileNumber);
        return createResponse(data);
    }

    //verify bill by barcode & key-in
    @RequestMapping(value = "/verify/{accessTokenID}", method = RequestMethod.POST)
    public @ResponseBody
    ProductResponse createBillPayment(
            @PathVariable String accessTokenID,
            @RequestBody Map<String, String> request) {

        StopWatch timer = startTimer("createBillPayment ("+accessTokenID+")");

        String billID = getRequestParameter(request, PARAM_BILL_ID, "");
        String amount = getRequestParameter(request, PARAM_AMOUNT, "").replaceAll(",", "");
        BigDecimal inputAmount = new BigDecimal(amount);

        BillPaymentDraft paymentDraft = this.billPaymentService.verifyPaymentAbility(billID, inputAmount, accessTokenID);

        OTP otp = this.authService.requestOTP(paymentDraft.getID(), accessTokenID);

        Map<String, Object> data = BillResponse.builder()
                                        .setOTP(otp)
                                        .setPaymentDraft(paymentDraft)
                                        .buildBillCreateResponse();
        stopTimer(timer);

        return createResponse(data);
    }

    // verify favorite bill only
    @RequestMapping(value = "/bill/{billInfoID}/verify/{accessTokenID}", method = RequestMethod.POST)
    public @ResponseBody
    ProductResponse verifyBillPayment(
            @PathVariable String billInfoID,
            @PathVariable String accessTokenID,
            @RequestBody Map<String, String> request) {

        StopWatch timer = startTimer("verifyBillPayment for favorite bill ("+accessTokenID+")");
        
        String inputAmount = getRequestParameter(request, PARAM_AMOUNT, "").replaceAll(",", "");
        if (ValidateUtil.isEmpty(inputAmount)) {
            throw new InvalidParameterException("60000");
        }

        BigDecimal amount = new BigDecimal(inputAmount);
        BillPaymentDraft paymentDraft = billPaymentService.verifyPaymentAbility(
                billInfoID, amount, accessTokenID);

        Map<String, Object> data = BillResponse.builder()
                                        .setPaymentDraft(paymentDraft)
                                        .buildBillFavoriteResponse();

        Bill bill = paymentDraft.getBillInfo();
        if(ValidateUtil.isMobileNumber(bill.getRef1())) {
            String formattedMobileNumber = Utils.formatMobileNumber(bill.getRef1());
            data.put(PARAM_REF1, formattedMobileNumber);
        }else if(ValidateUtil.isTelNumber(bill.getRef1())){
            String formattedTelNumber = Utils.formatTelNumber(bill.getRef1());
            data.put(PARAM_REF1, formattedTelNumber);
        }

        stopTimer(timer);

        return createResponse(data);
    }

    //confirm all bills
    @RequestMapping(value = "/{draftID}/confirm/{accessTokenID}", method = RequestMethod.PUT)
    public @ResponseBody
    ProductResponse confirmBillPayment(
            @PathVariable String draftID,
            @PathVariable String accessTokenID,
            @RequestBody Map<String, String> request) {

        StopWatch timer = startTimer("confirmBillPayment ("+accessTokenID+")");

        BillPaymentDraft draft = billPaymentService.getBillPaymentDraftDetail(draftID, accessTokenID);
        BillPaymentDraft.Status draftStatus = draft.getStatus();
        
        if (draftStatus != Status.OTP_CONFIRMED) {
            String otpStr = getRequestParameter(request, PARAM_OTP_STR, "");
            String otpRef = getRequestParameter(request, PARAM_OTP_REF, "");
            String mobile = getRequestParameter(request, PARAM_MOBILE_NUMBER, "");

            OTP otp = new OTP(mobile, otpRef, otpStr);
            authService.verifyOTP(draftID, otp, accessTokenID);
        }

        BillPaymentTransaction.Status transactionStatus = billPaymentService.performPayment(draftID, accessTokenID);

        Map<String, Object> data = new HashMap<String, Object>();
        data.put(PARAM_BILL_PAYMENT_STATUS, transactionStatus.getStatus());
        data.put(PARAM_BILL_PAYMENT_ID, draftID); //billPaymentID has the same value as draftID
        
        stopTimer(timer);

        return createResponse(data);
    }

    // transaction status
    @RequestMapping(value = "/{billPaymentID}/status/{accessTokenID}", method = RequestMethod.GET)
    public @ResponseBody
    ProductResponse getBillPaymentStatus(
            @PathVariable String billPaymentID,
            @PathVariable String accessTokenID) {

        StopWatch timer = startTimer("getBillPaymentStatus ("+accessTokenID+")");

        BillPaymentTransaction.Status sts = this.billPaymentService.getBillPaymentStatus(billPaymentID, accessTokenID);

        Map<String, Object> data = new HashMap<String, Object>();
        data.put(PARAM_BILL_PAYMENT_STATUS, sts.getStatus());

        stopTimer(timer);

        return createResponse(data);
    }

    //get payment result
    @RequestMapping(value = "/{billPaymentID}/details/{accessTokenID}", method = RequestMethod.GET)
    public @ResponseBody
    ProductResponse getBillPaymentDetail(
            @PathVariable String billPaymentID,
            @PathVariable String accessTokenID) {

        StopWatch timer = startTimer("getBillPaymentDetail ("+accessTokenID+")");

        BillPaymentTransaction txn = this.billPaymentService.getBillPaymentResult(billPaymentID, accessTokenID);

        BigDecimal currentBalance = this.profileService.getEwalletBalance(accessTokenID);

        Map<String, Object> data = BillResponse.builder()
                                        .setPaymentTransaction(txn)
                                        .setWalletBalance(currentBalance)
                                        .buildBillPaymentDetailResponse();

        Bill bill = txn.getDraftTransaction().getBillInfo();
        String target = Utils.removeSuffix(bill.getTarget());
        BillConfiguration billConf = billConfigurationManager.getBillReference(target);
        if (billConf != null) {
        	data.put(PARAM_BILL_NAME_EN, billConf.getTitleEn());
        	data.put(PARAM_BILL_NAME_TH, billConf.getTitleTh());
        }
        if ("tmvh".equals(target) || "trmv".equals(target) || "rft".equals(target)) {
            // remark message that display at the bottom of receipt
            data.put(PARAM_REMARK_EN, messageManager.getMessageEn("payment.bill.remark"));
            data.put(PARAM_REMARK_TH, messageManager.getMessageTh("payment.bill.remark"));
        }

        if(ValidateUtil.isMobileNumber(bill.getRef1())){
            String formattedMobileNumber = Utils.formatMobileNumber(bill.getRef1());
            data.put(PARAM_REF1, formattedMobileNumber);
        }else if(ValidateUtil.isTelNumber(bill.getRef1())){
            String formattedTelNumber = Utils.formatTelNumber(bill.getRef1());
            data.put(PARAM_REF1, formattedTelNumber);
        }
        
        stopTimer(timer);

        return createResponse(data);
    }
    
    //getKeyIn placeholder
    @RequestMapping(value = "/key-in/place-holder/{billCode}/{accessTokenID}", method = RequestMethod.GET)
    public @ResponseBody
    ProductResponse getKeyInInputPlaceHolders(
            @PathVariable String accessTokenID, 
            @PathVariable String billCode) {
        
    	Map<String, Object> data = new HashMap<String, Object>();
    	data.put(PARAM_TARGET, billCode);
    	data.putAll(billConfigurationManager.getBillInfoResponse(billCode));

        return createResponse(data);
    }
    
    //getAllBillpayment
    @RequestMapping(value = "/list/{accessTokenID}", method = RequestMethod.GET)
    public @ResponseBody
    ProductResponse getAllBillPaymentList(
    		@PathVariable String accessTokenID) {
        
    	Map<String, Object> data = new HashMap<String, Object>();
    	data.put("categories", billCategoryConfigurationManager.getBillCategoryConfigurations());

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
    
    private StopWatch startTimer(String id) {
    	StopWatch timer = new StopWatch(id);
        timer.start();
        return timer;
    }
    
    private void stopTimer(StopWatch timer) {
    	timer.stop();
        logger.info(timer.shortSummary());
    }
    
	private String getTargetTitle(String target) {
		return targetNameMap.containsKey(target) ? targetNameMap.get(target) : "";
    }

}

