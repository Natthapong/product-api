package th.co.truemoney.product.api.util;

import java.util.HashMap;
import java.util.Map;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class BillReferenceUtil {

	@SuppressWarnings("rawtypes")
	private Map<String, Map> billConfigList;
	private final String isOnlineInquiry = "isOnlineInquiry";
	
	@SuppressWarnings("rawtypes")
	public BillReferenceUtil(){
		try {
			JsonFactory factory = new JsonFactory();
			ObjectMapper m = new ObjectMapper(factory);

			TypeReference<HashMap<String, Map>> typeRef;
			typeRef = new TypeReference<HashMap<String, Map>>() {
			};
			ClassPathResource resource = new ClassPathResource("bill_online.json");
			billConfigList = m.readValue(resource.getFile(), typeRef);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("rawtypes")
	public Boolean isOnlineInquiry(String billCode){
		Map inquiryStatus =  (Map) billConfigList.get(billCode);
		return (inquiryStatus == null) ? false : (Boolean) inquiryStatus.get("isInquiryOnline");
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map<String, String> getBillInfoResponse(String billCode) {
		return (Map) billConfigList.get(billCode);
	}
}
