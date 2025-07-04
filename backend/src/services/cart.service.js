"use strict";

const {
  ConflictRequestError,
  NotFoundError,
} = require("../core/error.response");
const { cart } = require("../models/cart.model");
const { billRepo } = require("../models/bill.model");
const Products = require("../models/product.model");
const Account = require("../models/account.model");
const { discountRepo } = require("../models/disscount.model");
const productModel = require("../models/product.model");
const vnpayConfig = require("../configs/vnpay");
const moment = require("moment");
const qs = require("qs");
const crypto = require("crypto");
const detailsVariantModel = require("../models/detailsVariant.model");

const { VNPay, ignoreLogger, ProductCode, VnpLocale, dateFormat } = require('vnpay');
const { log } = require("console");
const { sendPushNotification } = require("../helpers/onesignal.helper");
const PendingPayment = require('../models/pendingPayment.model');



class CartService {
  //Start Repo

  //Tạo mới giỏ hàng hoặc cập nhật giỏ hàng
  static async createUserCart({ userId, product }) {
    try {
      const query = {
        cart_userId: userId,
        cart_state: "active",
      };

      console.log("check pro", product);

      // Tạo cart_product với thông tin đầy đủ
      const productToAdd = {
        productId: product.productId,
        name: product.name,
        price: product.price,
        image: product.image,
        quantity: product.quantity || 1,
        detailsVariantId: product.detailsVariantId,
        stock: product.stock,
      };

      // Chỉ thêm biến thể nếu có
      if (product.variant) {
        productToAdd.variant = product.variant;
      }

      // Tìm giỏ hàng hiện tại
      const existingCart = await cart.findOne(query);

      if (!existingCart) {
        // Nếu chưa có giỏ hàng, tạo mới
        return await cart.create({
          cart_userId: userId,
          cart_state: "active",
          cart_products: [productToAdd],
        });
      }

      // Nếu đã có giỏ hàng, thêm sản phẩm vào
      existingCart.cart_products.push(productToAdd);
      return await existingCart.save(); // Trigger middleware pre-save
    } catch (error) {
      console.error("Error creating user cart:", error);
      throw error;
    }
  }
  //Check sản phẩm có trong giỏ hàng hay chưa
  // Trong phương thức isProductInCart
  static async isProductInCart({ userId, productId, detailsVariantId }) {
    try {
      console.log("Checking if product in cart:", {
        userId,
        productId,
        detailsVariantId,
      });

      // Chuyển đổi kiểu dữ liệu để đảm bảo nhất quán
      const userIdObj = userId.toString();
      const productIdObj = productId.toString();
      const detailsVariantIdObj = detailsVariantId
        ? detailsVariantId.toString()
        : null;

      // Tìm giỏ hàng
      const userCart = await cart.findOne({
        cart_userId: userIdObj,
        cart_state: "active",
      });

      if (
        !userCart ||
        !userCart.cart_products ||
        userCart.cart_products.length === 0
      ) {
        console.log("Cart not found or empty");
        return false;
      }

      // Log thông tin giỏ hàng
      console.log("Cart ID:", userCart._id);
      console.log("Cart products count:", userCart.cart_products.length);

      let foundProduct = false;

      // Lặp qua từng sản phẩm để so sánh ID
      for (const item of userCart.cart_products) {
        try {
          const itemProductId = item.productId.toString();
          const itemVariantId = item.detailsVariantId
            ? item.detailsVariantId.toString()
            : null;

          console.log(`Comparing product ${itemProductId} vs ${productIdObj}`);
          console.log(
            `Comparing variant ${itemVariantId} vs ${detailsVariantIdObj}`
          );

          if (itemProductId === productIdObj) {
            if (detailsVariantIdObj) {
              // Nếu có detailsVariantId, kiểm tra chi tiết biến thể
              if (itemVariantId === detailsVariantIdObj) {
                console.log("Found product with matching variant");
                foundProduct = true;
                break;
              }
            } else {
              // Nếu không có detailsVariantId, tìm sản phẩm không có biến thể
              if (!itemVariantId) {
                console.log("Found product without variant");
                foundProduct = true;
                break;
              }
            }
          }
        } catch (err) {
          console.error("Error comparing cart item:", err);
          // Tiếp tục kiểm tra các mục khác
        }
      }

      console.log("Is product in cart:", foundProduct);
      return foundProduct;
    } catch (error) {
      console.error("Error checking product in cart:", error);
      throw error;
    }
  }

  static async getProductFromCart({ userId, productId, variantId }) {
    try {
      console.log("Getting product from cart:", {
        userId,
        productId,
        variantId,
      });

      const userCart = await cart.findOne({
        cart_userId: userId,
        cart_state: "active",
      });

      if (
        !userCart ||
        !userCart.cart_products ||
        userCart.cart_products.length === 0
      ) {
        console.log("Cart not found or empty");
        return null;
      }

      console.log(
        "Cart items:",
        JSON.stringify(userCart.cart_products, null, 2)
      );

      // Tìm sản phẩm trong giỏ hàng
      let matchingProduct;

      if (variantId) {
        // Tìm sản phẩm với biến thể cụ thể
        console.log(
          `Looking for product ${productId} with variant ${variantId}`
        );
        matchingProduct = userCart.cart_products.find((p) => {
          const productMatch = p.productId.toString() === productId.toString();
          let variantMatch = false;

          // Kiểm tra variant trong cả hai vị trí có thể có
          if (p.variant && p.variant.variantId) {
            variantMatch =
              p.variant.variantId.toString() === variantId.toString();
          } else if (p.variantId) {
            variantMatch = p.variantId.toString() === variantId.toString();
          }

          return productMatch && variantMatch;
        });
      } else {
        // Tìm sản phẩm không có biến thể
        console.log(`Looking for product ${productId} without variant`);
        matchingProduct = userCart.cart_products.find((p) => {
          const productMatch = p.productId.toString() === productId.toString();
          const noVariant =
            (!p.variant || !p.variant.variantId) && !p.variantId;
          return productMatch && noVariant;
        });
      }

      console.log("Matching product:", matchingProduct ? "found" : "not found");
      return matchingProduct || null;
    } catch (error) {
      console.error("Error getting product from cart:", error);
      throw error;
    }
  }
  //Cập nhật số lượng sản phẩm trong giỏ hàng
  static async updateUserCartQuantity({ userId, product }) {
    try {
      const userExists = await Account.exists({ _id: userId });
      if (!userExists) {
        throw new NotFoundError("User not found");
      }

      const { productId, quantity, detailsVariantId } = product;

      // Nếu số lượng <= 0, chuyển sang phương thức xóa hoặc giảm số lượng
      if (quantity <= 0) {
        const isInCart = await CartService.isProductInCart({
          userId,
          productId,
          detailsVariantId,
        });

        if (!isInCart && quantity < 0) {
          throw new NotFoundError(
            "Cannot decrease quantity for product not in cart"
          );
        }

        if (quantity === 0) {
          console.log("Quantity is zero, deleting product");
          return await this.deleteUserCart({
            userId,
            productId,
            variantId: detailsVariantId,
          });
        } else {
          console.log("Quantity is negative, updating quantity");
          return await CartService.updateUserCartQuantity({
            userId,
            product: {
              productId,
              quantity,
              detailsVariantId,
            },
          });
        }
      }

      // Tìm giỏ hàng trước
      const userCart = await cart.findOne({
        cart_userId: userId,
        cart_state: "active",
      });

      if (!userCart) {
        throw new NotFoundError("Cart not found");
      }

      // Kiểm tra sản phẩm có trong giỏ hàng không trước
      const isInCart = await CartService.isProductInCart({
        userId,
        productId,
        detailsVariantId,
      });

      if (isInCart) {
        // Tìm sản phẩm trong giỏ hàng
        let updatedStock = 0;
        if (detailsVariantId) {
          const detailsVariant = await detailsVariantModel.findById(
            detailsVariantId
          );
          if (detailsVariant) {
            updatedStock = detailsVariant.stock;
          }
        } else {
          const existingProduct = await Products.findById(productId);
          if (existingProduct) {
            updatedStock = existingProduct.product_stock;
          }
        }
        const productIndex = userCart.cart_products.findIndex(
          (item) =>
            item.productId.toString() === productId.toString() &&
            (detailsVariantId
              ? item.detailsVariantId?.toString() ===
              detailsVariantId.toString()
              : !item.detailsVariantId)
        );

        if (productIndex === -1) {
          throw new NotFoundError("Product not found in cart");
        }

        // Cập nhật số lượng
        userCart.cart_products[productIndex].quantity += quantity;
        userCart.cart_products[productIndex].stock = updatedStock;
        // Lưu giỏ hàng
        const updatedCart = await userCart.save();
        return updatedCart;
      } else {
        console.log("Product not in cart, adding as new item");

        // Nếu sản phẩm không có trong giỏ hàng, cần kiểm tra sản phẩm có tồn tại không
        try {
          // Sử dụng kết hợp await với try/catch để xử lý lỗi tìm kiếm sản phẩm
          const existingProduct = await Products.findById(productId);

          if (!existingProduct) {
            console.log(`Product with ID ${productId} not found in database`);
            throw new NotFoundError("Product not found in database");
          }

          console.log(`Found product: ${existingProduct.product_name}`);

          // Nếu sản phẩm không có trong giỏ hàng, thêm mới
          return await CartService.addToCart({
            userId,
            product: {
              product: {
                productId,
                detailsVariantId,
                quantity,
              },
            },
          });
        } catch (error) {
          console.error("Error finding product:", error);

          // Kiểm tra nếu lỗi là CastError (invalid ObjectId)
          if (error.name === "CastError") {
            throw new NotFoundError(`Invalid product ID format: ${productId}`);
          }

          throw new NotFoundError(`Error finding product: ${error.message}`);
        }
      }
    } catch (error) {
      console.error("Error updating user cart:", error);
      throw error;
    }
  }

  //End Repo


  static async checkout({
    userId,
    address,
    phone_number,
    receiver_name,
    payment_method,
    discount_code,
    req
  }) {
    const clientIp =
      req.headers['x-forwarded-for']?.toString().split(',')[0] ||
      req.socket?.remoteAddress ||
      '127.0.0.1';
    const currentCart = await cart.findOne({
      cart_userId: userId,
      cart_state: "active",
    });

    if (!currentCart) {
      return {
        code: 400,
        message: "Cart not found",
        status: "error",
      };
    }

    if (!currentCart.cart_products || currentCart.cart_products.length === 0) {
      return {
        code: 400,
        message: "Cart is empty. Cannot proceed to checkout.",
        status: "error",
      };
    }

    const selectedProducts = currentCart.cart_products.filter(
      (p) => p.isSelected
    );

    if (!selectedProducts || selectedProducts.length === 0) {
      return {
        code: 400,
        message: "No selected products to checkout.",
        status: "error",
      };
    }

    let total = 0;
    const bulkUpdateOps = [];

    for (const item of selectedProducts) {
      const product = await productModel.findById(item.productId);

      if (!product) {
        return {
          code: 404,
          message: `Product with ID ${item.productId} not found`,
          status: "error",
        };
      }

      if (product.product_stock < item.quantity) {
        return {
          code: 400,
          message: `Not enough stock for product ${product.product_name}`,
          status: "error",
        };
      }

      if (item.detailsVariantId) {
        const variant = await detailsVariantModel.findById(
          item.detailsVariantId
        );
        if (!variant) {
          return {
            code: 404,
            message: `Variant not found for product ${item.productId}`,
            status: "error",
          };
        }

        if (variant.stock < item.quantity) {
          return {
            code: 400,
            message: `Not enough stock for variant of product ${product.product_name}`,
            status: "error",
          };
        }

        await detailsVariantModel.updateOne(
          { _id: item.detailsVariantId },
          { $inc: { stock: -item.quantity } }
        );
      }

      bulkUpdateOps.push({
        updateOne: {
          filter: { _id: item.productId },
          update: { $inc: { product_stock: -item.quantity } },
        },
      });

      total += item.price * item.quantity;
    }

    if (bulkUpdateOps.length > 0) {
      await productModel.bulkWrite(bulkUpdateOps);
    }

    const shippingFee = 35000;
    total += shippingFee;

    let discountAmount = 0;
    let discount = null;

    if (discount_code) {
      discount = await discountRepo
        .findOne({
          code: discount_code,
          isDraft: false,
          startDate: { $lte: new Date() },
          endDate: { $gte: new Date() },
        })
        .lean();

      if (discount) {

        if (discount.usedByUsers?.some(id => id.toString() === userId.toString())) {
          return {
            code: 400,
            message: "Bạn đã sử dụng mã giảm giá này rồi.",
            status: "error",
          };
        }


        if (discount.minOrderValue && total < discount.minOrderValue) {
          return {
            code: 400,
            message: `Đơn hàng chưa đủ giá trị tối thiểu để áp dụng mã giảm giá.`,
            status: "error",
          };
        }

        if (discount.usageLimit && discount.usageCount >= discount.usageLimit) {
          return {
            code: 400,
            message: `Mã giảm giá đã được sử dụng hết lượt.`,
            status: "error",
          };
        }

        if (discount.discountType === "percentage") {
          discountAmount = (discount.discountValue / 100) * total;
          if (discount.maxDiscountAmount) {
            discountAmount = Math.min(
              discountAmount,
              discount.maxDiscountAmount
            );
          }
        } else if (discount.discountType === "fixed") {
          discountAmount = discount.discountValue;
        } else if (discount.discountType === "shipping") {
          discountAmount = shippingFee;
        }
      }
    }

    total -= discountAmount;

    const orderCode = Math.floor(10000 + Math.random() * 90000);




    // Nếu là VNPay thì trả về link thanh toán
    // Nếu là thanh toán qua VNPay: chỉ lưu tạm vào PendingPayment

    if (payment_method === "vnpay") {


      await PendingPayment.create({
        order_code: orderCode,
        user_id: currentCart.cart_userId,
        products: selectedProducts,
        address,
        total,
        shipping_fee: shippingFee,
        phone_number,
        receiver_name,
        discount_code: discount_code || null,
        discount_amount: discountAmount || 0,
        cart_id: currentCart._id,
      });

      const vnpay = new VNPay({
        tmnCode: 'HMC4RYL1',
        secureSecret: 'GP6FEUU3UDKCOXM1P5OE3AU1AJN5CDP4',
        vnpayHost: 'https://sandbox.vnpayment.vn',
        testMode: true,
        hashAlgorithm: 'SHA512',
        loggerFn: ignoreLogger,
      });

      const tomorrow = new Date();
      tomorrow.setDate(tomorrow.getDate() + 1);
      console.log('client id_____:', clientIp);

      const vnpayResponse = await vnpay.buildPaymentUrl({
        vnp_Amount: total,
        vnp_IpAddr: clientIp,
        vnp_TxnRef: orderCode.toString(),
        vnp_OrderInfo: `Thanh toán đơn hàng #${orderCode}`,
        vnp_OrderType: ProductCode.Other,
        vnp_ReturnUrl: `https://viettech.store/v1/api/bill/vnpay-return`,
        vnp_Locale: VnpLocale.VN,
        vnp_CreateDate: dateFormat(new Date()),
        vnp_ExpireDate: dateFormat(tomorrow),
      });

      return {
        code: 200,
        message: "Redirect to VNPay",
        paymentUrl: vnpayResponse,
        orderCode, // dùng orderCode để đối chiếu khi VNPay trả về
      };

    }

    // Tạo đơn hàng trước
    const newBill = await billRepo.create({
      user_id: currentCart.cart_userId,
      products: selectedProducts,
      order_code: orderCode,
      address: address,
      total: total,
      shipping_fee: shippingFee,
      phone_number: phone_number,
      receiver_name: receiver_name,
      status: "pending",
      payment_method: payment_method || "tm",
      discount_code: discount_code || null,
      discount_amount: discountAmount || 0,
    });

    if (discount) {
      await discountRepo.updateOne(
        { _id: discount._id },
        {
          $inc: { usageCount: 1 },
          $addToSet: { usedByUsers: userId } // Thêm user vào danh sách đã dùng mã
        }
      );
    }

    currentCart.cart_products = currentCart.cart_products.filter(
      (p) => !p.isSelected
    );
    if (currentCart.cart_products.length === 0) {
      await currentCart.deleteOne();
    } else {
      await currentCart.save();
    }


    // Nếu là COD hoặc thanh toán khác
    await sendPushNotification({
      titleAdmin: "🧾 Đơn hàng mới!",
      messageAdmin: `Đơn hàng có mã #${orderCode} đã được đặt thành công.`,
      url: "/v1/api/admin/bills" || "https://viettech.store",
      userId: userId.toString(),
      targets: "admin",
      data: { orderCode }
    });



    // await currentCart.deleteOne()
    return newBill;
  }


  static async checkoutNow({
    userId,
    productId,
    quantity,
    detailsVariantId,
    address,
    phone_number,
    receiver_name,
    payment_method,
    discount_code,
    req,
  }) {
    const clientIp =
      req.headers['x-forwarded-for']?.toString().split(',')[0] ||
      req.socket?.remoteAddress ||
      '127.0.0.1';

    const product = await productModel.findById(productId);
    if (!product) {
      return {
        code: 404,
        message: `Product not found`,
        status: "error",
      };
    }

    if (product.product_stock < quantity) {
      return {
        code: 400,
        message: `Not enough stock for product ${product.product_name}`,
        status: "error",
      };
    }

    let price = product.product_price;
    let variant = null;

    if (detailsVariantId) {
      variant = await detailsVariantModel.findById(detailsVariantId);
      if (!variant) {
        return {
          code: 404,
          message: `Variant not found`,
          status: "error",
        };
      }
      if (variant.stock < quantity) {
        return {
          code: 400,
          message: `Not enough stock for variant`,
          status: "error",
        };
      }
      await detailsVariantModel.updateOne(
        { _id: detailsVariantId },
        { $inc: { stock: -quantity } }
      );
      price = variant.price || price;
    }

    await productModel.updateOne(
      { _id: productId },
      { $inc: { product_stock: -quantity } }
    );

    let total = price * quantity;
    const shippingFee = 35000;
    total += shippingFee;

    let discountAmount = 0;
    let discount = null;

    if (discount_code) {
      discount = await discountRepo.findOne({
        code: discount_code,
        isDraft: false,
        startDate: { $lte: new Date() },
        endDate: { $gte: new Date() },
      }).lean();

      if (discount) {

        if (discount.usedByUsers?.some(id => id.toString() === userId.toString())) {
          return {
            code: 400,
            message: "Bạn đã sử dụng mã giảm giá này rồi.",
            status: "error",
          };
        }

        if (discount.minOrderValue && total < discount.minOrderValue) {
          return {
            code: 400,
            message: `Đơn hàng chưa đủ giá trị tối thiểu để áp dụng mã giảm giá.`,
            status: "error",
          };
        }

        if (discount.usageLimit && discount.usageCount >= discount.usageLimit) {
          return {
            code: 400,
            message: `Mã giảm giá đã được sử dụng hết lượt.`,
            status: "error",
          };
        }

        if (discount.discountType === "percentage") {
          discountAmount = (discount.discountValue / 100) * total;
          if (discount.maxDiscountAmount) {
            discountAmount = Math.min(discountAmount, discount.maxDiscountAmount);
          }
        } else if (discount.discountType === "fixed") {
          discountAmount = discount.discountValue;
        } else if (discount.discountType === "shipping") {
          discountAmount = shippingFee;
        }
      }
    }

    total -= discountAmount;
    const orderCode = Math.floor(10000 + Math.random() * 90000);

    const selectedProducts = [
      {
        productId,
        name: product.product_name,
        image: product.product_thumbnail,
        detailsVariantId: detailsVariantId || null,
        quantity,
        price,
        isSelected: true,
      }
    ];




    if (payment_method === "vnpay") {
      const vnpay = new VNPay({
        tmnCode: 'HMC4RYL1',
        secureSecret: 'GP6FEUU3UDKCOXM1P5OE3AU1AJN5CDP4',
        vnpayHost: 'https://sandbox.vnpayment.vn',
        testMode: true,
        hashAlgorithm: 'SHA512',
        loggerFn: ignoreLogger,
      });

      const tomorrow = new Date();
      tomorrow.setDate(tomorrow.getDate() + 1);

      await PendingPayment.create({
        order_code: orderCode,
        user_id: userId,
        products: selectedProducts,
        address,
        total,
        shipping_fee: shippingFee,
        phone_number,
        receiver_name,
        discount_code: discount_code || null,
        discount_amount: discountAmount || 0,
        cart_id: null,
      });

      const vnpayResponse = await vnpay.buildPaymentUrl({
        vnp_Amount: total,
        vnp_IpAddr: clientIp,
        vnp_TxnRef: orderCode.toString(),
        vnp_OrderInfo: `Thanh toán đơn hàng #${orderCode}`,
        vnp_OrderType: ProductCode.Other,
        vnp_ReturnUrl: `https://www.viettech.store/v1/api/bill/vnpay-return?isCheckoutNow=true`,
        vnp_Locale: VnpLocale.VN,
        vnp_CreateDate: dateFormat(new Date()),
        vnp_ExpireDate: dateFormat(tomorrow),
      });

      return {
        code: 200,
        message: "Redirect to VNPay",
        paymentUrl: vnpayResponse,
        billId: orderCode,
      };
    }

    const newBill = await billRepo.create({
      user_id: userId,
      products: selectedProducts,
      order_code: orderCode,
      address,
      total: total,
      shipping_fee: shippingFee,
      phone_number,
      receiver_name,
      status: "pending",
      payment_method: payment_method || "tm",
      discount_code: discount_code || null,
      discount_amount: discountAmount || 0,
    });

    if (discount) {
      await discountRepo.updateOne(
        { _id: discount._id },
        { $inc: { usageCount: 1 } }
      );
    }

    await sendPushNotification({
      titleAdmin: "🧾 Đơn hàng mới!",
      messageAdmin: `Đơn hàng có mã #${orderCode} đã được đặt thành công.`,
      url: "/v1/api/admin/bills" || "https://viettech.store",
      userId: userId.toString(),
      targets: "admin",
      data: { orderCode }
    });

    return newBill;
  }


  static async updateIsSelected({
    userId,
    productId,
    detailsVariantId,
    isSelected,
  }) {
    try {
      // Tìm giỏ hàng của người dùng
      const currentCart = await cart.findOne({
        cart_userId: userId,
        cart_state: "active",
      });

      if (!currentCart) {
        return {
          code: 400,
          message: "Cart not found",
          status: "error",
        };
      }

      // Tìm sản phẩm trong giỏ hàng
      const productIndex = currentCart.cart_products.findIndex(
        (p) =>
          p.productId.toString() === productId.toString() &&
          p.detailsVariantId?.toString() === detailsVariantId?.toString()
      );

      if (productIndex === -1) {
        return {
          code: 404,
          message: "Product not found in cart",
          status: "error",
        };
      }

      // Cập nhật trạng thái isSelected
      currentCart.cart_products[productIndex].isSelected = isSelected;

      // Lưu lại giỏ hàng sau khi cập nhật
      await currentCart.save();

      return {
        code: 200,
        message: "Product selection updated successfully",
        status: "success",
      };
    } catch (error) {
      console.error("Error updating isSelected:", error);
      return {
        code: 500,
        message: "Internal server error",
        status: "error",
      };
    }
  }

  static async addToCart({ userId, product = {} }) {
    try {
      const productInput = product.product;
      if ((productInput.quantity || 1) < 0) {
        throw new ConflictRequestError(
          "Cannot add product with negative quantity"
        );
      }

      const userExists = await Account.exists({ _id: userId });
      if (!userExists) {
        throw new NotFoundError("User not found");
      }

      const existingProduct = await Products.findById(productInput.productId);
      if (!existingProduct) {
        throw new NotFoundError("Product not found in database");
      }

      let productName = existingProduct.product_name;
      let productPrice = existingProduct.product_price;
      let productImage =
        existingProduct.product_thumbnail ||
        (existingProduct.image_ids?.length > 0
          ? existingProduct.image_ids[0]
          : null);
      let detailsVariantId = null;
      let productStock = existingProduct.product_stock;
      // Nếu có biến thể, xử lý giá và thông tin theo variant
      if (productInput.detailsVariantId) {
        const DetailsVariant = require("../models/detailsVariant.model");
        const detailsVariant = await DetailsVariant.findById(
          productInput.detailsVariantId
        );

        if (!detailsVariant) {
          throw new NotFoundError("DetailsVariant not found");
        }

        productPrice = detailsVariant.price || productPrice;
        detailsVariantId = detailsVariant._id;
        productStock = detailsVariant.stock;
      }

      console.log("product quantity:", productInput.quantity);
      console.log("product variant:", detailsVariantId);

      // Tạo sản phẩm để thêm vào giỏ
      const productToAdd = {
        productId: existingProduct._id,
        name: productName,
        price: productPrice,
        image: productImage,
        stock: productStock,
        quantity: productInput.quantity || 1,
        isSelected: true,
        detailsVariantId: detailsVariantId,
      };

      const isInCart = await CartService.isProductInCart({
        userId,
        productId: productToAdd.productId,
        detailsVariantId: productToAdd.detailsVariantId,
      });

      if (isInCart) {
        return await CartService.updateUserCartQuantity({
          userId,
          product: {
            productId: productToAdd.productId,
            quantity: productInput.quantity || 1,
            detailsVariantId: productToAdd.detailsVariantId,
          },
        });
      }

      return await CartService.createUserCart({
        userId,
        product: productToAdd,
      });
    } catch (error) {
      console.error("Error adding to cart:", error);
      throw error;
    }
  }

  //update cart
  static async updateUserCart({ userId, product }) {
    try {
      const userExists = await Account.exists({ _id: userId });
      if (!userExists) {
        throw new NotFoundError("User not found");
      }

      const { productId, quantity, detailsVariantId } = product;
      if (quantity <= 0) {
        const isInCart = await CartService.isProductInCart({
          userId,
          productId,
          detailsVariantId,
        });

        if (!isInCart && quantity < 0) {
          throw new NotFoundError(
            "Cannot decrease quantity for product not in cart"
          );
        }

        if (quantity === 0) {
          return await this.deleteUserCart({
            userId,
            productId,
            variantId: detailsVariantId,
          });
        } else {
          return await CartService.updateUserCartQuantity({
            userId,
            product: {
              productId,
              quantity,
              detailsVariantId,
            },
          });
        }
      }

      // Tìm thông tin sản phẩm từ database
      const existingProduct = await Products.findById(productId);
      if (!existingProduct) {
        throw new NotFoundError("Product not found in database");
      }
      let updatedStock = existingProduct.product_stock;

      if (detailsVariantId) {
        const detailsVariant = await detailsVariantModel.findById(
          detailsVariantId
        );
        if (detailsVariant) {
          updatedStock = detailsVariant.stock;
        }
      }
      // Kiểm tra sản phẩm có trong giỏ hàng không
      const isInCart = await CartService.isProductInCart({
        userId,
        productId,
        detailsVariantId,
      });

      if (!isInCart) {
        // Nếu sản phẩm không có trong giỏ hàng, thêm mới
        return await CartService.addToCart({
          userId,
          product: {
            product: {
              productId,
              detailsVariantId,
              quantity,
            },
          },
        });
      }

      // Tìm giỏ hàng
      const userCart = await cart.findOne({
        cart_userId: userId,
        cart_state: "active",
      });

      if (!userCart) {
        throw new NotFoundError("Cart not found");
      }

      // Tìm sản phẩm trong giỏ hàng
      const productIndex = userCart.cart_products.findIndex(
        (item) =>
          item.productId.toString() === productId.toString() &&
          (detailsVariantId
            ? item.detailsVariantId?.toString() === detailsVariantId.toString()
            : !item.detailsVariantId)
      );

      if (productIndex === -1) {
        throw new NotFoundError("Product not found in cart");
      }

      // Cập nhật số lượng
      userCart.cart_products[productIndex].quantity = quantity;
      userCart.cart_products[productIndex].stock = updatedStock;
      // Lưu giỏ hàng
      const updatedCart = await userCart.save();
      return updatedCart;
    } catch (error) {
      console.error("Error updating user cart:", error);
      throw error;
    }
  }
  //delete cart

  static async deleteUserCart({ userId, productId, variantId }) {
    try {
      console.log("Deleting from cart:", { userId, productId, variantId });

      const userExists = await Account.exists({ _id: userId });
      if (!userExists) {
        throw new NotFoundError("User not found");
      }

      const query = {
        cart_userId: userId,
        cart_state: "active",
      };

      // Tìm giỏ hàng trước khi xóa để kiểm tra
      const userCart = await cart.findOne(query);
      if (!userCart) {
        throw new NotFoundError("Cart not found");
      }

      console.log(
        "Current cart products:",
        JSON.stringify(userCart.cart_products, null, 2)
      );

      let pullCondition;

      // Xây dựng điều kiện xóa dựa trên sự hiện diện của variantId
      if (variantId) {
        // Với sản phẩm có biến thể
        console.log(`Deleting product ${productId} with variant ${variantId}`);
        pullCondition = {
          productId: productId,
          detailsVariantId: variantId,
        };
      } else {
        // Với sản phẩm không có biến thể
        console.log(`Deleting product ${productId} without variant`);
        pullCondition = {
          productId: productId,
          $or: [
            { detailsVariantId: { $exists: false } },
            { detailsVariantId: null },
          ],
        };
      }

      console.log("Pull condition:", JSON.stringify(pullCondition, null, 2));

      // Thực hiện xóa sản phẩm khỏi giỏ hàng
      const result = await cart.updateOne(query, {
        $pull: {
          cart_products: pullCondition,
        },
      });

      console.log("Delete result:", result);

      // Kiểm tra kết quả và cập nhật cart_count_product nếu cần
      const updatedCart = await cart.findOne(query);
      if (updatedCart) {
        updatedCart.cart_count_product = updatedCart.cart_products.length;
        await updatedCart.save();
      }

      return {
        success: result.modifiedCount > 0,
        message:
          result.modifiedCount > 0
            ? "Product removed from cart successfully"
            : "No product was removed from cart",
        modifiedCount: result.modifiedCount,
      };
    } catch (error) {
      console.error("Error deleting from cart:", error);
      throw error;
    }
  }

  //get cart
  static async getListUserCart({ userId }) {
    try {
      const userCart = await cart
        .findOne({
          cart_userId: userId,
          cart_state: "active",
        })
        .lean();

      if (!userCart) {
        return {
          _id: null,
          cart_state: "active",
          cart_products: [],
          cart_count_product: 0,
          cart_userId: userId,
        };
      }

      const productIds = userCart.cart_products.map((item) => item.productId);
      const detailsVariantIds = userCart.cart_products
        .map((item) => item.detailsVariantId)
        .filter(Boolean);

      const products = await Products.find(
        { _id: { $in: productIds } },
        {
          product_name: 1,
          product_price: 1,
          product_thumbnail: 1,
          product_stock: 1,
          image_ids: 1,
          category: 1,
        }
      ).populate('category', 'name') // Lấy tên danh mục
        .lean();

      const productMap = {};
      products.forEach((product) => {
        productMap[product._id.toString()] = product;
      });

      const DetailsVariant = require("../models/detailsVariant.model");
      const variants = await DetailsVariant.find({
        _id: { $in: detailsVariantIds },
      }).lean();

      const variantMap = {};
      variants.forEach((v) => {
        variantMap[v._id.toString()] = v;
      });

      const enrichedProducts = userCart.cart_products
        .map((item) => {
          const productId = item.productId.toString();
          const variantId = item.detailsVariantId?.toString();
          const product = productMap[productId];
          const variant = variantId ? variantMap[variantId] : null;

          if (!product) {
            console.warn(`Product with ID ${productId} not found in database`);
            return null;
          }

          let updatedStock = product.product_stock;
          if (variant) {
            updatedStock = variant.stock;
          }

          return {
            ...item,
            stock: updatedStock,
            product_details: {
              name: product.product_name,
              price: product.product_price,
              thumbnail: product.product_thumbnail,
              stock: product.product_stock,
              image_ids: product.image_ids,
              category: product.category,
            },
            variant_details: variant
              ? {
                price: variant.price,
                stock: variant.stock,
                variantDetails: variant.variantDetails,
              }
              : null,
          };
        })
        .filter(Boolean);

      const result = { ...userCart };
      result.cart_products = enrichedProducts;
      result.cart_count_product = enrichedProducts.length;

      return result;
    } catch (error) {
      console.error("Error getting user cart:", error);
      throw error;
    }
  }
}
module.exports = CartService;
