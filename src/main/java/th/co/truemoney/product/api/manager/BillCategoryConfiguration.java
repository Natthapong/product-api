package th.co.truemoney.product.api.manager;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BillCategoryConfiguration {

	private String categoryID;
	private String categoryLogo;
	private String categoryTitleTh;
	private String categoryTitleEn;
	private List<BillInformation> items = new ArrayList<BillInformation> ();
	
	public String getCategoryID() {
		return categoryID;
	}
	public void setCategoryID(String categoryID) {
		this.categoryID = categoryID;
	}
	public String getCategoryLogo() {
		return categoryLogo;
	}
	public void setCategoryLogo(String categoryLogo) {
		this.categoryLogo = categoryLogo;
	}
	public String getCategoryTitleTh() {
		return categoryTitleTh;
	}
	public void setCategoryTitleTh(String categoryTitleTh) {
		this.categoryTitleTh = categoryTitleTh;
	}
	public String getCategoryTitleEn() {
		return categoryTitleEn;
	}
	public void setCategoryTitleEn(String categoryTitleEn) {
		this.categoryTitleEn = categoryTitleEn;
	}
	public List<BillInformation> getItems() {
		return items;
	}
	public void setItems(List<BillInformation> items) {
		this.items = items;
	}
		
}
