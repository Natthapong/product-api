package th.co.truemoney.product.api.domain;

import java.util.Map;

public class ProductResponse {
	
	private String code;
	private String namespace;
	private String headerTh;
	private String headerEn;
	private String messageTh;
	private String messageEn;
	private Map<String, Object> data;
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getHeaderTh() {
		return headerTh;
	}
	public void setHeaderTh(String headerTh) {
		this.headerTh = headerTh;
	}
	public String getHeaderEn() {
		return headerEn;
	}
	public void setHeaderEn(String headerEn) {
		this.headerEn = headerEn;
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

}
