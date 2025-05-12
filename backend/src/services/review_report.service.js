const ReviewReport = require("../models/review_report.model");
const Review = require("../models/review.model");

class ReviewReportService {
    // Thêm báo cáo review
    static async addReviewReport(review_id, account_id, reason, status) {
        if (!review_id || !account_id || !reason || !status) {
            throw new Error("Vui lòng nhập đầy đủ các trường review_id, account_id, reason và status.");
        }
    
        return await ReviewReport.create({ review_id, account_id, reason, status });
    }
    
    // Lấy danh sách báo cáo
    static async getAllReports() {
        return await ReviewReport.find().populate("review_id account_id", "contents_review username full_name");
    }

    static async updateReportStatus(report_id, status) {
        const report = await ReviewReport.findById(report_id);
        if (!report) throw new Error("Không tìm thấy báo cáo");

        // Cập nhật trạng thái của review
        await Review.findByIdAndUpdate(report.review_id, { status }, { new: true });

        // Xóa báo cáo sau khi xử lý
        await ReviewReport.findByIdAndDelete(report_id);

        return { message: "Cập nhật thành công & Xóa báo cáo", status };
    }
}

module.exports = ReviewReportService;
