package com.carealls.ui.base

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.carealls.utils.SessionManager

abstract class BaseFragment : Fragment(), View.OnClickListener {

    protected var sessionManager: SessionManager? = null
    protected var mActivity: Activity? = null


    protected abstract fun setBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): ViewDataBinding

    protected abstract fun createActivityObject()

    protected abstract fun initializeObject()

    protected abstract fun initializeOnCreateObject()

    protected abstract fun setListeners()

    override fun onResume() {
        super.onResume()
        if (mActivity == null) throw NullPointerException("must create activity object")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        sessionManager = SessionManager.getInstance(activity)
        var binding = setBinding(inflater, container)
        val view = binding.root
        createActivityObject()
        initializeObject()
        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeOnCreateObject()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()
    }
}