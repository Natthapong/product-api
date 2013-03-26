package th.co.truemoney.product.api.controller;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.junit.Test;

public class TestLogoutController extends BaseTestController {

	@Test
	public void logoutSuccess() throws Exception {
		when(
			this.profileServiceMock.logout(
					any(String.class)
			)
		).thenReturn("WHATEVER");
		
		this.verifySuccess(doPOST("/signout/_token_string_", Collections.EMPTY_MAP));
	}
	
}
