package com.carealls.ui.components.activites.addProduct

import android.text.TextUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.carealls.R
import com.carealls.network.ApiResponse
import com.carealls.network.RestApi
import com.carealls.network.RestApiFactory
import com.carealls.pojo.AddProductData
import com.carealls.pojo.AllProductData
import com.carealls.pojo.EditProductData
import com.carealls.utils.UtilityMethod
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.MultipartBody

class AddProductViewModel : ViewModel() {
    var responseLiveAddProductData = MutableLiveData<ApiResponse<AddProductData>>()
    var apiAddProductResponse: ApiResponse<AddProductData>? = null

    var responseLiveEditProductData = MutableLiveData<ApiResponse<EditProductData>>()
    var apiEditProductResponse: ApiResponse<EditProductData>? = null

    var responseAllProductLiveData = MutableLiveData<ApiResponse<AllProductData>>()
    var apiAllProductResponse: ApiResponse<AllProductData>? = null


    private var restApi: RestApi? = null
    private var subscription: Disposable? = null

    init {
        restApi = RestApiFactory.create()
        apiAddProductResponse = ApiResponse(ApiResponse.Status.LOADING, null, null)
        //apiAllProductResponse = ApiResponse(ApiResponse.Status.LOADING, null, null)
        apiEditProductResponse = ApiResponse(ApiResponse.Status.LOADING, null, null)

    }

    fun addProductApi(
        reqMethod: MultipartBody.Part,
        req_id: MultipartBody.Part,
        req_product_name: MultipartBody.Part,
        req_product_desc: MultipartBody.Part,
        req_product_price: MultipartBody.Part,
        req_listId: MultipartBody.Part,
        profile_photo: MultipartBody.Part?
    ) {
        subscription = restApi!!.addProduct(reqMethod,req_id,req_product_name,req_product_desc,req_product_price,req_listId,profile_photo)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                responseLiveAddProductData.postValue(apiAddProductResponse!!.loading())
            }
            .subscribe(
                { result ->
                    responseLiveAddProductData.postValue(apiAddProductResponse!!.success(result))
                },
                { throwable ->
                    responseLiveAddProductData.postValue(apiAddProductResponse!!.error(throwable))
                }
            )
    }

    /*fun allProduct(reqData: HashMap<String, String>) {
        subscription = restApi!!.allProduct(reqData)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                responseAllProductLiveData.postValue(apiAllProductResponse!!.loading())
            }
            .subscribe(
                { result ->
                    responseAllProductLiveData.postValue(apiAllProductResponse!!.success(result))
                },
                { throwable ->
                    responseAllProductLiveData.postValue(apiAllProductResponse!!.error(throwable))
                }
            )
    }*/

    fun editProductApi(
        reqMethod: MultipartBody.Part,
        req_id: MultipartBody.Part,
        req_product_name: MultipartBody.Part,
        req_product_desc: MultipartBody.Part,
        req_product_price: MultipartBody.Part,
        req_listId: MultipartBody.Part,
        profile_photo: MultipartBody.Part?,
    ) {
        subscription = restApi!!.editProduct(reqMethod,req_id,req_product_name,req_product_desc,req_product_price,req_listId,profile_photo)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                responseLiveEditProductData.postValue(apiEditProductResponse!!.loading())
            }
            .subscribe(
                { result ->
                    responseLiveEditProductData.postValue(apiEditProductResponse!!.success(result))
                },
                { throwable ->
                    responseLiveEditProductData.postValue(apiEditProductResponse!!.error(throwable))
                }
            )
    }


    fun disposeSubscriber() {
        if (subscription != null)
            subscription!!.dispose()
    }

    /*Validations*/
    fun isValidFormData(
        mActivity: AppCompatActivity,
        productName: String,
        description: String,
        uploadImg: String,
        price: String
    ): Boolean {

        /*if (productName.equals("Product Name", false)) {
            UtilityMethod.showSnackBarMsgError(
                mActivity,
                mActivity.getString(R.string.select_product_name)
            )
            return false
        }*/
        if (TextUtils.isEmpty(productName)) {
            UtilityMethod.showSnackBarMsgError(
                mActivity,
                mActivity.getString(R.string.enter_product_name)
            )
            return false
        }
        if (productName.length < 3) {
            UtilityMethod.showSnackBarMsgError(
                mActivity,
                mActivity.getString(R.string.minimum_3_char_long_product_name)
            )
            return false
        }

        if (TextUtils.isEmpty(description)) {
            UtilityMethod.showSnackBarMsgError(
                mActivity,
                mActivity.getString(R.string.enter_description)
            )
            return false
        }
        if (description.length < 3) {
            UtilityMethod.showSnackBarMsgError(
                mActivity,
                mActivity.getString(R.string.minimum_3_char_long_description)
            )
            return false
        }
        if (!uploadImg.isEmpty() && uploadImg == "Upload Image") {
            UtilityMethod.showSnackBarMsgError(mActivity, mActivity.getString(R.string.please_select_image))
            return false
        }

        if (TextUtils.isEmpty(price)) {
            UtilityMethod.showSnackBarMsgError(
                mActivity,
                mActivity.getString(R.string.enter_price)
            )
            return false
        }

        return true
    }
}