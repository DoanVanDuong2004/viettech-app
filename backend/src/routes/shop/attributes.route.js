const express = require("express");
const attributeModel = require("../../models/attribute.model");
const productModel = require("../../models/product.model");
const router = express.Router();

async function isAttributeUsed(attributeId) {
  return await productModel.exists({
    attributeIds: attributeId,
    isDeleted: { $ne: true } // chỉ xét sản phẩm còn hoạt động
  });
}

// Lấy tất cả thuộc tính chưa bị xoá mềm
router.get('/all', async (req, res) => {
  try {
    const attributes = await attributeModel.find({ isDeleted: { $ne: true } });
    res.json({ success: true, attributes });
  } catch (err) {
    console.error('Lỗi lấy thuộc tính:', err);
    res.status(500).json({ success: false, message: 'Lỗi khi lấy thuộc tính' });
  }
});

// Tạo hoặc cập nhật thêm giá trị vào thuộc tính
router.post('/', async (req, res) => {
  const { name, values } = req.body;

  if (!name || !Array.isArray(values)) {
    return res.status(400).json({ success: false, message: "Thiếu tên hoặc danh sách giá trị." });
  }

  // Normalize giá trị
  const cleanedValues = [...new Set(values.map(val => val.trim()).filter(v => v))];

  try {
    let existing = await attributeModel.findOne({ name });

    //  Nếu thuộc tính bị xoá mềm → khôi phục
    if (existing && existing.isDeleted) {
      existing.isDeleted = false;
      existing.values = cleanedValues;
      await existing.save();
      return res.status(200).json({ success: true, message: "Đã khôi phục thuộc tính bị xoá.", attribute: existing });
    }

    if (existing) {
      const newValues = cleanedValues.filter(val => !existing.values.includes(val));

      if (newValues.length === 0) {
        return res.status(200).json({ success: true, message: "Thuộc tính đã tồn tại và không có giá trị mới.", attribute: existing });
      }

      existing.values = [...existing.values, ...newValues];
      await existing.save();

      return res.status(200).json({ success: true, message: "Đã cập nhật thêm giá trị mới vào thuộc tính.", attribute: existing });
    }

    const newAttr = await attributeModel.create({
      name: name.trim(),
      values: cleanedValues,
      isDeleted: false
    });

    res.status(201).json({ success: true, message: "Đã tạo thuộc tính mới.", attribute: newAttr });

  } catch (err) {
    console.error('Lỗi tạo/cập nhật thuộc tính:', err);
    res.status(500).json({ success: false, message: "Lỗi server khi tạo/cập nhật thuộc tính." });
  }
});

// Cập nhật thuộc tính nếu chưa được dùng
router.put('/:id', async (req, res) => {
  const { name, values } = req.body;

  if (!name || !Array.isArray(values)) {
    return res.status(400).json({ success: false, message: "Thiếu tên hoặc danh sách giá trị." });
  }

  const cleanedValues = [...new Set(values.map(val => val.trim()).filter(v => v))];

  try {
    const used = await isAttributeUsed(req.params.id);
    if (used) {
      return res.status(400).json({ success: false, message: "Thuộc tính đang được sử dụng. Không thể sửa." });
    }

    const updated = await attributeModel.findByIdAndUpdate(
      req.params.id,
      { name: name.trim(), values: cleanedValues },
      { new: true }
    );

    res.json({ success: true, message: "Cập nhật thành công.", attribute: updated });

  } catch (err) {
    console.error("Lỗi cập nhật:", err);
    res.status(500).json({ success: false, message: "Lỗi server khi cập nhật." });
  }
});

// Xoá mềm nếu chưa được dùng
router.delete('/:id', async (req, res) => {
  try {
    const used = await isAttributeUsed(req.params.id);

    if (used) {
      return res.status(400).json({ success: false, message: "Thuộc tính đang được sử dụng. Không thể xoá." });
    }

    await attributeModel.findByIdAndUpdate(req.params.id, { isDeleted: true });
    res.json({ success: true, message: "Đã xoá thuộc tính (mềm)." });

  } catch (err) {
    console.error("Lỗi xoá:", err);
    res.status(500).json({ success: false, message: "Lỗi server khi xoá." });
  }
});

module.exports = router;
