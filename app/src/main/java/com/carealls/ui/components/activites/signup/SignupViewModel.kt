package com.carealls.ui.components.activites.signup

import android.text.TextUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.carealls.R
import com.carealls.network.ApiResponse
import com.carealls.network.RestApi
import com.carealls.network.RestApiFactory
import com.carealls.pojo.SignupData
import com.carealls.utils.UtilityMethod
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class SignupViewModel : ViewModel() {
    var responseLiveData = MutableLiveData<ApiResponse<SignupData>>()
    var apiResponse: ApiResponse<SignupData>? = null


    private var restApi: RestApi? = null
    private var subscription: Disposable? = null

    init {
        restApi = RestApiFactory.create()
        apiResponse = ApiResponse(ApiResponse.Status.LOADING, null, null)
    }

    fun signupApi(reqData: HashMap<String, String>) {
        subscription = restApi!!.signup(reqData)
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
    fun isValidFormData(
        mActivity: AppCompatActivity,
        fullName: String,
        email: String,
        mobileNumber: String,
        password: String,
        confirmPassword: String
    ): Boolean {

        if (TextUtils.isEmpty(fullName)) {
            UtilityMethod.showSnackBarMsgError(
                mActivity,
                mActivity.getString(R.string.enter_full_name)
            )
            return false
        }

        if (fullName.length < 3) {
            UtilityMethod.showSnackBarMsgError(
                mActivity,
                mActivity.getString(R.string.minimum_3_char_long)
            )
            return false
        }

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

        if (TextUtils.isEmpty(mobileNumber)) {
            UtilityMethod.showSnackBarMsgError(mActivity, mActivity.getString(R.string.enter_valid_mobile_number))
            return false
        }
        if (mobileNumber.length < 9) {
            UtilityMethod.showSnackBarMsgError(mActivity, mActivity.getString(R.string.enter_valid_mobile_number))
            return false
        }

        if (TextUtils.isEmpty(password)) {
            UtilityMethod.showSnackBarMsgError(
                mActivity,
                mActivity.getString(R.string.enter_password)
            )
            return false
        }
        if (password.length < 6) {
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