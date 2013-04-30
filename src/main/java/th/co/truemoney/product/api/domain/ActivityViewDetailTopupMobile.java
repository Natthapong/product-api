package th.co.truemoney.product.api.domain;

import java.util.Map;

public class ActivityViewDetailTopupMobile {
	private Map<String, String> header;
	private Map<String, String> section1;
	private Map<String, String> section2;
	private Map<String, String> section3;
	private Map<String, String> section4;
	
	public ActivityViewDetailTopupMobile(){
		
	}

	public Map<String, String> getHeader() {
		return header;
	}

	public void setHeader(Map<String, String> header) {
		this.header = header;
	}

	public Map<String, String> getSection1() {
		return section1;
	}

	public void setSection1(Map<String, String> section1) {
		this.section1 = section1;
	}

	public Map<String, String> getSection2() {
		return section2;
	}

	public void setSection2(Map<String, String> section2) {
		this.section2 = section2;
	}

	public Map<String, String> getSection3() {
		return section3;
	}

	public void setSection3(Map<String, String> section3) {
		this.section3 = section3;
	}

	public Map<String, String> getSection4() {
		return section4;
	}

	public void setSection4(Map<String, String> section4) {
		this.section4 = section4;
	}

}
