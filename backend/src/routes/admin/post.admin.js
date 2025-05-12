const express = require('express');
const postController = require('../../controllers/post.controller');
const { post } = require('../../models/post.model');
const router = express.Router();

router.get("/list", postController.getPostListPage);
router.get("/create", postController.getCreatePostPage);
router.get("/edit/:id", postController.getEditPostPage);

router.put("/:id/toggle-status", postController.togglePostStatus);
router.get('/:id/preview', async (req, res) => {
    try {
        const { id } = req.params;
        const postData = await post.findById(id)
            .populate('thumbnail')
            .populate("images")
            .populate('category_id')
            .populate('account_id') 
            .lean();


        if (!postData) {
            return res.status(404).json({ message: 'Không tìm thấy bài viết' });
        }

        console.log("postData:", postData);


        return res.json({
            title: postData.title,
            author: postData.account_id?.full_name || postData.author || 'Không rõ',
            createdAt: postData.createdAt,
            category: postData.category_id?.name || '',
            thumbnail: postData.thumbnail?.url || '',
            content: postData.content,
            meta_description: postData.meta_description || '',
            tags: postData.tags || [],
            gallery: (postData.images || []).map(img => ({
                url: img.url,
                file_path: img.file_path
            }))
        });


    } catch (err) {
        console.error("Lỗi preview:", err);
        return res.status(500).json({ message: "Lỗi server" });
    }
});


module.exports = router;
