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
	
	@SuppressWarnings("unchecked")
	@Test
	public void getEmptyFavoriteList() {
		
		when(favoriteServiceMock.getFavorites(fakeAccessToken)).thenReturn(Collections.EMPTY_LIST);
		
		ProductResponse resp = favoriteController.getFavoriteList(fakeAccessToken);
		Map<String, Object> data = resp.getData();
		assertTrue( data.containsKey("groups"));
		List<FavoriteGroup> groupList = (List<FavoriteGroup>) data.get("groups");
		assertEquals(0, groupList.size());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void getFavoriteList() throws ParseException{
		Favorite fav1 = createFavorite("tmvh", "billpay", new BigDecimal(100), "89417250", parseDateTime("10/05/13 10:10"));
		Favorite fav2 = createFavorite("tlp", "billpay", new BigDecimal(200), "39402840489", parseDateTime("10/05/13 10:10"));
		Favorite fav3 = createFavorite("mea", "billpay", new BigDecimal(300), "1234567890123", parseDateTime("10/05/13 10:10"));
		
		List<Favorite> input = new ArrayList<Favorite>();
		input.add(fav2);
		input.add(fav1);
		input.add(fav3);
		
		List<Favorite> expected = new ArrayList<Favorite>();
		expected.add(fav1);
		expected.add(fav2);
		expected.add(fav3);
		
		when(favoriteServiceMock.getFavorites(fakeAccessToken)).thenReturn(input);
		
		ProductResponse resp = favoriteController.getFavoriteList(fakeAccessToken);
		Map<String, Object> data = resp.getData();
		assertTrue(data.containsKey("groups"));
		
		List<FavoriteGroup> favoriteGroupList = (List<FavoriteGroup>) data.get("groups");
		assertEquals(1, favoriteGroupList.size());
		
		FavoriteGroup group0 = favoriteGroupList.get(0);
		assertEquals("รายการบิล", group0.getTitleTh());
		assertEquals("BillPay", group0.getTitleEn());
		assertEquals("billpay", group0.getType());
		assertNotNull(group0.getItems());
		
		List<FavoriteItem> items = group0.getItems();
		assertEquals(3, items.size());
		
		int index = 0;
		
		for(FavoriteItem item: items){
			assertEquals(expected.get(index).getDate(), item.getDate());
			assertEquals(expected.get(index).getServiceCode(), item.getServiceCode());
			assertEquals(expected.get(index).getRef1(), item.getRef1());
			
			index++;
		}
		
	}
	
	private Date parseDateTime(String dt) throws ParseException {
		return new SimpleDateFormat("dd/MM/yy HH:mm").parse(dt);
	}
	
	private String formatDateTime(Date dt) throws ParseException {
		return new SimpleDateFormat("dd/MM/yy HH:mm").format(dt);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void sortOrderSuccess() throws ParseException{
		List<Favorite> favList = new ArrayList<Favorite>();
		Favorite fav1_1 = createFavorite("tmvh", "billpay", new BigDecimal(100), "89417250", parseDateTime("01/05/13 10:10"));
		Favorite fav1_2 = createFavorite("tmvh", "billpay", new BigDecimal(100), "89417250", parseDateTime("02/05/13 10:10"));
		Favorite fav1_3 = createFavorite("tmvh", "billpay", new BigDecimal(100), "89417250", parseDateTime("03/05/13 10:10"));
		Favorite fav2 = createFavorite("tlp", "billpay", new BigDecimal(200), "39402840489", parseDateTime("04/05/13 10:10"));
		Favorite fav3 = createFavorite("tcg", "billpay", new BigDecimal(300), "1234567890123", parseDateTime("05/05/13 10:10"));
		Favorite fav4 = createFavorite("trmv", "billpay", new BigDecimal(100), "89417250", parseDateTime("06/05/13 10:10"));
		
		favList.add(fav1_1);
		favList.add(fav2);
		favList.add(fav1_2);
		favList.add(fav3);
		favList.add(fav1_3);
		favList.add(fav4);
		
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
		assertEquals(10, item0.getWeight());
		assertEquals("01/05/13 10:10", formatDateTime(item0.getDate()));
		
		FavoriteItem item1 = items.get(1);
		assertEquals(10, item1.getWeight());
		assertEquals("02/05/13 10:10", formatDateTime(item1.getDate()));
		
		FavoriteItem item2 = items.get(2);
		assertEquals(10, item2.getWeight());
		assertEquals("03/05/13 10:10", formatDateTime(item2.getDate()));
		
		FavoriteItem item3 = items.get(3);
		assertEquals(9, item3.getWeight());
		
		FavoriteItem item4 = items.get(4);
		assertEquals(6, item4.getWeight());
		
		FavoriteItem item5 = items.get(5);
		assertEquals(5, item5.getWeight());
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
