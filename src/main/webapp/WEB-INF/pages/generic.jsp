<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

{
"p2p.amount.min":"10",

"p2p.amount.max":"10000",

"instruction.addmoney.atm" : "${apiHost}/m/tmn_webview/atm/index.html",

"instruction.addmoney.direct.debit" : "${apiHost}/m/tmn_webview/direct_debit/index.html",

"instruction.addmoney.kiosk" : "${apiHost}/m/tmn_webview/kiosk/kiosk.html",

"instruction.addmoney.trm" : "${apiHost}/m/tmn_webview/trm/trueshop.html",

"instruction.addmoney.tmx" : "${apiHost}/m/tmn_webview/tmx/cp_freshmart.html",

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

"register.cofirm.url" : "${apiHost}/api/v1/ewallet/profiles/verify-otp?"
}