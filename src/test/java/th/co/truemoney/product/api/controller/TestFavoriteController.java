package th.co.truemoney.product.api.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import th.co.truemoney.serviceinventory.ewallet.domain.Favorite;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

public class TestFavoriteController extends BaseTestController{
	
	private static final String fakeAccessToken = "1111111111";
	
	private static final String addFavoriteURL = String.format("/favorite/add/%s",fakeAccessToken);
	private static final String getFavoriteListURL = String.format("/favorite/%s",fakeAccessToken);
	private static final String removeFavoriteURL = String.format("/favorite/remove/%s",fakeAccessToken);

	@Test
	public void addFavoriteSuccess() throws Exception {
		Favorite favorite = createFavoriteStubbed();
		
		when( this.favoriteServiceMock.addFavorite(any(Favorite.class), anyString())).thenReturn(favorite);
		Map<String,String> req = new HashMap<String,String>();
		req.put("amount", "1000");
		req.put("ref1", "1234567890");
		req.put("serviceCode", "tmvh_c");
		req.put("serviceType", "billpay");
		
		this.verifySuccess(this.doPUT(addFavoriteURL, req))
		.andExpect(jsonPath("$..favoriteID").value("1111"));
	}
	
	@Test
	public void addFavoriteFail() throws Exception{
		when( this.favoriteServiceMock.addFavorite(any(Favorite.class), anyString()))
		.thenThrow(new ServiceInventoryException(400,"","",""));
		
		Map<String,String> req = new HashMap<String,String>();
		req.put("amount", "1000");
		req.put("ref1", "1234567890");
		req.put("serviceCode", "tmvh_c");
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
		favorite.setServiceCode("tmvh_c");
		favorite.setServiceType("billpay");
		
		return favorite;
	}
	
	@Test
	public void getFavoriteListSuccess() throws Exception{
		List<Favorite> favList = new ArrayList<Favorite>();
		favList.add(createFavoriteStubbed());
		
		when( this.favoriteServiceMock.getFavorites(fakeAccessToken)).thenReturn(favList);
		
		this.verifySuccess(this.doGET(getFavoriteListURL))
		.andExpect(jsonPath("$..weight").doesNotExist())
		.andExpect(jsonPath("$..date").doesNotExist())
		.andExpect(jsonPath("$..text2Th").value("รหัสลูกค้า/หมายเลขโทรศัพท์"));
	}
	
	@Test 
	public void getFavoriteListFail() throws Exception{
		when( this.favoriteServiceMock.getFavorites(fakeAccessToken))
		.thenThrow(new ServiceInventoryException(400, "", "", ""));
		
		this.verifyFailed(this.doGET(getFavoriteListURL));
	}
	
	@Test
	public void removeFavoriteSuccess() throws Exception{
		Map<String, String> reqBody = new HashMap<String, String>();
		reqBody.put("billCode", "tmvh");
		reqBody.put("ref1", "1111111");
		
		this.verifySuccess(this.doDELETE(removeFavoriteURL, reqBody));
	}
	
	@Test 
	public void removeFavoriteFail() throws Exception{
		Map<String, String> reqBody = new HashMap<String, String>();
		reqBody.put("billCode", "tmvh");
		reqBody.put("ref1", "1111111");
		
		Mockito.doThrow(new ServiceInventoryException(400, "", "", ""))
		.when(this.favoriteServiceMock)
		.deleteFavorite(anyString(), anyString(), anyString());
		
		this.verifyFailed(this.doDELETE(removeFavoriteURL, reqBody));
	}

}
