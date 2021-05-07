package com.carealls.ui.components.activites.forgotPass

import android.text.TextUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.carealls.R
import com.carealls.network.ApiResponse
import com.carealls.network.RestApi
import com.carealls.network.RestApiFactory
import com.carealls.pojo.ForgotPassData
import com.carealls.pojo.SignupData
import com.carealls.utils.UtilityMethod
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class ForgotPassViewModel : ViewModel() {
    var responseLiveData = MutableLiveData<ApiResponse<ForgotPassData>>()
    var apiResponse: ApiResponse<ForgotPassData>? = null


    private var restApi: RestApi? = null
    private var subscription: Disposable? = null

    init {
        restApi = RestApiFactory.create()
        apiResponse = ApiResponse(ApiResponse.Status.LOADING, null, null)
    }

    fun forgotPassApi(reqData: HashMap<String, String>) {
        subscription = restApi!!.forgotPass(reqData)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                responseLiveData.postValue(apiResponse!!.loading())
            }
            .subscribe(
                { result ->
                    responseLiveData.postValue(apiResponse!!.success(result))
                },
                { throwable ->
                    responseLiveData.postValue(apiResponse!!.error(throwable))
                }
            )

    }

    fun disposeSubscriber() {
        if (subscription != null)
            subscription!!.dispose()
    }

    /*Validations*/
    fun isValidFormData(mActivity: AppCompatActivity, email: String): Boolean {

        if (TextUtils.isEmpty(email)) {
            UtilityMethod.showSnackBarMsgError(
                mActivity,
                mActivity.getString(R.string.enter_email)
            )
            return false
        }

        if (!UtilityMethod.isValidEmail(email)) {
            UtilityMethod.showSnackBarMsgError(
                mActivity,
                mActivity.getString(R.string.enter_valid_email_id)
            )
            return false
        }
        return true
    }
}