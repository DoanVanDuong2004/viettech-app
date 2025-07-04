// const { apiKey } = require("../auth/checkAuth");
const AccessService = require("../services/access.service");
const express = require("express");
const KeyTokenService = require("../services/keytoken.service");
const JWT = require("jsonwebtoken");

class AccessController {
  // ✅ Đăng nhập tài khoản
  async login(req, res, next) {
    try {
      console.log(`[P]:: Login Request Received ::`, req.body);

      const { username , password } = req.body;

      console.log("check log",req.body);
      

      const result = await AccessService.login({ username , password });

      if (result.status === "error") {
        console.warn(`⚠️ Login Failed: ${result.message}`);
        return res.status(result.code).json(result);
      } else {
        console.log(`✅ Login Successful for User: ${result.metadata.account.username}`);
        console.log(`🔑 Access Token: ${result.metadata.tokens.accessToken}`);

        // res.cookie("token", result.metadata.tokens.accessToken, {
        //   httpOnly: true, // Ngăn JavaScript truy cập token
        //   secure: process.env.NODE_ENV === "production", // Chỉ dùng HTTPS trong môi trường production
        //   maxAge: 60 * 60 * 1000, // Hết hạn trong 1 giờ
        //   sameSite: "Strict" // Ngăn chặn CSRF
        // });

        // res.cookie("userId", result.metadata.account._id, {
        //   httpOnly: true, // Ngăn JavaScript truy cập userId
        //   secure: process.env.NODE_ENV === "production", // Chỉ dùng HTTPS trong môi trường production
        //   maxAge: 60 * 60 * 1000, // Hết hạn trong 1 giờ
        //   sameSite: "Strict" // Ngăn chặn CSRF
        // });


    
        return res.status(result.code).json({
          result,
          success: true,
          message: "Đăng nhập thành công",
          redirectUrl: '/v1/api/admin/dashboard' 
        });
      }
    } catch (error) {
      console.error(`❌ Server Error during Login:`, error.message);
      return next(error);
    }
  }

  async loginAdmin(req, res, next) {
    try {
      const { email, password } = req.body;
      const result = await AccessService.loginAdmin({ email, password });

      if (result.status === "error") {
        return res.status(result.code).json(result);
      }

      res.cookie("token", result.metadata.tokens.accessToken, {
        httpOnly: true,
        secure: process.env.NODE_ENV === "production",
        maxAge: 60 * 60 * 1000,
        sameSite: "Strict"
      });

      res.cookie("userId", result.metadata.account._id, {
        httpOnly: true,
        secure: process.env.NODE_ENV === "production",
        maxAge: 60 * 60 * 1000,
        sameSite: "Strict"
      });

      return res.status(result.code).json({
        result,
        success: true,
        message: "Đăng nhập admin thành công",
        redirectUrl: '/v1/api/admin/dashboard'
      });
    } catch (err) {
      console.error("Lỗi loginAdmin:", err);
      return next(err);
    }
  }


  // ✅ Đăng ký tài khoản
  signUp = async (req, res) => {
    try {
      const result = await AccessService.signUp({ body: req.body });

      console.log("📥 Kết quả trả về từ signUp:", result);

      if (!result || !result.code) {
        return res
          .status(500)
          .json({ message: "Unexpected error occurred!", status: "error" });
      }

      return res.status(result.code).json(result);
    } catch (error) {
      console.error("❌ Error in signUp:", error);
      return res
        .status(500)
        .json({ message: "Internal Server Error", error: error.message });
    }
  };
  // ✅ Đăng ký tài khoản nhân viên (chỉ dành cho Admin)
  signUpEmployee = async (req, res) => {
    try {
      const result = await AccessService.signUpEmployee({ body: req.body });

      console.log("📥 Kết quả trả về từ signUp:", result);

    
      if (!result || !result.code) {
        return res
          .status(500)
          .json({ message: "Unexpected error occurred!", status: "error" });
      }

      return res.status(result.code).json(result);
    } catch (error) {
      console.error("❌ Error in signUp:", error);
      return res
        .status(500)
        .json({ message: "Internal Server Error", error: error.message });
    }
  };
  logout = async (req, res) => {
    try {
      const { refreshToken } = req.body;
      console.log("🛠 Nhận refreshToken từ request:", refreshToken);

      res.clearCookie("token", {
        httpOnly: true,
        secure: process.env.NODE_ENV === "production",
        sameSite: "Strict"
      });

      if (!refreshToken) {
        console.error("❌ refreshToken bị thiếu trong request!");
        return res.status(400).json({ message: "Missing refresh token!" });
      }

      try {
        const keyToken = await KeyTokenService.findByRefreshToken(refreshToken);
        console.log("🔎 Tìm thấy KeyToken:", keyToken);
        if (!keyToken) {
          console.error("❌ Không tìm thấy KeyStore cho refreshToken này!");
          return res.status(400).json({ message: "Invalid refresh token!" });
        }

        console.log("🛠 UserID từ token:", keyToken.user);
        const result = await KeyTokenService.removeRefreshToken(keyToken.user, refreshToken);
        console.log("📌 Kết quả remove:", result);
        if (!result) {
          console.error("❌ Không thể xóa refreshToken.");
          return res.status(400).json({ message: "Logout failed!" });
        }

        console.log("✅ RefreshToken đã được xóa thành công!");
        return res.status(200).json({ message: "Logout successful!" });

      } catch (dbError) {
        console.error("❌ Lỗi khi thao tác với database:", dbError);
        return res.status(500).json({ message: "Database error" });
      }
    } catch (error) {
      console.error("❌ [LOGOUT ERROR]:", error);
      return res.status(500).json({ message: "Internal Server Error 2" });
    }
  };


}

module.exports = new AccessController();
