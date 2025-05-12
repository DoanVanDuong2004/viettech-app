const multer = require('multer');
const path = require('path');

// Cấu hình bộ nhớ tạm cho file upload
const storage = multer.diskStorage({
    destination: (req, file, cb) => {
        cb(null, 'uploads/excel/');
    },
    filename: (req, file, cb) => {
        cb(null, `variants_${Date.now()}${path.extname(file.originalname)}`);
    }
});

const uploadExcel = multer({
    storage,
    fileFilter: (req, file, cb) => {
        const ext = path.extname(file.originalname);
        if (ext !== '.xlsx') {
            return cb(new Error('Chỉ chấp nhận file .xlsx'));
        }
        cb(null, true);
    }
});

module.exports = uploadExcel;
