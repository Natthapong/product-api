package th.co.truemoney.product.api.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import th.co.truemoney.product.api.domain.ActivityViewItem;
import th.co.truemoney.product.api.domain.ProductResponse;
import th.co.truemoney.product.api.manager.BillConfigurationManager;
import th.co.truemoney.product.api.manager.MessageManager;
import th.co.truemoney.product.api.manager.OnlineResourceManager;
import th.co.truemoney.product.api.transaction.ServiceChannel;
import th.co.truemoney.product.api.util.ProductResponseFactory;
import th.co.truemoney.serviceinventory.ewallet.ActivityService;
import th.co.truemoney.serviceinventory.ewallet.domain.Activity;

@RunWith(Parameterized.class)
public class TestWalletActivityControllerUnit {

    ActivityService activityServiceMock;

    WalletActivityController controller;

    ProductResponseFactory responseFactory;
    static final Long CHANNEL_KIOS = Long.valueOf(ServiceChannel.CHANNEL_KIOSK.getId());
    static final Long CHANNEL_CPF = Long.valueOf(ServiceChannel.CHANNEL_CPF.getId());
    static final Long CHANNEL_TMX = Long.valueOf(ServiceChannel.CHANNEL_TMX.getId());
    static final Long CHANNEL_TRM = Long.valueOf(ServiceChannel.CHANNEL_TRM.getId());
    
    @Parameters
    public static Iterable<Object[]> data() throws ParseException {
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        return Arrays.asList(new Object[][] {
                { new Activity(1L, "topup_mobile", df.parse("10-03-2013"), "tmhtopup_c", "0812345678", 40L), new ActivityViewItem(String.valueOf(1L), "เติมเงินมือถือ", "10/03/13", "ทรูมูฟ เอช", "0.00", "081-234-5678") },
                { new Activity(1L, "topup_mobile", df.parse("10-03-2013"), "tmvtopup_c", "0812345678", 40L), new ActivityViewItem(String.valueOf(1L), "เติมเงินมือถือ", "10/03/13",  "ทรูมูฟ", "0.00", "081-234-5678") },
                { new Activity(2L, "add_money", df.parse("10-03-2013"), "debit", "KTB", 42L), new ActivityViewItem(String.valueOf(2L), "เติมเงิน Wallet", "10/03/13", "เอทีเอ็ม", "0.00", "ธนาคารกรุงไทย") },
                { new Activity(2L, "add_money", df.parse("10-03-2013"), "debit", "SCB", 42L), new ActivityViewItem(String.valueOf(2L), "เติมเงิน Wallet", "10/03/13", "เอทีเอ็ม", "0.00", "ธนาคารไทยพาณิชย์") },
                { new Activity(2L, "add_money", df.parse("10-03-2013"), "debit", "BBL", 42L), new ActivityViewItem(String.valueOf(2L), "เติมเงิน Wallet", "10/03/13", "เอทีเอ็ม", "0.00", "ธนาคารกรุงเทพ") },
                { new Activity(2L, "add_money", df.parse("10-03-2013"), "debit", "BAY", 42L), new ActivityViewItem(String.valueOf(2L), "เติมเงิน Wallet", "10/03/13", "เอทีเอ็ม", "0.00", "ธนาคารกรุงศรีอยุธยา") },
                { new Activity(2L, "add_money", df.parse("10-03-2013"), "tmcc", "12345", 42L), new ActivityViewItem(String.valueOf(2L), "เติมเงิน Wallet", "10/03/13", "บัตรเงินสดทรูมันนี่", "0.00", "") },
                { new Activity(2L, "add_money", df.parse("10-03-2013"), "cash", "BAY", CHANNEL_KIOS), new ActivityViewItem(String.valueOf(2L), "เติมเงิน Wallet", "10/03/13", "ตู้ทรูมันนี่", "0.00", "") },
                { new Activity(2L, "add_money", df.parse("10-03-2013"), "cash", "", CHANNEL_CPF), new ActivityViewItem(String.valueOf(2L), "เติมเงิน Wallet", "10/03/13", "ซีพี เฟรชมาร์ท", "0.00", "") },
                { new Activity(2L, "add_money", df.parse("10-03-2013"), "cash", "", CHANNEL_TMX), new ActivityViewItem(String.valueOf(2L), "เติมเงิน Wallet", "10/03/13", "จุดบริการทรูมันนี่", "0.00", "") },
                { new Activity(2L, "add_money", df.parse("10-03-2013"), "cash", "", CHANNEL_TRM), new ActivityViewItem(String.valueOf(2L), "เติมเงิน Wallet", "10/03/13", "ทรูช้อป", "0.00", "") },
                { new Activity(2L, "transfer", df.parse("10-03-2013"), "debtor", "0812345678", 40L), new ActivityViewItem(String.valueOf(2L), "โอนเงิน", "10/03/13", "ส่งเงิน", "0.00", "081-234-5678") },
                { new Activity(2L, "transfer", df.parse("10-03-2013"), "creditor", "0812345678", 40L), new ActivityViewItem(String.valueOf(2L), "โอนเงิน", "10/03/13", "รับเงิน", "0.00", "081-234-5678") },
                { new Activity(2L, "bonus", df.parse("10-03-2013"), "promo_direct_debit", "add_money", 40L), new ActivityViewItem(String.valueOf(2L), "โปรโมชั่น", "10/03/13", "คืนค่าธรรมเนียม", "0.00", "เติมเงินด้วยบัญชีธนาคาร") },
                { new Activity(3L, "billpay", df.parse("10-03-2013"), "catv_c", "111111111", 40L), new ActivityViewItem(String.valueOf(3L), "จ่ายบิล", "10/03/13", "ทรูวิชั่นส์", "0.00", "111111111") },
                { new Activity(3L, "billpay", df.parse("10-03-2013"), "dstv_c", "111111111", 40L), new ActivityViewItem(String.valueOf(3L), "จ่ายบิล", "10/03/13", "ทรูวิชั่นส์", "0.00", "111111111") },
                { new Activity(3L, "billpay", df.parse("10-03-2013"), "tr_c", "111111111", 40L), new ActivityViewItem(String.valueOf(3L), "จ่ายบิล", "10/03/13", "โทรศัพท์บ้านทรู", "0.00", "111111111") },
                { new Activity(3L, "billpay", df.parse("10-03-2013"), "tmvh_c", "111111111", 40L), new ActivityViewItem(String.valueOf(3L), "จ่ายบิล", "10/03/13", "ทรูมูฟ เอช", "0.00", "111111111") },
                { new Activity(3L, "billpay", df.parse("10-03-2013"), "trmv_c", "111111111", 40L), new ActivityViewItem(String.valueOf(3L), "จ่ายบิล", "10/03/13", "ทรูมูฟ", "0.00", "111111111") },
                { new Activity(3L, "billpay", df.parse("10-03-2013"), "ti_c", "111111111", 40L), new ActivityViewItem(String.valueOf(3L), "จ่ายบิล", "10/03/13", "ทรูออนไลน์", "0.00", "111111111") },
                { new Activity(3L, "billpay", df.parse("10-03-2013"), "tic_c", "111111111", 40L), new ActivityViewItem(String.valueOf(3L), "จ่ายบิล", "10/03/13", "ทรู 006", "0.00", "111111111") },
                { new Activity(3L, "billpay", df.parse("10-03-2013"), "tlp_c", "111111111", 40L), new ActivityViewItem(String.valueOf(3L), "จ่ายบิล", "10/03/13", "ทรูไลฟ์ พลัส", "0.00", "111111111") },
                { new Activity(3L, "billpay", df.parse("10-03-2013"), "tcg_c", "111111111", 40L), new ActivityViewItem(String.valueOf(3L), "จ่ายบิล", "10/03/13", "บิลกลุ่มทรู", "0.00", "111111111") },
                { new Activity(4L, "billpay", df.parse("10-03-2013"), "bblc_c", "222222222", 40L), new ActivityViewItem(String.valueOf(4L), "จ่ายบิล", "10/03/13", "บัตรเครดิตธนาคารกรุงเทพ", "0.00", "222222222") },
                { new Activity(4L, "billpay", df.parse("10-03-2013"), "tisco_c", "222222222", 40L), new ActivityViewItem(String.valueOf(4L), "จ่ายบิล", "10/03/13", "ธนาคารทิสโก้", "0.00", "222222222") },
                { new Activity(4L, "billpay", df.parse("10-03-2013"), "ghb_c", "222222222", 40L), new ActivityViewItem(String.valueOf(4L), "จ่ายบิล", "10/03/13", "ธนาคารอาคารสงเคราะห์", "0.00", "222222222") },
                { new Activity(4L, "billpay", df.parse("10-03-2013"), "ktc_c", "222222222", 40L), new ActivityViewItem(String.valueOf(4L), "จ่ายบิล", "10/03/13", "KTC", "0.00", "222222222") },
                { new Activity(4L, "billpay", df.parse("10-03-2013"), "aeon_c", "222222222", 40L), new ActivityViewItem(String.valueOf(4L), "จ่ายบิล", "10/03/13", "อิออน", "0.00", "222222222") },
                { new Activity(4L, "billpay", df.parse("10-03-2013"), "kcc_c", "222222222", 40L), new ActivityViewItem(String.valueOf(4L), "จ่ายบิล", "10/03/13", "บัตรเครดิตกรุงศรี", "0.00", "222222222") },
                { new Activity(4L, "billpay", df.parse("10-03-2013"), "pb_c", "222222222", 40L), new ActivityViewItem(String.valueOf(4L), "จ่ายบิล", "10/03/13", "เพาเวอร์บาย คาร์ด", "0.00", "222222222") },
                { new Activity(4L, "billpay", df.parse("10-03-2013"), "mistine_c", "222222222", 40L), new ActivityViewItem(String.valueOf(4L), "จ่ายบิล", "10/03/13", "มิสทีน", "0.00", "222222222") },
                { new Activity(4L, "billpay", df.parse("10-03-2013"), "bwc_c", "222222222", 40L), new ActivityViewItem(String.valueOf(4L), "จ่ายบิล", "10/03/13", "เอ็ม พาวเวอร์", "0.00", "222222222") },
                { new Activity(4L, "billpay", df.parse("10-03-2013"), "uob_c", "222222222", 40L), new ActivityViewItem(String.valueOf(4L), "จ่ายบิล", "10/03/13", "บัตรเครดิตธนาคารยูโอบี", "0.00", "222222222") },
                { new Activity(4L, "billpay", df.parse("10-03-2013"), "fc_c", "222222222", 40L), new ActivityViewItem(String.valueOf(4L), "จ่ายบิล", "10/03/13", "กรุงศรี เฟิร์สชอยส์", "0.00", "222222222") },
        });
    }

    private Activity fInput;

    private ActivityViewItem fOutput;

    public TestWalletActivityControllerUnit(Activity input, ActivityViewItem output) {
        this.fInput = input;
        this.fOutput = output;
    }

    @Before
    public void setup() {
        this.activityServiceMock = mock(ActivityService.class);
        this.responseFactory = new ProductResponseFactory();
        this.responseFactory.setMessageManager(mock(MessageManager.class));
        this.controller = new WalletActivityController();
        this.controller.setActivityService(this.activityServiceMock);
        this.controller.setResponseFactory(responseFactory);
        this.controller.setOnlineResourceManager(new OnlineResourceManager());
        this.controller.setBillConfigurationManager(new BillConfigurationManager());
    }

    public void tearDown() {
        reset(this.activityServiceMock);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void getActivities() throws Exception {
        String accessToken = "_whatever_";

        List<Activity> list = new ArrayList<Activity>();
        list.add(this.fInput);

        when(this.activityServiceMock.getActivities(accessToken)).thenReturn(list);

        ProductResponse resp = this.controller.getActivityList(accessToken);

        Map<String, Object> data = resp.getData();
        List<ActivityViewItem> viewItemList = (List<ActivityViewItem>) data.get("activities");
        assertTrue(viewItemList.size() > 0);

        ActivityViewItem viewItem = viewItemList.get(0);

        assertEquals(fOutput.getReportID(), viewItem.getReportID());
        assertEquals(fOutput.getText1Th(), viewItem.getText1Th());
        assertEquals(fOutput.getText2Th(), viewItem.getText2Th());
        assertEquals(fOutput.getText3Th(), viewItem.getText3Th());
        assertEquals(fOutput.getText4Th(), viewItem.getText4Th());
        assertEquals(fOutput.getText5Th(), viewItem.getText5Th());
    }
}
