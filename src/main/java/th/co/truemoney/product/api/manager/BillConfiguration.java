package th.co.truemoney.product.api.manager;import java.util.HashMap;import java.util.Map;public class BillConfiguration {	String titleEn;	String titleTh;	String ref1Type;	String ref2Type;    String ref1TitleEn;    String ref1TitleTh;    String ref2TitleEn;    String ref2TitleTh;    String ref1Placeholder;    String ref2Placeholder;    Boolean isInquiryOnline = false;    Float minAmount = 0.00f;    Float maxAmount = 10000.00f;    	public String getTitleEn() {		return notNullString(titleEn);	}	public void setTitleEn(String titleEn) {		this.titleEn = titleEn;	}	public String getTitleTh() {		return notNullString(titleTh);	}	public void setTitleTh(String titleTh) {		this.titleTh = titleTh;	}	public String getRef1Type() {		return ref1Type != null ? ref1Type : "none";	}	public void setRef1Type(String ref1Type) {		this.ref1Type = ref1Type;	}	public String getRef2Type() {		return ref2Type != null ? ref2Type : "none";	}	public void setRef2Type(String ref2Type) {		this.ref2Type = ref2Type;	}	public String getRef1TitleEn() {		return notNullString(ref1TitleEn);	}	public void setRef1TitleEn(String ref1TitleEn) {		this.ref1TitleEn = ref1TitleEn;	}	public String getRef1TitleTh() {		return notNullString(ref1TitleTh);	}	public void setRef1TitleTh(String ref1TitleTh) {		this.ref1TitleTh = ref1TitleTh;	}	public String getRef2TitleEn() {		return notNullString(ref2TitleEn);	}	public void setRef2TitleEn(String ref2TitleEn) {		this.ref2TitleEn = ref2TitleEn;	}	public String getRef2TitleTh() {		return notNullString(ref2TitleTh);	}	public void setRef2TitleTh(String ref2TitleTh) {		this.ref2TitleTh = ref2TitleTh;	}	public Boolean getIsInquiryOnline() {		return isInquiryOnline;	}	public void setIsInquiryOnline(Boolean isInquiryOnline) {		this.isInquiryOnline = isInquiryOnline;	}	public Float getMinAmount() {		return minAmount;	}	public void setMinAmount(Float minAmount) {		this.minAmount = minAmount;	}	public Float getMaxAmount() {		return maxAmount;	}	public void setMaxAmount(Float maxAmount) {		this.maxAmount = maxAmount;	}	public String getRef1Placeholder() {		return ref1Placeholder;	}	public void setRef1Placeholder(String ref1Placeholder) {		this.ref1Placeholder = ref1Placeholder;	}	public String getRef2Placeholder() {		return ref2Placeholder;	}	public void setRef2Placeholder(String ref2Placeholder) {		this.ref2Placeholder = ref2Placeholder;	}	private String notNullString(String str) {		return str != null ? str : "";	}	public Map<String, Object> asMap() {		Map<String, Object> map = new HashMap<String, Object>();		map.put("ref1Type", getRef1Type());    	map.put("ref2Type", getRef2Type());      	map.put("ref1TitleEn", getRef1TitleEn());      	map.put("ref1TitleTh", getRef1TitleTh());      	map.put("ref2TitleEn", getRef2TitleEn());      	map.put("ref2TitleTh", getRef2TitleTh());      	map.put("isInquiryOnline", getIsInquiryOnline());      	map.put("minAmount", String.valueOf(getMinAmount()));      	map.put("maxAmount", String.valueOf(getMaxAmount()));		return map;	}}