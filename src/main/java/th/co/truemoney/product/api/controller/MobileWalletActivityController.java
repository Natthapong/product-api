package th.co.truemoney.product.api.controller;

import java.text.SimpleDateFormat;
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
import th.co.truemoney.serviceinventory.ewallet.ActivityService;
import th.co.truemoney.serviceinventory.ewallet.domain.Activity;

@RequestMapping(value = "/profile/activities")
@Controller
public class MobileWalletActivityController extends BaseController {

	@Autowired
	ActivityService activityService;
	
	private static final String imagesURL = "https://secure.truemoney-dev.com/m/tmn_webview/logo_activity_type";
	
	
	@RequestMapping(value = "/list/{accessTokenID}", method = RequestMethod.GET)
	@ResponseBody
	public ProductResponse getActivityList(@PathVariable String accessTokenID) {
		List<Activity> activityList = activityService.getActivities(accessTokenID);
		
		List<ActivityViewItem> itemList = new ArrayList<ActivityViewItem>();
		SimpleDateFormat dt1 = new SimpleDateFormat("dd/mm/yy");
		for(Activity act:activityList){
			ActivityViewItem item = new ActivityViewItem();
			item.setReportID(String.valueOf(act.getReportID()));
			item.setLogoURL(mapLogoActivityType(act.getType()));
			item.setText1Th(mapMessageType(act.getType()));
			item.setText1En(mapMessageType(act.getType()));
			if (act.getTransactionDate() != null) {
				item.setText2Th(dt1.format(act.getTransactionDate()));
				item.setText2En(dt1.format(act.getTransactionDate()));
			}
			item.setText3Th(act.getAction());
			item.setText3En(act.getAction());
			item.setText4Th(act.getAmount().toString());
			item.setText4En(act.getAmount().toString());
			item.setText5Th(act.getRef1());
			item.setText5En(act.getRef1());
			
			itemList.add(item);
		}
		
		
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("activities", itemList);
		
		return this.responseFactory.createSuccessProductResonse(data);
	}
	
	private String mapLogoActivityType(String type){
		String result = "";
		
		if(ActivityType.TOPUP_MOBILE.equals(type)){
			result = imagesURL+"/topup_mobile.png";
		}else if(ActivityType.BILLPAY.equals(type)){
			result = imagesURL+"/billpay.png";
		}else if(ActivityType.BONUS.equals(type)){
			result = imagesURL+"/bonus.png";
		}else if(ActivityType.ADD_MONEY.equals(type)){
			result = imagesURL+"/add_money.png";
		}else if(ActivityType.TRANSFER_DEBTOR.equals(type)){
			result = imagesURL+"/transfer.png";
		}else if(ActivityType.TRANSFER_CREDITOR.equals(type)){
			result = imagesURL+"/transfer.png";
		}
		
		return result;
	}
	
	private String mapMessageType(String type){
		String result = "";
		
		if(ActivityType.TOPUP_MOBILE.equals(type)){
			result = ActivityType.TOPUP_MOBILE_TH;
		}else if(ActivityType.BILLPAY.equals(type)){
			result = ActivityType.BILLPAY_TH;
		}else if(ActivityType.BONUS.equals(type)){
			result = ActivityType.BONUS_TH;
		}else if(ActivityType.ADD_MONEY.equals(type)){
			result = ActivityType.ADD_MONEY_TH;
		}else if(ActivityType.TRANSFER_DEBTOR.equals(type)){
			result = ActivityType.TRANSFER_DEBTOR_TH;
		}else if(ActivityType.TRANSFER_CREDITOR.equals(type)){
			result = ActivityType.TRANSFER_CREDITOR_TH;
		}
		
		return result;
	}

	public void setActivityService(ActivityService activityService) {
		this.activityService = activityService;
	}
	
}
