package th.co.truemoney.product.api.domain;

import java.util.ArrayList;
import java.util.List;

public class FavoriteGroup {
	
	private String titleTh;
	private String titleEn;
	private String type;
	private List<FavoriteItem> items = new ArrayList<FavoriteItem>();

	public FavoriteGroup(String titleTh, String titleEn, String type) {
		super();
		this.titleTh = titleTh;
		this.titleEn = titleEn;
		this.type = type;
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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<FavoriteItem> getItems() {
		return items;
	}

	public void setItems(List<FavoriteItem> items) {
		this.items = items;
	}
	
	public void addItems(FavoriteItem item) {
		this.items.add(item);
	}
}
