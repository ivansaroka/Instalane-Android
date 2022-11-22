package com.infinix.instalane.data.remote

import com.infinix.instalane.data.remote.request.*
import com.infinix.instalane.data.remote.response.*
import okhttp3.MultipartBody
import retrofit2.http.*

interface ApiInterface {

    @POST("register")
    suspend fun register(@Body request: RegisterRequest): User

    @POST("register_google")
    suspend fun registerWithGoogle(@Body request: RegisterGoogleRequest): User

    @POST("register_fb")
    suspend fun registerWithFacebook(@Body request: RegisterFacebookRequest): User

    @GET("get_privacy_policy")
    suspend fun getPrivacy(): Legal

    @GET("get_terms_of_use")
    suspend fun getTerms(): Legal

    @POST("login")
    suspend fun login(@Body request: LoginRequest): User

    @POST("logout")
    suspend fun logout(@Body request: LogoutRequest): Any

    @HTTP(method = "DELETE", path = "delete_account", hasBody = true)
    suspend fun deleteAccount(@Body request: LogoutRequest): Any

    @POST("forgot_password")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequest): Any

    @POST("register_device_token")
    suspend fun registerDeviceToken(@Body request: DeviceTokenRequest): Any

    @PUT("edit_profile")
    suspend fun editProfile(@Body body: MultipartBody): User

    @PUT("edit_profile")
    suspend fun changePassword(@Body body: EditRequest): User

    @PUT("edit_profile")
    suspend fun setTwoFactor(@Body body: TwoFactorRequest): User

    @GET("get_profile")
    suspend fun getProfile(@Query("access_token") accessToken: String): User

    @GET("get_memberships")
    suspend fun getMemberships(@Query("access_token") accessToken: String): List<Memberships>

    @POST("add_membership")
    suspend fun addMembership(@Body request: AddMemberRequest): Memberships

    @GET("get_stores")
    suspend fun getStores(@Query("access_token") accessToken: String, @Query("lat") lat: Double?=null, @Query("long") long: Double?=null, @Query("radius") radius: Float?=null, @Query("store_id") storeId: Int?=null): List<Store>

    @GET("get_store_reviews")
    suspend fun getStoreReviews(@Query("access_token") accessToken: String, @Query("store_id") storeId: String, @Query("page") page:Int): List<Review>

    @POST("add_review")
    suspend fun addReview(@Body request: ReviewRequest): Any

    @GET("get_coupons")
    suspend fun getCoupons(@Query("access_token") accessToken: String, @Query("store_id") storeId: String?, @Query("code") code:String?): List<Coupon>

    @GET("get_product")
    suspend fun getProduct(@Query("access_token") accessToken: String, @Query("company_id") companyId: String, @Query("product_id") productId: Int?=null, @Query("code") code:String?=null): List<Product>

    @GET("get_categories")
    suspend fun getCategories(@Query("access_token") accessToken: String): List<Category>

    @GET("get_notifications")
    suspend fun getNotifications(@Query("access_token") accessToken: String, @Query("page") page: Int): List<Notification>

    @GET("get_order")
    suspend fun getOrder(@Query("access_token") accessToken: String, @Query("order_id") orderId: String): Order

    @POST("add_note")
    suspend fun addNote(@Body request: NoteRequest): Note

    @GET("get_purchase_history")
    suspend fun getPurchaseHistory(@Query("access_token") accessToken: String, @Query("page") page: Int): List<PurchaseHistory>

    @GET("get_companies")
    suspend fun getCompanies(@Query("access_token") accessToken: String): List<Memberships.Company>

    @POST("create_order")
    suspend fun createOrder(@Body request: CreateOrderRequest.Body): Order

    @POST("confirm_order")
    suspend fun confirmOrder(@Body request: ConfirmOrderRequest): Any

    @POST("payment_intent")
    suspend fun paymentIntent(@Body request: PaymentRequest): PaymentIntentResponse

    @POST("send_code")
    suspend fun sendCode(@Body request: Any): Any

    @POST("validate_code")
    suspend fun validateCode(@Body request: ValidateCodeRequest): Any


}