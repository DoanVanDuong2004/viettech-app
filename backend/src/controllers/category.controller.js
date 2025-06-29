const { log } = require("console");
const categoryModel = require("../models/category.model");
const Category = require("../models/category.model");
const productModel = require("../models/product.model");

// Tạo một danh mục mới
const createCategory = async (req, res) => {
    try {
        const { name, parent_category, attributes_template } = req.body;

        // Parse attributes_template nếu được gửi dưới dạng chuỗi
        const parsedAttributes = typeof attributes_template === "string"
            ? JSON.parse(attributes_template)
            : attributes_template;

        // Ưu tiên file upload nếu có
        const thumbnail = req.file
            ? `/uploads/${req.file.filename}` // hoặc URL đầy đủ nếu dùng domain
            : req.body.thumbnail || "";

        if (!name || !parsedAttributes) {
            return res.status(400).json({
                success: false,
                message: "Name and attributes_template are required!"
            });
        }

        const categoryData = {
            name,
            parent_category: parent_category || null,
            attributes_template: parsedAttributes,
            thumbnail
        };

        const category = await categoryModel.create(categoryData);
        res.status(201).json({ success: true, category });
    } catch (error) {
        res.status(500).json({ success: false, message: error.message });
    }
};

// Lấy tất cả danh mục
const getAllCategories = async (req, res) => {
    try {
        const categories = await Category.find().populate("parent_category").lean();

        // Đếm số sản phẩm cho từng category
        const categoriesWithCount = await Promise.all(
            categories.map(async (category) => {
                const count = await productModel.countDocuments({ category: category._id });
                return {
                    ...category,
                    productCount: count
                };
            })
        );

        res.status(200).json({ success: true, categories: categoriesWithCount });
    } catch (error) {
        console.error("Lỗi getAllCategories:", error);
        res.status(500).json({ success: false, message: error.message });
    }
};


const getAllCategories_Admin = async (req, res) => {
    console.log("check getAllCategories_Admin: ", res);

    try {
        const { page = 1, limit = 10, search = "", type = "",
            sortBy = "createdAt",
            sortOrder = "desc",
        } = req.query;

        const query = {};

        console.log("query", res);

        // Tìm kiếm theo tên
        if (search) {
            query.name = { $regex: search, $options: "i" };
        }

        // Lọc theo loại danh mục
        if (type === "parent") {
            query.parent_category = null;
        } else if (type === "child") {
            query.parent_category = { $ne: null };
        }

        const skip = (parseInt(page) - 1) * parseInt(limit);

        const sortOption = {};
        sortOption[sortBy] = sortOrder === "asc" ? 1 : -1;


        console.log("sortOption", req.query);


        const [categories, totalCategories] = await Promise.all([
            Category.find(query)
                .populate("parent_category")
                .sort(sortOption)
                .skip(skip)
                .limit(parseInt(limit))
                .lean(),
            Category.countDocuments(query),
        ]);

        return {
            success: true,
            categories,
            currentPage: parseInt(page),
            totalPages: Math.ceil(totalCategories / limit),
            totalCategories,
            limit: parseInt(limit),
            search,
            type,
            sortBy,
            sortOrder,
        };

    } catch (error) {
        console.error("❌ Error fetching categories:", error);
    }
};

// Lấy một danh mục theo id
const getCategoryById = async (req, res) => {
    try {
        const category = await Category.findById(req.params.id).populate("parent_category");
        if (!category) {
            return res.status(404).json({ success: false, message: "Category not found!" });
        }
        res.status(200).json({ success: true, category });
    } catch (error) {
        res.status(500).json({ success: false, message: error.message });
    }
};

const getAttributesByCategory = async (req, res, next) => {
    try {
        const category = await Category.findById(req.params.id);
        if (!category) return res.status(404).json({ message: "Không tìm thấy danh mục!" });

        return res.status(200).json({ attributes: category.attributes_template || [] });
    } catch (error) {
        return res.status(500).json({ message: "Lỗi server", error: error.message });
    }
};

// Cập nhật thông tin danh mục
const updateCategory = async (req, res) => {
    try {
        const { name, parent_category, attributes_template, thumbnail } = req.body;

        const parsedAttributes = typeof attributes_template === "string"
            ? JSON.parse(attributes_template)
            : attributes_template;

        let updatedData = {
            name,
            parent_category,
            attributes_template: parsedAttributes,
            thumbnail,
        };

        if (req.file) {
            // Xoá ảnh cũ
            const category = await Category.findById(req.params.id);
            if (category?.thumbnail) {
                const fs = require("fs");
                const path = require("path");
                const oldPath = path.join(__dirname, "..", "..", category.thumbnail);
                if (fs.existsSync(oldPath)) fs.unlinkSync(oldPath);
            }

            updatedData.thumbnail = `/uploads/${req.file.filename}`;
        }

        const updatedCategory = await Category.findByIdAndUpdate(
            req.params.id,
            updatedData,
            { new: true }
        );

        if (!updatedCategory) {
            return res.status(404).json({ success: false, message: "Category not found!" });
        }

        res.status(200).json({ success: true, updatedCategory });
    } catch (error) {
        res.status(500).json({ success: false, message: error.message });
    }
};



// Xóa một danh mục
const deleteCategory = async (req, res) => {
    try {
        const deletedCategory = await Category.findByIdAndDelete(req.params.id);
        if (!deletedCategory) {
            return res.status(404).json({ success: false, message: "Category not found!" });
        }
        res.status(200).json({ success: true, message: "Category deleted successfully!" });
    } catch (error) {
        res.status(500).json({ success: false, message: error.message });
    }
};

module.exports = {
    createCategory,
    getAllCategories,
    getCategoryById,
    updateCategory,
    deleteCategory,
    getAllCategories_Admin,
    getAttributesByCategory
};
