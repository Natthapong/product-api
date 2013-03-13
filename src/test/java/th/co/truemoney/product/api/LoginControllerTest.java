package th.co.truemoney.product.api;

import static org.junit.Assert.fail;

import java.security.InvalidParameterException;

import org.junit.Test;

import th.co.truemoney.product.api.controller.UserActionController;

public class LoginControllerTest {

	//@Test(expected=InvalidParameterException.class)
	@Test
	public void testLoginInvalidParameter() {
		UserActionController controller = new UserActionController();
		controller.login(null, null);
		//fail("Login validation failed.");
	}
	
}
