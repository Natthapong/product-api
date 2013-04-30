package th.co.truemoney.product.api.controller;

import java.math.BigDecimal;
import java.text.DecimalFormat;
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
import th.co.truemoney.serviceinventory.ewallet.domain.ActivityDetail;

@RequestMapping(value = "/profile/activities")
@Controller
public class MobileWalletActivityController extends BaseController {

	@Autowired
	ActivityService activityService;

	private static final String imagesURL = "https://secure.truemoney-dev.com/m/tmn_webview/images/logo_activity_type";

	@RequestMapping(value = "/list/{accessTokenID}", method = RequestMethod.GET)
	@ResponseBody
	public ProductResponse getActivityList(@PathVariable String accessTokenID) {
		List<Activity> activityList = activityService
				.getActivities(accessTokenID);

		List<ActivityViewItem> itemList = new ArrayList<ActivityViewItem>();
		SimpleDateFormat dt1 = new SimpleDateFormat("dd/mm/yy");
		for (Activity act : activityList) {
			ActivityViewItem item = new ActivityViewItem();
			item.setReportID(String.valueOf(act.getReportID()));
			item.setLogoURL(mapLogoActivityType(act.getType()));
			item.setText1Th(mapMessageType(act.getType(), act.getAction()));
			item.setText1En(mapMessageType(act.getType(), act.getAction()));
			if (act.getTransactionDate() != null) {
				item.setText2Th(dt1.format(act.getTransactionDate()));
				item.setText2En(dt1.format(act.getTransactionDate()));
			}
			item.setText3Th(mapMessageAction(act.getAction()));
			item.setText3En(mapMessageAction(act.getAction()));

			item.setText4Th(formatTotalAmount(act.getTotalAmount()));
			item.setText4En(formatTotalAmount(act.getTotalAmount()));

			if (ActivityType.TOPUP_MOBILE.equals(act.getType())
					|| ActivityType.TRANSFER_CREDITOR.equals(act.getType())
					|| ActivityType.TRANSFER_DEBTOR.equals(act.getType())) {
				item.setText5Th(formatMobileNumber(act.getRef1()));
				item.setText5En(formatMobileNumber(act.getRef1()));
			} else if (ActivityType.ADD_MONEY.equals(act.getType())) {
				if (ActivityType.DIRECT_DEBIT.equals(act.getAction())) {
					item.setText5Th(mapBankName(act.getRef1()));
					item.setText5En(mapBankName(act.getRef1()));
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

	// @RequestMapping(value = "/{reportID}/detail/{accessTokenID}", method =
	// RequestMethod.GET)
	// @ResponseBody
	// public ProductResponse getActivityDetails(@PathVariable String reportID,
	// @PathVariable String accessTokenID) {
	// ActivityDetail activityDetail = activityService.getActivityDetail(new
	// Long(reportID), accessTokenID);
	// Map<String, Object> data = new HashMap<String, Object>();
	// data.put("header", "bullshit");
	// return this.responseFactory.createSuccessProductResonse(data);
	// }

	private String mapLogoActivityType(String type) {
		String result = "";

		if (ActivityType.TOPUP_MOBILE.equals(type)) {
			result = imagesURL + "/topup_mobile.png";
		} else if (ActivityType.BILLPAY.equals(type)) {
			result = imagesURL + "/billpay.png";
		} else if (ActivityType.BONUS.equals(type)) {
			result = imagesURL + "/bonus.png";
		} else if (ActivityType.ADD_MONEY.equals(type)) {
			result = imagesURL + "/add_money.png";
		} else if (ActivityType.TRANSFER_DEBTOR.equals(type)) {
			result = imagesURL + "/transfer.png";
		} else if (ActivityType.TRANSFER_CREDITOR.equals(type)) {
			result = imagesURL + "/transfer.png";
		}

		return result;
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
		} else if ("d.trmvtopup".equals(action)) {
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
			result = ActivityType.TRANSFER;
		} else if ("creditor".equals(action)) {
			result = ActivityType.RECIEVE;
		}

		return result;
	}

	private String formatTotalAmount(BigDecimal totalAmount) {
		DecimalFormat format = new DecimalFormat("##,###.##");
		String totalAmountFormat = format.format(totalAmount);
		if (totalAmount.compareTo(BigDecimal.ZERO) == 1) {
			totalAmountFormat = "+" + totalAmountFormat;
		}
		return totalAmountFormat;
	}

	private String formatMobileNumber(String mobileNumber) {
		return String.valueOf(mobileNumber).replaceFirst(
				"(\\d{3})(\\d{3})(\\d)", "$1-$2-$3");
	}

	public void setActivityService(ActivityService activityService) {
		this.activityService = activityService;
	}

}
