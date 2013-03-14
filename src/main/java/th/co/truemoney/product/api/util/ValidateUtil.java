package th.co.truemoney.product.api.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidateUtil {

	public static Pattern MOBILE_PATTERN = Pattern.compile("^[0][89][0-9]{8}");
	public static Pattern EMAIL_PATTERN = Pattern
			.compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
					+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");

	public static boolean checkMobileNumber(String mobileNo) {
		if (!checkIsNull(mobileNo)) {
			Matcher matcher = MOBILE_PATTERN.matcher(mobileNo);
			return matcher.matches();
		}
		return false;
	}

	public static boolean checkEmail(String email) {
		if (!checkIsNull(email)) {
			Matcher matcher = EMAIL_PATTERN.matcher(email);
			return matcher.matches();
		}
		return false;
	}

<<<<<<< HEAD
	public static boolean checkIsNull(String param) {
=======
	public static boolean checkIsNull(String param){
>>>>>>> a10b2647c25e4a6cc2709bfe7e5959306e3db4a1
		if (param == null) {
			return true;
		} else {
			return false;
		}
	}

}
