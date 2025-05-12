const express = require("express");
const crypto = require("crypto");
const querystring = require("querystring");
const moment = require("moment");
const config = require("../../configs/vnpay");

const router = express.Router();

// API tạo URL thanh toán
router.post("/create_payment_url", (req, res) => {
  let { amount, orderId } = req.body;

  let vnp_Params = {
    vnp_Version: "2.1.0",
    vnp_Command: "pay",
    vnp_TmnCode: config.vnp_TmnCode,
    vnp_Amount: Math.round(amount * 100), //  Nhân 100 để đảm bảo số nguyên
    vnp_CurrCode: "VND",
    vnp_TxnRef: orderId,
    vnp_OrderInfo: `Thanh toán đơn hàng #${orderId}`,
    vnp_OrderType: "billpayment",
    vnp_Locale: "vn",
    vnp_ReturnUrl: config.vnp_ReturnUrl,
    vnp_IpAddr: req.ip,
    vnp_CreateDate: moment().format("YYYYMMDDHHmmss"),
  };

  //  Sắp xếp tham số theo thứ tự alphabet
  vnp_Params = Object.fromEntries(Object.entries(vnp_Params).sort());

  //  Tạo chữ ký SHA512
  let signData = querystring.stringify(vnp_Params, { encode: false });
  let hmac = crypto.createHmac("sha512", config.vnp_HashSecret);
  let signed = hmac.update(signData).digest("hex");
  vnp_Params["vnp_SecureHash"] = signed; 
  let paymentUrl = `${config.vnp_Url}?${querystring.stringify(vnp_Params)}`;
  res.json({ status: "success", paymentUrl });
});

//  API xử lý callback từ VNPAY
router.get("/callback", (req, res) => {
  let vnp_Params = req.query;
  let secureHash = vnp_Params["vnp_SecureHash"];
  delete vnp_Params["vnp_SecureHash"];

  //Sắp xếp tham số theo thứ tự alphabet
  vnp_Params = Object.fromEntries(Object.entries(vnp_Params).sort());

  // Tạo lại chữ ký để kiểm tra tính hợp lệ
  let signData = querystring.stringify(vnp_Params, { encode: false });
  let hmac = crypto.createHmac("sha512", config.vnp_HashSecret);
  let signed = hmac.update(signData).digest("hex");

  if (secureHash === signed) {
    if (vnp_Params["vnp_ResponseCode"] === "00") {
      res.json({ message: "Thanh toán thành công", data: vnp_Params });
    } else {
      res.json({ message: "Thanh toán thất bại", data: vnp_Params });
    }
  } else {
    res.status(400).json({ message: "Chữ ký không hợp lệ!" });
  }
});

module.exports = router;
