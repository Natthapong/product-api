package th.co.truemoney.product.api.handler;

import static th.co.truemoney.product.api.domain.ServiceChannel.CHANNEL_ATM;
import static th.co.truemoney.product.api.domain.ServiceChannel.CHANNEL_IBANKING;
import static th.co.truemoney.product.api.domain.ServiceChannel.CHANNEL_KIOSK;
import static th.co.truemoney.product.api.domain.ServiceChannel.CHANNEL_MOBILE;
import static th.co.truemoney.product.api.domain.ServiceChannel.CHANNEL_TRM;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import th.co.truemoney.product.api.domain.ServiceChannel;
import th.co.truemoney.product.api.util.BankUtil;
import th.co.truemoney.product.api.util.Utils;
import th.co.truemoney.serviceinventory.ewallet.domain.ActivityDetail;

@Component
public class AddMoneyActivityDetailViewHandler extends GeneralActivityDetailViewHandler {

	private static final String SOF_TMCC = "tmcc";//no ref1 in list page
//	private static final Long KIOSK_CHANNEL = 33l;//
//	private static final Long CP_FRESH_MART_CHANNEL = 38l;//
//	private static final Long TMX_CHANNEL = 39l;//
//	private static final Long IOS_APP_CHANNEL = 40l;
//	private static final Long ATM_CHANNEL = 42l;
//	private static final Long IBANKING = 43l;
//	private static final Long TRM_CHANNEL = 44l;//
	
	
	@SuppressWarnings("serial")
	static final Map<String , String> ref1TitleMap = new HashMap<String , String>() {{
	    put("ksk_ref1_title_en", "Total amount");
	    put("ksk_ref1_title_th", "ยอดเงินเข้า Wallet");
	    put("cpf_ref1_title_en", "Store number");
	    put("cpf_ref1_title_th", "หมายเลขสาขา");
	    put("tmx_ref1_title_en", "Store number");
	    put("tmx_ref1_title_th", "หมายเลขร้านค้า");
	    put("mbl_ref1_title_en", "Bank");
	    put("mbl_ref1_title_th", "ธนาคาร");
	    put("atm_ref1_title_en", "Bank");
	    put("atm_ref1_title_th", "ธนาคาร");
	    put("ibk_ref1_title_en", "Bank");
	    put("ibk_ref1_title_th", "ธนาคาร");
	    put("trm_ref1_title_en", "Total amount");
	    put("trm_ref1_title_th", "ยอดเงินเข้า Wallet");
	}};
	
	private ServiceChannel channel;

	@Override
	public void handle(ActivityDetail activity) {
		super.handle(activity);
		Long channelID = activity.getChannel();
		channel = ServiceChannel.getChannel(channelID.intValue());
	}

	@Override
	public Map<String, String> buildSection1() {
		Map<String, String> section1 = super.buildSection1();
		String channelNameEn = "";
		String channelNameTh = "";
		String channelLogo = "";
		
		if (SOF_TMCC.equals(activity.getAction())) {
			channelNameEn = "True Money Cash Card";
			channelNameTh = "บัตรเงินสดทรูมันนี่";
		} else {
			channelNameEn = channel.getNameEn();
			channelNameTh = channel.getNameTh();
			if (channel == CHANNEL_MOBILE) {
				channelLogo = onlineResourceManager.getBankLogoURL(activity.getRef1());
				section1.put("logoURL", channelLogo);
			}
		}
		section1.put("titleEn", channelNameEn);
		section1.put("titleTh", channelNameTh);
		return section1;
	}
	
	@Override
	public Map<String, Object> buildSection2() {
		Map<String, Object> section2 = super.buildSection2();
		Map<String, Object> column1 = new HashMap<String, Object>();
		
		String ref1TitleEn = "";
		String ref1TitleTh = "";
		String ref1Value = "";
		
		String action  = activity.getAction();
		if (SOF_TMCC.equals(action)) {
		 	ref1TitleTh = "รหัสบัตรเงินสด";
			ref1TitleEn = "Cash Card PIN";
			ref1Value = activity.getRef1();
		} else {
			ref1TitleEn = AddMoneyActivityDetailViewHandler.ref1TitleMap.get(channel.getAbbre() + "_ref1_title_en");
			ref1TitleTh = AddMoneyActivityDetailViewHandler.ref1TitleMap.get(channel.getAbbre() + "_ref1_title_th");
			ref1Value = activity.getRef1();
			
			if (channel == CHANNEL_KIOSK || channel == CHANNEL_TRM) {
				ref1Value = Utils.formatAbsoluteAmount(activity.getTotalAmount());
			} else if (channel == CHANNEL_ATM 
					|| channel == CHANNEL_MOBILE 
					|| channel == CHANNEL_IBANKING) {
				ref1Value = BankUtil.getThaiBankName(ref1Value);
			}
			
			if (channel == CHANNEL_MOBILE) {
				Map<String, String> cell2 = new HashMap<String, String>();
				cell2.put("titleTh", "หมายเลขบัญชี");
				cell2.put("titleEn", "Account number");
				cell2.put("value", StringUtils.hasText(activity.getRef2()) ? activity.getRef2() : "-");
				column1.put("cell2", cell2);
			}
		}
		Map<String, String> cell1 = new HashMap<String, String>();
		cell1.put("titleEn", ref1TitleEn);
		cell1.put("titleTh", ref1TitleTh);
		cell1.put("value", ref1Value);
		column1.put("cell1", cell1);
		section2.put("column1", column1);
		return section2;
	}

	@Override
	public Map<String, Object> buildSection3() {
		Map<String, Object> section3 = new HashMap<String, Object>();
		
		// Channel TRM and KIOSK don't suppose to have section 4.
		// Display section 4's data on section 3 area.
		if (channel == CHANNEL_TRM || channel == CHANNEL_KIOSK) { 
			return super.buildSection4();
		} else {
			String action  = activity.getAction();
			if (SOF_TMCC.equals(action)) {
				Map<String, Object> column31 = new HashMap<String, Object>();
				Map<String, Object> column32 = new HashMap<String, Object>();
				Map<String, String> cell311 = new HashMap<String, String>();
				Map<String, String> cell312 = new HashMap<String, String>();
				Map<String, String> cell321 = new HashMap<String, String>();
				cell311.put("titleTh", "จำนวนเงิน");
				cell311.put("titleEn", "Total amount");
				cell311.put("value", Utils.formatAbsoluteAmount(activity.getTotalAmount()));
				column31.put("cell1", cell311);
				section3.put("column1", column31);			
				cell312.put("titleTh", "ยอดเงินเข้า Wallet");
				cell312.put("titleEn", "Amount");
				cell312.put("value", Utils.formatAbsoluteAmount(activity.getAmount()));
				cell321.put("titleTh", "ค่าธรรมเนียม");
				cell321.put("titleEn", "Total fee");
				cell321.put("value", Utils.formatAbsoluteAmount(activity.getTotalFeeAmount()));
				column32.put("cell1", cell321);
				column31.put("cell2", cell312);
				section3.put("column2", column32);
			} else {
				Map<String, Object> column31 = new HashMap<String, Object>();
				Map<String, String> cell311 = new HashMap<String, String>();
				cell311.put("titleTh", "ยอดเงินเข้า Wallet");
				cell311.put("titleEn", "Total amount");
				cell311.put("value", Utils.formatAbsoluteAmount(activity.getTotalAmount()));
				column31.put("cell1", cell311);
				section3.put("column1", column31);
			}
		}
		return section3;
	}
	
	@Override
	public Map<String, Object> buildSection4() {
		// Channel TRM and KIOSK don't suppose to have section 4, 
		if (channel == CHANNEL_TRM || channel == CHANNEL_KIOSK) {
			return new HashMap<String, Object>();
		}
		return super.buildSection4();
	}
	
}
