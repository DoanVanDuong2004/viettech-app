"use strict";
const { readVietnameseNumber } = require("../auth/middlewares/vnNumber");
const logModel = require("../models/log.model");
const BillService = require("../services/bill.service");
const ExcelJS = require("exceljs");
const ejs = require('ejs');
const path = require('path');
const puppeteer = require('puppeteer');
const fs = require('fs');
const { VNPay } = require('vnpay');
const { billRepo } = require('../models/bill.model');
const PendingPayment = require('../models/pendingPayment.model');
const { discountRepo } = require("../models/disscount.model");
const { cart } = require('../models/cart.model');


const { sendPushNotification } = require("../helpers/onesignal.helper");
const accountModel = require("../models/account.model");
const notificationModel = require("../models/notification.model");


const logoPath = path.join(__dirname, '../../uploads/logo_viettech.png'); // đường dẫn thật
const logoData = fs.readFileSync(logoPath);
const logoBase64 = `data:image/png;base64,${logoData.toString('base64')}`;

function getVietnameseStatus(statusCode) {
  const map = {
    active: "Đang xử lý",
    completed: "Hoàn thành",
    cancelled: "Đã hủy",
    pending: "Chờ xác nhận"
  };
  return map[statusCode] || statusCode;
}


class BillController {
  static async getBillById(req, res, next) {
    try {
      const { billId } = req.params;
      const bill = await BillService.getBillById({ billId });
      return res.status(200).json(bill);
    } catch (error) {
      next(error);
    }
  }

  static async updateBillStatus(req, res, next) {
    try {
      const { billId } = req.params;
      const { status } = req.body;
      console.log("check req", req.user);

      const userId = req.user.userId || req.user._id;
      console.log("check userId: ", userId);

      const oldBill = await BillService.getBillById({ billId });

      console.log("check old bill", oldBill);

      const updatedBill = await BillService.updateBillStatus({
        billId,
        status,
      });

      await logModel.create({
        target_type: "Bill",
        target_id: billId,
        action: "status_change",
        before: { status: oldBill.status },
        after: { status },
        changed_by: userId,
        note: `Cập nhật trạng thái đơn hàng từ "${oldBill.status}" sang "${status}"`
      });

      const account = await accountModel.findById(userId);
      console.log("check account", account.oneSignalId);
      if (account?.oneSignalId) {
        await sendPushNotification({
          titleUser: "Đơn hàng của bạn đã cập nhật",
          messageUser: `Đơn hàng #${oldBill.order_code} đã được chuyển sang trạng thái "${getVietnameseStatus(status)}"`,
          titleAdmin: "📥 Có đơn hàng mới được cập nhật",
          messageAdmin: `Đơn hàng #${oldBill.order_code} của người dùng vừa được cập nhật sang "${getVietnameseStatus(status)}"`,
          url: "/v1/api/admin/bills",
          userId: oldBill.user_id,
          targets: "both",
          data: { billId, status },
          type: "order"
        });


        // await notificationModel.create({
        //   receiverId: userId, // ai sẽ nhìn thấy thông báo
        //   senderId: req.user?._id, // người thao tác cập nhật
        //   title: "Đơn hàng đã cập nhật!",
        //   message: `Đơn hàng #${oldBill.order_code} đã chuyển sang trạng thái "${status}"`,
        //   url: `/v1/api/admin/bills`, // cho web chuyển hướng
        //   type: "order",
        //   data: { billId: billId, status: status } // để app mobile có thể deep-link nếu cần
        // });

      }


      return res.status(200).json({
        message: "Bill status updated successfully",
        statusCode: 200,
        metadata: updatedBill,
      });
    } catch (error) {
      next(error);
    }
  }

  static async getBillLogs(req, res, next) {
    try {
      const { billId } = req.params;

      const logs = await logModel.find({
        target_type: "Bill",
        target_id: billId
      }).populate("changed_by", "name email")
        .sort({ created_at: -1 });

      return res.status(200).json({
        message: "Fetched bill logs successfully",
        statusCode: 200,
        metadata: logs
      });
    } catch (error) {
      next(error);
    }
  }


  static async getTotalRevenue(req, res) {
    try {
      const { startDate, endDate } = req.query;
      const result = await BillService.getTotalRevenue({ startDate, endDate });
      return res.status(200).json(result);
    } catch (error) {
      return res.status(500).json({ message: "Internal Server Error", error });
    }
  }

  static async getAllBills(req, res, next) {
    try {
      const bills = await BillService.getAllBills();
      return res.status(200).json({
        message: "Fetch all bills successfully",
        statusCode: 200,
        metadata: bills,
      });
    } catch (error) {
      next(error);
    }
  }

  static async getAllBills_Admin({ search, phone, status, payment_method, start_date, end_date, page = 1, limit = 5 }) {
    const filter = {};

    if (Array.isArray(search)) {
      search = search.find(v => typeof v === 'string' && v.trim() !== '') || '';
    } else {
      search = String(search || '');
    }

    if (search) {
      const numericSearch = search.replace(/[^0-9]/g, "");
      if (!isNaN(numericSearch) && numericSearch !== "") {
        filter.order_code = Number(numericSearch);
      } else {
        filter.receiver_name = { $regex: search, $options: "i" };
      }
    }

    if (phone) {
      filter.phone_number = { $regex: String(phone), $options: "i" };
    }

    if (status) {
      filter.status = status;
    }

    if (payment_method) {
      filter.payment_method = payment_method;
    }

    if (start_date || end_date) {
      filter.createdAt = {};
      if (start_date) filter.createdAt.$gte = new Date(start_date);
      if (end_date) filter.createdAt.$lte = new Date(end_date);
    }

    const skip = (parseInt(page) - 1) * parseInt(limit);
    const result = await BillService.getAllBillForAdmin(filter, skip, parseInt(limit));
    return result;
  }



  static async exportBillsToExcel(req, res, next) {
    try {
      // Lấy danh sách hóa đơn từ service
      const bills = await BillService.getAllBills();

      // Tạo workbook và worksheet
      const workbook = new ExcelJS.Workbook();
      const worksheet = workbook.addWorksheet("Bills");

      // Đặt tiêu đề cho các cột
      worksheet.columns = [
        { header: "Mã Đơn Hàng", key: "order_code", width: 15 },
        { header: "Tên Khách Hàng", key: "receiver_name", width: 30 },
        { header: "Địa Chỉ", key: "address", width: 30 },
        { header: "Số Điện Thoại", key: "phone_number", width: 20 },
        {
          header: "Tổng Tiền",
          key: "total",
          width: 20,
          style: { numFmt: "#,##0" },
        },
        {
          header: "Phí Vận Chuyển",
          key: "shipping_fee",
          width: 20,
          style: { numFmt: "#,##0" },
        },
        { header: "Phương Thức Thanh Toán", key: "payment_method", width: 20 },
        { header: "Trạng Thái", key: "status", width: 15 },
        {
          header: "Ngày Tạo",
          key: "createdAt",
          width: 20,
          style: { numFmt: "mm/dd/yyyy" },
        },
        {
          header: "Ngày Cập Nhật",
          key: "updatedAt",
          width: 20,
          style: { numFmt: "mm/dd/yyyy" },
        },
      ];

      // Thêm dữ liệu hóa đơn vào worksheet
      bills.forEach((bill) => {
        worksheet.addRow({
          order_code: bill.order_code,
          receiver_name: bill.receiver_name,
          address: bill.address,
          phone_number: bill.phone_number,
          total: bill.total,
          shipping_fee: bill.shipping_fee,
          payment_method: bill.payment_method,
          status: bill.status,
          createdAt: new Date(bill.createdAt).toLocaleDateString("en-US"),
          updatedAt: new Date(bill.updatedAt).toLocaleDateString("en-US"),
        });
      });

      // Thiết lập header để trình duyệt tải file Excel

      res.setHeader(
        "Content-Type",
        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
      );
      res.setHeader(
        "Content-Disposition",
        "attachment; filename=all_bills.xlsx"
      );

      // Ghi file ra response
      await workbook.xlsx.write(res);
      res.end();
    } catch (error) {
      next(error);
    }
  }

  static async getBillsByStatus(req, res, next) {
    try {
      const { status } = req.params; // Lấy trạng thái từ URL
      const result = await BillService.getBillsByStatus({ status });
      return res.status(200).json(result);
    } catch (error) {
      next(error);
    }
  }
  static async getTotalRevenue(req, res) {
    try {
      const { startDate, endDate } = req.query;
      const result = await BillService.getTotalRevenue({ startDate, endDate });
      return res.status(200).json(result);
    } catch (error) {
      return res.status(500).json({ message: "Internal Server Error", error });
    }
  }

  static async renderInvoicePage(req, res, next) {
    try {
      const { billId } = req.params;

      // Lấy đơn hàng theo ID
      let bill = await BillService.getBillById({ billId });
      console.log("check bill create", bill);


      if (!bill) {
        return res.status(404).send("Không tìm thấy đơn hàng.");
      }

      // Populate thông tin biến thể của từng sản phẩm trong đơn
      // bill = await bill.populate("products.detailsVariantId").execPopulate?.();

      // (Optional) Nếu muốn populate thêm `variantDetails.variantId`, thì dùng nested populate thủ công

      res.render("admin/invoice", {
        bill,
        logoPath: "/uploads/logo_viettech.png",
        companyName: "CÔNG TY TNHH VietTech",
        issuedDate: new Date(bill.createdAt).toLocaleDateString("vi-VN"),
        totalInWords: readVietnameseNumber(bill.total) + " đồng",
      });
    } catch (error) {
      next(error);
    }
  }




  static async downloadInvoice(req, res, next) {
    try {
      const { id } = req.params;
      const bill = await BillService.getBillById({ billId: id });
      if (!bill) return res.status(404).send('Không tìm thấy đơn hàng');

      const totalInWords = readVietnameseNumber(bill.total);

      const html = await ejs.renderFile(
        path.join(__dirname, '../../views/admin/invoice.ejs'),
        {
          bill,
          logoPath: logoBase64,
          companyName: "CÔNG TY TNHH VietTech",
          issuedDate: new Date(bill.createdAt).toLocaleDateString('vi-VN'),
          totalInWords
        }
      );

      // Ghi ra HTML để debug
      // fs.writeFileSync('test-invoice.html', html);

      const browser = await puppeteer.launch({
        headless: true,
        args: ['--no-sandbox', '--disable-setuid-sandbox'] // ✅ thêm nếu deploy trên Linux
      });
      const page = await browser.newPage();
      await page.setContent(html, { waitUntil: 'networkidle0' });

      const pdfBuffer = await page.pdf({ format: 'A4', printBackground: true });
      await browser.close();

      res.setHeader('Content-Type', 'application/pdf');
      res.setHeader('Content-Disposition', `attachment; filename="invoice-${bill.order_code}.pdf"`);
      res.setHeader('Content-Length', pdfBuffer.length);
      res.end(pdfBuffer);

    } catch (err) {
      console.error('Error generating invoice PDF:', err);
      next(err);
    }
  }

  static async getBillsByUserId(req, res, next) {
    try {
      const { userId } = req.params;
      console.log("userId: ", userId);

      const bills = await BillService.getBillsByUserId({ userId });

      return res.status(200).json({
        message: "Lấy danh sách hóa đơn theo userId thành công",
        statusCode: 200,
        metadata: bills
      });
    } catch (error) {
      next(error);
    }
  }

  // static async handleVnpayReturn(req, res, next) {
  //   try {
  //     const vnpay = new VNPay({
  //       tmnCode: 'HMC4RYL1',
  //       secureSecret: 'GP6FEUU3UDKCOXM1P5OE3AU1AJN5CDP4',
  //       vnpayHost: 'https://sandbox.vnpayment.vn',
  //       testMode: true,
  //       hashAlgorithm: 'SHA512',
  //     });

  //     const isValid = vnpay.verifyReturnUrl(req.query);

  //     if (!isValid) {
  //       return res.status(400).json({
  //         code: 400,
  //         message: 'Checksum không hợp lệ!',
  //         status: 'error',
  //       });
  //     }

  //     const { vnp_ResponseCode, vnp_TxnRef } = req.query;

  //     if (vnp_ResponseCode !== '00') {
  //       return res.redirect(`http://localhost:3056/payment-failure?reason=${vnp_ResponseCode}&orderCode=${vnp_TxnRef}`);
  //     }

  //     const bill = await billRepo.findOne({ order_code: vnp_TxnRef });

  //     if (!bill) {
  //       return res.status(404).json({
  //         code: 404,
  //         message: 'Không tìm thấy đơn hàng!',
  //         status: 'error',
  //       });
  //     }

  //     if (!bill.isPay) {
  //       bill.isPay = true;
  //       await bill.save();

  //     }


  //     return res.redirect(`http://localhost:3056/payment-success?orderCode=${bill.order_code}&receiverName=${encodeURIComponent(bill.receiver_name)}&phoneNumber=${bill.phone_number}&address=${encodeURIComponent(bill.address)}`);
  //   } catch (error) {
  //     console.error('Lỗi xử lý VNPay Return:', error);
  //     return res.status(500).json({
  //       code: 500,
  //       message: 'Lỗi máy chủ khi xử lý thanh toán!',
  //       status: 'error',
  //     });
  //   }
  // }

  static async handleVnpayReturn(req, res, next) {
    try {
      const vnpay = new VNPay({
        tmnCode: 'HMC4RYL1',
        secureSecret: 'GP6FEUU3UDKCOXM1P5OE3AU1AJN5CDP4',
        vnpayHost: 'https://sandbox.vnpayment.vn',
        testMode: true,
        hashAlgorithm: 'SHA512',
      });

      const isValid = vnpay.verifyReturnUrl(req.query);

      if (!isValid) {
        return res.status(400).json({
          code: 400,
          message: 'Checksum không hợp lệ!',
          status: 'error',
        });
      }

      const { vnp_ResponseCode, vnp_TxnRef, isCheckoutNow } = req.query;

      if (vnp_ResponseCode !== '00') {
        return res.redirect(`https://www.viettech.store/payment-failure?reason=${vnp_ResponseCode}&orderCode=${vnp_TxnRef}`);
      }

      // 1. Tìm trong PendingPayment
      const pending = await PendingPayment.findOne({ order_code: vnp_TxnRef });

      if (!pending) {
        return res.status(404).json({
          code: 404,
          message: 'Không tìm thấy đơn hàng chờ xử lý!',
          status: 'error',
        });
      }

      // 2. Tạo đơn hàng chính thức
      const bill = await billRepo.create({
        user_id: pending.user_id,
        products: pending.products,
        order_code: pending.order_code,
        address: pending.address,
        total: pending.total,
        shipping_fee: pending.shipping_fee,
        phone_number: pending.phone_number,
        receiver_name: pending.receiver_name,
        status: 'pending',
        payment_method: 'vnpay',
        isPay: true,
        discount_code: pending.discount_code,
        discount_amount: pending.discount_amount,
      });

      // 3. Nếu có discount code, tăng lượt sử dụng
      if (pending.discount_code) {
        await discountRepo.updateOne(
          { code: pending.discount_code },
          { $inc: { usageCount: 1 } }
        );
      }

      // 4. Xoá khỏi PendingPayment
      await pending.deleteOne();

      // 5. Gửi thông báo (nếu cần)
      // await sendPushNotification({
      //   titleAdmin: "🧾 Đơn hàng VNPay mới!",
      //   messageAdmin: `Đơn hàng #${pending.order_code} đã thanh toán thành công.`,
      //   url: "/v1/api/admin/bills",
      //   userId: pending.user_id.toString(),
      //   targets: "admin",
      //   data: { orderCode: pending.order_code }
      // });

      const userCart = await cart.findOne({
        cart_userId: pending.user_id,
        cart_state: "active",
      });

      console.log("🛒 Cart products:", JSON.stringify(userCart.cart_products, null, 2));
      console.log("💸 Pending products:", JSON.stringify(pending.products, null, 2));


      if (!isCheckoutNow) {
        if (userCart && userCart.cart_products) {
          const newCartProducts = userCart.cart_products.filter((itemInCart) => {
            return !pending.products.some((p) => {
              const sameProductId = p.productId.toString() === itemInCart.productId.toString();

              const bothNoVariant = !p.detailsVariantId && !itemInCart.detailsVariantId;
              const bothHaveSameVariant =
                p.detailsVariantId &&
                itemInCart.detailsVariantId &&
                p.detailsVariantId.toString() === itemInCart.detailsVariantId.toString();

              return sameProductId && (bothNoVariant || bothHaveSameVariant);
            });
          });
          console.log("🧹 New cart products after filter:", JSON.stringify(newCartProducts, null, 2));


          if (newCartProducts.length === 0) {
            console.log("🗑️ Deleting entire cart for user:", pending.user_id.toString());

            await userCart.deleteOne();
          } else {
            console.log("💾 Updating cart for user:", pending.user_id.toString());

            userCart.cart_products = newCartProducts;
            await userCart.save();
          }
        }
      }


      return res.redirect(
        `https://viettech.store/payment-success?billId=${bill._id}&orderCode=${bill.order_code}&receiverName=${encodeURIComponent(bill.receiver_name)}&phoneNumber=${bill.phone_number}&address=${encodeURIComponent(bill.address)}`

      );
    } catch (error) {
      console.error('Lỗi xử lý VNPay Return:', error);
      return res.status(500).json({
        code: 500,
        message: 'Lỗi máy chủ khi xử lý thanh toán!',
        status: 'error',
      });
    }
  }


}

module.exports = BillController;
