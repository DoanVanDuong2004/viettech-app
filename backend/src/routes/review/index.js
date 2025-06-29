const express = require("express");
const router = express.Router();
const ReviewController = require("../../controllers/review.controller");
const { apiKey, permissions } = require("../../auth/checkAuth");
const {authentication}=require("../../auth/authUtils")

// Lấy danh sách review theo productId (chỉ lấy review active và không bị báo cáo)
router.get("/getReviewsByProduct/:productId", ReviewController.getReviewsByProduct);
// Lấy tất cả review
router.get("/getAll", ReviewController.getAllReviews);
// Lấy thống kê số lượng đánh giá và trung bình sao theo productId
router.get("/getReviewStats/:productId", ReviewController.getReviewStatsByProduct);
// Lấy danh sách review theo account_id
router.get("/getReviewsByAccount/:accountId", ReviewController.getReviewsByAccount);

// Lấy danh sách review theo accountId và productId
router.get("/getReview/:accountId/:productId", ReviewController.getReviews);

router.use(authentication)

// Thêm review mới
router.post("/add", ReviewController.addReview);

// Cập nhật nội dung review theo reviewId
router.put("/update/:reviewId", ReviewController.updateReview);



module.exports = router;
