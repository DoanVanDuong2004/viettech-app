"use strict";

const PostService = require("../services/post.service");
const { SuccessResponse } = require("../core/success.response");
const { ErrorResponse } = require("../core/error.response");
const { StatusCodes } = require("../utils/httpStatusCode");
const accountModel = require("../models/account.model");
const { post } = require("../models/post.model");
const categoryModel = require("../models/category.model");
const Image = require("../models/image.model");
const { sendPushNotification } = require("../helpers/onesignal.helper");
const { htmlToPlainText } = require("../helpers/plainText.helper");



class PostController {
  createPost = async (req, res, next) => {
    try {
      const {
        title,
        content,
        slug,
        status,
        account_id,
        category_id,
        tags,
        related_products,
      } = req.body;

      const payload = {
        title,
        content,
        slug,
        status,
        account_id,
        category_id,
        tags,
        related_products,
      };

      // ✅ Xử lý thumbnail nếu có file upload
      if (req.files?.thumbnail?.[0]) {
        const file = req.files.thumbnail[0];
        const thumbnail = await new Image({
          file_name: file.originalname,
          file_path: file.path,
          file_size: file.size,
          file_type: file.mimetype,
          url: `${req.protocol}://${req.get('host')}/uploads/${file.filename}`
        }).save();

        payload.thumbnail = thumbnail._id;
      }

      // ✅ Xử lý gallery nếu có
      if (req.files?.gallery_uploads?.length > 0) {
        const galleryImages = await Promise.all(req.files.gallery_uploads.map(file => {
          return new Image({
            file_name: file.originalname,
            file_path: file.path,
            file_size: file.size,
            file_type: file.mimetype,
            url: `${req.protocol}://${req.get('host')}/uploads/${file.filename}`
          }).save();
        }));

        payload.images = galleryImages.map(img => img._id);
      }

      console.log("Payload:", payload);



      if (!title || !slug || !content) {
        return new ErrorResponse("Missing required fields", StatusCodes.BAD_REQUEST).send(res);
      }

      const result = await PostService.createPost(payload);
      if (result.status === 'publish') {
        await sendPushNotification({
          titleUser: "📰 Bài viết mới từ VietTech!",
          messageUser: `👉 ${result.title} đã đăng rồi, cùng đọc nhé!`,
          titleAdmin: "🆕 Bài viết vừa được đăng",
          messageAdmin: `${result.title} đã publish thành công.`,
          url: `https://www.viettech.store/v1/api/post/details/${result.slug}`,
          targets: "all", // Gửi cho tất cả người dùng luông
          data: { postId: result._id.toString() },
          type: "new_post"
        });

      }

      new SuccessResponse({
        message: "Post created successfully",
        metadata: result,
      }).send(res);
    } catch (error) {
      next(error);
    }
  };

  updatePost = async (req, res, next) => {
    try {
      const postId = req.params.id;




      if (!postId) {
        return new ErrorResponse("Post ID is required", StatusCodes.BAD_REQUEST).send(res);
      }

      // Gộp dữ liệu cần cập nhật
      const updateData = {
        ...req.body,
        postId,
        thumbnail: req.body.thumbnail_id,
        images: req.body.image_ids,
      };

      console.log("Update post ID:", postId, "Update data:", updateData);

      //  Nếu không nhập `author` thì lấy theo account_id
      if (!req.body.author && req.body.account_id) {
        const acc = await accountModel.findById(req.body.account_id).lean();
        if (acc) {
          updateData.author = acc.full_name || acc.username || acc.email || "Không rõ";
        }
      } else {
        updateData.author = req.body.author; // nếu có nhập thì dùng
      }

      const result = await PostService.updatePost(updateData);

      new SuccessResponse({
        message: "Post updated successfully",
        metadata: result,
      }).send(res);
    } catch (error) {
      next(error);
    }
  };


  deletePost = async (req, res, next) => {
    const postId = req.params.id;
    if (!postId) {
      return new ErrorResponse(
        "Post ID is required",
        StatusCodes.BAD_REQUEST
      ).send(res);
    }
    const result = await PostService.deletePost({ postId });

    new SuccessResponse({
      message: "Post deleted successfully",
      metadata: result,
    }).send(res);
  };

  getAllPost = async (req, res, next) => {
    new SuccessResponse({
      message: "Post list successfully",
      metadata: await PostService.getAllPosts(req.query),
    }).send(res);
  };

  getPostById = async (req, res, next) => {
    try {
      const { id } = req.params;
      const { format } = req.query; 

      if (!id) {
        return new ErrorResponse("Id is required", StatusCodes.BAD_REQUEST).send(res);
      }

      const post = await PostService.getPostById({ postId: id, format });

      new SuccessResponse({
        message: "Get post successfully",
        metadata: post,
      }).send(res);
    } catch (error) {
      next(error);
    }
  };


  getPostListPage = async (req, res) => {
    try {
      const { search, status, sort, page = 1, limit = 10 } = req.query;
      const filter = {};

      if (search) {
        filter.title = { $regex: search, $options: "i" };
      }
      if (status) {
        filter.status = status;
      }

      const sortOptions = {
        createdAt_desc: { createdAt: -1 },
        createdAt_asc: { createdAt: 1 },
        title_asc: { title: 1 },
        title_desc: { title: -1 },
      };
      const sortQuery = sortOptions[sort] || { createdAt: -1 };

      const skip = (parseInt(page) - 1) * parseInt(limit);

      //  Lấy bài viết kèm populate
      const [posts, total] = await Promise.all([
        post.find(filter)
          .populate("thumbnail")
          .populate("images")
          .populate("related_products")
          .populate("account_id")
          .sort(sortQuery)
          .skip(skip)
          .limit(limit)
          .lean(),
        post.countDocuments(filter),
      ]);

      //  Map tác giả
      // const authorsMap = {};
      // posts.forEach(p => {
      //   const acc = p.account_id;
      //   if (acc && acc._id) {
      //     authorsMap[acc._id.toString()] = acc.full_name || acc.username || acc.email || "Không rõ";
      //   }
      // });

      posts.forEach((p, index) => {
        console.log(`[${index}] Post Title: ${p.title}`);
        console.log(`   ➤ account_id:`, p.account_id);
      });

      // console.log("Authors map:", authorsMap["65e4a201d4a1d6b87e4e3f11"]);



      //  Map thumbnail
      const thumbnails = {};
      posts.forEach(p => {
        console.log("Post ID:", p._id.toString(), "Thumbnail:", p.thumbnail);

        if (p.thumbnail && p.thumbnail.file_path) {
          thumbnails[p._id.toString()] = p.thumbnail.file_path;
        }
      });

      //  Map images (gallery)
      // const images = {};
      // posts.forEach(p => {
      //   if (Array.isArray(p.images)) {
      //     console.log("Post ID:", p._id.toString(), "Images:", p.images);

      //     p.images.forEach(img => {
      //       if (img && img._id && img.file_path) {
      //         images[img._id.toString()] = img.file_path;
      //       }
      //     });
      //   }
      // });

      //  Map products (related_products)
      // const products = {};
      // posts.forEach(p => {
      //   if (Array.isArray(p.related_products)) {
      //     p.related_products.forEach(prod => {
      //       if (prod && prod._id) {
      //         products[prod._id.toString()] = prod.product_name || `Sản phẩm #${prod._id.toString().slice(-4)}`;
      //       }
      //     });
      //   }
      // });

      //  Lấy danh mục
      const categories = await categoryModel.find({}).lean();

      // console.log("Danh sách bài viết:", images);




      return res.render("admin/post-list", {
        posts,
        thumbnails,
        currentPage: parseInt(page),
        totalPages: Math.ceil(total / limit),
        categories,
        query: req.query
      });
    } catch (error) {
      console.error("Lỗi khi load trang quản lý bài viết:", error);
      return res.status(500).send("Lỗi server khi load trang quản lý bài viết.");
    }
  };


  getCreatePostPage = async (req, res) => {
    try {
      const categories = await categoryModel.find({}).lean();
      const products = []; // await productModel.find({}).lean();

      res.render("admin/post-form", {
        action: "Add",
        post: {},
        isEdit: false,
        postModel: null,
        categories,
        products,
        currentUser: req.user || { fullname: 'Admin' }
      });
    } catch (error) {
      console.error("Lỗi hiển thị trang tạo bài viết:", error);
      res.status(500).send("Lỗi server");
    }
  };


  getEditPostPage = async (req, res) => {
    try {
      const postId = req.params.id;

      if (!postId) {
        return res.status(400).send("Thiếu ID bài viết.");
      }

      const postModel = await post.findById(postId)
        .populate("thumbnail")
        .populate("images")
        .populate("related_products")
        .populate("category_id")
        .populate("account_id")
        .lean();


      // if (postModel && postModel.content) {
      //   postModel.content = cleanHtmlContent(postModel.content);
      // }
      
      if (!postModel) {
        return res.status(404).send("Không tìm thấy bài viết.");
      }

      const categories = await categoryModel.find({}).lean();
      const products = []; //  await productModel.find({}) nếu hiển thị sẵn danh sách
      console.log("Post model:", postModel);
      postModel.content = htmlToPlainText(postModel.content || "");

      return res.render("admin/post-form", {
        action: "Edit",
        isEdit: true,
        post: postModel,
        categories,
        products,
        currentUser: req.user || { fullname: 'Admin' }
      });
    } catch (error) {
      console.error("Lỗi hiển thị trang chỉnh sửa bài viết:", error);
      return res.status(500).send("Lỗi server khi hiển thị trang chỉnh sửa.");
    }
  };


  togglePostStatus = async (req, res) => {
    try {
      const { id } = req.params;
      const { newStatus } = req.body;
      console.log("Toggle post status:", id, newStatus);

      if (!['draft', 'publish'].includes(newStatus)) {
        return res.status(400).json({ success: false, message: 'Trạng thái không hợp lệ.' });
      }

      const posts = await post.findById(id);
      if (!posts) {
        return res.status(404).json({ success: false, message: 'Không tìm thấy bài viết.' });
      }

      const oldStatus = posts.status; //  Lưu lại trạng thái cũ

      posts.status = newStatus;
      await posts.save();

      //  Nếu chuyển từ draft → publish thì gửi thông báo
      if (oldStatus === 'draft' && newStatus === 'publish') {
        await sendPushNotification({
          titleUser: "📰 Bài viết mới từ VietTech!",
          messageUser: `👉 ${posts.title} vừa được đăng rồi, cùng đọc nhé!`,
          titleAdmin: "🆕 Bài viết vừa được đăng",
          messageAdmin: `${posts.title} đã được publish thành công.`,
          url: `https://www.viettech.store/v1/api/post/details/${posts.slug}`,
          targets: "all",
          data: { postId: posts._id.toString() },
          type: "new_post"
        });
      }

      return res.status(200).json({
        success: true,
        message: `Đã chuyển trạng thái bài viết thành ${newStatus === 'publish' ? 'Đã đăng' : 'Bản nháp'}.`
      });
    } catch (err) {
      console.error('togglePostStatus error:', err);
      return res.status(500).json({ success: false, message: 'Có lỗi xảy ra.' });
    }
  };

  getPostDetailPage = async (req, res) => {
    try {
      const { slug } = req.params;

      if (!slug) {
        return res.status(400).send("Thiếu slug bài viết.");
      }

      const postModel = await post.findOne({ slug, status: "publish" })
        .populate("thumbnail")
        .populate("images")
        .populate("account_id")
        .populate("related_products")
        .populate("category_id")
        .lean();

      if (!postModel) {
        return res.status(404).send("Không tìm thấy bài viết hoặc bài viết chưa được public.");
      }

      return res.render("post/post-detail", { post: postModel });
    } catch (error) {
      console.error("Lỗi hiển thị chi tiết bài viết:", error);
      return res.status(500).send("Lỗi server khi hiển thị bài viết.");
    }
  };


}

module.exports = new PostController();