require('dotenv').config();
const axios = require('axios');
const accountModel = require('../models/account.model');
const notificationModel = require('../models/notification.model');

const appId = process.env.ONESIGNAL_APP_ID;
const apiKey = process.env.ONESIGNAL_API_KEY;

if (!appId) {
    console.error('❌ Thiếu ONESIGNAL_APP_ID');
    throw new Error('Thiếu App ID trong cấu hình OneSignal');
}
if (!apiKey) {
    console.error('❌ Thiếu ONESIGNAL_API_KEY');
    throw new Error('Thiếu REST API Key trong cấu hình OneSignal');
}

console.log('ONESIGNAL_APP_ID:', appId);
console.log('ONESIGNAL_API_KEY:', apiKey);

//Hàm build filters cho OneSignal
function buildFilters(targets, userId) {
    const filters = [];

    if (targets === "all") {
        // Gửi cho tất cả: customer, admin, staff
        filters.push(
            { field: "tag", key: "role", relation: "=", value: "customer" },
            { operator: "OR" },
            { field: "tag", key: "role", relation: "=", value: "admin" },
            { operator: "OR" },
            { field: "tag", key: "role", relation: "=", value: "staff" }
        );
    }

    if (targets === "user") {
        // Gửi đúng 1 user cụ thể
        if (userId) {
            filters.push({ field: "tag", key: "externalUserId", relation: "=", value: userId });
        }
    }

    if (targets === "admin") {
        filters.push({ field: "tag", key: "role", relation: "=", value: "admin" });
    }

    if (targets === "admin_staff") {
        filters.push({ field: "tag", key: "role", relation: "=", value: "staff" });
    }

    if (targets === "both") {
        if (userId) {
            filters.push({ field: "tag", key: "externalUserId", relation: "=", value: userId });
            filters.push({ operator: "OR" });
        }
        filters.push({ field: "tag", key: "role", relation: "=", value: "admin" });
    }

    return filters;
}



/**
 * Gửi push notification đến user, admin hoặc tất cả
 * @param {Object} params
 * @param {string} params.titleUser - Tiêu đề cho user
 * @param {string} params.messageUser - Nội dung cho user
 * @param {string} params.titleAdmin - Tiêu đề cho admin
 * @param {string} params.messageAdmin - Nội dung cho admin
 * @param {string} [params.url] - URL khi click
 * @param {string} [params.userId] - externalUserId
 * @param {string} [params.targets] - "user", "admin", "both", "admin_staff"
 * @param {object} [params.data] - Dữ liệu phụ đính kèm
 * @param {string} [params.type] - Loại thông báo
 */
const sendPushNotification = async ({
    titleUser,
    messageUser,
    titleAdmin,
    messageAdmin,
    url,
    userId,
    targets = "user",
    data = {},
    type = "custom"
}) => {
    console.log("🔔 Gửi thông báo đến:", targets, userId);

    try {
        const filters = buildFilters(targets, userId);
        console.log("🔖 Filters:", filters);

        const pushRes = await axios.post(
            'https://onesignal.com/api/v1/notifications',
            {
                app_id: appId,
                contents: { en: messageAdmin || messageUser || "Bạn có thông báo mới" },
                headings: { en: titleAdmin || titleUser || "Thông báo" },
                url: url || 'https://viettech.store',
                filters,
            },
            {
                headers: {
                    Authorization: `Basic ${apiKey}`,
                    'Content-Type': 'application/json',
                },
            }
        );

        // ✅ Lưu thông báo vào DB
        const notifications = [];

        // User
        if (targets === "user" || targets === "both" || targets === "all") {
            if (userId) {
                notifications.push({
                    receiverId: userId,
                    title: titleUser || "Thông báo",
                    message: messageUser || "Bạn có thông báo mới",
                    url,
                    type,
                    data,
                });
            }
        }

        // Admin / Staff
        if (targets === "admin" || targets === "both" || targets === "admin_staff" || targets === "all") {
            const validRoles = targets === "admin_staff" ? ["admin", "staff"] : ["admin"];
            const accounts = await accountModel.find().populate("role_id");

            const targetAccounts = accounts.filter(acc => {
                const role = acc.role_id?.name?.toLowerCase();
                if (!role) return false;

                if (targets === "all") {
                    // Khi gửi all thì lấy hết admin, staff, customer
                    return ["admin", "staff", "customer"].includes(role);
                }

                if (targets === "admin") {
                    return role === "admin";
                }

                if (targets === "admin_staff") {
                    return ["admin", "staff"].includes(role);
                }

                if (targets === "user") {
                    return role === "customer";
                }

                if (targets === "both") {
                    return ["admin", "customer"].includes(role);
                }

                return false;
            });


            for (const acc of targetAccounts) {
                notifications.push({
                    receiverId: acc._id.toString(),
                    title: (["admin", "admin_staff"].includes(targets) ? (titleAdmin || "Thông báo") : (titleUser || "Thông báo")),
                    message: (["admin", "admin_staff"].includes(targets) ? (messageAdmin || "Bạn có thông báo mới") : (messageUser || "Bạn có thông báo mới")),
                    url,
                    type,
                    data,
                });
            }

        }

        if (notifications.length > 0) {
            await notificationModel.insertMany(notifications);
            console.log("💾 Đã lưu thông báo:", notifications.length);
        }

        return {
            pushResult: pushRes.data,
            savedNotifications: notifications.length,
        };
    } catch (error) {
        console.error("❌ Push send failed:", error.response?.data || error.message);
        throw error;
    }
};

module.exports = { sendPushNotification };
