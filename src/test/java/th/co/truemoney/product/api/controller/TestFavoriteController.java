package th.co.truemoney.product.api.controller;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.junit.Test;

import th.co.truemoney.serviceinventory.ewallet.domain.Favorite;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

public class TestFavoriteController extends BaseTestController{
	
	private static final String fakeAccessToken = "1111111111";
	
	private static final String addFavoriteURL = String.format("/favorite/add/%s",fakeAccessToken);

	@Test
	public void addFavoriteSuccess() throws Exception {
		Favorite favorite = createFavoriteStubbed();
		
		when( this.favoriteServiceMock.addFavorite(any(Favorite.class))).thenReturn(favorite);
		Map<String,String> req = new HashMap<String,String>();
		req.put("amount", "1000");
		req.put("ref1", "1234567890");
		req.put("serviceCode", "tmvh");
		req.put("serviceType", "billpay");
		
		this.verifySuccess(this.doPUT(addFavoriteURL, req))
		.andExpect(jsonPath("$..favoriteID").value("1111"));
	}
	
	@Test
	public void addFavoriteFail() throws Exception{
		when( this.favoriteServiceMock.addFavorite(any(Favorite.class)))
		.thenThrow(new ServiceInventoryException(400,"","",""));
		
		Map<String,String> req = new HashMap<String,String>();
		req.put("amount", "1000");
		req.put("ref1", "1234567890");
		req.put("serviceCode", "tmvh");
		req.put("serviceType", "billpay");
		
		this.verifyFailed(this.doPUT(addFavoriteURL, req));
	}
	
	@Test
	public void invalidRequestParameter() throws Exception {
		Map<String, String> req = new HashMap<String, String>();
		this.verifyBadRequest(this.doPUT(addFavoriteURL, req));
	}
	
	private Favorite createFavoriteStubbed(){
		Favorite favorite = new Favorite();
		favorite.setAmount(new BigDecimal(1000));
		favorite.setFavoriteID(new Long("1111"));
		favorite.setRef1("1234567890");
		favorite.setServiceCode("tmvh");
		favorite.setServiceType("billpay");
		
		return favorite;
	}

}
