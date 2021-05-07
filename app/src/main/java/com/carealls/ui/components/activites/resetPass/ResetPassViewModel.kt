package com.carealls.ui.components.activites.resetPass

import android.text.TextUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.carealls.R
import com.carealls.network.ApiResponse
import com.carealls.network.RestApi
import com.carealls.network.RestApiFactory
import com.carealls.pojo.ForgotPassData
import com.carealls.pojo.ResetPassData
import com.carealls.utils.UtilityMethod
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class ResetPassViewModel : ViewModel() {
    var responseLiveResendOtpData = MutableLiveData<ApiResponse<ForgotPassData>>()
    var apiResendOtpResponse: ApiResponse<ForgotPassData>? = null

    var responseLiveResetPassData = MutableLiveData<ApiResponse<ResetPassData>>()
    var apiResetPassResponse: ApiResponse<ResetPassData>? = null


    private var restApi: RestApi? = null
    private var subscription: Disposable? = null

    init {
        restApi = RestApiFactory.create()
        apiResendOtpResponse = ApiResponse(ApiResponse.Status.LOADING, null, null)
        apiResetPassResponse = ApiResponse(ApiResponse.Status.LOADING, null, null)
    }

    fun forgotPassApi(reqData: HashMap<String, String>) {
        subscription = restApi!!.forgotPass(reqData)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                responseLiveResendOtpData.postValue(apiResendOtpResponse!!.loading())
            }
            .subscribe(
                { result ->
                    responseLiveResendOtpData.postValue(apiResendOtpResponse!!.success(result))
                },
                { throwable ->
                    responseLiveResendOtpData.postValue(apiResendOtpResponse!!.error(throwable))
                }
            )
    }
    fun resetPassApi(reqData: HashMap<String, String>) {
        subscription = restApi!!.resetPass(reqData)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                responseLiveResetPassData.postValue(apiResetPassResponse!!.loading())
            }
            .subscribe(
                { result ->
                    responseLiveResetPassData.postValue(apiResetPassResponse!!.success(result))
                },
                { throwable ->
                    responseLiveResetPassData.postValue(apiResetPassResponse!!.error(throwable))
                }
            )
    }

    fun disposeSubscriber() {
        if (subscription != null)
            subscription!!.dispose()
    }

    /*Validations*/
    fun isValidFormData(mActivity: AppCompatActivity, email: String?): Boolean {

        if (TextUtils.isEmpty(email)) {
            UtilityMethod.showSnackBarMsgError(
                mActivity,
                mActivity.getString(R.string.enter_email)
            )
            return false
        }

        if (!UtilityMethod.isValidEmail(email!!)) {
            UtilityMethod.showSnackBarMsgError(
                mActivity,
                mActivity.getString(R.string.enter_valid_email_id)
            )
            return false
        }
        return true
    }

    fun isValidFormResetPassData(mActivity: AppCompatActivity, otp: String?, password: String?, confirmPassword: String?): Boolean {

        if (TextUtils.isEmpty(otp)) {
            UtilityMethod.showSnackBarMsgError(
                mActivity,
                mActivity.getString(R.string.enter_otp)
            )
            return false
        }

        if (TextUtils.isEmpty(password)) {
            UtilityMethod.showSnackBarMsgError(
                mActivity,
                mActivity.getString(R.string.enter_password)
            )
            return false
        }
        if (password!!.length < 6) {
            UtilityMethod.showSnackBarMsgError(
                mActivity,
                mActivity.getString(R.string.minimum_6_characters)
            )
            return false
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            UtilityMethod.showSnackBarMsgError(
                mActivity,
                mActivity.getString(R.string.enter_cpassword)
            )
            return false
        }
        if (password != confirmPassword) {
            UtilityMethod.showSnackBarMsgError(
                mActivity,
                mActivity.getString(R.string.password_not_match)
            )
            return false
        }
        return true
    }
}