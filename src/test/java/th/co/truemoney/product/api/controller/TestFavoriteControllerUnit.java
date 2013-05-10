package th.co.truemoney.product.api.controller;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
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
	
	SimpleDateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm");
	
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
	public void getFavoriteList() throws ParseException{
		List<Favorite> favList = new ArrayList<Favorite>();
		Favorite fav1 = createFavorite("d.tmvh", "billpay", new BigDecimal(100), "89417250", df.parse("10/05/13 10:10"));
		Favorite fav2 = createFavorite("d.tlp", "billpay", new BigDecimal(200), "39402840489", df.parse("10/05/13 10:10"));
		Favorite fav3 = createFavorite("d.mea", "billpay", new BigDecimal(300), "1234567890123", df.parse("10/05/13 10:10"));
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
	
	@Test
	public void sortOrderSuccess() throws ParseException{
		List<Favorite> favList = new ArrayList<Favorite>();
		Favorite fav1_1 = createFavorite("d.tmvh", "billpay", new BigDecimal(100), "89417250", df.parse("01/05/13 10:10"));
		Favorite fav1_2 = createFavorite("d.tmvh", "billpay", new BigDecimal(100), "89417250", df.parse("02/05/13 10:10"));
		Favorite fav1_3 = createFavorite("d.tmvh", "billpay", new BigDecimal(100), "89417250", df.parse("03/05/13 10:10"));
		Favorite fav2 = createFavorite("d.tlp", "billpay", new BigDecimal(200), "39402840489", df.parse("04/05/13 10:10"));
		Favorite fav3 = createFavorite("d.mea", "billpay", new BigDecimal(300), "1234567890123", df.parse("05/05/13 10:10"));
		Favorite fav4 = createFavorite("d.trmv", "billpay", new BigDecimal(100), "89417250", df.parse("06/05/13 10:10"));
		
		favList.add(fav1_2);
		favList.add(fav1_1);
		favList.add(fav2);
		favList.add(fav4);
		favList.add(fav1_3);
		favList.add(fav3);
		
		when(favoriteServiceMock.getFavorites(fakeAccessToken)).thenReturn(favList);
		
		ProductResponse resp = favoriteController.getFavoriteList(fakeAccessToken);
		Map<String, Object> data = resp.getData();
		assertTrue(data.containsKey("groups"));
		
		List<FavoriteGroup> favoriteGroupList = (List<FavoriteGroup>) data.get("groups");
		assertNotNull(favoriteGroupList.size());
		
		FavoriteGroup group0 = favoriteGroupList.get(0);
		assertNotNull(group0.getItems());
		
		List<FavoriteItem> items = group0.getItems();
		assertEquals(6, items.size());
		
		FavoriteItem item0 = items.get(0);
		assertEquals("01/05/13 10:10", df.format(item0.getDate()));
		
		FavoriteItem item1 = items.get(0);
		assertEquals("02/05/13 10:10", df.format(item1.getDate()));
		
		FavoriteItem item2 = items.get(0);
		assertEquals("03/05/13 10:10", df.format(item2.getDate()));
		
		FavoriteItem item3 = items.get(0);
		assertEquals("04/05/13 10:10", df.format(item3.getDate()));
		
		FavoriteItem item4 = items.get(0);
		assertEquals("05/05/13 10:10", df.format(item4.getDate()));
		
		FavoriteItem item5 = items.get(0);
		assertEquals("06/05/13 10:10", df.format(item5.getDate()));
	}
	
	private Favorite createFavorite(String serviceCode, String serviceType, BigDecimal amount, String ref1, Date date) {
		Favorite fav = new Favorite();
		fav.setAmount(amount);
		fav.setRef1(ref1);
		fav.setServiceCode(serviceCode);
		fav.setServiceType(serviceType);
		fav.setDate(date);
		return fav;
	}
}
