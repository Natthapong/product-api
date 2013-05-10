package th.co.truemoney.product.api.domain;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class FavoriteItem {
	
	private String text1Th;
	private String text1En;
	private String text2Th;
	private String text2En;
	private String logoURL;
	private String serviceCode;
	private String ref1;
	
	private int weight;
	private Date date;
	
	public FavoriteItem() {
		super();
	}
	
	public FavoriteItem(String text1Th, String text2Th, String logoURL, String serviceCode, String ref1) {
		super();
		this.text1Th = text1Th;
		this.text1En = text1Th;
		this.text2Th = text2Th;
		this.text2En = text2Th;
		this.logoURL = logoURL;
		this.serviceCode = serviceCode;
		this.ref1 = ref1;
	}

	public String getText1Th() {
		return text1Th;
	}

	public void setText1Th(String text1Th) {
		this.text1Th = text1Th;
	}

	public String getText1En() {
		return text1En;
	}

	public void setText1En(String text1En) {
		this.text1En = text1En;
	}

	public String getText2Th() {
		return text2Th;
	}

	public void setText2Th(String text2Th) {
		this.text2Th = text2Th;
	}

	public String getText2En() {
		return text2En;
	}

	public void setText2En(String text2En) {
		this.text2En = text2En;
	}

	public String getLogoURL() {
		return logoURL;
	}

	public void setLogoURL(String logoURL) {
		this.logoURL = logoURL;
	}

	public String getServiceCode() {
		return serviceCode;
	}

	public void setServiceCode(String serviceCode) {
		this.serviceCode = serviceCode;
	}

	public String getRef1() {
		return ref1;
	}

	public void setRef1(String ref1) {
		this.ref1 = ref1;
	}
	
	@JsonIgnore
	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}
	
	@JsonIgnore
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

}
