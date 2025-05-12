import com.datn.viettech_md_12.data.interfaces.UserService
import retrofit2.Response

class UserRepository (
    private val apiService : UserService
){
    suspend fun signUp(request: RegisterRequest) : Response<RegisterResponse>{
        return apiService.signUp(request)
    }
    suspend fun signIn(request: LoginRequest) : Response<LoginResponse>{
        return apiService.signIn(request)
    }
    suspend fun changePassword(
        token: String,
        clientId: String,
        request: ChangePasswordRequest
    ): Response<ChangePasswordResponse> {
        return apiService.changePassword(token, clientId, request)
    }
    suspend fun sendEmail(email: String): Response<MessageResponse> {
        return apiService.forgotPassword(ForgotPasswordRequest(email = email))
    }
    suspend fun resetPassword(email: String, otp: String, newPassword: String): Response<MessageResponse> {
        return apiService.forgotPassword(
            ForgotPasswordRequest(
                email = email,
                otp = otp,
                newPassword = newPassword
            )
        )
    }
    suspend fun logout(tokens: Tokens): Response<MessageResponse> {
        return apiService.logout(tokens)
    }
    suspend fun updateProfileImage(request: UpdateImageToAccountRequest): Response<UpdateImageToAccountResponse> {
        return apiService.updateProfileImage(request)
    }
    suspend fun getAccountById(id: String): Response<AccountDetailResponse> {
        return apiService.getAccountById(id)
    }


}
