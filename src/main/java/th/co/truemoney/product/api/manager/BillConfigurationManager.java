package th.co.truemoney.product.api.manager;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class BillConfigurationManager {

	private Map<String, BillConfiguration> configurations;
	
	private ObjectMapper mapper = new ObjectMapper(new JsonFactory());

	private String configFile = "bill_online2.json";
	
	public BillConfigurationManager(){
		try {
			configurations = mapper.readValue(
					new ClassPathResource(configFile).getFile(), 
					new TypeReference<HashMap<String, BillConfiguration>>() {});
		} catch (JsonParseException e1) {
			e1.printStackTrace();
		} catch (IOException e2) {
			e2.printStackTrace();
		}
	}
	
	public Boolean isOnlineInquiry(String billCode){
		BillConfiguration conf =  configurations.get(billCode);
		return conf != null ? conf.getIsInquiryOnline() : false;
	}
	
	public String getRef1TitleTh(String billCode) {
		BillConfiguration conf =  configurations.get(billCode);
		return conf != null ? conf.getRef1TitleTh() : "รหัสลูกค้า/หมายเลขโทรศัพท์";
	}

	public String getRef1TitleEn(String billCode) {
		BillConfiguration conf =  configurations.get(billCode);
		return conf != null ? conf.getRef1TitleEn() : "Account/Mobile Number";
	}

	public String getRef2TitleEn(String billCode) {
		BillConfiguration conf = configurations.get(billCode);
		return conf != null ? conf.getRef2TitleEn() : "invoice number";
	}

	public String getRef2TitleTh(String billCode) {
		BillConfiguration conf = configurations.get(billCode);
		return conf != null ? conf.getRef2TitleTh() : "เลขที่ใบแจ้งค่าบริการ";
	}
	
	public BillConfiguration getBillReference(String billCode) {
		return configurations.get(billCode);
	}

	public String getTitleTh(String billCode) {
		BillConfiguration conf = configurations.get(billCode);
		return conf != null ? conf.getTitleTh() : "-";
	}
	
	public String getTitleEn(String billCode) {
		BillConfiguration conf = configurations.get(billCode);
		return conf != null ? conf.getTitleEn() : "-";
	}
}