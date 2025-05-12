window.OneSignalDeferred = window.OneSignalDeferred || [];

OneSignalDeferred.push(async (OneSignal) => {
    try {
        await OneSignal.init({
            // appId: "72f70b7a-10a0-476e-b0c6-5f558f9e89b8",
            appId: "29ae4e65-bafd-49fb-8c3b-cde54d2bf2bb",
            notifyButton: { enable: true }, 
            serviceWorkerPath: "/OneSignalSDKWorker.js",
        });

        console.log("✅ Đã khởi tạo OneSignal");

        OneSignal.Notifications.addEventListener('foregroundWillDisplay', function (event) {
            console.log("🔔 Vừa nhận thông báo mới từ OneSignal:", event);
            console.log("🔔 Thông báo:", event.notification.body);

            let title = event.notification.title || "Thông báo";
            let message = event.notification.body || "Bạn có thông báo mới!";
            let icon = "🔔";

            if (title.toLowerCase().includes('thành công')) {
                icon = "✅";
            } else if (title.toLowerCase().includes('lỗi') || message.toLowerCase().includes('lỗi')) {
                icon = "⚠️";
            } else if (title.toLowerCase().includes('cảnh báo')) {
                icon = "🚨";
            }

            showPopupNotification({
                title: title,
                message: message,
                url: event.notification.launchURL || "/",
                icon: icon 
            });

            fetchNotifications(); 
        });


        await new Promise(resolve => setTimeout(resolve, 1000));

        if (!OneSignal.User) {
            console.warn("⚠️ OneSignal.User không khả dụng", OneSignal);
            return;
        }

        console.log("Các thuộc tính có sẵn của OneSignal.User:", Object.keys(OneSignal.User));

        if (!OneSignal.User.PushSubscription) {
            console.warn("⚠️ OneSignal.User.PushSubscription không khả dụng");
            console.log("Các thuộc tính có sẵn của OneSignal.User:", Object.keys(OneSignal.User));
            return;
        }

        const subscription = OneSignal.User.PushSubscription;

      
        console.log("Các thuộc tính của subscription:", Object.keys(subscription));

       
        const daDangKy = subscription.optedIn;
        console.log("📦 Đã đăng ký:", daDangKy);

        if (daDangKy) {
            await xuLyDangKy(OneSignal);
        }

        // Lắng nghe sự thay đổi đăng ký
        subscription.addEventListener("change", async (event) => {
            console.log("🔔 Trạng thái đăng ký thay đổi:", event.current.optedIn);
            if (event.current.optedIn) {
                await xuLyDangKy(OneSignal);
            }
        });

    } catch (error) {
        console.error("❌ Lỗi trong luồng OneSignal:", error);
    }
});

/**
 * Xử lý khi người dùng đăng ký thông báo
 * @param {Object} OneSignal - Instance của OneSignal
 */
async function xuLyDangKy(OneSignal) {
    try {
        const oneSignalId = OneSignal.User._currentUser?.onesignalId;
        if (!oneSignalId) {
            console.warn("⚠️ Không tìm thấy OneSignal ID");
            return;
        }
        
        
        let role = (localStorage.getItem("role") || "user").toLowerCase();
        console.log("role sau khi chuyển về chữ thường:", role);
        
        if (!["admin", "staff", "customer"].includes(role)) role = "user";
        const userId = localStorage.getItem("userId");

        console.log("🎯 OneSignal ID:", oneSignalId);
        console.log("🔖 Gán tags:", { externalUserId: userId, role });


        await OneSignal.User.addTags({ externalUserId: userId, role });
        console.log("Đã gán tags cho OneSignal ID:", OneSignal.User);
        const tags = await OneSignal.User.getTags();
        console.log("🔖 Tags hiện tại:", tags);

        await guiIdDenServer(oneSignalId);
    } catch (error) {
        console.error("❌ Lỗi khi xử lý đăng ký:", error);
    }
}


/**
 * Gửi ID người dùng đến server
 * @param {string} userId - ID OneSignal của người dùng
 */
async function guiIdDenServer(userId) {
    const accessToken = localStorage.getItem('accessToken');
    const userIds = localStorage.getItem('userId');
    const apiKey = 'c244dcd1532c91ab98a1c028e4f24f81457cdb2ac83e2ca422d36046fec84233589a4b51eda05e24d8871f73653708e3b13cf6dd1415a6330eaf6707217ef683'
    try {
        const response = await fetch('/v1/api/admin/onesignal/save-player-id', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                authorization: accessToken,
                'x-client-id': userIds,
                'x-api-key': apiKey
            },
            body: JSON.stringify({ oneSignalId: userId }),
        });

        if (!response.ok) {
            throw new Error(`Lỗi HTTP! trạng thái: ${response.status}`);
        }

        const data = await response.json();
        console.log("✅ Đã gửi đến server:", data);
    } catch (error) {
        console.error("❌ Gửi ID thất bại:", error);
    }
}