const { authentication } = require("../../auth/authUtils");
const BillController = require("../../controllers/bill.controller");
const express = require("express");
const logController = require("../../controllers/log.controller");
const logModel = require("../../models/log.model");
const router = express.Router();

router.get('/all', async (req, res) => {
    try {
        const { action, range, page = 1, limit = 10 } = req.query;
        const skip = (parseInt(page) - 1) * parseInt(limit);

        const query = {};
        const now = new Date();

        if (action) query.action = action;

        if (range === "today") {
            const start = new Date(now.setHours(0, 0, 0, 0));
            const end = new Date(now.setHours(23, 59, 59, 999));
            query.changed_at = { $gte: start, $lte: end };
        } else if (range === "week") {
            const start = new Date(now.setDate(now.getDate() - 7));
            query.changed_at = { $gte: start };
        } else if (range === "month") {
            const start = new Date(now.getFullYear(), now.getMonth(), 1);
            query.changed_at = { $gte: start };
        }

        const [logs, total] = await Promise.all([
            logModel.find(query)
                .populate('changed_by', 'full_name')
                .sort({ changed_at: -1 })
                .skip(skip)
                .limit(parseInt(limit)),
            logModel.countDocuments(query)
        ]);

        res.status(200).json({
            success: true,
            logs,
            currentPage: parseInt(page),
            totalPages: Math.ceil(total / limit),
            total
        });
    } catch (error) {
        res.status(500).json({ success: false, message: error.message });
    }
});

router.get('/recent', logController.getRecentLogs);
router.get("/:billId/logs", authentication, BillController.getBillLogs);

module.exports = router;
