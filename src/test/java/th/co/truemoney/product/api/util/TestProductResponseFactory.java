package th.co.truemoney.product.api.util;

import static org.junit.Assert.assertThat;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import th.co.truemoney.product.api.config.TestWebConfig;
import th.co.truemoney.product.api.domain.ProductResponse;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestWebConfig.class })
public class TestProductResponseFactory {
	
	@Autowired
	ProductResponseFactory responseFactory;
	
	@Test
	public void displayPaymentAmountRangeErrorMessage() {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("minAmount", new BigDecimal(1145.52));
		data.put("maxAmount", new BigDecimal(10000.00));
		
		ServiceInventoryException lessThanMinimumAmountException = new ServiceInventoryException();
		lessThanMinimumAmountException.setErrorNamespace("TMN-SERVICE-INVENTORY");
		lessThanMinimumAmountException.setErrorCode("20001");
		lessThanMinimumAmountException.setData(data);
		
		ProductResponse response = responseFactory.createErrorProductResponse(lessThanMinimumAmountException);
		assertThat(response.getMessageTh(), Matchers.containsString("1,145.52"));
		assertThat(response.getMessageTh(), Matchers.containsString("10,000"));
	}
	
	@Test
	public void errorMessageForBillMEAOverdue(){
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("target", "mea");
    	data.put("amount", new BigDecimal(1234.55));
    	data.put("dueDate", "10/05/2013");
    	
    	ServiceInventoryException overdueBillPayException = new ServiceInventoryException();
		overdueBillPayException.setErrorNamespace("TMN-PRODUCT");
		overdueBillPayException.setErrorCode("80000");
		overdueBillPayException.setData(data);
		
		ProductResponse response = responseFactory.createErrorProductResponse(overdueBillPayException);
		assertThat(response.getMessageTh(), Matchers.containsString("1,234.55"));
		assertThat(response.getMessageTh(), Matchers.containsString("10/05/2013"));
	}
	

}
