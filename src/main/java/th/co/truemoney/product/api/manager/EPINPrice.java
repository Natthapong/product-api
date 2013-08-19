package th.co.truemoney.product.api.manager;

import java.io.Serializable;

public class EPINPrice implements Serializable {
	
	private static final long serialVersionUID = -9013294994166653315L;
	private String price;

	public EPINPrice() {
		super();
	}
	
	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}
	
}
