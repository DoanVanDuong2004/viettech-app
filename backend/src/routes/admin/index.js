const express = require("express");
const accessController = require("../../controllers/access.controller");
const { apiKey } = require("../../auth/checkAuth");
const { authSession, authentication } = require("../../auth/middlewares/authMiddleware");
const accountModel = require("../../models/account.model");

const router = express.Router();


// router.get('/login', (req, res) => {
//    res.render('home/login');
// });

router.use(authentication)


router.use("/products", require("./product.admin"));
router.use("/categories", require("./category.admin"));
router.use("/bills", require("./bill.admin"));
router.use("/user", require("./user.admin"));
router.use("/reports", require("./reports.admin"));
router.use("/discounts", require("./discount.admin"));
router.use("/posts", require("./post.admin"));
router.use("/reviews", require("./review.admin"));
router.use("/onesignal", require("./onesignal"));
router.use("/attributes", require("./attribute.admin"));


router.get('/dashboard', (req, res) => {
    res.render('admin/dashboard')
})

router.get('/profile', async (req, res) => {
    try {
        const userId = req.user?.userId;
        if (!userId) return res.redirect("/login");

        const user = await accountModel
            .findById(userId)
            .populate("role_id")
            .populate("profile_image")
            .lean();

        if (!user) return res.status(404).send("Không tìm thấy tài khoản");

        console.log("🔍 Tài khoản người dùng:", user);


        res.render('admin/profile', { user });
    } catch (err) {
        console.error("❌ Lỗi khi load profile:", err);
        res.status(500).send("Lỗi server");
    }
});

router.get('/settings', async (req, res) => {
    try {
        const user = await accountModel
            .findById(req.user.userId)
            .populate('profile_image')
            .lean();
        res.render('admin/settings', { user });
    } catch (err) {
        console.error("❌ Lỗi khi load settings:", err);
        res.status(500).send("Lỗi server");
    }
});

module.exports = router;