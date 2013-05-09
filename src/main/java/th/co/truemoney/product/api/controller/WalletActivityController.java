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
import th.co.truemoney.product.api.util.BankUtil;
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
			item.setText1Th(WalletActivity.getTypeInThai(act.getType()));
			item.setText1En(WalletActivity.getTypeInThai(act.getType()));
			if (act.getTransactionDate() != null) {
				item.setText2Th(Utils.formatDate(act.getTransactionDate()));
				item.setText2En(Utils.formatDate(act.getTransactionDate()));
			}
			item.setText3Th(WalletActivity.getActionInThai(act.getAction()));
			item.setText3En(WalletActivity.getActionInEnglish(act.getAction()));

			item.setText4Th(Utils.formatSignedAmount(act.getTotalAmount()));
			item.setText4En(Utils.formatSignedAmount(act.getTotalAmount()));
			
			if (TYPE.TOPUP_MOBILE == WalletActivity.getType(act.getType()) 
					|| TYPE.TRANSFER == WalletActivity.getType(act.getType())) {
				item.setText5Th(Utils.formatMobileNumber(act.getRef1()));
				item.setText5En(Utils.formatMobileNumber(act.getRef1()));
			} else if (TYPE.ADD_MONEY == WalletActivity.getType(act.getType())) {
				if ("debit".equals(act.getAction())) {
					item.setText5Th(BankUtil.getThaiBankName(act.getRef1()));
					item.setText5En(BankUtil.getEnglishBankName(act.getRef1()));
				}else{
					item.setText5Th(act.getRef1());
					item.setText5En(act.getRef1());
				}
			} else if ("add_money".equalsIgnoreCase(act.getRef1())) {
				item.setText5Th("เติมเงินด้วยบัญชีธนาคาร");
				item.setText5En("Direct Debit Topup");
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
		TYPE t = WalletActivity.getType(type);
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
	/*
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
	*/
	public void setActivityService(ActivityService activityService) {
		this.activityService = activityService;
	}

	public void setOnlineResourceManager(OnlineResourceManager onlineResourceManager) {
		this.onlineResourceManager = onlineResourceManager;
	}

}
