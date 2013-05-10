package th.co.truemoney.product.api.controller;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import th.co.truemoney.product.api.domain.ProductResponse;
import th.co.truemoney.product.api.manager.MessageManager;
import th.co.truemoney.product.api.util.ProductResponseFactory;
import th.co.truemoney.serviceinventory.ewallet.FavoriteService;
import th.co.truemoney.serviceinventory.ewallet.domain.Favorite;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class TestFavoriteControllerUnit {

	FavoriteController favoriteController;
	
	FavoriteService favoriteServiceMock;
	
	ProductResponseFactory responseFactory;
	
	private static final String fakeAccessToken = "1111111111";
	
	@Before
	public void setup() {
		this.favoriteController = new FavoriteController();
		this.favoriteServiceMock = mock(FavoriteService.class);
		this.responseFactory = new ProductResponseFactory();
		this.responseFactory.setMessageManager(mock(MessageManager.class));
		this.favoriteController.setFavoriteService(favoriteServiceMock);
		this.favoriteController.setResponseFactory(responseFactory);
	}
	
	public void tearDown() {
		reset(this.favoriteServiceMock);
	}
	
	@Test
	public void getEmptyFavoriteList() {
		when(favoriteServiceMock.getFavorites(fakeAccessToken)).thenReturn(Collections.EMPTY_LIST);
		ProductResponse resp = favoriteController.getFavoriteList(fakeAccessToken);
		Map<String, Object> data = resp.getData();
		assertTrue( data.containsKey("groups"));
		List<Favorite> favoriteList = (List<Favorite>) data.get("groups");
		assertEquals(0, favoriteList.size());
	}
	
}
