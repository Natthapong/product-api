package th.co.truemoney.product.api.controller;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

import java.util.Map;


import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import th.co.truemoney.product.api.domain.ActivityViewDetailTopupMobile;
import th.co.truemoney.product.api.domain.ProductResponse;
import th.co.truemoney.serviceinventory.ewallet.domain.ActivityDetail;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

public class TestMobileWalletActivityDetailController extends
		BaseTestController {

	@Autowired
	MobileWalletActivityController controller;
	
	private static final String fakeAccessTokenID = "111111";
	
	private String getActivityDetailURL(String reportID) {
		return String.format("/profile/activities/%s/detail/%s", reportID ,fakeAccessTokenID);
	}

	@Test
	public void getActivityDetailSuccess() throws Exception {
		when(
				this.activityServiceMock.getActivityDetail(1L,fakeAccessTokenID)
		).thenReturn(new ActivityDetail());
		
		this.verifySuccess(this.doGET(getActivityDetailURL("1")));
	}
	
	@Test
	public void getActivityDetailFail() throws Exception {
		when(this.activityServiceMock.getActivityDetail(2L,fakeAccessTokenID))
				.thenThrow(new ServiceInventoryException(400,"","",""));
		this.verifyFailed(this.doGET(getActivityDetailURL("2")));
	}
	
	@Test
	public void test(){
		ProductResponse resp = controller.getActivityDetails("11111", "1111111");
		Map<String, Object> data = resp.getData();
		assertEquals(1, data.size());
		assertNotNull(data.get("header"));
		//assertEquals( , actual)
	}
	
}
