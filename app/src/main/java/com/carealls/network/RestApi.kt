package com.carealls.network

import com.carealls.pojo.*
import io.reactivex.Observable
import okhttp3.MultipartBody
import retrofit2.http.*


interface RestApi {

    @POST("/api.php")
    @FormUrlEncoded
    fun signup(@FieldMap reqData: Map<String, String>): Observable<SignupData>

    @FormUrlEncoded
    @POST("/api.php")
    fun login(@FieldMap reqData: Map<String, String>): Observable<LoginData>

    @FormUrlEncoded
    @POST("/api.php")
    fun forgotPass(@FieldMap reqData: Map<String, String>): Observable<ForgotPassData>

    @FormUrlEncoded
    @POST("/api.php")
    fun resetPass(@FieldMap reqData: Map<String, String>): Observable<ResetPassData>

    @FormUrlEncoded
    @POST("/api.php")
    fun sociallogin(@FieldMap reqData: Map<String, String?>): Observable<SocialLoginData>

    @FormUrlEncoded
    @POST("/api.php")
    fun dashboard(@FieldMap reqData: Map<String, String?>): Observable<DashboardData>

    @FormUrlEncoded
    @POST("/api.php")
    fun getProfile(@FieldMap reqData: Map<String, String?>): Observable<ProfileData>

    @Multipart
    @POST("/api.php")
    fun updateProfile(
        @Part reqMethod: MultipartBody.Part,
        @Part reqId: MultipartBody.Part,
        @Part reqName: MultipartBody.Part,
        @Part reqEmail: MultipartBody.Part,
        @Part reqPhone: MultipartBody.Part,
        @Part reqAddress: MultipartBody.Part,
        @Part profilePhoto: MultipartBody.Part?
    ): Observable<UpdateProfileData>

    @FormUrlEncoded
    @POST("/api.php")
    fun support(@FieldMap reqData: Map<String, String?>): Observable<SupportData>

    @FormUrlEncoded
    @POST("/api.php")
    fun allCategory(@FieldMap reqData: Map<String, String?>): Observable<AllCategoryData>

    @FormUrlEncoded
    @POST("/api.php")
    fun allLocation(@FieldMap reqData: Map<String, String?>): Observable<AllLocationData>

    @FormUrlEncoded
    @POST("/api.php")
    fun allListing(@FieldMap reqData: Map<String, String?>): Observable<AllListingData>

    @FormUrlEncoded
    @POST("/api.php")
    fun allProduct(@FieldMap reqData: Map<String, String?>): Observable<AllProductData>

    @FormUrlEncoded
    @POST("/api.php")
    fun addReview(@FieldMap reqData: Map<String, String?>): Observable<AddReviewData>

    @Multipart
    @POST("/api.php")
    fun addListing(
        @Part reqMethod: MultipartBody.Part,
        @Part reqId: MultipartBody.Part,
        @Part reqCatId: MultipartBody.Part,
        @Part reqBusinessName: MultipartBody.Part,
        @Part reqDescription: MultipartBody.Part,
        @Part reqPhone: MultipartBody.Part,
        @Part reqWhatsappNo: MultipartBody.Part,
        @Part reqAddress: MultipartBody.Part,
        @Part profilePhoto: MultipartBody.Part?,
        @Part profilePhoto1: MultipartBody.Part?,
        @Part profilePhoto2: MultipartBody.Part?,
        @Part profilePhoto3: MultipartBody.Part?,
        @Part profilePhoto4: MultipartBody.Part?,
        @Part profilePhoto5: MultipartBody.Part?,
        @Part profilePhoto6: MultipartBody.Part?,
        @Part profilePhoto7: MultipartBody.Part?,
        @Part profilePhoto8: MultipartBody.Part?,
    ): Observable<AddListingData>


    @FormUrlEncoded
    @POST("/api.php")
    fun getPackage(@FieldMap reqData: Map<String, String?>): Observable<PackageData>

    @Multipart
    @POST("/api.php")
    fun addProduct(
        @Part reqMethod: MultipartBody.Part,
        @Part reqId: MultipartBody.Part,
        @Part reqProductName: MultipartBody.Part,
        @Part reqProudctDesc: MultipartBody.Part,
        @Part reqProductPrice: MultipartBody.Part,
        @Part reqListId: MultipartBody.Part,
        @Part profilePhoto: MultipartBody.Part?
    ): Observable<AddProductData>

    @FormUrlEncoded
    @POST("/api.php")
    fun sumitPackage(@FieldMap reqData: Map<String, String?>): Observable<SubmitPackageData>

    @FormUrlEncoded
    @POST("/api.php")
    fun categoryDetail(@FieldMap reqData: Map<String, String?>): Observable<CategoryDetailData>

    @FormUrlEncoded
    @POST("/api.php")
    fun categoryProduct(@FieldMap reqData: Map<String, String?>): Observable<CategoryProductData>

    @FormUrlEncoded
    @POST("/api.php")
    fun sendEnquiry(@FieldMap reqData: Map<String, String?>): Observable<SendEnquiryData>

    @FormUrlEncoded
    @POST("/api.php")
    fun listingDetail(@FieldMap reqData: Map<String, String?>): Observable<ListingDetailData>

    @FormUrlEncoded
    @POST("/api.php")
    fun gallery(@FieldMap reqData: Map<String, String?>): Observable<GalleryData>

    @FormUrlEncoded
    @POST("/api.php")
    fun reviewList(@FieldMap reqData: Map<String, String?>): Observable<ReviewListData>

    @FormUrlEncoded
    @POST("/api.php")
    fun productDetail(@FieldMap reqData: Map<String, String?>): Observable<ProductDetailData>

    @FormUrlEncoded
    @POST("/api.php")
    fun updateListingDetail(@FieldMap reqData: Map<String, String?>): Observable<UpdateListingDetailData>

    @Multipart
    @POST("/api.php")
    fun editProduct(
        @Part reqMethod: MultipartBody.Part,
        @Part reqId: MultipartBody.Part,
        @Part reqProductName: MultipartBody.Part,
        @Part reqProudctDesc: MultipartBody.Part,
        @Part reqProductPrice: MultipartBody.Part,
        @Part reqListId: MultipartBody.Part,
        @Part profilePhoto: MultipartBody.Part?
    ): Observable<EditProductData>

    @Multipart
    @POST("/api.php")
    fun editListing(
        @Part reqMethod: MultipartBody.Part,
        @Part reqId: MultipartBody.Part,
        @Part reqCatId: MultipartBody.Part,
        @Part reqBusinessName: MultipartBody.Part,
        @Part reqDescription: MultipartBody.Part,
        @Part reqPhone: MultipartBody.Part,
        @Part reqWhatsappNo: MultipartBody.Part,
        @Part reqAddress: MultipartBody.Part,
        @Part profilePhoto: MultipartBody.Part?,
        @Part profilePhoto1: MultipartBody.Part?,
        @Part profilePhoto2: MultipartBody.Part?,
        @Part profilePhoto3: MultipartBody.Part?,
        @Part profilePhoto4: MultipartBody.Part?,
        @Part profilePhoto5: MultipartBody.Part?,
        @Part profilePhoto6: MultipartBody.Part?,
        @Part profilePhoto7: MultipartBody.Part?,
        @Part profilePhoto8: MultipartBody.Part?,
        @Part reqListId: MultipartBody.Part
    ): Observable<EditUpdateListingData>

    @FormUrlEncoded
    @POST("/api.php")
    fun locationListing(@FieldMap reqData: Map<String, String?>): Observable<LocationListingData>

    @FormUrlEncoded
    @POST("/api.php")
    fun search(@FieldMap reqData: Map<String, String?>): Observable<SearchData>

    @FormUrlEncoded
    @POST("/api.php")
    fun deleteGallery(@FieldMap reqData: Map<String, String?>): Observable<DeleteGalleryData>

    @FormUrlEncoded
    @POST("/api.php")
    fun deleteProduct(@FieldMap reqData: Map<String, String?>): Observable<DeleteProductData>

}