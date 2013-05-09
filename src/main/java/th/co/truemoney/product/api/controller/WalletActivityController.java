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
import th.co.truemoney.product.api.domain.WalletActivity;
import th.co.truemoney.product.api.domain.WalletActivity.TYPE;
import th.co.truemoney.product.api.handler.ActivityDetailViewHandler;
import th.co.truemoney.product.api.handler.AddMoneyActivityDetailViewHandler;
import th.co.truemoney.product.api.handler.BillPayActivityDetailViewHandler;
import th.co.truemoney.product.api.handler.BonusActivityDetailViewHandler;
import th.co.truemoney.product.api.handler.TopupMobileActivityDetailViewHandler;
import th.co.truemoney.product.api.handler.TransferActivityDetailViewHandler;
import th.co.truemoney.product.api.manager.OnlineResourceManager;
import th.co.truemoney.product.api.util.Utils;
import th.co.truemoney.serviceinventory.ewallet.ActivityService;
import th.co.truemoney.serviceinventory.ewallet.domain.Activity;
import th.co.truemoney.serviceinventory.ewallet.domain.ActivityDetail;

@RequestMapping(value = "/profile/activities")
@Controller
public class WalletActivityController extends BaseController {

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
		
		ActivityDetail activity = activityService.getActivityDetail(new Long(reportID), accessTokenID);
		
		ActivityDetailViewHandler handler = getActivityDetailHandler(activity.getType());
		handler.handle(activity);
		
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("section1", handler.buildSection1());
		data.put("section2", handler.buildSection2());
		data.put("section3", handler.buildSection3());
		data.put("section4", handler.buildSection4());
		 
		return this.responseFactory.createSuccessProductResonse(data);
	}
	
	private ActivityDetailViewHandler getActivityDetailHandler(String type) {
		TYPE t = WalletActivity.getEnum(type);
		switch (t) {
			case TOPUP_MOBILE:
				return new TopupMobileActivityDetailViewHandler();
			case ADD_MONEY:
				return new AddMoneyActivityDetailViewHandler();
			case TRANSFER:
				return new TransferActivityDetailViewHandler();
			case BILLPAY:
				return new BillPayActivityDetailViewHandler();
			case BONUS:
				return new BonusActivityDetailViewHandler();
			default:
				throw new IllegalArgumentException("No support handler for '" + type + "' activity type");
		}
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
