"use strict";

const { sendPushNotification } = require("../helpers/onesignal.helper");
const categoryModel = require("../models/category.model");
const { discountRepo } = require("../models/disscount.model");
const productModel = require("../models/product.model");
const DiscountService = require("../services/disscount.service");

class DiscountController {
    static async createDiscount(req, res, next) {
        console.log("Create discount request body:", req.body);

        try {
            const data = req.body;
            const result = await DiscountService.createDiscount(data);
            return res.status(result.statusCode).json(result);
        } catch (error) {
            next(error);
        }
    }

    
    static async getAllDiscounts(req, res) {
        try {
            const { search, status, sort, page = 1, limit = 20 } = req.query;

            const filter = {};
            const now = new Date();

            if (search) {
                filter.name = { $regex: search, $options: "i" };
            }

            if (status === "active") {
                filter.isDraft = false;
                filter.startDate = { $lte: now };
                filter.endDate = { $gte: now };
            } else if (status === "scheduled") {
                filter.isDraft = false;
                filter.startDate = { $gt: now };
            } else if (status === "expired") {
                filter.isDraft = false;
                filter.endDate = { $lt: now };
            } else if (status === "draft") {
                filter.isDraft = true;
            }

            const sortOptions = {
                discount_asc: { discountValue: 1 },
                discount_desc: { discountValue: -1 },
                start_date_asc: { startDate: 1 },
                start_date_desc: { startDate: -1 },
                name_asc: { name: 1 },
                name_desc: { name: -1 }
            };

            const sortQuery = sortOptions[sort] || { createdAt: -1 };
            const skip = (parseInt(page) - 1) * parseInt(limit);

            const [discounts, total] = await Promise.all([
                DiscountService.getDiscounts(filter, sortQuery, skip, parseInt(limit)),
                DiscountService.countDiscounts(filter)
            ]);

            return res.status(200).json({
                success: true,
                total,
                currentPage: parseInt(page),
                totalPages: Math.ceil(total / limit),
                data: discounts
            });
        } catch (error) {
            console.error("Lỗi khi getAllDiscounts:", error);
            return res.status(500).json({ message: "Internal Server Error", error });
        }
    }


    static async updateDiscount(req, res) {
        try {
            const { code } = req.params;
            const updateData = req.body;
            const response = await DiscountService.updateDiscount(code, updateData);
            return res.status(response.statusCode).json(response);
        } catch (error) {
            return res.status(500).json({ message: "Internal Server Error", error });
        }
    }

    static async deleteDiscount(req, res) {
        try {
            const { code } = req.params;
            const response = await DiscountService.deleteDiscount(code);
            return res.status(response.statusCode).json(response);
        } catch (error) {
            return res.status(500).json({ message: "Internal Server Error", error });
        }
    }

    static async validateDiscount(req, res) {
        try {
            const { code } = req.body;
            const userId = req.account?._id || req.user?._id; // hoặc nơi khác bạn lưu user

            console.log("Validate discount request body:", req.user._id);
            
    
            if (!code) {
                return res.status(400).json({ message: "Discount code is required" });
            }
    
            if (!userId) {
                return res.status(401).json({ message: "Unauthorized: missing user info" });
            }
    
            const response = await DiscountService.validateDiscountCode(code, userId);
            return res.status(response.statusCode).json(response);
        } catch (error) {
            return res.status(500).json({ message: "Internal Server Error", error });
        }
    }
    

    static async getDiscountListPage(req, res, next) {
        try {
            const { search, status, sort, page = 1, limit = 10 } = req.query;
            const filter = {};
            if (search) {
                filter.name = { $regex: search, $options: 'i' };
            }
            const now = new Date();
            if (status === "active") {
                filter.isDraft = false;
                filter.startDate = { $lte: now };
                filter.endDate = { $gte: now };
            } else if (status === "scheduled") {
                filter.isDraft = false;
                filter.startDate = { $gt: now };
            } else if (status === "expired") {
                filter.isDraft = false;
                filter.endDate = { $lt: now };
            } else if (status === "draft") {
                filter.isDraft = true;
            }

            const sortOptions = {
                discount_asc: { discountValue: 1 },
                discount_desc: { discountValue: -1 },
                start_date_asc: { startDate: 1 },
                start_date_desc: { startDate: -1 },
                end_date_asc: { endDate: 1 },
                name_asc: { name: 1 },
                name_desc: { name: -1 }
            };

            const sortQuery = sortOptions[sort] || { createdAt: -1 };
            const skip = (parseInt(page) - 1) * parseInt(limit);

            const [discounts, total] = await Promise.all([
                DiscountService.getDiscounts(filter, sortQuery, skip, limit),
                DiscountService.countDiscounts(filter)
            ]);

            const totalPages = Math.ceil(total / limit);



            res.render('admin/discount-list', {
                discounts,
                query: req.query,
                currentPage: parseInt(page),
                totalPages
            });
        } catch (error) {
            next(error);
        }
    }

    static async getCreateDiscountPage(req, res) {
        try {
            const products = await productModel.find().lean(); 
            const categories = await categoryModel.find().lean();

            res.render('admin/discount-form', {
                isEdit: false,
                discount: {},
                products,
                categories,
            });
        } catch (error) {
            console.error("Error render create page:", error);
            res.status(500).send("Lỗi khi load trang tạo khuyến mãi.");
        }
    }

    static async getEditDiscountPage(req, res) {
        try {
            const discount = await discountRepo.findById(req.params.id)
                .populate('appliedProducts')
                .populate('appliedCategories');

            if (!discount) {
                return res.status(404).send('Khuyến mãi không tồn tại');
            }

            res.render('admin/discount-form', {
                isEdit: true,
                discount,
            });
        } catch (err) {
            console.error('Lỗi lấy khuyến mãi để sửa:', err);
            res.status(500).send('Lỗi server');
        }
    };

    static async toggleDiscountStatus(req, res) {
        try {
            const { id } = req.params;
            const discount = await discountRepo.findById(id);

            if (!discount) {
                return res.status(404).json({ success: false, message: "Không tìm thấy khuyến mãi" });
            }

            const wasDraft = discount.isDraft;
            // Nếu đang là nháp và muốn chuyển sang hoạt động thì kiểm tra hạn
            if (wasDraft && !discount.isDraft && discount.expiredAt && new Date(discount.expiredAt) < new Date()) {
                return res.status(400).json({
                    success: false,
                    message: "Không thể kích hoạt khuyến mãi đã hết hạn"
                });
            }

            discount.isDraft = !discount.isDraft;
            await discount.save();


            if (wasDraft && !discount.isDraft) {
                await sendPushNotification({
                    titleUser: "🎁 Voucher khuyến mãi mới!",
                    messageUser: `Bạn vừa nhận được voucher "${discount.name}". Đừng bỏ lỡ!`,
                    titleAdmin: "🎉 Voucher đã kích hoạt",
                    messageAdmin: `Khuyến mãi "${discount.name}" đã được đăng thành công!`,
                    url: `https://www.viettech.store/v1/api/shop/discounts/${discount._id}`,
                    targets: "all",
                    data: { discountId: discount._id.toString() },
                    type: "discount"
                });
            }


            return res.status(200).json({
                success: true,
                message: `Đã ${discount.isDraft ? 'chuyển sang Bản nháp' : 'kích hoạt'} thành công`,
                metadata: discount
            });
        } catch (error) {
            console.error('toggleDiscountStatus error:', error);
            return res.status(500).json({ success: false, message: "Lỗi server khi cập nhật trạng thái" });
        }
    }


}

module.exports = DiscountController;
