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

import th.co.truemoney.product.api.domain.ProductResponse;
import th.co.truemoney.serviceinventory.ewallet.ActivityService;
import th.co.truemoney.serviceinventory.ewallet.domain.Activity;

@RequestMapping(value = "/profile/activities")
@Controller
public class MobileWalletActivityController extends BaseController {

	@Autowired
	ActivityService activityService;

	@RequestMapping(value = "/list/{accessTokenID}", method = RequestMethod.GET)
	@ResponseBody
	public ProductResponse getActivityList(@PathVariable String accessTokenID) {
		List<Activity> activityList = activityService
				.getActivities(accessTokenID);

		List<Map<String, String>> result = new ArrayList<Map<String, String>>();
		for (Activity activity : activityList) {
			Map<String, String> data = new HashMap<String, String>();
			//data.put("reportID", activity.getReportID());
			result.add(data);
		}

		Map<String, Object> returnData = new HashMap<String, Object>();
		returnData.put("activities", result);

		return this.responseFactory.createSuccessProductResonse(returnData);
	}

}
