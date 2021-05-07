package  com.carealls.ui.components.activites.home

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import com.carealls.BuildConfig
import com.carealls.R
import com.carealls.databinding.ActivityHomeBinding
import com.carealls.databinding.DialogLogoutBinding
import com.carealls.databinding.DialogNoInternetConnectedBinding
import com.carealls.pojo.DrawerData
import com.carealls.ui.base.BaseBindingActivity
import com.carealls.ui.components.activites.login.LoginActivity
import com.carealls.ui.components.adapters.DrawerAdapter
import com.carealls.ui.components.fragments.home.HomeFragment
import com.carealls.ui.components.fragments.listingDetail.ListingDetailFragment
import com.carealls.ui.components.fragments.manageListing.ManageListingFragment
import com.carealls.ui.components.fragments.productDetail.ProductDetailFragment
import com.carealls.ui.components.fragments.profile.ProfileFragment
import com.carealls.ui.components.fragments.support.SupportFragment
import com.carealls.ui.components.fragments.viewAll.ViewAllFragment
import com.carealls.utils.SessionManager
import com.carealls.utils.SingleShotLocationProvider
import com.carealls.utils.SingleShotLocationProvider.requestSingleUpdate
import com.carealls.utils.UtilityMethod
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import kotlinx.android.synthetic.main.app_bar.*
import java.lang.String
import java.util.*


class HomeActivity : BaseBindingActivity() {
    private var binding: ActivityHomeBinding? = null
    private var onClickListener: View.OnClickListener? = null
    private var viewModel: HomeViewModel? = null

    private var list: ArrayList<DrawerData>? = null
    private var adapter: DrawerAdapter? = null


    companion object {
        private val TAG = HomeActivity::class.qualifiedName
        const val REQUEST_CHECK_SETTINGS = 123
        var drawerLayout: DrawerLayout? = null

        fun startActivity(activity: Activity, bundle: Bundle?, isClear: Boolean) {
            val intent = Intent(activity, HomeActivity::class.java)
            if (bundle != null) intent.putExtra("bundle", bundle)
            if (isClear) intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            activity.startActivity(intent)
        }
    }

    override fun setBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home)
        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        onClickListener = this
        binding!!.lifecycleOwner = this
    }

    override fun createActivityObject(savedInstanceState: Bundle?) {
        mActivity = this
        drawerLayout = findViewById(R.id.drawer_layout)
    }

    override fun initializeObject() {
        setActionBar()

        replaceFragment(HomeFragment(), null)

        setDrawerAdapter()
        setUserData()

        val uri: Uri? = intent.data
        if (uri != null) {
            if(uri.getQueryParameter("type").equals("LDF"))
            {
                var bundle = Bundle()
                bundle.putString("listId", uri.getQueryParameter("lId"))
                (mActivity as HomeActivity).changeFragment(ListingDetailFragment(), true, bundle)
            }
           else if(uri.getQueryParameter("type").equals("PDF"))
            {
                var bundle = Bundle()
                bundle.putString("productId", uri.getQueryParameter("pId"))
                (mActivity as HomeActivity).changeFragment(ProductDetailFragment(), true, bundle)
            }

            Log.e(TAG,"id -"+uri.getQueryParameter("id")+"pass - "+uri.getQueryParameter("pass"))
        }
    }


    override fun setListeners() {
        binding!!.layoutMain.appBar.ivHeaderProfile.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view!!.id) {
            R.id.ll_main -> {
                val position: Int = view.tag as Int
                adapter!!.selectedPos = position
                adapter!!.notifyDataSetChanged()

                when (position) {
                    0 -> replaceFragment(HomeFragment(), null)
                    1 -> {
                        val bundle = Bundle()
                        bundle.putString("type", "All Categories")
                        replaceFragment(ViewAllFragment(), bundle)
                    }
                    2 -> {
                        val bundle = Bundle()
                        bundle.putString("type", "All Listing")
                        replaceFragment(ViewAllFragment(), bundle)
                    }
                    3 -> replaceFragment(ProfileFragment(), null)
                    4 -> replaceFragment(ManageListingFragment(), null)
                    5 -> replaceFragment(SupportFragment(), null)
                    6 -> shareApp()
                    7 -> logOutDialog()
                }
                binding!!.drawerLayout.closeDrawer(GravityCompat.START)
            }

            R.id.imvClose -> {
                binding!!.drawerLayout.closeDrawer(GravityCompat.START)
            }
            R.id.ivHeaderProfile -> {
                replaceFragment(ProfileFragment(), null)
            }
        }
    }

    private fun shareApp() {
        try {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "CareAlls")
            var shareMessage = "\nInstall CareAlls App:\n"
            shareMessage = shareMessage + "https://play.google.com/store/apps/details?id="+BuildConfig.APPLICATION_ID+"\n\n"
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
            startActivity(Intent.createChooser(shareIntent, "choose one"))
        } catch (e: java.lang.Exception) {
            //e.toString();
        }
    }

    private fun setUserData() {
        UtilityMethod.loadProfile(binding!!.navHeader.ivNavProfile, sessionManager!!.profilE_PIC)
        UtilityMethod.loadProfile(
            binding!!.layoutMain.appBar.ivHeaderProfile,
            sessionManager!!.profilE_PIC
        )
        binding!!.navHeader.txtuserName.setText(sessionManager!!.name)
        binding!!.navHeader.tvEmail.setText(sessionManager!!.email)
    }

    private fun logOutDialog() {
        val dialog = Dialog(this, R.style.Theme_Dialog)
        val dialogBinding: DialogLogoutBinding = DataBindingUtil.inflate(
            LayoutInflater.from(mActivity),
            R.layout.dialog_logout,
            null,
            false
        )
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.window!!.setGravity(Gravity.CENTER)
        dialog.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCanceledOnTouchOutside(false)
        dialog.setCancelable(false)
        dialog.setContentView(dialogBinding.getRoot())


        dialogBinding.tvTitle.setText("Logout")
        dialogBinding.tvMessage.setText("Are you sure, you want to logout?")
        dialogBinding.btnNo.setOnClickListener(View.OnClickListener { dialog.dismiss() })
        dialogBinding.btnYes.setOnClickListener(View.OnClickListener {
            dialog.dismiss()
            sessionManager!!.logout()
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
            val googleSignInClient = GoogleSignIn.getClient(mActivity!!, gso)
            googleSignInClient.signOut() //gmail logout

            LoginActivity.startActivity(mActivity!!, null, true)
            finish()
        })

        dialog.show()
    }

    private fun setActionBar() {

        val toggle = object : ActionBarDrawerToggle(
            this,
            binding!!.drawerLayout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        ) {
            override fun onDrawerOpened(drawerView: View) {
                super.onDrawerOpened(drawerView)
                UtilityMethod.hideKeyboard(mActivity!!)
            }

            override fun onDrawerClosed(drawerView: View) {
                super.onDrawerClosed(drawerView)
                UtilityMethod.hideKeyboard(mActivity!!)
            }
        }

        toggle.isDrawerIndicatorEnabled = false
        binding!!.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        toggle.setHomeAsUpIndicator(R.drawable.ic_menu)

        toggle.setToolbarNavigationClickListener {
            if (binding!!.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                binding!!.drawerLayout.closeDrawer(GravityCompat.START)
            } else binding!!.drawerLayout.openDrawer(GravityCompat.START)
        }
    }

    private fun setDrawerAdapter() {

        list = ArrayList()
        for ((index, drawerName) in resources.getStringArray(R.array.drawerNames).withIndex()) {
            list!!.add(
                DrawerData(index, drawerName)
            )
        }

        adapter = DrawerAdapter(mActivity, list!!, onClickListener)
        binding!!.recyclerView.adapter = adapter
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == HomeActivity.REQUEST_CHECK_SETTINGS) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                try {
                    requestSingleUpdate(mActivity, object :
                        SingleShotLocationProvider.LocationCallback {
                        override fun onNewLocationAvailable(location: SingleShotLocationProvider.GPSCoordinates) {
                            Log.e(
                                TAG,
                                "my location is - " + location.latitude.toString() + " " + location.longitude
                            )
                            if (!SessionManager.getInstance(mActivity).isSELECT_LOCATION()) {
                                sessionManager!!.setLOCATION(
                                    UtilityMethod.getCompleteAddressString(
                                        mActivity,
                                        location.latitude.toDouble(),
                                        location.longitude.toDouble()
                                    )
                                )
                                sessionManager!!.setLATITUDE(String.valueOf(location.latitude))
                                sessionManager!!.setLONGITUDE(String.valueOf(location.longitude))
                                binding!!.layoutMain.appBar.txtAddress.setText(sessionManager!!.getLOCATION())
                                binding!!.layoutMain.appBar.txtAddress.setSelected(true)

                            }
                            Log.e(TAG, "location - " + sessionManager!!.getLOCATION())
                        }
                    })
                } catch (e: Exception) {
                    Log.e(TAG, "error - " + e.message)
                }
            } else {
                //User clicks No
                buildAlertMessageNoGps()
            }
        }
    }

    private fun buildAlertMessageNoGps() {
        val dialog = Dialog(this, R.style.Theme_Dialog)
        val dialogBinding: DialogNoInternetConnectedBinding = DataBindingUtil.inflate(
            LayoutInflater.from(mActivity),
            R.layout.dialog_no_internet_connected,
            null,
            false
        )
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.window!!.setGravity(Gravity.CENTER)
        dialog.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCanceledOnTouchOutside(false)
        dialog.setCancelable(false)
        dialog.setContentView(dialogBinding.getRoot())
        dialogBinding.tvTitle.setText("GPS Settings")
        dialogBinding.tvMessage.setText("GPS is not enabled. Please goto settings page to enable")
        dialogBinding.btnOK.setText("Settings")
        dialogBinding.btnOK.setOnClickListener(View.OnClickListener {
            dialog.dismiss()
            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            finish()
        })
        dialog.show()
    }

    override fun onBackPressed() {
        if (binding!!.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding!!.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            if (supportFragmentManager.backStackEntryCount == 1) {
                finish()
            } else {
                super.onBackPressed()
            }
        }
    }

    override fun onDestroy() {
        // viewModel!!.disposeSubscriber()
        super.onDestroy()
    }


}





