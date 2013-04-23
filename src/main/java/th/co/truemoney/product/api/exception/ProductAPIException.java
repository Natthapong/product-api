package th.co.truemoney.product.api.exception;

public class ProductAPIException extends RuntimeException {

	private static final long serialVersionUID = 4877170576013641980L;
	
	public ProductAPIException(String code) {
		super(code);
	}

}
