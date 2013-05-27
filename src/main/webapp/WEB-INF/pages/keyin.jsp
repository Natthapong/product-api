<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page language="java" contentType="application/json; charset=UTF-8" pageEncoding="UTF-8"%>
{
    "category": [
        {
            "categoryID": 1,
            "categoryLogo": "${apiHost}/m/tmn_webview/images/logo_bill_type/true.png",
            "categoryTitleTh": "บิลทรู",
            "categoryTitleEn": "True Bill",
            "items": [
                {
                    "serviceCode": "tmvh",
                    "titleTh": "TrueMove H",
                    "titleEn": "TrueMove H",
                    "logo": "${apiHost}/m/tmn_webview/images/logo_bill/tmvh@2x.png",
                    "enabled": true
                },
                {
                    "serviceCode": "trmv",
                    "titleTh": "TrueMove",
                    "titleEn": "TrueMove",
                    "logo": "${apiHost}/m/tmn_webview/images/logo_bill/trmv@2x.png",
                    "enabled": true
                },
                {
                    "serviceCode": "tr",
                    "titleTh": "True Fixed Line",
                    "titleEn": "True Fixed Line",
                    "logo": "${apiHost}/m/tmn_webview/images/logo_bill/tr@2x.png",
                    "enabled": true
                },
                {
                    "serviceCode": "ti",
                    "titleTh": "True Online",
                    "titleEn": "True Online",
                    "logo": "${apiHost}/m/tmn_webview/images/logo_bill/ti@2x.png",
                    "enabled": true
                },
                {
                    "serviceCode": "tlp",
                    "titleTh": "TrueLife Plus",
                    "titleEn": "TrueLife Plus",
                    "logo": "${apiHost}/m/tmn_webview/images/logo_bill/tlp@2x.png",
                    "enabled": true
                },
                {
                    "serviceCode": "dstv",
                    "titleTh": "TrueVisions - Satellite",
                    "titleEn": "TrueVisions - Satellite",
                    "logo": "${apiHost}/m/tmn_webview/images/logo_bill/dstv@2x.png",
                    "enabled": false
                },
                {
                    "serviceCode": "catv",
                    "titleTh": "TrueVisions - Cable",
                    "titleEn": "TrueVisions - Cable",
                    "logo": "${apiHost}/m/tmn_webview/images/logo_bill/catv@2x.png",
                    "enabled": false
                },
                {
                    "serviceCode": "tic",
                    "titleTh": "True 006",
                    "titleEn": "True 006",
                    "logo": "${apiHost}/m/tmn_webview/images/logo_bill/tic@2x.png",
                    "enabled": true
                }
            ]
        },
        {
            "categoryID": 2,
            "categoryLogo": "${apiHost}/m/tmn_webview/images/logo_bill_type/utility.png",
            "categoryTitleTh": "สาธารณูปโภค",
            "categoryTitleEn": "Utility",
            "items": []
        },
        {
            "categoryID": 3,
            "categoryLogo": "${apiHost}/m/tmn_webview/images/logo_bill_type/credit.png",
            "categoryTitleTh": "บัตรเครดิต/สินเชื่อส่วนบุคคล",
            "categoryTitleEn": "Credit Card/Personal Loan",
            "items": [
                {
                    "serviceCode": "BBL",
                    "titleTh": "บัตรเครดิตธนาคารกรุงเทพ",
                    "titleEn": "บัตรเครดิตธนาคารกรุงเทพ",
                    "logo": "${apiHost}/m/tmn_webview/images/logo_credit_card/bblc@2x.png",
                    "enabled": false
                },
                {
                    "serviceCode": "TISCO",
                    "titleTh": "ธนาคารทิสโก้",
                    "titleEn": "ธนาคารทิสโก้",
                    "logo": "${apiHost}/m/tmn_webview/images/logo_credit_card/tisco@2x.png",
                    "enabled": false
                },
                {
                    "serviceCode": "CIMB",
                    "titleTh": "ธนาคารซีไอเอ็มบี ไทย",
                    "titleEn": "ธนาคารซีไอเอ็มบี ไทย",
                    "logo": "${apiHost}/m/tmn_webview/images/logo_credit_card/cimb@2x.png",
                    "enabled": false
                },
                {
                    "serviceCode": "UOB",
                    "titleTh": "บัตรเครดิตธนาคารยูโอบี",
                    "titleEn": "บัตรเครดิตธนาคารยูโอบี",
                    "logo": "${apiHost}/m/tmn_webview/images/logo_credit_card/uob@2x.png",
                    "enabled": false
                },
                {
                    "serviceCode": "GHB",
                    "titleTh": "ธนาคารอาคารสงเคราะห์",
                    "titleEn": "ธนาคารอาคารสงเคราะห์",
                    "logo": "${apiHost}/m/tmn_webview/images/logo_credit_card/ghb@2x.png",
                    "enabled": false
                },
                {
                    "serviceCode": "AEON",
                    "titleTh": "บัตรอิออน",
                    "titleEn": "บัตรอิออน",
                    "logo": "${apiHost}/m/tmn_webview/images/logo_credit_card/aeon@2x.png",
                    "enabled": false
                },
                {
                    "serviceCode": "KTC",
                    "titleTh": "บัตรเครดิต KTC",
                    "titleEn": "บัตรเครดิต KTC",
                    "logo": "${apiHost}/m/tmn_webview/images/logo_credit_card/ktc@2x.png",
                    "enabled": false
                },
                {
                    "serviceCode": "KCC",
                    "titleTh": "บัตรเครดิตกรุงศรี จีอี",
                    "titleEn": "บัตรเครดิตกรุงศรี จีอี",
                    "logo": "${apiHost}/m/tmn_webview/images/logo_credit_card/kcc@2x.png",
                    "enabled": false
                },
                {
                    "serviceCode": "CENTRAL",
                    "titleTh": "เซ็นทรัล เครดิตคาร์ด",
                    "titleEn": "เซ็นทรัล เครดิตคาร์ด",
                    "logo": "${apiHost}/m/tmn_webview/images/logo_credit_card/central@2x.png",
                    "enabled": false
                },
                {
                    "serviceCode": "TESCO",
                    "titleTh": "บัตรเครดิตเทสโก้ โลตัส",
                    "titleEn": "บัตรเครดิตเทสโก้ โลตัส",
                    "logo": "${apiHost}/m/tmn_webview/images/logo_credit_card/tesco@2x.png",
                    "enabled": false
                },
                {
                    "serviceCode": "PW",
                    "titleTh": "เพาวเวอร์บาย คาร์ด",
                    "titleEn": "เพาวเวอร์บาย คาร์ด",
                    "logo": "",
                    "enabled": false
                },
                {
                    "serviceCode": "ROBINSON",
                    "titleTh": "ซิมเพิล วีซ่า คาร์ด",
                    "titleEn": "ซิมเพิล วีซ่า คาร์ด",
                    "logo": "${apiHost}/m/tmn_webview/images/logo_credit_card/robinson@2x.png",
                    "enabled": false
                },
                {
                    "serviceCode": "HSBC",
                    "titleTh": "บัตรเครดิต HSBC",
                    "titleEn": "บัตรเครดิต HSBC",
                    "logo": "",
                    "enabled": false
                },
                {
                    "serviceCode": "HP",
                    "titleTh": "บัตรเครดิตโฮมโปร",
                    "titleEn": "บัตรเครดิตโฮมโปร",
                    "logo": "${apiHost}/m/tmn_webview/images/logo_credit_card/hp@2x.png",
                    "enabled": false
                },
                {
                    "serviceCode": "QUICK",
                    "titleTh": "สินเชื่อเงินสดควิกแคช",
                    "titleEn": "สินเชื่อเงินสดควิกแคช",
                    "logo": "${apiHost}/m/tmn_webview/images/logo_credit_card/quick@2x.png",
                    "enabled": false
                },
                {
                    "serviceCode": "GEPC",
                    "titleTh": "สินเชื่อเงินสดจีอี เพอร์ซัลนอลโลน",
                    "titleEn": "สินเชื่อเงินสดจีอี เพอร์ซัลนอลโลน",
                    "logo": "${apiHost}/m/tmn_webview/images/logo_credit_card/gepc@2x.png",
                    "enabled": false
                },
                {
                    "serviceCode": "CENTRALPL",
                    "titleTh": "สินเชื่อเซ็นทรัล เอ็กซ์คลูซีฟ แคช",
                    "titleEn": "สินเชื่อเซ็นทรัล เอ็กซ์คลูซีฟ แคช",
                    "logo": "${apiHost}/m/tmn_webview/images/logo_credit_card/centralpl@2x.png",
                    "enabled": false
                },
                {
                    "serviceCode": "TESCOPL",
                    "titleTh": "สินเชื่อเงินสดเทสโก้ เพอร์ซัลนอลโลน",
                    "titleEn": "สินเชื่อเงินสดเทสโก้ เพอร์ซัลนอลโลน",
                    "logo": "${apiHost}/m/tmn_webview/images/logo_credit_card/tesopl@2x.png",
                    "enabled": false
                },
                {
                    "serviceCode": "DREAM",
                    "titleTh": "กรุงศรี ดรีมโลน",
                    "titleEn": "กรุงศรี ดรีมโลน",
                    "logo": "${apiHost}/m/tmn_webview/images/logo_credit_card/dream@2x.png",
                    "enabled": false
                },
                {
                    "serviceCode": "FC",
                    "titleTh": "กรุงศรี เฟิร์สช้อยส์คาร์ด",
                    "titleEn": "กรุงศรี เฟิร์สช้อยส์คาร์ด",
                    "logo": "${apiHost}/m/tmn_webview/images/logo_credit_card/fc@2x.png",
                    "enabled": false
                },
                {
                    "serviceCode": "EASY",
                    "titleTh": "บัตรอีซี่บาย / ยูเมะ พลัส",
                    "titleEn": "บัตรอีซี่บาย / ยูเมะ พลัส",
                    "logo": "${apiHost}/m/tmn_webview/images/logo_credit_card/easy@2x.png",
                    "enabled": false
                }
            ]
        },
        {
            "categoryID": 4,
            "categoryLogo": "${apiHost}/m/tmn_webview/images/logo_bill_type/leasing.png",
            "categoryTitleTh": "ลีสซิ่ง",
            "categoryTitleEn": "Leasing",
            "items": []
        },
        {
            "categoryID": 5,
            "categoryLogo": "${apiHost}/m/tmn_webview/images/logo_bill_type/insurance.png",
            "categoryTitleTh": "ประกันภัย",
            "categoryTitleEn": "Insurance",
            "items": []
        },
        {
            "categoryID": 6,
            "categoryLogo": "${apiHost}/m/tmn_webview/images/logo_bill_type/others.png",
            "categoryTitleTh": "อื่นๆ",
            "categoryTitleEn": "Others",
            "items": []
        }
    ]
}