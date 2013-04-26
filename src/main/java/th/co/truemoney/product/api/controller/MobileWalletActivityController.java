package th.co.truemoney.product.api.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import th.co.truemoney.product.api.domain.ProductResponse;
import th.co.truemoney.serviceinventory.ewallet.ActivityService;
import th.co.truemoney.serviceinventory.ewallet.domain.Activity;

@RequestMapping(value = "/profile/activities")
@Controller
public class MobileWalletActivityController extends BaseController{
	
	@Autowired
	ActivityService activityService;
	
	@RequestMapping(value = "/list/{accessTokenID}", method = RequestMethod.GET)
	@ResponseBody
	public ProductResponse getActivityList(@PathVariable String accessTokenID) {
		List<Activity> activityList = activityService.getActivities(accessTokenID);
		
		Map<String, Object> data = new HashMap<String, Object>();
		
		
		return this.responseFactory.createSuccessProductResonse(data);
	}
	
}
