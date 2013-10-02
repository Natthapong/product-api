package th.co.truemoney.product.api.exception;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.Matchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.util.List;

import org.junit.Test;

import th.co.truemoney.product.api.controller.BaseTestController;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

public class TestMapServiceException extends BaseTestController {

	@Test
	@SuppressWarnings("unchecked")
    public void test() throws Exception {
		String barcode = "1234567890";
		String tokenid = "0000000000";
		String getBillInfoURL = String.format("/bill-payment/barcode/%s/%s", barcode, tokenid);
		
        when(
        	billPaymentServiceMock.retrieveBillInformationWithBarcode(any(List.class), anyString())
        ).thenThrow(new ServiceInventoryException(500, "1001", "", "ENGINE"));
            
        this.verifyFailed(this.doGET(getBillInfoURL))
        	.andExpect(jsonPath("$.code").value("1001"))
			.andExpect(jsonPath("$.namespace").value("SIENGINE"));
    }


}
