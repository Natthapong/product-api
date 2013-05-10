package th.co.truemoney.product.api.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import th.co.truemoney.product.api.domain.FavoriteGroup;
import th.co.truemoney.product.api.domain.FavoriteItem;
import th.co.truemoney.product.api.domain.ProductResponse;
import th.co.truemoney.product.api.manager.MessageManager;
import th.co.truemoney.product.api.manager.OnlineResourceManager;
import th.co.truemoney.product.api.util.ProductResponseFactory;
import th.co.truemoney.serviceinventory.ewallet.FavoriteService;
import th.co.truemoney.serviceinventory.ewallet.domain.Favorite;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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
		this.favoriteController.setOnlineResourceManager(new OnlineResourceManager());
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
		List<FavoriteGroup> groupList = (List<FavoriteGroup>) data.get("groups");
		assertEquals(0, groupList.size());
	}
	
	@Test
	public void getFavoriteList(){
		List<Favorite> favList = new ArrayList<Favorite>();
		Favorite fav1 = createFavorite("d.tmvh", "billpay", new BigDecimal(100), "89417250");
		Favorite fav2 = createFavorite("d.tlp", "billpay", new BigDecimal(200), "39402840489");
		Favorite fav3 = createFavorite("d.mea", "billpay", new BigDecimal(300), "1234567890123");
		favList.add(fav1);
		favList.add(fav2);
		favList.add(fav3);
		
		when(favoriteServiceMock.getFavorites(fakeAccessToken)).thenReturn(favList);
		
		ProductResponse resp = favoriteController.getFavoriteList(fakeAccessToken);
		Map<String, Object> data = resp.getData();
		assertTrue(data.containsKey("groups"));
		
		List<FavoriteGroup> favoriteGroupList = (List<FavoriteGroup>) data.get("groups");
		assertEquals(1, favoriteGroupList.size());
		
		FavoriteGroup group0 = favoriteGroupList.get(0);
		assertEquals("รายการบิล", group0.getTiltleTh());
		assertEquals("BillPay", group0.getTiltleEn());
		assertEquals("billpay", group0.getType());
		assertNotNull(group0.getItems());
		
		List<FavoriteItem> items = group0.getItems();
		assertEquals(3, items.size());
		
		FavoriteItem item0 = items.get(0);
		assertEquals("https://secure.truemoney-dev.com/m/tmn_webview/images/logo_bill/tmvh@2x.png", item0.getLogoURL());
		assertEquals("TrueMove H", item0.getText1Th());
		assertEquals("89417250", item0.getText2Th());
		assertEquals("89417250", item0.getRef1());
		assertEquals("d.tmvh", item0.getServiceCode());
		
		FavoriteItem item1 = items.get(1);
		assertEquals("https://secure.truemoney-dev.com/m/tmn_webview/images/logo_bill/tlp@2x.png", item1.getLogoURL());
		assertEquals("TrueLife Plus", item1.getText1Th());
		assertEquals("39402840489", item1.getText2Th());
		assertEquals("39402840489", item1.getRef1());
		assertEquals("d.tlp", item1.getServiceCode());
		
		FavoriteItem item2 = items.get(2);
		assertEquals("https://secure.truemoney-dev.com/m/tmn_webview/images/logo_bill/mea@2x.png", item2.getLogoURL());
		assertEquals("การไฟฟ้านครหลวง", item2.getText1Th());
		assertEquals("1234567890123", item2.getText2Th());
		assertEquals("1234567890123", item2.getRef1());
		assertEquals("d.mea", item2.getServiceCode());
	}
	
	private Favorite createFavorite(String serviceCode, String serviceType, BigDecimal amount, String ref1) {
		Favorite fav = new Favorite();
		fav.setAmount(amount);
		fav.setRef1(ref1);
		fav.setServiceCode(serviceCode);
		fav.setServiceType(serviceType);
		return fav;
	}
}
