package th.co.truemoney.product.api.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
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
import th.co.truemoney.product.api.manager.BillConfigurationManager;
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
	private ActivityService activityService;
	
	@Autowired
	private OnlineResourceManager onlineResourceManager;
	
	@Autowired
	private BillConfigurationManager billConfigurationManager;
	
	@RequestMapping(value = "/list/{accessTokenID}", method = RequestMethod.GET)
	@ResponseBody
	public ProductResponse getActivityList(@PathVariable String accessTokenID) {
		List<Activity> activityList = activityService.getActivities(accessTokenID);

		List<ActivityViewItem> itemList = new ArrayList<ActivityViewItem>();
		for (Activity act : activityList) {
			TYPE t = WalletActivity.getType(act.getType());
			String action = Utils.removeSuffix(act.getAction());
			
			ActivityViewItem item = new ActivityViewItem();
			item.setReportID(String.valueOf(act.getReportID()));
			item.setLogoURL(getActivityTypeLogo(act));
			item.setText1En(WalletActivity.getTypeInEnglish(t));
			item.setText1Th(WalletActivity.getTypeInThai(t));
			item.setText2En(getDateString(act.getTransactionDate()));
			item.setText2Th(getDateString(act.getTransactionDate()));
			item.setText3En(getActionNameEn(t, action));
			item.setText3Th(getActionNameTh(t, action));
			item.setText4En(getAmountString(act.getAmount()));
			item.setText4Th(getAmountString(act.getAmount()));
			item.setText5En(getRef1StringEn(t, action, act.getRef1()));
			item.setText5Th(getRef1StringTh(t, action, act.getRef1()));
			itemList.add(item);
		}

		Map<String, Object> data = new HashMap<String, Object>();
		data.put("activities", itemList);

		return this.responseFactory.createSuccessProductResonse(data);
	}

	private String getActivityTypeLogo(Activity activity) {
		return onlineResourceManager.getActivityTypeLogoURL(activity);
	}
	
	private String getActionNameEn(TYPE t, String action) {
		if (t == TYPE.BILLPAY) {
			return  billConfigurationManager.getTitleEn(action);
		} else {
			return WalletActivity.getActionInEnglish(action);
		}
	}
	
	private String getActionNameTh(TYPE t, String action) {
		if (t == TYPE.BILLPAY) {
			return billConfigurationManager.getTitleTh(action);
		} else {
			return WalletActivity.getActionInThai(action);
		}
	}
	
	private String getRef1StringEn(TYPE t, String action, String ref1) {
		if (t == TYPE.TOPUP_MOBILE || t == TYPE.TRANSFER || t == TYPE.BILLPAY) {
			return Utils.formatTelephoneNumber(ref1);
		} else if (t == TYPE.ADD_MONEY && "debit".equals(action)) {
			return BankUtil.getEnglishBankName(ref1);
		} else if ("add_money".equalsIgnoreCase(ref1)) {
			return "Direct Debit Topup";
		} else {
			return ref1;
		}
	}
	
	private String getRef1StringTh(TYPE t, String action, String ref1) {
		if (t == TYPE.TOPUP_MOBILE || t == TYPE.TRANSFER || t == TYPE.BILLPAY) {
			return Utils.formatTelephoneNumber(ref1);
		} else if (t == TYPE.ADD_MONEY && "debit".equals(action)) {
			return BankUtil.getThaiBankName(ref1);
		} else if ("add_money".equalsIgnoreCase(ref1)) {
			return "เติมเงินด้วยบัญชีธนาคาร";
		} else {
			return ref1;
		}
	}
	
	private String getAmountString(BigDecimal amount) {
		return Utils.formatSignedAmount(amount);
	}
	
	private String getDateString(Date d) {
		return Utils.formatDate(d);
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
		
		Map<String, Object> sec4 = handler.buildSection4();
		if (sec4 != null && sec4.size() > 0) {
			data.put("section4", handler.buildSection4());
		}
		
		TYPE t = WalletActivity.getType(Utils.removeSuffix(activity.getType()));
		if (t ==  TYPE.TRANSFER) {
			data.put("personalMessage", transferActivityDetailViewHandler.buildPersonalMessage(activity.getPersonalMessage()) );
		}
		 
		return this.responseFactory.createSuccessProductResonse(data);
	}
	
	@Autowired
	private TopupMobileActivityDetailViewHandler topupMobileActivityDetailViewHandler;
	@Autowired
	private AddMoneyActivityDetailViewHandler addMoneyActivityDetailViewHandler;
	@Autowired
	private TransferActivityDetailViewHandler transferActivityDetailViewHandler;
	@Autowired
	private BillPayActivityDetailViewHandler billPayActivityDetailViewHandler;
	@Autowired
	private BonusActivityDetailViewHandler bonusActivityDetailViewHandler;
	
	
	private ActivityDetailViewHandler getActivityDetailHandler(String type) {
		TYPE t = WalletActivity.getType(type);
		switch (t) {
			case TOPUP_MOBILE:
				return topupMobileActivityDetailViewHandler;
			case ADD_MONEY:
				return addMoneyActivityDetailViewHandler;
			case TRANSFER:
				return transferActivityDetailViewHandler;
			case BILLPAY:
				return billPayActivityDetailViewHandler;
			case BONUS:
				return bonusActivityDetailViewHandler;
			default:
				throw new IllegalArgumentException("No support handler for '" + type + "' activity type");
		}
	}
	
	public void setActivityService(ActivityService activityService) {
		this.activityService = activityService;
	}

	public void setOnlineResourceManager(OnlineResourceManager onlineResourceManager) {
		this.onlineResourceManager = onlineResourceManager;
	}

	public void setBillConfigurationManager(BillConfigurationManager billConfigurationManager) {
		this.billConfigurationManager = billConfigurationManager;
	}

}
