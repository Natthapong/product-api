package th.co.truemoney.product.api.manager;

import java.util.Arrays;
import java.util.Collections;
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
			if (priceStr == null) {
				priceList = Collections.emptyList();
			} else {
				priceStr = priceStr.trim();
				priceStr = priceStr.replaceAll("\\[", "")
								   .replaceAll("\\]", "")
								   .replaceAll("\"", "")
								   .replaceAll(" ", "");
				priceList = Arrays.asList(priceStr.split(","));
			}
			
		}
		return priceList;
	}
}
