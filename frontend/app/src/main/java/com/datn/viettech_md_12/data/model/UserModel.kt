data class RegisterRequest(
    val username: String,
    val full_name: String,
    val phone: String,
    val email: String,
    val password: String
)
// Request login
data class LoginRequest(
    val username: String,
    val password: String
)
// Data class cho response đăng ký
data class RegisterResponse(
    val code: Int,
    val message: String,
    val status: String,
    val metadata: Metadata
)

// Metadata chứa thông tin user và token
data class Metadata(
    val account: Account,
    val profile_image:String,
    val tokens: Tokens
)

// Thông tin tài khoản user
data class Account(
    val _id: String,
    val username: String,
    val full_name: String,
    val email: String,
    val phone: String,
    val status: String
)

// Token đăng nhập
data class Tokens(
    val accessToken: String,
    val refreshToken: String
)
data class LoginResponse(
    val result: LoginResult
)
data class LoginResult(
    val code: Int,
    val message: String,
    val status: String,
    val metadata: Metadata
)
//Change Password
data class ChangePasswordRequest(
    val accountId: String,
    val oldPassword: String,
    val newPassword: String
)

data class ChangePasswordResponse(
    val code: Int,
    val message: String,
    val status: String
)
//Forgot Password
data class ForgotPasswordRequest(
    val email: String,
    val otp: String? = null,
    val newPassword: String? = null
)

data class MessageResponse(
    val message: String
)

data class UpdateImageToAccountRequest(
    val accountId: String,
    val imageId: String
)
data class UpdateImageToAccountResponse(
    val success: Boolean,
    val message: String,
    val account: AccountResponse
)

data class AccountResponse(
    val _id: String,
    val username: String,
    val email: String,
    val profile_image: ProfileImage
)

data class ProfileImage(
    val _id: String,
    val file_name: String,
    val file_path: String,
    val file_size: Int,
    val file_type: String,
    val url: String,
    val uploaded_at: String,
    val createdAt: String,
    val updatedAt: String,
    val __v: Int
)

data class AccountDetailResponse(
    val code: Int,
    val message: String,
    val status: String,
    val data: AccountDetail
)

data class AccountDetail(
    val _id: String,
    val username: String,
    val full_name: String,
    val phone: String,
    val address: String,
    val email: String,
    val role_id: RoleId,
    val status: String,
    val profile_image: ProfileImage,
    val updatedAt: String,
    val oneSignalId: String,
    val role: String
)

data class RoleId(
    val _id: String,
    val name: String
)
