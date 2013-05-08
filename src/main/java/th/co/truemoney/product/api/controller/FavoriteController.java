package th.co.truemoney.product.api.controller;

import java.math.BigDecimal;
import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import th.co.truemoney.product.api.domain.ProductResponse;
import th.co.truemoney.serviceinventory.ewallet.FavoriteService;
import th.co.truemoney.serviceinventory.ewallet.domain.Favorite;

@Controller
public class FavoriteController extends BaseController{
	
	@Autowired
	FavoriteService favoriteService;
	
	@RequestMapping(value="/favorite/add/{accessTokenID}", method = RequestMethod.PUT)
	@ResponseBody
	public ProductResponse addFavorite(@PathVariable String accessTokenID, @RequestBody Map<String, String> request){
		
		Favorite reqFavorite = buildFavorite(request);
		
		Favorite respFavorite = favoriteService.addFavorite(reqFavorite);
		
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

}
