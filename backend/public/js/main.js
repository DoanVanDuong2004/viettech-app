let currentTab = 'all';
let notifications = [];

// Hàm định dạng thời gian nhận vào từ API
function formatTime(timestamp) {
  if (!timestamp) return "Vừa xong";

  const now = new Date();
  const notificationTime = new Date(timestamp);
  const diffMs = now - notificationTime;
  const diffMins = Math.floor(diffMs / 60000);
  const diffHours = Math.floor(diffMins / 60);
  const diffDays = Math.floor(diffHours / 24);

  if (diffMins < 1) return "Vừa xong";
  if (diffMins < 60) return `${diffMins} phút trước`;
  if (diffHours < 24) return `${diffHours} giờ trước`;
  if (diffDays < 30) return `${diffDays} ngày trước`;

  return notificationTime.toLocaleDateString('vi-VN');
}

function getNotificationStyle(notification) {
  const title = notification.title?.toLowerCase() || '';
  const message = notification.message?.toLowerCase() || '';
  const content = title + ' ' + message;

  if (content.includes('đơn hàng') || content.includes('thanh toán')) {
    return { icon: 'fa-shopping-cart', type: 'primary' };
  } else if (content.includes('hết hàng') || content.includes('cảnh báo') || content.includes('lỗi')) {
    return { icon: 'fa-exclamation-triangle', type: 'warning' };
  } else if (content.includes('thành công') || content.includes('hoàn thành')) {
    return { icon: 'fa-check-circle', type: 'success' };
  } else if (content.includes('cập nhật') || content.includes('hệ thống')) {
    return { icon: 'fa-sync-alt', type: 'info' };
  } else {
    return { icon: 'fa-bell', type: 'primary' };
  }
}

function renderNotifications(filter = 'all') {
  const notificationList = document.getElementById('notificationList');
  if (!notificationList) return;

  // Lọc thông báo
  let filteredNotifications = notifications;
  if (filter === 'unread') {
    filteredNotifications = notifications.filter(notification => !notification.isRead);
  }

  // Cập nhật tab hiện tại
  currentTab = filter;

  // Xóa nội dung cũ
  notificationList.innerHTML = '';

  // Hiển thị thông báo mới
  if (filteredNotifications.length === 0) {
    notificationList.innerHTML = `
            <li class="list-group-item text-center py-4">
                <div class="empty-state">
                    <i class="fas fa-bell-slash text-muted mb-3" style="font-size: 2rem;"></i>
                    <p class="text-muted">Không có thông báo ${filter === 'unread' ? 'chưa đọc' : ''}</p>
                </div>
            </li>
        `;
    return;
  }

  filteredNotifications.forEach(notification => {
    // Xác định icon và loại thông báo
    const { icon, type } = getNotificationStyle(notification);

    // Tạo item thông báo
    const notificationItem = document.createElement('li');
    notificationItem.className = `list-group-item ${!notification.isRead ? 'unread' : ''}`;
    notificationItem.style.cursor = 'pointer';

    notificationItem.innerHTML = `
            <div class="notification-item">
                <div class="notification-icon ${type}">
                    <i class="fas ${icon}"></i>
                </div>
                <div class="notification-content">
                    <div class="notification-title">${notification.title || 'Thông báo'}</div>
                    <div class="notification-message">${notification.message || ''}</div>
                    <div class="notification-time">
                        <i class="far fa-clock"></i> ${formatTime(notification.createdAt)}
                    </div>
                </div>
            </div>
        `;

    // Thêm sự kiện click
    notificationItem.addEventListener('click', () => {
      if (!notification.isRead) markAsRead(notification._id);
      if (notification.url) window.location.href = notification.url;
    });

    notificationList.appendChild(notificationItem);
  });
}

// Hàm cập nhật badge thông báo
function updateNotificationBadge() {
  const unreadCount = notifications.filter(n => !n.isRead).length;
  const badge = document.getElementById('notificationBadge');

  if (badge) {
    badge.textContent = unreadCount;
    badge.style.display = unreadCount > 0 ? 'flex' : 'none';
  }
}

// Hàm lấy thông báo từ API - giữ nguyên chức năng gọi API
async function fetchNotifications() {
  const accessToken = localStorage.getItem('accessToken');
  const userIds = localStorage.getItem('userId');
  const apiKey = 'c244dcd1532c91ab98a1c028e4f24f81457cdb2ac83e2ca422d36046fec84233589a4b51eda05e24d8871f73653708e3b13cf6dd1415a6330eaf6707217ef683';

  try {
    const response = await fetch('/v1/api/notification', {
      headers: {
        'Content-Type': 'application/json',
        authorization: accessToken,
        'x-client-id': userIds,
        'x-api-key': apiKey
      },
      credentials: 'include' // nếu dùng cookie để xác thực
    });

    const data = await response.json();
    notifications = data.notifications || [];

    // Cập nhật UI
    updateNotificationBadge();
    renderNotifications(currentTab);

  } catch (error) {
    console.error("Lỗi khi load thông báo:", error);
    notifications = [];
  }
}

// Gọi API đánh dấu đã đọc 
async function markAsRead(notificationId) {
  const accessToken = localStorage.getItem('accessToken');
  const userIds = localStorage.getItem('userId');
  const apiKey = 'c244dcd1532c91ab98a1c028e4f24f81457cdb2ac83e2ca422d36046fec84233589a4b51eda05e24d8871f73653708e3b13cf6dd1415a6330eaf6707217ef683';

  try {
    await fetch(`/v1/api/notification/${notificationId}/read`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
        authorization: accessToken,
        'x-client-id': userIds,
        'x-api-key': apiKey
      },
      credentials: 'include'
    });

    // Cập nhật local state
    notifications = notifications.map(noti => {
      if (noti._id === notificationId) {
        return { ...noti, isRead: true };
      }
      return noti;
    });

    // Cập nhật UI
    updateNotificationBadge();
    renderNotifications(currentTab);

  } catch (err) {
    console.error('Không thể đánh dấu đã đọc', err);
  }
}

// Hàm đánh dấu tất cả thông báo là đã đọc
async function markAllAsRead() {
  const accessToken = localStorage.getItem('accessToken');
  const userIds = localStorage.getItem('userId');
  const apiKey = 'c244dcd1532c91ab98a1c028e4f24f81457cdb2ac83e2ca422d36046fec84233589a4b51eda05e24d8871f73653708e3b13cf6dd1415a6330eaf6707217ef683';

  try {
    await fetch(`/v1/api/notification/read-all`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
        authorization: accessToken,
        'x-client-id': userIds,
        'x-api-key': apiKey
      },
      credentials: 'include'
    });

    // Cập nhật trạng thái cục bộ
    notifications = notifications.map(noti => ({ ...noti, isRead: true }));

    // Cập nhật UI
    updateNotificationBadge();
    renderNotifications(currentTab);

  } catch (err) {
    console.error('Không thể đánh dấu tất cả đã đọc', err);
  }
}


document.addEventListener('DOMContentLoaded', function () {
  const registerForm = document.querySelector('form[action="/register"]');

  const name = JSON.parse(localStorage.getItem("account")) || "Người dùng";
  console.log("Name:", name);
  const avatar = localStorage.getItem("avatar") || "https://via.placeholder.com/40?text=U";



  const nameEl = document.getElementById("user-fullname");
  const avatarEl = document.getElementById("user-avatar");

  if (nameEl) nameEl.textContent = name.full_name;
  if (avatarEl) avatarEl.src = name.avatar

  if (registerForm) {
    registerForm.addEventListener('submit', function (event) {
      const password = document.getElementById('password').value;
      const confirmPassword = document.getElementById('confirm_password').value;

      // Check if passwords match
      if (password !== confirmPassword) {
        event.preventDefault();
        alert('Mật khẩu không khớp. Vui lòng thử lại!');
      }

      if (password.length < 6) {
        event.preventDefault();
        alert('Mật khẩu phải có ít nhất 6 ký tự');
      }
    });
  }

  // Initialize tooltips and popovers
  var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
  var tooltipList = tooltipTriggerList.map(function (tooltipTriggerEl) {
    return new bootstrap.Tooltip(tooltipTriggerEl);
  });

  var popoverTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="popover"]'));
  var popoverList = popoverTriggerList.map(function (popoverTriggerEl) {
    return new bootstrap.Popover(popoverTriggerEl);
  });

  const currentLocation = location.pathname;
  const navLinks = document.querySelectorAll('.navbar-nav .nav-link');

  navLinks.forEach(link => {
    if (link.getAttribute('href') === currentLocation) {
      link.classList.add('active');
    }
  });

  fetchNotifications();





  // const nameEl = document.getElementById("user-fullname");
  //

  // if (nameEl) nameEl.textContent = name;
  // if (avatarEl) avatarEl.src = avatar;


  const notificationToggle = document.getElementById('notificationToggle');
  const notificationBox = document.getElementById('notificationBox');
  const tabButtons = document.querySelectorAll('.tab-btn');
  const markAllReadBtn = document.querySelector('.mark-all-read');

  // Xử lý sự kiện hiển thị/ẩn popup thông báo
  if (notificationToggle && notificationBox) {
    notificationToggle.addEventListener('click', function (e) {
      e.stopPropagation();

      if (notificationBox.classList.contains('show')) {
        notificationBox.classList.remove('show');
        setTimeout(() => {
          notificationBox.style.display = 'none';
        }, 300);
      } else {
        notificationBox.style.display = 'block';
        setTimeout(() => {
          notificationBox.classList.add('show');
        }, 10);
      }
    });
  }

  // Xử lý sự kiện chuyển tab
  if (tabButtons) {
    tabButtons.forEach(button => {
      button.addEventListener('click', function () {
        tabButtons.forEach(btn => btn.classList.remove('active'));
        this.classList.add('active');

        const tabType = this.getAttribute('data-tab');
        renderNotifications(tabType);
      });
    });
  }

  // Xử lý sự kiện đánh dấu tất cả đã đọc
  if (markAllReadBtn) {
    markAllReadBtn.addEventListener('click', function () {
      markAllAsRead();
    });
  }

  // Đóng popup khi click bên ngoài
  document.addEventListener('click', function (e) {
    if (notificationBox && !notificationBox.contains(e.target) && e.target !== notificationToggle) {
      notificationBox.classList.remove('show');
      setTimeout(() => {
        notificationBox.style.display = 'none';
      }, 300);
    }
  });


});

function togglePasswordVisibility(inputId) {
  const passwordInput = document.getElementById(inputId);
  if (passwordInput.type === 'password') {
    passwordInput.type = 'text';
  } else {
    passwordInput.type = 'password';
  }
}

async function logout() {
  try {
    const refreshToken = localStorage.getItem("refreshToken"); 
    console.log("refreshToken", refreshToken);


    const response = await fetch("/v1/api/access/logout", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({ refreshToken }),
    });

    const data = await response.json();

    if (response.ok) {
      console.log("✅ Logout thành công:", data.message);

      // Xóa token khỏi trình duyệt
      localStorage.removeItem("accessToken");
      localStorage.removeItem("refreshToken");
      sessionStorage.clear(); // Xóa tất cả session nếu cần

      // Chuyển hướng về trang đăng nhập
      window.location.href = "/";
    } else {
      console.error("❌ Lỗi khi logout:", data.message);
    }
  } catch (error) {
    console.error("❌ Lỗi logout trên client:", error);
  }
}


// Hiển thị popup nhỏ khi có thông báo mới
function showPopupNotification({ title, message, url, icon = '✓' }) {
  // Tạo overlay mờ 
  const existingOverlay = document.getElementById('popup-overlay');
  if (!existingOverlay) {
    const overlay = document.createElement('div');
    overlay.id = 'popup-overlay';
    overlay.style.position = 'fixed';
    overlay.style.top = '0';
    overlay.style.left = '0';
    overlay.style.width = '100%';
    overlay.style.height = '100%';
    overlay.style.backgroundColor = 'rgba(0, 0, 0, 0.3)';
    overlay.style.zIndex = '9998';
    overlay.style.opacity = '0';
    overlay.style.transition = 'opacity 0.3s ease';
    overlay.style.pointerEvents = 'none';
    document.body.appendChild(overlay);

    setTimeout(() => {
      overlay.style.opacity = '1';
    }, 10);
  }

  // Xóa popup cũ 
  const existingPopup = document.querySelector('.popup-notification');
  if (existingPopup) {
    existingPopup.remove();
  }

  // Tạo popup mới
  const popup = document.createElement('div');
  popup.className = 'popup-notification';

  popup.innerHTML = `
    <div class="popup-icon">${icon}</div>
    <div class="popup-content">
      <div class="popup-title">${title}</div>
      <div class="popup-message">${message}</div>
    </div>
    <div class="popup-close">×</div>
  `;

  // Thêm style cho popup
  popup.style.position = 'fixed';
  popup.style.top = '30px';
  popup.style.right = '30px';  
  popup.style.bottom = '';
  popup.style.backgroundColor = '#ffffff';
  popup.style.color = '#333333';
  popup.style.padding = '20px';
  popup.style.borderRadius = '10px';
  popup.style.boxShadow = '0 5px 25px rgba(0, 0, 0, 0.15)';
  popup.style.zIndex = '9999';
  popup.style.display = 'flex';
  popup.style.alignItems = 'center';
  popup.style.minWidth = '300px';
  popup.style.maxWidth = '400px';
  popup.style.transform = 'translateY(20px)';
  popup.style.opacity = '0';
  popup.style.transition = 'all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1)';
  popup.style.cursor = 'default';
  popup.style.fontSize = '14px';
  popup.style.borderLeft = '4px solid #4CAF50';

  
  const popupIcon = popup.querySelector('.popup-icon');
  popupIcon.style.backgroundColor = '#4CAF50';
  popupIcon.style.color = 'white';
  popupIcon.style.width = '32px';
  popupIcon.style.height = '32px';
  popupIcon.style.borderRadius = '50%';
  popupIcon.style.display = 'flex';
  popupIcon.style.alignItems = 'center';
  popupIcon.style.justifyContent = 'center';
  popupIcon.style.marginRight = '15px';
  popupIcon.style.fontSize = '16px';
  popupIcon.style.flexShrink = '0';

  const popupContent = popup.querySelector('.popup-content');
  popupContent.style.flex = '1';

  const popupTitle = popup.querySelector('.popup-title');
  popupTitle.style.fontWeight = 'bold';
  popupTitle.style.marginBottom = '5px';
  popupTitle.style.fontSize = '16px';


  const popupMessage = popup.querySelector('.popup-message');
  popupMessage.style.color = '#666';
  popupMessage.style.lineHeight = '1.4';


  const closeButton = popup.querySelector('.popup-close');
  closeButton.style.position = 'absolute';
  closeButton.style.top = '10px';
  closeButton.style.right = '15px';
  closeButton.style.fontSize = '20px';
  closeButton.style.cursor = 'pointer';
  closeButton.style.opacity = '0.6';
  closeButton.style.transition = 'opacity 0.2s';


  closeButton.addEventListener('mouseover', () => {
    closeButton.style.opacity = '1';
  });
  closeButton.addEventListener('mouseout', () => {
    closeButton.style.opacity = '0.6';
  });


  document.body.appendChild(popup);


  setTimeout(() => {
    popup.style.transform = 'translateY(0)';
    popup.style.opacity = '1';
  }, 10);

  popup.addEventListener('click', (e) => {
    if (e.target.className === 'popup-close') {
      closePopup();
    } else if (!e.target.className.includes('popup-close')) {
      window.location.href = url;
    }
  });


  function closePopup() {
    const overlay = document.getElementById('popup-overlay');
    popup.style.transform = 'translateY(20px)';
    popup.style.opacity = '0';

    if (overlay) {
      overlay.style.opacity = '0';
    }

    setTimeout(() => {
      popup.remove();
      if (overlay) {
        overlay.remove();
      }
    }, 300);
  }

  
  const autoCloseTimeout = setTimeout(() => {
    closePopup();
  }, 5000);

  
  popup.addEventListener('mouseenter', () => {
    clearTimeout(autoCloseTimeout);
  });

  
  popup.addEventListener('mouseleave', () => {
    const newTimeout = setTimeout(() => {
      closePopup();
    }, 3000);
  });

  return {
    close: closePopup
  };
}

