const express = require('express');
const { getAccount, getAllAccount } = require('../../controllers/account.controller');
const accountController = require('../../controllers/account.controller');
const roleModel = require('../../models/role.model');
const { authentication } = require('../../auth/authUtils');
const { isAdmin } = require('../../auth/middlewares/authMiddleware');
const upload = require("../../auth/middlewares/upload.middleware");
const router = express.Router();

// Admin page hiển thị danh sách
router.get("/list", isAdmin, async (req, res, next) => {

    try {


        const result = await accountController.getAllAccounts(req, res, next);
        const roles = await roleModel.find().lean();
        // console.log("Danh sách người dùng:", result.accounts);


        res.render("admin/user-list", {
            users: result.accounts,
            roles,
            currentPage: result.page,
            totalPages: result.totalPages,
            totalAccounts: result.totalAccounts,
            limit: result.limit,
            search: result.search,
            role: result.role,
            status: result.status
        });


    } catch (error) {
        console.error("❌ Error loading users:", error);
        res.status(500).send("Lỗi khi tải danh sách người dùng!");
    }
});


router.put("/:id", isAdmin, upload.single("profile_image"), accountController.adminUpdateAccount);


// router.get('/admin/users/create', accessController.renderCreateForm); // optional

// // API
// router.post('/users', accessController.createUser);
// router.put('/users/:id', accessController.updateUser);
// router.delete('/users/:id', accessController.deleteUser);

// // Lọc (AJAX partial render)
// router.get('/admin/users/partial', accessController.renderPartialList);

// // API lấy role nếu cần
// router.get('/roles', accessController.getAllRoles);

module.exports = router;
