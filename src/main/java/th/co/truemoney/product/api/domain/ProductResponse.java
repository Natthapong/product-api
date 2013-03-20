package th.co.truemoney.product.api.domain;

import java.util.HashMap;
import java.util.Map;

public class ProductResponse {
	
	private String code;
	private String namespace;
	private String titleTh;
	private String titleEn;
	private String messageTh;
	private String messageEn;
	private String originalMessage;
	private Map<String, Object> data;
	
	public ProductResponse() {
		this.code      = "";
		this.namespace = "";
		this.titleTh   = "";
		this.titleEn   = "";
		this.messageTh = "";
		this.messageEn = "";
		this.originalMessage = "";
		this.data = new HashMap<String, Object>();
	}
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getMessageTh() {
		return messageTh;
	}
	public void setMessageTh(String messageTh) {
		this.messageTh = messageTh;
	}
	public String getMessageEn() {
		return messageEn;
	}
	public void setMessageEn(String messageEn) {
		this.messageEn = messageEn;
	}
	public String getNamespace() {
		return namespace;
	}
	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}
	public Map<String, Object> getData() {
		return data;
	}
	public void setData(Map<String, Object> data) {
		this.data = data;
	}
	public String getTitleTh() {
		return titleTh;
	}
	public void setTitleTh(String titleTh) {
		this.titleTh = titleTh;
	}
	public String getTitleEn() {
		return titleEn;
	}
	public void setTitleEn(String titleEn) {
		this.titleEn = titleEn;
	}
	public String getOriginalMessage() {
		return originalMessage;
	}
	public void setOriginalMessage(String originalMessage) {
		this.originalMessage = originalMessage;
	}

}
