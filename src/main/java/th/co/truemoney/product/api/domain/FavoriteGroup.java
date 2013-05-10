package th.co.truemoney.product.api.domain;

import java.util.ArrayList;
import java.util.List;

public class FavoriteGroup {
	
	private String tiltleTh;
	private String tiltleEn;
	private String type;
	private List<FavoriteItem> items = new ArrayList<FavoriteItem>();

	public FavoriteGroup(String tiltleTh, String tiltleEn, String type) {
		super();
		this.tiltleTh = tiltleTh;
		this.tiltleEn = tiltleEn;
		this.type = type;
	}

	public String getTiltleTh() {
		return tiltleTh;
	}

	public void setTiltleTh(String tiltleTh) {
		this.tiltleTh = tiltleTh;
	}

	public String getTiltleEn() {
		return tiltleEn;
	}

	public void setTiltleEn(String tiltleEn) {
		this.tiltleEn = tiltleEn;
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
