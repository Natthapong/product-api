package th.co.truemoney.product.api.util;

import java.util.regex.Pattern;

public class ValidateUtil {

	private static Pattern MOBILE_PATTERN = Pattern.compile("^[0][89][0-9]{8}");
	private static Pattern EMAIL_PATTERN = Pattern
            .compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");

    public static boolean checkMobileNumber(String mobileNumber) {
        if (isEmpty(mobileNumber)) {
            return false;
        } else {
            return MOBILE_PATTERN.matcher(mobileNumber).matches();
        }
    }

    public static boolean checkEmail(String email) {
        if (isEmpty(email)) {
            return false;
        } else {
            return EMAIL_PATTERN.matcher(email).matches();
        }
    }

    public static boolean isEmpty(String s) {
        if (s != null) {
            return s.isEmpty();
        }

        return true;
    }
}

