package th.co.truemoney.product.api.manager;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class EPINConfigurationManager {
	
	@Autowired
	private Environment env;
	
	private List<String> priceList = null;
	
	public List<String> getEpinPrice() {
		if (priceList == null) {
			String priceStr = env.getProperty("\"available.epin.prices\"");
			priceStr = priceStr.trim();
			priceStr = priceStr.replace("[", "");
			priceStr = priceStr.replace("]", "");
			priceStr = priceStr.replaceAll("\"", "");
			priceStr = priceStr.replaceAll(" ", "");
			System.out.println(priceStr);
			priceList = Arrays.asList(priceStr.split(","));
		}
		return priceList;
	}
	
}
