package th.co.truemoney.product.api.manager;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class EPINConfigurationManager {
	
	private Map<String, List<EPINPrice>> configurations;
	
	private ObjectMapper mapper = new ObjectMapper(new JsonFactory());

	private String configFile = "src/main/webapp/WEB-INF/pages/epinPrice.jsp";
	
	public EPINConfigurationManager(){
		try {
			configurations = mapper.readValue(
					new FileSystemResource(configFile).getFile(), 
					new TypeReference<HashMap<String, List<EPINPrice>>>() {});
		} catch (JsonParseException e1) {
			e1.printStackTrace();
		} catch (IOException e2) {
			e2.printStackTrace();
		}
	}
	
	public List<EPINPrice> getEpinPrice() {
		List<EPINPrice> configuration = configurations.get("available.epin.prices");
		return configuration;
	}
	
}
