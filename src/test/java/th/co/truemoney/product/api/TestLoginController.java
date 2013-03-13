package th.co.truemoney.product.api;

import static org.junit.Assert.fail;

import java.security.InvalidParameterException;

import org.junit.Test;

import th.co.truemoney.product.api.controller.UserActionController;

public class TestLoginController {
	
	@Test(expected=InvalidParameterException.class)
	public void testLoginInvalidUsernameAndAccessKey() {
		UserActionController controller = new UserActionController();
		controller.login(null, null);
		fail("Login validation failed.");
	}
	
	@Test(expected=InvalidParameterException.class)
	public void testLoginInvalidUsernameFormat() {
		UserActionController controller = new UserActionController();
		controller.login("-wrong@emailaddress.com", "!@#$%^");
		fail("Login validation failed.");
	}
}
