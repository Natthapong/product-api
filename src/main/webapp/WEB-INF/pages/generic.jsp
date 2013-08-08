<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page language="java" contentType="application/json; charset=UTF-8" pageEncoding="UTF-8"%>
{
"p2p.amount.min":"10",

"p2p.amount.max":"10000",

"instruction.addmoney.atm" : "${mHost}/info/addmoney/instruction/atm/index.html",

"instruction.addmoney.direct.debit" : "${mHost}/info/addmoney/instruction/direct_debit/index.html",

"instruction.addmoney.kiosk" : "${mHost}/info/addmoney/instruction/kiosk/kiosk.html",

"instruction.addmoney.trm" : "${mHost}/info/addmoney/instruction/trm/trueshop.html",

"instruction.addmoney.tmx" : "${mHost}/info/addmoney/instruction/tmx/cp_freshmart.html",

"instruction.addmoney.truemoneyexpress" : "${mHost}/info/addmoney/instruction/tmx/tmx.html",

"instruction.addmoney.cashcard" : "${mHost}/info/addmoney/instruction/cashcard/cashcard.html",

"signin.url" : "${apiHost}/v1/signin?",

"signout.url" : "${apiHost}/v1/signout/%@?",

"ewallet.addmoney.directdebit.soflist.url" : "${apiHost}/v1/add-money/ewallet/banks/%@/%@?",

"ewallet.addmoney.directdebit.verify.url" : "${apiHost}/v1/directdebit/quote/create/%@?",

"ewallet.addmoney.directdebit.otp.url" : "${apiHost}/v1/directdebit/order/create/%@?",

"ewallet.addmoney.directdebit.confirm.url" : "${apiHost}/v1/directdebit/order/confirm/%@?",

"ewallet.addmoney.directdebit.status.url" : "${apiHost}/v1/directdebit/order/%@/status/%@?",

"ewallet.addmoney.directdebit.confirm.detail.url" : "${apiHost}/v1/directdebit/order/%@/details/%@?",

"register.webview.termofservice.url" : "${mHost}/info/register/term_of_service.html",

"register.webview.policy.url" : "${mHost}/info/register/policy.html",

"register.verify.email.url" : "${apiHost}/v1/ewallet/profiles/validate-email?",

"register.prepare.url" : "${apiHost}/v1/ewallet/profiles?",

"register.confirm.url" : "${apiHost}/v1/ewallet/profiles/verify-otp?",

"ewallet.p2p.transfer.url" : "${apiHost}/v1/transfer/draft-transaction/%@?",

"ewallet.p2p.verify.url" : "${apiHost}/v1/transfer/draft-transaction/%@/send-otp/%@?",

"ewallet.p2p.confirm.otp.url" : "${apiHost}/v1/transfer/transaction/%@/%@?",

"ewallet.p2p.checkstatus.url" : "${apiHost}/v1/transfer/transaction/%@/status/%@?",

"ewallet.p2p.transfer.detail.url" : "${apiHost}/v1/transfer/transaction/%@/%@?",

"payment.barcode.detail.url" : "${apiHost}/v1/bill-payment/barcode/%@/%@?",

"payment.bill.create.url" : "${apiHost}/v1/bill-payment/verify/%@?",

"payment.bill.confirm.url" : "${apiHost}/v1/bill-payment/%@/confirm/%@?",

"payment.bill.status.url" : "${apiHost}/v1/bill-payment/%@/status/%@?",

"payment.bill.detail.url" : "${apiHost}/v1/bill-payment/%@/details/%@?",

"topup.mobile.verify.url" : "${apiHost}/v1/topup/mobile/draft/verifyAndCreate/%@?",

"topup.mobile.send.otp.url" : "${apiHost}/v1/topup/mobile/sendotp/%@/%@?",

"topup.mobile.confirm.otp.url" : "${apiHost}/v1/topup/mobile/confirm/%@/%@?",

"topup.mobile.status.url" : "${apiHost}/v1/topup/mobile/%@/status/%@?",

"topup.mobile.details.url" : "${apiHost}/v1/topup/mobile/%@/details/%@?",

"profile.activities.list.url" : "${apiHost}/v1/profile/activities/list/%@?",

"profile.activities.detail.url" : "${apiHost}/v1/profile/activities/%@/detail/%@?",

"account.profile.url" : "${apiHost}/v1/profile/%@?",

"account.profile.image.upload.url" : "${apiHost}/v1/profile/image/%@?",

"favorite.list.url" : "${apiHost}/v1/favorite/%@?",

"favorite.billpay.add.url" : "${apiHost}/v1/favorite/add/%@?",

"favorite.billpay.remove.url" : "${apiHost}/v1/favorite/remove/%@?",

"payment.bill.favorite.verify.url" : "${apiHost}/v1/bill-payment/favorite/verify/%@?",

"payment.bill.keyin.list.url" : "${apiHost}/v1/bill-payment/key-in/list?",

"payment.bill.keyin.list.presscon.url" : "${apiHost}/v1/bill-payment/key-in/list/press-con?",

"payment.bill.keyin.info.url" : "${apiHost}/v1/bill-payment/key-in/place-holder/%@/%@?",

"payment.bill.keyin.input.url" : "${apiHost}/v1/bill-payment/key-in/bill/%@?",

"payment.bill.favorite.info.url" : "${apiHost}/v1/bill-payment/favorite/bill/%@?",

"payment.bill.verify.url" : "${apiHost}/v1/bill-payment/bill/%@/verify/%@?",

"canRegister" : "true",

"forgot.password.active" : "true",

"forgot.pin.active" : "true",

"forgot.password.url" : "${mHost}/forgot-password",

"forgot.pin.url" : "${mHost}/forgot-pin",

"more.bill.messageEn" : "ดูรายการบิลที่รับชําระทั้งหมดEn",

"more.bill.messageTh" : "ดูรายการบิลที่รับชําระทั้งหมด",

"more.bill.url" : "http://truemoney.com/wallet/bill.html",

"profile.change.pin.url" : "${apiHost}/v1//profile/change-pin/%@?",

"profile.change.password.url" : "${apiHost}/v1/profile/change-password/%@?",

"profile.update.url" : "${apiHost}/v1/profile/%@?",

"system.offline.message" : "กำลังปรับปรุงระบบ เพื่อเพิ่มความสามารถในการให้บริการได้ดีขึ้น คุณสามารถใช้งานได้ตามปกติเวลา 05.00 น.",

"system.offline.title" : "ไม่สามารถทำรายการได้",

"system.offline" : "false"

}
