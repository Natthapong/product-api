package th.co.truemoney.product.api.manager;

import java.io.Serializable;
import java.util.List;

public class EPINConfiguration implements Serializable {

	private static final long serialVersionUID = 1560950672480242853L;
	
	private List<String> priceList;

	public EPINConfiguration() {
		
	}
	
	public List<String> getPriceList() {
		return priceList;
	}

	public void setPriceList(List<String> priceList) {
		this.priceList = priceList;
	}
	
}
