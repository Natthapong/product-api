package th.co.truemoney.product.api.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.security.InvalidParameterException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import th.co.truemoney.product.api.config.TestWebConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = { TestWebConfig.class })
public class TestValidateUtil {
	@Test
	public void shouldPassTestMobileValidator() throws InvalidParameterException {
		assertTrue(ValidateUtil.checkMobileNumber("0868185055"));
	}

	@Test
	public void shouldFailTestMobileValidator() throws InvalidParameterException{
		assertFalse(ValidateUtil.checkMobileNumber("0268185055"));
	}
	
	@Test
	public void testInValidMobile() throws InvalidParameterException{
		assertFalse(ValidateUtil.checkMobileNumber("0268185055"));
	}
	
	@Test
	public void testInValidMobileLength() throws InvalidParameterException{
		assertFalse(ValidateUtil.checkMobileNumber("08999999"));
	}
	
	@Test
	public void testValidEmail() throws InvalidParameterException{
		assertTrue(ValidateUtil.checkEmail("apinya@gmail.com"));
	}
	
	@Test
	public void testInValidEmailNoDot() throws InvalidParameterException{
		assertFalse(ValidateUtil.checkEmail("apinya@gmail"));
	}
	
	@Test
	public void testInValidEmail() throws InvalidParameterException{
		assertFalse(ValidateUtil.checkEmail("apinya"));
	}

	@Test
	public void testInValidEmailEmpty() throws InvalidParameterException{
		assertFalse(ValidateUtil.checkEmail(""));
	}

	
}
