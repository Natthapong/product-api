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
import th.co.truemoney.product.api.util.ValidateUtil;
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

	@RequestMapping(value = "/list/{accessTokenID}", method = RequestMethod.GET)
	@ResponseBody
	public ProductResponse getActivityList(@PathVariable String accessTokenID) {
		List<Activity> activityList = activityService.getActivities(accessTokenID);

		List<ActivityViewItem> itemList = new ArrayList<ActivityViewItem>();
		for (Activity act : activityList) {
			String type = act.getType();
			String action = act.getAction();
			String ref1 = act.getRef1();
			
			String logoURL = onlineResourceManager.getActivityTypeLogoURL(type);
			String txt1En = WalletActivity.getTypeInEnglish(type);
			String txt1Th = WalletActivity.getTypeInThai(type);
			String txt2En = Utils.formatDate(act.getTransactionDate());
			String txt2Th = txt2En;
			String txt3En = WalletActivity.getActionInEnglish(action);
			String txt3Th = WalletActivity.getActionInThai(action);
			String txt4En = Utils.formatSignedAmount(act.getTotalAmount());
			String txt4Th = txt4En;
			String txt5En = ref1;
			String txt5Th = ref1;
			TYPE t = WalletActivity.getType(type);
			if (t == TYPE.TOPUP_MOBILE || t == TYPE.TRANSFER) {
				txt5En = Utils.formatMobileNumber(ref1);
				txt5Th = txt5En;
			} else if (t == TYPE.ADD_MONEY && "debit".equals(action)) {
				txt5En = BankUtil.getEnglishBankName(ref1);
				txt5Th = BankUtil.getThaiBankName(ref1);
			} else if ("add_money".equalsIgnoreCase(ref1)) {
				txt5En = "Direct Debit Topup";
				txt5Th = "เติมเงินด้วยบัญชีธนาคาร";
			} else if (t == TYPE.BILLPAY) {
				if(ValidateUtil.isMobileNumber(ref1)){
					txt5En = Utils.formatMobileNumber(ref1);
					txt5Th = txt5En;
				}else if(ValidateUtil.isTelNumber(ref1)){
					txt5En = Utils.formatTelNumber(ref1);
					txt5Th = txt5En;
				}
			}
			
			ActivityViewItem item = new ActivityViewItem();
			item.setReportID(String.valueOf(act.getReportID()));
			item.setLogoURL(logoURL);
			item.setText1En(txt1En);
			item.setText1Th(txt1Th);
			item.setText2En(txt2En);
			item.setText2Th(txt2Th);
			item.setText3En(txt3En);
			item.setText3Th(txt3Th);
			item.setText4En(txt4En);
			item.setText4Th(txt4Th);
			item.setText5En(txt5En);
			item.setText5Th(txt5Th);
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
		
		if (TYPE.TRANSFER.name().equalsIgnoreCase(activity.getType()))
		{
			data.put("personalMessage", transferActivityDetailViewHandler.buildPersonalMessage(activity.getPersonalMessage()) );
		}else{
			data.put("transfer error", "no personal message type: " + activity.getType());
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

}
