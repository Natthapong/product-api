package th.co.truemoney.product.api.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import th.co.truemoney.product.api.domain.ActivityType;
import th.co.truemoney.product.api.domain.ActivityViewItem;
import th.co.truemoney.product.api.domain.ProductResponse;
import th.co.truemoney.product.api.manager.OnlineResourceManager;
import th.co.truemoney.product.api.util.Utils;
import th.co.truemoney.serviceinventory.ewallet.ActivityService;
import th.co.truemoney.serviceinventory.ewallet.domain.Activity;
import th.co.truemoney.serviceinventory.ewallet.domain.ActivityDetail;

@RequestMapping(value = "/profile/activities")
@Controller
public class MobileWalletActivityController extends BaseController {

	@Autowired
	ActivityService activityService;
	
	@Autowired
	OnlineResourceManager onlineResourceManager;

	@RequestMapping(value = "/list/{accessTokenID}", method = RequestMethod.GET)
	@ResponseBody
	public ProductResponse getActivityList(@PathVariable String accessTokenID) {
		List<Activity> activityList = activityService
				.getActivities(accessTokenID);

		List<ActivityViewItem> itemList = new ArrayList<ActivityViewItem>();
		
		for (Activity act : activityList) {
			ActivityViewItem item = new ActivityViewItem();
			item.setReportID(String.valueOf(act.getReportID()));
			item.setLogoURL(onlineResourceManager.getActivityTypeLogoURL(act.getType()));
			item.setText1Th(mapMessageType(act.getType(), act.getAction()));
			item.setText1En(mapMessageType(act.getType(), act.getAction()));
			if (act.getTransactionDate() != null) {
				item.setText2Th(Utils.formatDate(act.getTransactionDate()));
				item.setText2En(Utils.formatDate(act.getTransactionDate()));
			}
			item.setText3Th(mapMessageAction(act.getAction()));
			item.setText3En(mapMessageAction(act.getAction()));

			item.setText4Th(Utils.formatSignedAmount(act.getTotalAmount()));
			item.setText4En(Utils.formatSignedAmount(act.getTotalAmount()));

			if (ActivityType.TOPUP_MOBILE.equals(act.getType())
					|| ActivityType.TRANSFER.equals(act.getType())) {
				item.setText5Th(Utils.formatMobileNumber(act.getRef1()));
				item.setText5En(Utils.formatMobileNumber(act.getRef1()));
			} else if (ActivityType.ADD_MONEY.equals(act.getType())) {
				if ("debit".equals(act.getAction())) {
					item.setText5Th(mapBankName(act.getRef1()));
					item.setText5En(mapBankName(act.getRef1()));
				}else{
					item.setText5Th(act.getRef1());
					item.setText5En(act.getRef1());
				}
			} else if (ActivityType.ADD_MONEY.equals(act.getRef1())) {
				item.setText5Th(ActivityType.DIRECT_DEBIT_ADDMONEY);
				item.setText5En(ActivityType.DIRECT_DEBIT_ADDMONEY);
			} else {
				item.setText5Th(act.getRef1());
				item.setText5En(act.getRef1());
			}

			itemList.add(item);
		}

		Map<String, Object> data = new HashMap<String, Object>();
		data.put("activities", itemList);

		return this.responseFactory.createSuccessProductResonse(data);
	}

	 @RequestMapping(value = "/{reportID}/detail/{accessTokenID}", method =RequestMethod.GET)
	 @ResponseBody
	 public ProductResponse getActivityDetails(@PathVariable String reportID, @PathVariable String accessTokenID) {
		 ActivityDetail detail = activityService.getActivityDetail(new Long(reportID), accessTokenID);
		 Map<String, Object> data = new HashMap<String, Object>();
		 
		 Map<String, String> header = new HashMap<String, String>();
		 header.put("textTh", mapMessageType(detail.getType(), detail.getAction()));
		 header.put("textEn", detail.getType());
		 data.put("header", header);
		 data.put("section1", buildSection1(detail));
		 data.put("section2", buildSection2(detail));
		 data.put("section3", buildSection3(detail));
		 data.put("section4", buildSection4(detail));
		 
		 return this.responseFactory.createSuccessProductResonse(data);
	 }
	 
	 private Map<String, String> buildSection1(ActivityDetail detail) {
		 String type = detail.getType();
		 Map<String, String> section1 = new HashMap<String, String>();
		 
		 if (ActivityType.TOPUP_MOBILE.equals(type) 
				 || ActivityType.BILLPAY.equals(type)) {
			 section1.put("logoURL", onlineResourceManager.getActivityActionLogoURL(detail.getAction()));
			 section1.put("titleTh", "");
			 section1.put("titleEn", "");
		 } else if (ActivityType.ADD_MONEY.equals(type)) {
			 section1.put("logoURL", onlineResourceManager.getBankLogoURL(detail.getRef1()));
			 section1.put("titleTh", "บัญชีธนาคาร");
			 section1.put("titleEn", "bank account");
		 } else if (ActivityType.TRANSFER.equals(type)) {
			 String transferTxt = "";
			 if (ActivityType.TRANSFER_DEBTOR.equals(detail.getAction())) {
				 transferTxt = ActivityType.TRANSFER_TXT;
			 } else if (ActivityType.TRANSFER_CREDITOR.equals(detail.getAction())) {
				 transferTxt = ActivityType.RECIEVE_TXT;
			 } 
			 section1.put("titleTh", transferTxt);
			 section1.put("titleEn", detail.getAction());
		 } else {
			 section1.put("titleTh", "คืนค่าธรรมเนียม");
			 section1.put("titleEn", "kickback");
		 }
		 return section1;
	 }
	 
	 private Map<String, Object> buildSection2(ActivityDetail detail) {
		 Map<String, Object> section2 = new HashMap<String, Object>();
		 Map<String, Object> column1 = new HashMap<String, Object>();
		 Map<String, String> cell1 = new HashMap<String, String>();
		 Map<String, String> cell2 = new HashMap<String, String>();
		 if (ActivityType.TOPUP_MOBILE.equals(detail.getType())) {
			 cell1.put("titleTh", "หมายเลขโทรศัพท์");
			 cell1.put("titleEn", "mobile number");
			 cell1.put("value", Utils.formatMobileNumber(detail.getRef1()));
		 } else if (ActivityType.BONUS.equals(detail.getType())) {
			 cell1.put("titleTh", "ทำรายการ");
			 cell1.put("titleEn", "activity");
			 cell1.put("value", ActivityType.DIRECT_DEBIT_ADDMONEY);
		 } else if (ActivityType.ADD_MONEY.equals(detail.getType())) {
			 cell1.put("titleTh", "ธนาคาร");
			 cell1.put("titleEn", "bank name");
			 cell1.put("value", mapBankName(detail.getRef1()));
			 cell2.put("titleTh", "หมายเลขบัญชี");
			 cell2.put("titleEn", "account number");
			 cell2.put("value", detail.getRef2());
			 column1.put("cell2", cell2);
		 } else if (ActivityType.TRANSFER.equals(detail.getType())) {
			 if (ActivityType.TRANSFER_DEBTOR.equals(detail.getAction()))
				 cell1.put("titleTh", "หมายเลขผู้รับ");
			 else
				 cell1.put("titleTh", "หมายเลขผู้ส่ง");
			 cell1.put("titleEn", "account number");
			 cell1.put("value", Utils.formatMobileNumber(detail.getRef1()));
			 if (ActivityType.TRANSFER_DEBTOR.equals(detail.getAction()))
				 cell2.put("titleTh", "ชื่อผู้รับ");
			 else
				 cell2.put("titleTh", "ชื่อผู้ส่ง");
			 cell2.put("titleEn", "account owner");
			 cell2.put("value", detail.getRef2());
			 column1.put("cell2", cell2);
		 } else {
			 cell1.put("titleTh", "รหัสลูกค้า");
			 cell1.put("titleEn", "customer ID");
			 cell1.put("value", detail.getRef1());
			 cell2.put("titleTh", "เลขที่ใบแจ้งค่าบริการ");
			 cell2.put("titleEn", "invoice number");
			 cell2.put("value", detail.getRef2());
			 column1.put("cell2", cell2);
		 }
		 column1.put("cell1", cell1);
		 section2.put("column1", column1);
		 return section2;
	 }
	 
	 public Map<String, Object> buildSection3(ActivityDetail detail) {
		 Map<String, Object> section3 = new HashMap<String, Object>();
		 Map<String, Object> column31 = new HashMap<String, Object>();
		 Map<String, Object> column32 = new HashMap<String, Object>();
		 Map<String, String> cell311 = new HashMap<String, String>();
		 Map<String, String> cell312 = new HashMap<String, String>();
		 Map<String, String> cell321 = new HashMap<String, String>();
		 cell311.put("titleTh", "จำนวนเงิน");
		 cell311.put("titleEn", "amount");
		 cell311.put("value", Utils.formatAbsoluteAmount(detail.getAmount()));
		 column31.put("cell1", cell311);
		 section3.put("column1", column31);
		 
		 if (!(ActivityType.BONUS.equals(detail.getType()) || ActivityType.TRANSFER.equals(detail.getType()))) {
			 cell312.put("titleTh", "รวมเงินที่ชำระ");
			 cell312.put("titleEn", "total amount");
			 cell312.put("value", Utils.formatAbsoluteAmount(detail.getTotalAmount()));
			 cell321.put("titleTh", "ค่าธรรมเนียม");
			 cell321.put("titleEn", "total fee");
			 cell321.put("value", Utils.formatAbsoluteAmount(detail.getTotalFeeAmount()));
			 column31.put("cell2", cell312);
			 column32.put("cell1", cell321);
			 section3.put("column2", column32);
		 }
		 return section3;
	 }
	 
	 public Map<String, Object> buildSection4(ActivityDetail detail) {
		 Map<String, Object> section4 = new HashMap<String, Object>();
		 Map<String, Object> column41 = new HashMap<String, Object>();
		 Map<String, Object> column42 = new HashMap<String, Object>();
		 Map<String, String> cell411 = new HashMap<String, String>();
		 Map<String, String> cell421 = new HashMap<String, String>();
		 cell411.put("titleTh", "วันที่-เวลา");
		 cell411.put("titleEn", "transaction date");
		 cell411.put("value", Utils.formatDateTime(detail.getTransactionDate()));	 
		 cell421.put("titleTh", "เลขที่อ้างอิง");
		 cell421.put("titleEn", "transaction ID");
		 cell421.put("value", detail.getTransactionID());
		 column41.put("cell1", cell411);
		 column42.put("cell1", cell421);
		 section4.put("column1", column41);
		 section4.put("column2", column42);
		 return section4;
	 }
	 
	 private String mapBankName(String ref1) {
		String bankName = "";
		if ("KTB".equals(ref1)) {
			bankName = ActivityType.KTB_ADDMONEY;
		} else if ("SCB".equals(ref1)) {
			bankName = ActivityType.SCB_ADDMONEY;
		} else if ("BBL".equals(ref1)) {
			bankName = ActivityType.BBL_ADDMONEY;
		} else if ("BAY".equals(ref1)) {
			bankName = ActivityType.BAY_ADDMONEY;
		}
		return bankName;
	 }

	 private String mapMessageType(String type, String action) {
		String result = "";

		if (ActivityType.TOPUP_MOBILE.equals(type)) {
			result = ActivityType.TOPUP_MOBILE_TH;
		} else if (ActivityType.BILLPAY.equals(type)) {
			result = ActivityType.BILLPAY_TH;
		} else if (ActivityType.BONUS.equals(type)) {
			result = ActivityType.BONUS_TH;
		} else if (ActivityType.ADD_MONEY.equals(type)) {
			result = ActivityType.ADD_MONEY_TH;
		} else if (ActivityType.TRANSFER.equals(type)) {
			if (ActivityType.TRANSFER_DEBTOR.equals(action)) {
				result = ActivityType.TRANSFER_DEBTOR_TH;
			} else if (ActivityType.TRANSFER_CREDITOR.equals(action)) {
				result = ActivityType.TRANSFER_CREDITOR_TH;
			}
		}

		return result;
	}

	private String mapMessageAction(String action) {
		String result = "";

		if ("d.tmvhtopup".equals(action)) {
			result = ActivityType.TMVH_TOPUP;
		} else if ("d.tmvtopup".equals(action)) {
			result = ActivityType.TRMV_TOPUP;
		} else if ("d.tmvh".equals(action)) {
			result = ActivityType.TMVH_BILLPAY;
		} else if ("d.trmv".equals(action)) {
			result = ActivityType.TRMV_BILLPAY;
		} else if ("d.catv".equals(action)) {
			result = ActivityType.CATV_BILLPAY;
		} else if ("d.dstv".equals(action)) {
			result = ActivityType.DSTV_BILLPAY;
		} else if ("d.tr".equals(action)) {
			result = ActivityType.TR_BILLPAY;
		} else if ("d.ti".equals(action)) {
			result = ActivityType.TI_BILLPAY;
		} else if ("d.tic".equals(action)) {
			result = ActivityType.TIC_BILLPAY;
		} else if ("d.tlp".equals(action)) {
			result = ActivityType.TLP_BILLPAY;
		} else if ("d.tcg".equals(action)) {
			result = ActivityType.TCG_BILLPAY;
		} else if ("promo_direct_debit".equals(action)) {
			result = ActivityType.DEBIT_ADDMONEY;
		} else if ("debit".equals(action)) {
			result = ActivityType.DIRECT_DEBIT;
		} else if ("debtor".equals(action)) {
			result = ActivityType.TRANSFER_TXT;
		} else if ("creditor".equals(action)) {
			result = ActivityType.RECIEVE_TXT;
		} else if ("mmcc".equals(action)) {
			result = ActivityType.CASHCARD;
		}

		return result;
	}
	
	public void setActivityService(ActivityService activityService) {
		this.activityService = activityService;
	}

	public void setOnlineResourceManager(OnlineResourceManager onlineResourceManager) {
		this.onlineResourceManager = onlineResourceManager;
	}

}
