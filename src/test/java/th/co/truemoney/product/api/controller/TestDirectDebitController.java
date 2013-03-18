package th.co.truemoney.product.api.controller;

import static org.junit.Assert.fail;

import org.junit.Ignore;
import org.junit.Test;

import th.co.truemoney.product.api.controller.DirectDebitController;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

public class TestDirectDebitController {

	@Ignore
	@Test(expected = ServiceInventoryException.class)
	public void notGetListBank() {
		DirectDebitController controller = new DirectDebitController();
		controller.getUserDirectDebitSources("user1.test.v1@gmail.com", "55555555");
		fail("Cannot get bank list.");
	}

}
