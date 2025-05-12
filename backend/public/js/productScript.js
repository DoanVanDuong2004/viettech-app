
async function deleteProduct(productId) {
    if (!productId) return;

    try {
        const response = await fetch(`/v1/api/shop/products/${productId}`, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json'
            }
        });

        if (response.ok) {
            showAlertModal("Xóa sản phẩm thành công!");
            location.reload();
        } else {
            showAlertModal("Lỗi: Không thể xóa sản phẩm.");
        }
    } catch (error) {
        console.error("Lỗi khi xóa sản phẩm:", error);
        showAlertModal("Đã xảy ra lỗi khi xóa sản phẩm.");
    }
}

document.querySelectorAll('.nav-link').forEach(item => {
    item.addEventListener('click', function () {
        console.log("da o day");

        // Xóa lớp active khỏi tất cả các liên kết
        document.querySelectorAll('.nav-link').forEach(link => link.classList.remove('active'));

        // Thêm lớp active vào mục được chọn
        this.classList.add('active');
    });
});

document.getElementById('exportProductsButton').addEventListener('click', function () {
    fetch('/v1/api/admin/products/export', {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json'
        }
    })
        .then(response => {
            if (response.ok) {
                return response.blob();
            }
            throw new Error('Error exporting products')
        })
        .then(blob => {
            const link = document.createElement('a');
            link.href = URL.createObjectURL(blob);  
            link.download = 'all_products.xlsx';
            link.click();  
        })
        .catch(error => {
            console.error('Error:', error);
            showAlertModal('Có lỗi khi xuất file.');
        })
});

function toggleStatus(productId, isDraft) {
    const confirmText = isDraft
        ? "Bạn muốn chuyển sản phẩm này sang trạng thái HIỂN THỊ?"
        : "Bạn muốn CHUYỂN SANG BẢN NHÁP sản phẩm này?";

    showConfirmModal(confirmText, async () => {
        try {
            const response = await fetch(`/v1/api/admin/products/${productId}/toggle-status`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json'
                }
            });

            const data = await response.json();

            if (data.success) {
                showAlertModal(data.message);
                location.reload();
            } else {
                showAlertModal("Lỗi: " + data.message);
            }
        } catch (error) {
            console.error("Lỗi khi thay đổi trạng thái:", error);
            showAlertModal("Đã xảy ra lỗi.");
        }
    });
}


document.getElementById('applyFilter').addEventListener('click', function () {
    const name = document.getElementById('searchName').value;
    const status = document.getElementById('filterStatus').value;
    const stock = document.getElementById('filterStock').value;
    const sort = document.getElementById('sortOption').value;

    let query = '?';

    if (name) query += `search=${encodeURIComponent(name)}&`;
    if (status) query += `status=${status}&`;
    if (stock) query += `stock=${stock}&`;
    if (sort) query += `sort=${sort}&`;

    window.location.href = `/v1/api/admin/products/list${query}`;
});

function showAlertModal(message, title = 'Thông báo') {
    document.getElementById('globalModalLabel').innerText = title;
    document.getElementById('globalModalBody').innerText = message;
    document.getElementById('globalModalFooter').innerHTML = `
      <button type="button" class="btn btn-primary" data-bs-dismiss="modal">OK</button>
    `;
    new bootstrap.Modal(document.getElementById('globalModal')).show();
  }
  
  function showConfirmModal(message, onConfirm, title = 'Xác nhận') {
    document.getElementById('globalModalLabel').innerText = title;
    document.getElementById('globalModalBody').innerText = message;
        document.getElementById('globalModalFooter').innerHTML = `
        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
        <button type="button" class="btn btn-danger" id="confirmYesBtn">Xác nhận</button>
        `;
  
    const modal = new bootstrap.Modal(document.getElementById('globalModal'));
    modal.show();
  
    
    setTimeout(() => {
      document.getElementById('confirmYesBtn').onclick = () => {
        modal.hide();
        onConfirm();
      };
    }, 100);
  }
  

document.getElementById('searchButton').addEventListener('click', function () {
    document.getElementById('applyFilter').click();
});

