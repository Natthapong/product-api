package th.co.truemoney.product.api.controller;

import java.math.BigDecimal;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import th.co.truemoney.product.api.domain.FavoriteGroup;
import th.co.truemoney.product.api.domain.FavoriteItem;
import th.co.truemoney.product.api.domain.ProductResponse;
import th.co.truemoney.product.api.domain.WalletActivity;
import th.co.truemoney.product.api.manager.OnlineResourceManager;
import th.co.truemoney.serviceinventory.ewallet.FavoriteService;
import th.co.truemoney.serviceinventory.ewallet.domain.Favorite;

@Controller
public class FavoriteController extends BaseController {

	@Autowired
	private FavoriteService favoriteService;
	
	@Autowired
	private OnlineResourceManager onlineResourceManager;

	@RequestMapping(value = "/favorite/add/{accessTokenID}", method = RequestMethod.PUT)
	@ResponseBody
	public ProductResponse addFavorite(@PathVariable String accessTokenID,
			@RequestBody Map<String, String> request) {

		Favorite reqFavorite = buildFavorite(request);

		Favorite respFavorite = favoriteService.addFavorite(reqFavorite,
				accessTokenID);

		Map<String, Object> data = new HashMap<String, Object>();
		data.put("favoriteID", String.valueOf(respFavorite.getFavoriteID()));
		return this.responseFactory.createSuccessProductResonse(data);
	}

	private Favorite buildFavorite(Map<String, String> request) {
		validate(request);

		Favorite reqFavorite = new Favorite();
		reqFavorite.setServiceCode(request.get("serviceCode"));
		reqFavorite.setServiceType(request.get("serviceType"));
		reqFavorite.setRef1(request.get("ref1"));
		reqFavorite.setAmount(new BigDecimal(request.get("amount")));
		return reqFavorite;
	}

	private void validate(Map<String, String> request) {
		String amount = request.get("amount");
		if (!StringUtils.hasText(amount)) {
			throw new InvalidParameterException("50005");
		}
		try {
			new BigDecimal(amount);
		} catch (NumberFormatException e) {
			throw new InvalidParameterException("50005");
		}
		if (!StringUtils.hasText(request.get("serviceCode"))) {
			throw new InvalidParameterException("50006");
		}
		if (!StringUtils.hasText(request.get("serviceType"))) {
			throw new InvalidParameterException("50007");
		}
		if (!StringUtils.hasText(request.get("ref1"))) {
			throw new InvalidParameterException("50008");
		}
	}

	@RequestMapping(value = "/favorite/{accessTokenID}", method = RequestMethod.GET)
	@ResponseBody
	public ProductResponse getFavoriteList(@PathVariable String accessTokenID) {

		List<Favorite> favoriteList = favoriteService.getFavorites(accessTokenID);

		Map<String, Object> data = new HashMap<String, Object>();
		List<FavoriteGroup> groups = new ArrayList<FavoriteGroup>();
		data.put("groups", groups);
		
		if (favoriteList == null) {
			favoriteList = new ArrayList<Favorite>();
		}
		
		if (favoriteList.size() > 0) {
			
			FavoriteGroup group0 = new FavoriteGroup("รายการบิล", "BillPay", "billpay");
			groups.add(group0);
		
			for (Favorite favorite : favoriteList) {
				String logo = onlineResourceManager.getActivityActionLogoURL(favorite.getServiceCode());
				String textServiceCode = WalletActivity.getActionInThai(favorite.getServiceCode());
				group0.addItems(new FavoriteItem( textServiceCode, favorite.getRef1(), logo, favorite.getServiceCode(), 
						favorite.getRef1(), favorite.getDate(), WalletActivity.getWeightFromServiceCode(favorite.getServiceCode()) ) );
			}
			order(group0.getItems());
		}
		return this.responseFactory.createSuccessProductResonse(data);
	}
	
	public void setFavoriteService(FavoriteService favoriteService) {
		this.favoriteService = favoriteService;
	}
	
	public void setOnlineResourceManager(OnlineResourceManager onlineResourceManager) {
		this.onlineResourceManager = onlineResourceManager;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void order(List<FavoriteItem> itemList){
		Collections.sort(itemList,  new Comparator() {

			@Override
			public int compare(Object o1, Object o2) {
				FavoriteItem fi1 = (FavoriteItem) o1;
				FavoriteItem fi2 = (FavoriteItem) o2;
				int x1 = fi1.getWeight();
				int x2 = fi2.getWeight();
				
				if((x1 - x2) != 0){
					return x2 - x1;
				}else{
					Date d1 = (Date) fi1.getDate();
					Date d2 = (Date) fi2.getDate();
					return d2.compareTo(d1);
				}
			}
			
		});
	}
}
