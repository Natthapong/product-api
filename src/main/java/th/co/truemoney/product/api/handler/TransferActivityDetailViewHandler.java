package th.co.truemoney.product.api.handler;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import th.co.truemoney.product.api.domain.WalletActivity;
import th.co.truemoney.product.api.util.Utils;

@Component
public class TransferActivityDetailViewHandler extends GeneralActivityDetailViewHandler {

    @Override
    public Map<String, String> buildSection1() {
        Map<String, String> section1 = super.buildSection1();
        section1.put("titleTh", WalletActivity.getActionInThai(activity.getAction()));
        section1.put("titleEn", WalletActivity.getActionInEnglish(activity.getAction()));
        return section1;
    }

    @Override
    public Map<String, Object> buildSection2() {
        Map<String, Object> section2 = super.buildSection2();
        Map<String, Object> column1 = new HashMap<String, Object>();
        Map<String, String> cell1 = new HashMap<String, String>();
        Map<String, String> cell2 = new HashMap<String, String>();
        if ("debtor".equals(activity.getAction())) {
            cell1.put("titleTh", "หมายเลขผู้รับ");
        } else {
            cell1.put("titleTh", "หมายเลขผู้ส่ง");
        }
        cell1.put("titleEn", "account number");
        cell1.put("value", Utils.formatMobileNumber(activity.getRef1()));
        if ("debtor".equals(activity.getAction())) {
            cell2.put("titleTh", "ชื่อผู้รับ");
        } else {
            cell2.put("titleTh", "ชื่อผู้ส่ง");
        }
        cell2.put("titleEn", "account owner");
        cell2.put("value", StringUtils.hasText(activity.getRef2()) ? activity.getRef2() : "-");
        column1.put("cell2", cell2);
        column1.put("cell1", cell1);
        section2.put("column1", column1);
        return section2;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Map<String, Object> buildSection3() {
        Map<String, Object> section3 = super.buildSection3();
		Map<String, Object> column1 = (Map<String, Object>)section3.get("column1");
        if (column1.containsKey("cell2")) {
            column1.remove("cell2");
        }
        if (section3.containsKey("column2")) {
            section3.remove("column2");
        }
        return section3;
    }
    
    public Map<String,Object> buildPersonalMessage(String personalMessageValue)
    {
    	Map<String, Object> personalMessageData = new HashMap<String, Object>();
    	if(personalMessageValue == null){
    		personalMessageValue = "";
    	}
    	personalMessageData.put("value",personalMessageValue);
    	return personalMessageData;
    }
}
