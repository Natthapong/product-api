package th.co.truemoney.product.api;

import static org.junit.Assert.fail;

import java.security.InvalidParameterException;

import org.junit.Ignore;
import org.junit.Test;

import th.co.truemoney.product.api.controller.UserActionController;

public class TestLoginController {
	
	@Ignore
	@Test(expected=InvalidParameterException.class)
	public void testLoginInvalidUsernameAndAccessKey() {
		UserActionController controller = new UserActionController();
		controller.signin(null, null);
		fail("Login validation failed.");
	}
	
	@Ignore
	@Test(expected=InvalidParameterException.class)
	public void testLoginInvalidUsernameFormat() {
		UserActionController controller = new UserActionController();
		controller.signin("-wrong@emailaddress.com", "!@#$%^");
		fail("Login validation failed.");
	}
}
