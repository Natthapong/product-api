package th.co.truemoney.product.api.controller;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

import java.security.InvalidParameterException;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import th.co.truemoney.product.api.domain.LoginBean;
import th.co.truemoney.product.api.util.MessageManager;
import th.co.truemoney.serviceinventory.ewallet.TmnProfileService;
import th.co.truemoney.serviceinventory.ewallet.domain.Login;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

@RunWith(MockitoJUnitRunner.class)
public class TestLoginController {
	
	UserActionController userController;
	
	TmnProfileService profileService = Mockito.mock(TmnProfileService.class);
	
	MessageManager messageManager = Mockito.mock(MessageManager.class);
	
	@Before
	public void setup() {
		this.userController = new UserActionController();
		this.userController.setProfileService(profileService);
		this.userController.setMessageManager(messageManager);
	}
	
	/**
	 * Test Sign in failed with empty username and password
	 */
	@Test(expected=InvalidParameterException.class)
	public void loginWithEmptyUsernameAndPassword() throws InvalidParameterException {
		userController.signin(new LoginBean(null, null));
		fail("Login validation failed.");
	}
	
	/**
	 * Test Sign in invalid username
	 */
	@Test(expected=InvalidParameterException.class)
	public void loginWithInvalidUsernameFormat() throws InvalidParameterException {
		userController.signin(new LoginBean("wrong_email_address", "password"));
		fail("Login validation failed.");
	}
	
	/**
	 * Test Sign in Success
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void loginSuccess() {
		Integer CHANNEL_ID = 41;
		String username = "customer@truemoney.co.th";
		String password = "hashed-string-password";
		Login login = new Login(username, password);
		when(
			profileService.login(CHANNEL_ID, login)
		).thenReturn("ANY_NOT_NULL_STRING");
		
		Map<String, Object> result = userController.signin(new LoginBean(username, password));
		assertNotNull(result);
		
		Map<String, Object> data = (Map<String, Object>) result.get("data");
		assertNotNull(data);
		assertTrue(data.containsKey("accessToken"));
		assertTrue(data.containsKey("fullname"));
		assertTrue(data.containsKey("currentBalance"));
	}
	
	/**
	 * Test Sign in Failed
	 */
	public void loginFailed() {
		Integer CHANNEL_ID = 41;
		String username = "customer@truemoney.co.th";
		String password = "hashed-string-password";
		Login login = new Login(username, password);
		when(
			profileService.login(CHANNEL_ID, login)
		).thenThrow(new ServiceInventoryException("CODE", "ERR_DESC", "NAMESPACE"));
		
		try {
			userController.signin(new LoginBean(username, password));
			fail("login exception handling failed");
		} catch (ServiceInventoryException e) {
			assertNotNull(e.getErrorCode());
			assertNotNull(e.getErrorDescription());
			assertNotNull(e.getErrorNamespace());
		}
	}
	
}
