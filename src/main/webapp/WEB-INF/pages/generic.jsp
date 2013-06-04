<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page language="java" contentType="application/json; charset=UTF-8" pageEncoding="UTF-8"%>
{
"p2p.amount.min":"10",

"p2p.amount.max":"10000",

"instruction.addmoney.atm" : "${apiHost}/m/tmn_webview/addmoney/instruction/atm/index.html",

"instruction.addmoney.direct.debit" : "${apiHost}/m/tmn_webview/addmoney/instruction/direct_debit/index.html",

"instruction.addmoney.kiosk" : "${apiHost}/m/tmn_webview/addmoney/instruction/kiosk/kiosk.html",

"instruction.addmoney.trm" : "${apiHost}/m/tmn_webview/addmoney/instruction/trm/trueshop.html",

"instruction.addmoney.tmx" : "${apiHost}/m/tmn_webview/addmoney/instruction/tmx/cp_freshmart.html",

"signin.url" : "${apiHost}/api/v1/signin?",

"signout.url" : "${apiHost}/api/v1/signout/%@?",

"ewallet.addmoney.directdebit.soflist.url" : "${apiHost}/api/v1/add-money/ewallet/banks/%@/%@?",

"ewallet.addmoney.directdebit.verify.url" : "${apiHost}/api/v1/directdebit/quote/create/%@?",

"ewallet.addmoney.directdebit.otp.url" : "${apiHost}/api/v1/directdebit/order/create/%@?",

"ewallet.addmoney.directdebit.confirm.url" : "${apiHost}/api/v1/directdebit/order/confirm/%@?",

"ewallet.addmoney.directdebit.status.url" : "${apiHost}/api/v1/directdebit/order/%@/status/%@?",

"ewallet.addmoney.directdebit.confirm.detail.url" : "${apiHost}/api/v1/directdebit/order/%@/details/%@?",

"register.webview.termofservice.url" : "${apiHost}/m/tmn_webview/register/term_of_service.html",

"register.webview.policy.url" : "${apiHost}/m/tmn_webview/register/policy.html",

"register.verify.email.url" : "${apiHost}/api/v1/ewallet/profiles/validate-email?",

"register.prepare.url" : "${apiHost}/api/v1/ewallet/profiles?",

"register.confirm.url" : "${apiHost}/api/v1/ewallet/profiles/verify-otp?",

"ewallet.p2p.transfer.url" : "${apiHost}/api/v1/transfer/draft-transaction/%@?",

"ewallet.p2p.verify.url" : "${apiHost}/api/v1/transfer/draft-transaction/%@/send-otp/%@?",

"ewallet.p2p.confirm.otp.url" : "${apiHost}/api/v1/transfer/transaction/%@/%@?",

"ewallet.p2p.checkstatus.url" : "${apiHost}/api/v1/transfer/transaction/%@/status/%@?",

"ewallet.p2p.transfer.detail.url" : "${apiHost}/api/v1/transfer/transaction/%@/%@?",

"payment.barcode.detail.url" : "${apiHost}/api/v1/bill-payment/barcode/%@/%@?",

"payment.bill.create.url" : "${apiHost}/api/v1/bill-payment/create/%@?",

"payment.bill.confirm.url" : "${apiHost}/api/v1/bill-payment/%@/confirm/%@?",

"payment.bill.status.url" : "${apiHost}/api/v1/bill-payment/%@/status/%@?",

"payment.bill.detail.url" : "${apiHost}/api/v1/bill-payment/%@/details/%@?",

"topup.mobile.verify.url" : "${apiHost}/api/v1/topup/mobile/draft/verifyAndCreate/%@?",

"topup.mobile.send.otp.url" : "${apiHost}/api/v1/topup/mobile/sendotp/%@/%@?",

"topup.mobile.confirm.otp.url" : "${apiHost}/api/v1/topup/mobile/confirm/%@/%@?",

"topup.mobile.status.url" : "${apiHost}/api/v1/topup/mobile/%@/status/%@?",

"topup.mobile.details.url" : "${apiHost}/api/v1/topup/mobile/%@/details/%@?",

"profile.activities.list.url" : "${apiHost}/api/v1/profile/activities/list/%@?",

"profile.activities.detail.url" : "${apiHost}/api/v1/profile/activities/%@/detail/%@?",

"account.profile.url" : "${apiHost}/api/v1/profile/%@?",

"favorite.list.url" : "${apiHost}/api/v1/favorite/%@?",

"favorite.billpay.add.url" : "${apiHost}/api/v1/favorite/add/%@?",

"favorite.billpay.remove.url" : "${apiHost}/api/v1/favorite/remove/%@?",

"payment.bill.favorite.verify.url" : "${apiHost}/api/v1/bill-payment/favorite/verify/%@?",

"payment.bill.keyin.list.url" : "${apiHost}/api/v1/bill-payment/key-in/list?",

"payment.bill.keyin.list.presscon.url" : "${apiHost}/api/v1/bill-payment/key-in/list/press-con?",

"payment.bill.keyin.info.url" : "${apiHost}/api/v1/bill-payment/info/%@/%@?",

"payment.bill.keyin.input.url" : "${apiHost}/api/v1/bill-payment/key-in/%@?",

"payment.bill.inquiry.info.url" : "${apiHost}/api/v1/bill-payment/inquiry/%@/%@?",

"canRegister" : "true"
}