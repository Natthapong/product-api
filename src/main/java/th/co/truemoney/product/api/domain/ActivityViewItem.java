package th.co.truemoney.product.api.domain;

public class ActivityViewItem {

	private String reportID;
	private String logoURL;
	private String text1Th;
	private String text1En;
	private String text2Th;
	private String text2En;
	private String text3Th;
	private String text3En;
	private String text4Th;
	private String text4En;
	private String text5Th;
	private String text5En;
	
	public ActivityViewItem() {
	}
	
	public ActivityViewItem(String reportID, String txt1) {
		super();
		this.reportID = reportID;
		this.text1Th = txt1;
	}

	public String getReportID() {
		return reportID;
	}
	
	public void setReportID(String reportID) {
		this.reportID = reportID;
	}

	public String getLogoURL() {
		return logoURL;
	}

	public void setLogoURL(String logoURL) {
		this.logoURL = logoURL;
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

	public String getText3Th() {
		return text3Th;
	}

	public void setText3Th(String text3Th) {
		this.text3Th = text3Th;
	}

	public String getText3En() {
		return text3En;
	}

	public void setText3En(String text3En) {
		this.text3En = text3En;
	}

	public String getText4Th() {
		return text4Th;
	}

	public void setText4Th(String text4Th) {
		this.text4Th = text4Th;
	}

	public String getText4En() {
		return text4En;
	}

	public void setText4En(String text4En) {
		this.text4En = text4En;
	}

	public String getText5Th() {
		return text5Th;
	}

	public void setText5Th(String text5Th) {
		this.text5Th = text5Th;
	}

	public String getText5En() {
		return text5En;
	}

	public void setText5En(String text5En) {
		this.text5En = text5En;
	}

}
