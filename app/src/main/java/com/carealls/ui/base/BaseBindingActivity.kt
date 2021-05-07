package com.carealls.ui.base

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.carealls.R
import com.carealls.utils.Env
import com.carealls.utils.SessionManager


abstract class BaseBindingActivity : AppCompatActivity(), ToolbarCallback, View.OnClickListener {

    protected var mActivity: AppCompatActivity? = null

    protected var sessionManager: SessionManager? = null

    protected var fragment: Fragment? = null

    protected abstract fun setBinding()

    protected abstract fun createActivityObject(savedInstanceState: Bundle?)

    protected abstract fun initializeObject()

    protected abstract fun setListeners()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sessionManager = SessionManager.getInstance(this)
        createActivityObject(savedInstanceState)
        setBinding()
        initializeObject()
        setListeners()
    }

    override fun setToolbarCustomTitle(titleKey: String) {

    }

    override fun showUpIconVisibility(isVisible: Boolean) {
        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(isVisible)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        if (mActivity == null) throw NullPointerException("must create activity object")
        Env.currentActivity = mActivity
    }

    fun changeFragment(fragment: Fragment, isAddToBack: Boolean, bundle: Bundle?) {
        this.fragment = fragment
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment.javaClass, bundle, fragment.javaClass.name)
        if (isAddToBack) transaction.addToBackStack(fragment.javaClass.name)
        transaction.commit()
    }

    fun changeFragment(fragment: Fragment, isAddToBack: Boolean) {
        this.fragment = fragment
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment, fragment.javaClass.name)
        if (isAddToBack) transaction.addToBackStack(fragment.javaClass.name)
        transaction.commit()
    }

    open fun replaceFragment(fragment: Fragment, bundle: Bundle?) {
        val backStateName = fragment.javaClass.name
        val manager = this.supportFragmentManager
        val fragmentPopped = manager.popBackStackImmediate(backStateName, 0)
        if (!fragmentPopped && manager.findFragmentByTag(backStateName) == null) {
            //fragment not in back stack, create it.
            val ft = manager.beginTransaction()
            fragment.arguments = bundle
            ft.replace(R.id.container, fragment, backStateName)
            ft.addToBackStack(backStateName)
            ft.commit()
        }
    }

    fun replaceCurrentFragment(
        fragment: Fragment,
        isAddToBack: Boolean,
        bundle: Bundle?,
        frameLayoutRecent: Int
    ) {
        this.fragment = fragment
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(frameLayoutRecent, fragment.javaClass, bundle, fragment.javaClass.name)
        if (isAddToBack) transaction.addToBackStack(fragment.javaClass.name)
        transaction.commit()
    }


}