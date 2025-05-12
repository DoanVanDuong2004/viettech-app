"use strict";

const {
  ErrorResponse,
  ConflictRequestError,
  NotFoundError,
} = require("../core/error.response");
const { htmlToPlainText } = require("../helpers/plainText.helper");
const { post } = require("../models/post.model");
const { StatusCodes } = require("../utils/httpStatusCode");
const { decode } = require('html-entities');

function stripHtmlTags(input) {
  if (!input) return '';

  // 1. Replace important tags (br, p, h1-h6, li) with \n
  let text = input
    .replace(/<\s*(br|p|div|h[1-6]|li|hr)[^>]*>/gi, '\n')
    .replace(/<\s*\/\s*(p|div|h[1-6]|li)[^>]*>/gi, '\n');

  // 2. Remove all other HTML tags
  text = text.replace(/<[^>]*>/g, '');

  // 3. Remove all \r (carriage return)
  text = text.replace(/\r/g, '');

  // 4. Replace multiple newlines with a single newline
  text = text.replace(/\n{2,}/g, '\n');

  // 5. Trim spaces at start/end
  text = text.trim();

  return text;
}




class PostService {
  // Repository methods
  static async createPostRepo(payload) {
    return await post.create(payload);
  }

  static async getPostByIdRepo({ postId }) {
    try {
      const foundPost = await post
        .findById(postId)
        .populate("account_id")
        .populate("category_id")
        .populate("related_products")
        .populate("thumbnail")
        .populate("images")
        .lean();
      if (!foundPost) {
        throw new NotFoundError("Post not found");
      }
      return foundPost;
    } catch (error) {
      if (error.name === "CastError") {
        throw new NotFoundError("Invalid post ID format");
      }
      console.error("Error finding post by ID:", error);
      throw error;
    }
  }

  static async updatePostRepo({ postId, ...updateFields }) {
    const updatedPost = await post.findOneAndUpdate(
      { _id: postId },
      { $set: { ...updateFields, updatedAt: new Date() } },
      { new: true }
    );

    if (!updatedPost) {
      throw new NotFoundError("Post not found");
    }

    return updatedPost;
  }

  static async deletePostRepo({ postId }) {
    const foundPost = await post.findById(postId);
    if (!foundPost) {
      throw new NotFoundError("Post not found");
    }
    const deletedPost = await post.findByIdAndDelete(postId);
    if (!deletedPost) {
      throw new ConflictRequestError("Cannot delete post");
    }
    return deletedPost;
  }

  static async getAllPostRepo({ filter = {}, select = "" } = {}) {
    try {
      const finalFilter = {
        ...filter,
        status: "publish",
      };

      const posts = await post
        .find(finalFilter)
        .select(select)
        .populate("account_id")
        .populate("category_id")
        .populate("related_products")
        .populate("thumbnail")
        .populate("images")
        .sort({ createdAt: -1 })
        .lean();

      return posts;
    } catch (error) {
      console.error("Error getting posts:", error);
      throw error;
    }
  }


  // Service methods
  static async createPost(payload) {
    const { title, content, slug } = payload;
    if (!title || !content || !slug) {
      throw new ErrorResponse(
        "Title, slug, and content are required",
        StatusCodes.BAD_REQUEST
      );
    }

    const existing = await post.findOne({ slug });
    if (existing) {
      throw new ConflictRequestError("Slug already exists");
    }

    return await PostService.createPostRepo(payload);
  }

  static async updatePost({ postId, ...updateFields }) {
    try {
      if (!postId) {
        throw new ErrorResponse("Post ID is required", StatusCodes.BAD_REQUEST);
      }

      const existingPost = await post.findById(postId);
      if (!existingPost) {
        throw new NotFoundError("Post not found");
      }

      return await PostService.updatePostRepo({ postId, ...updateFields });
    } catch (error) {
      console.error("Error updating post:", error);
      throw error;
    }
  }

  static async deletePost({ postId }) {
    try {
      if (!postId) {
        throw new ErrorResponse("Post ID is required", StatusCodes.BAD_REQUEST);
      }

      const deletedPost = await PostService.deletePostRepo({ postId });

      return {
        deleted: true,
        data: deletedPost,
      };
    } catch (error) {
      console.error("Error deleting post:", error);
      throw error;
    }
  }

  static async getAllPosts({ filter = {}, select = "", sortBy = "createdAt" } = {}) {
    try {
      const posts = await PostService.getAllPostRepo({ filter, select });

      return {
        data: posts,
        count: posts.length,
      };
    } catch (error) {
      console.error("Error getting all posts:", error);
      throw error;
    }
  }

  static async getPostById({ postId, format = "html" }) {
    try {
      if (!postId) {
        throw new ErrorResponse("Post ID is required", StatusCodes.BAD_REQUEST);
      }

      const foundPost = await PostService.getPostByIdRepo({ postId });

      if (format === "plain") {
        const decoded = decode(foundPost.content); // B1: decode &lt; => <
        const plainText = stripHtmlTags(decoded);  // B2: xóa thẻ HTML
        foundPost.content = plainText;
      }

      return foundPost;
    } catch (error) {
      console.error("Error getting post by ID:", error);
      throw error;
    }
  }
}

module.exports = PostService;
