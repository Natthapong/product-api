package th.co.truemoney.product.api.domain;

import org.apache.commons.lang.NotImplementedException;

public enum ServiceChannel {
	CHANNEL_TMX(39, "tmx", "True Money Express", "จุดบริการทรูมันนี่"),
	CHANNEL_TRM(44, "trm", "True Shop", "ทรูช้อป"),
	CHANNEL_ATM(42, "atm", "ATM", "เอทีเอ็ม"),
	CHANNEL_KIOSK(33, "ksk", "True Money Kiosk", "ตู้ทรูมันนี่"),
	CHANNEL_MOBILE(40, "mbl", "Bank account", "บัญชีธนาคาร"),
	CHANNEL_IBANKING(43, "ibk", "iBanking", "ไอแบงก์กิ้ง"),
	CHANNEL_CP_FRESHMART(38, "cpf", "CP Fresh Mart", "ซีพี เฟรชมาร์ท");
	
	private Integer id;
	
	private String abbre;
	
	private String nameEn;
	
	private String nameTh;
	
	private ServiceChannel(Integer id, String abbre, String nameEn, String nameTh) {
		this.id = id;
		this.abbre = abbre;
		this.nameEn = nameEn;
		this.nameTh = nameTh;
	}
	
	public Integer getId() {
		return id;
	}

	public String getAbbre() {
		return abbre;
	}

	public String getNameEn() {
		return nameEn;
	}

	public String getNameTh() {
		return nameTh;
	}
	
	public static ServiceChannel getChannel(Integer id) {
		switch (id) {
		case 33:
			return CHANNEL_KIOSK;
		case 38:
			return CHANNEL_CP_FRESHMART;
		case 39:
			return CHANNEL_TMX;
		case 40:
			return CHANNEL_MOBILE;
		case 42:
			return CHANNEL_ATM;
		case 43:
			return CHANNEL_IBANKING;
		case 44:
			return CHANNEL_TRM;
		default:
			throw new NotImplementedException("Channel with id " + id + " not support.");
		}
	}
}
