package th.co.truemoney.product.api.manager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class BillCategoryConfigurationManager {

	private Map<String, BillCategoryConfiguration> configurations;
	
	private ObjectMapper mapper = new ObjectMapper(new JsonFactory());

	private String configFile = "bill_information.json";
	
	private ArrayList<BillCategoryConfiguration> billCategoryConfigurations;

    @Autowired @Qualifier("mHost")
    private String mHost;
    
	public BillCategoryConfigurationManager(){
		try {
			configurations = mapper.readValue(
					new ClassPathResource(configFile).getFile(), 
					new TypeReference<HashMap<String, Collection<BillCategoryConfiguration>>>() {});
		} catch (JsonParseException e1) {
			e1.printStackTrace();
		} catch (IOException e2) {
			e2.printStackTrace();
		}
	}
	
	public ArrayList<BillCategoryConfiguration> getBillCategoryConfigurations() {
		if (this.billCategoryConfigurations == null) {
			this.billCategoryConfigurations = getAllBillPayments();
		}
		return this. billCategoryConfigurations;
	}

	public void setBillCategoryConfigurations(ArrayList<BillCategoryConfiguration> billCategoryConfigurations) {
		this.billCategoryConfigurations = billCategoryConfigurations;
	}

	public void setmHost(String mHost) {
		this.mHost = mHost;
	}

	private ArrayList<BillCategoryConfiguration> getAllBillPayments() {
		Collection<BillCategoryConfiguration> billCategoryConfigurations = configurations.values();
		if (billCategoryConfigurations != null) {
			Iterator it = billCategoryConfigurations.iterator();
			ArrayList<BillCategoryConfiguration> newBillCategoryList = new ArrayList<BillCategoryConfiguration>();
			while(it.hasNext()) {
				ArrayList<BillCategoryConfiguration> billCategoryConfigurationList = (ArrayList<BillCategoryConfiguration>) it.next();
				for (BillCategoryConfiguration billCategoryConfiguration : billCategoryConfigurationList) {
					List<BillInformation> newBillInfomations = new ArrayList<BillInformation>();
					List<BillInformation> billInformations = (List<BillInformation>) billCategoryConfiguration.getItems();
					for (BillInformation billInformation : billInformations) {
						billInformation.setLogo(this.mHost+billInformation.getLogo());
						newBillInfomations.add(billInformation);
					}
					billCategoryConfiguration.setCategoryLogo(this.mHost+billCategoryConfiguration.getCategoryLogo());
					newBillCategoryList.add(billCategoryConfiguration);
				}
			}
			return newBillCategoryList;
		} else {
			return new ArrayList<BillCategoryConfiguration>();
		}
	}
	
}