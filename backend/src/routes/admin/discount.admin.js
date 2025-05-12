const express = require('express');
const DiscountController = require('../../controllers/disscount.controller');
const disscountModel = require('../../models/disscount.model');
const ExcelJS = require('exceljs');
const router = express.Router();


router.get('/list', DiscountController.getDiscountListPage);
router.get('/create', DiscountController.getCreateDiscountPage);
router.get('/edit/:id', DiscountController.getEditDiscountPage);
router.put('/:id/toggle-status', DiscountController.toggleDiscountStatus);
router.get('/export', async (req, res) => {
    try {
        const discounts = await disscountModel.discountRepo.find();

        const workbook = new ExcelJS.Workbook();
        const worksheet = workbook.addWorksheet('Danh sách khuyến mãi');

        // Tiêu đề
        worksheet.columns = [
            { header: 'ID', key: '_id', width: 24 },
            { header: 'Tên khuyến mãi', key: 'name', width: 30 },
            { header: 'Mã giảm giá', key: 'code', width: 20 },
            { header: 'Loại', key: 'discountType', width: 15 },
            { header: 'Giá trị', key: 'discountValue', width: 15 },
            { header: 'Ngày bắt đầu', key: 'startDate', width: 20 },
            { header: 'Ngày kết thúc', key: 'endDate', width: 20 },
            { header: 'Trạng thái', key: 'status', width: 18 },
            { header: 'Người tạo', key: 'createdBy', width: 20 }
        ];

        // Ghi dữ liệu
        discounts.forEach(discount => {
            const now = new Date();
            let status = 'Bản nháp';
            if (!discount.isDraft) {
                if (now < discount.startDate) status = 'Đã lên lịch';
                else if (now > discount.endDate) status = 'Đã hết hạn';
                else status = 'Đang hoạt động';
            }

            worksheet.addRow({
                _id: discount._id.toString(),
                name: discount.name,
                code: discount.code || 'Tự động',
                discountType:
                    discount.discountType === 'percentage'
                        ? 'Phần trăm'
                        : discount.discountType === 'fixed'
                            ? 'Cố định'
                            : 'Miễn phí ship',
                discountValue:
                    discount.discountType === 'shipping' ? '' : discount.discountValue,
                startDate: discount.startDate.toLocaleDateString('vi-VN'),
                endDate: discount.endDate.toLocaleDateString('vi-VN'),
                status,
                createdBy: discount.createdBy || 'Admin'
            });
        });

        // Tạo buffer & gửi file
        res.setHeader(
            'Content-Type',
            'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
        );
        res.setHeader(
            'Content-Disposition',
            'attachment; filename=all_discounts.xlsx'
        );

        await workbook.xlsx.write(res);
        res.end();
    } catch (err) {
        console.error('Export error:', err);
        res.status(500).json({ success: false, message: 'Lỗi xuất file Excel.' });
    }
});


module.exports = router;
