package com.carealls.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.location.Geocoder
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.preference.PreferenceManager
import android.text.ClipboardManager
import android.text.SpannableString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.carealls.R
import com.carealls.databinding.DialogNoInternetConnectedBinding
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.*
import java.lang.reflect.Type
import java.net.URLEncoder
import java.nio.charset.Charset
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern
import kotlin.math.log10
import kotlin.math.pow


object UtilityMethod {

    @SuppressLint("MissingPermission")
    fun hasInternet(context: Context): Boolean {
        try {
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = connectivityManager.activeNetworkInfo
            return networkInfo != null && networkInfo.isConnectedOrConnecting
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }


    fun showToastMessageDefault(activity: Activity, message: String) {
        Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
    }

    fun showToastMessageSuccess(activity: Activity, message: String) {
        val toast = Toast(activity)
        toast.duration = Toast.LENGTH_LONG;
        val toastView = LayoutInflater.from(activity).inflate(R.layout.custom_toast, null)
        toastView.background = ContextCompat.getDrawable(activity, R.drawable.bg_round_green)

        val txtMessage = toastView.findViewById<TextView>(R.id.txtMessage)
        txtMessage.text = message

        toast.view = toastView
        toast.show()
    }

    fun showToastMessageError(activity: Activity, message: String) {
        val toast = Toast(activity)
        toast.duration = Toast.LENGTH_LONG
        val toastView = LayoutInflater.from(activity).inflate(R.layout.custom_toast, null)
        toastView.background = ContextCompat.getDrawable(activity, R.drawable.bg_round_red)

        val txtMessage = toastView.findViewById<TextView>(R.id.txtMessage)
        txtMessage.text = message

        toast.view = toastView
        toast.show()
    }

    fun showSnackBarMsgError(activity: Activity, message: String?) {
        val parentLayout = activity.findViewById<View>(android.R.id.content)
        Snackbar.make(parentLayout, message!!, Snackbar.LENGTH_LONG)
            .setAction("CLOSE") {
                //                        finish();
            }
            .setActionTextColor(activity.resources.getColor(R.color.color_0057FF))
            .show()
    }

    @JvmStatic
    fun loadImage(view: ImageView?, imageUrl: String) {
        Glide.with(view!!.context)
            .load(imageUrl)
            .placeholder(R.color.color_view)
            .error(ContextCompat.getDrawable(view.context, R.color.color_view))
            .into(view)
    }

    @JvmStatic
    fun loadProfile(view: ImageView?, imageUrl: String) {
        Glide.with(view!!.context)
            .load(imageUrl)
            .placeholder(R.mipmap.ic_launcher)
            .error(ContextCompat.getDrawable(view.context, R.mipmap.ic_launcher))
            .into(view)
    }

    fun hideKeyboard(activity: Activity) {
        val imm: InputMethodManager =
            activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        var view = activity.currentFocus
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun showKeyboard(activity: Activity) {
        val imm: InputMethodManager =
            activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        var view = activity.currentFocus
        if (view == null) {
            view = View(activity)
        }
        imm.toggleSoftInputFromWindow(view.windowToken, InputMethodManager.SHOW_FORCED, 0)
    }


    fun saveArrayList(activity: Context, list: ArrayList<String>, key: String?) {
        val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity)
        val editor: SharedPreferences.Editor = prefs.edit()
        val gson = Gson()
        val json: String = gson.toJson(list)
        editor.putString(key, json)
        editor.apply()
    }

    fun getArrayList(activity: Context, key: String?): ArrayList<String> {
        val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity)
        val gson = Gson()
        val json: String? = prefs.getString(key, null)
        val type: Type = object : TypeToken<ArrayList<String?>?>() {}.getType()
        return gson.fromJson(json, type)
    }

    @SuppressLint("SimpleDateFormat")
    fun getFormatChangeDate(ourDate: String?): String? {
        var ourDate = ourDate
        try {
            //SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            //formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
            val value = formatter.parse(ourDate)
            val dateFormatter =
                SimpleDateFormat("dd/MM/yyyy") //this format changeable
            dateFormatter.timeZone = TimeZone.getDefault() //set local time zone
            ourDate = dateFormatter.format(value)
        } catch (e: java.lang.Exception) {
            ourDate = ""
        }
        return ourDate
    }

    @SuppressLint("SimpleDateFormat")
    fun getFormatChangeDate1(date: String?): String? {
        var ourDate = date
        try {
            //SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
            //formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
            val value = formatter.parse(ourDate)
            val dateFormatter =
                SimpleDateFormat("dd/MM/yyyy") //this format changeable
            dateFormatter.timeZone = TimeZone.getDefault() //set local time zone
            ourDate = dateFormatter.format(value)
        } catch (e: java.lang.Exception) {
            ourDate = ""
        }
        return ourDate
    }

    fun getCurrentDateTime(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.getDefault())
        val currentDateandTime = sdf.format(Date())
        return currentDateandTime
    }


    fun dateConvertAgo(date: String?): String {
        var newTime = ""
        newTime = try {
            @SuppressLint("SimpleDateFormat")
            val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
            format.timeZone = TimeZone.getDefault()

            val past = format.parse(date)
            val now = Date()
            val seconds =
                TimeUnit.MILLISECONDS.toSeconds(now.time - past.time)
            val minutes =
                TimeUnit.MILLISECONDS.toMinutes(now.time - past.time)
            val hours =
                TimeUnit.MILLISECONDS.toHours(now.time - past.time)
            val days =
                TimeUnit.MILLISECONDS.toDays(now.time - past.time)
            if (seconds < 60) {
                println("$seconds seconds ago")
                "$seconds seconds ago"
            } else if (minutes < 60) {
                println("$minutes minutes ago")
                "$minutes minutes ago"
            } else if (hours < 24) {
                println("$hours hours ago")
                "$hours hours ago"
            } else {
                println("$days days ago")
                "$days days ago"
            }
        } catch (j: java.lang.Exception) {
            j.printStackTrace()
            ""
        }
        return newTime
    }


    fun loadJSONFromAsset(activity: Activity, fileName: String): String? {
        var json: String? = null
        json = try {
            val `is`: InputStream = activity.getAssets().open(fileName)
            val size: Int = `is`.available()
            val buffer = ByteArray(size)
            `is`.read(buffer)
            `is`.close()
            String(buffer, Charset.defaultCharset())
        } catch (ex: IOException) {
            ex.printStackTrace()
            return null
        }
        return json
    }

    fun isDeviceOnline(context: Context): Boolean {
        var isDeviceOnLine = false
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (cm != null) {
            val netInfo = cm.activeNetworkInfo
            isDeviceOnLine = netInfo != null && netInfo.isConnected
        }
        return isDeviceOnLine
    }

    fun setAppBar(mActivity: Activity) {
        val toolbar: Toolbar = mActivity.findViewById(R.id.toolbar)
        toolbar.visibility = View.GONE
        val drawerLayout: DrawerLayout = mActivity.findViewById(R.id.drawer_layout)
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
    }

    fun isValidEmail(target: CharSequence): Boolean {
        return Pattern.compile(
            "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                    + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                    + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                    + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                    + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                    + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$"
        ).matcher(target).matches();
    }

    fun isOnline(context: Context): Boolean {
        val connectivityManager =
            (context as Activity).getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = connectivityManager.activeNetworkInfo
        val isConnected = netInfo != null && netInfo.isConnected
        return isConnected
    }

    fun showNoInternetConnectedDialog(mActivity: Activity) {
        val dialog = Dialog(mActivity, R.style.Theme_Dialog)
        val dialogBinding: DialogNoInternetConnectedBinding = DataBindingUtil.inflate(
            LayoutInflater.from(mActivity),
            R.layout.dialog_no_internet_connected,
            null,
            false
        )
        dialogBinding.tvTitle.text = mActivity.getText(R.string.internet_issue)
        dialogBinding.tvMessage.text = mActivity.getText(R.string.please_check_your_internet)
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

        dialogBinding.btnOK.setOnClickListener(View.OnClickListener {
            dialog.dismiss()
        })
        dialog.show()
    }


    fun multiTextClicked(
        textView: TextView,
        all: String,
        arr_text: Array<String>,
        arr_color: Array<String?>,
        arr_ratio: FloatArray,
        arr_isClick: BooleanArray,
        arr_isUnderLine: BooleanArray,
        textClickListener: TextClickListener
    ) {

        // initialize constructor with all string
        val spannableString = SpannableString(all)
        for (pos in arr_text.indices) {
            // to change ratio of text in percentage
            spannableString.setSpan(
                RelativeSizeSpan(arr_ratio[pos]),
                all.indexOf(arr_text[pos]),
                all.indexOf(arr_text[pos]) + arr_text[pos].length,
                0
            )
            // to change color of text
            spannableString.setSpan(
                ForegroundColorSpan(
                    Color.parseColor(
                        arr_color[pos]
                    )
                ),
                all.indexOf(arr_text[pos]),
                all.indexOf(arr_text[pos]) + arr_text[pos].length,
                0
            )
            if (arr_isClick[pos]) {
                // click specific string of text view
                spannableString.setSpan(
                    ClickSpanText(
                        arr_text[pos],
                        all.indexOf(arr_text[pos]),
                        arr_color[pos],
                        arr_isUnderLine[pos],
                        textClickListener
                    ),
                    all.indexOf(arr_text[pos]),
                    all.indexOf(arr_text[pos]) + arr_text[pos].length,
                    0
                )
            }
        }
        textView.text = spannableString
        textView.movementMethod = LinkMovementMethod.getInstance()
    }

    private class ClickSpanText(
        clickString: String,
        private val clickPosition: Int,
        private val color: String?,
        isUnderLine: Boolean,
        mTextClickListener: TextClickListener
    ) :
        ClickableSpan() {
        private val clickString: String
        private val mTextClickListener: TextClickListener
        private val isUnderLine: Boolean

        override fun updateDrawState(ds: TextPaint) {
            super.updateDrawState(ds)
            ds.isUnderlineText = isUnderLine
            ds.color = Color.parseColor(color)
        }

        override fun onClick(view: View) {
            mTextClickListener.getClickedString(clickString)
        }

        init {
            this.mTextClickListener = mTextClickListener
            this.clickString = clickString
            this.isUnderLine = isUnderLine
        }
    }

    fun audioCall(activity: Activity, number: String) {
        activity.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:$number")))
    }
    fun sendEmail(activity: Activity, email: String) {
        val intent = Intent(Intent.ACTION_SEND)
        val recipients = arrayOf(email)
        intent.putExtra(Intent.EXTRA_EMAIL, recipients)
        intent.type = "text/html"
        intent.setPackage("com.google.android.gm")
        activity.startActivity(Intent.createChooser(intent, "Send mail"))
    }

    fun decodeUriToBitmap(mContext: Context, sendUri: Uri?): Bitmap? {
        var getBitmap: Bitmap? = null
        try {
            val image_stream: InputStream?
            try {
                image_stream = mContext.contentResolver.openInputStream(sendUri!!)
                getBitmap = BitmapFactory.decodeStream(image_stream)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return getBitmap
    }

    @Throws(IOException::class)
    fun copyStream(input: InputStream, output: OutputStream) {
        val buffer = ByteArray(1024)
        var bytesRead: Int
        while (input.read(buffer).also { bytesRead = it } != -1) {
            output.write(buffer, 0, bytesRead)
        }
    }

    fun createAppDir(mActivity: Activity, mFileTemp: File?, TEMP_PHOTO_FILE_NAME: String?): File? {
        var mFileTemp = mFileTemp
        val root = Environment.getExternalStorageDirectory().toString()
        File(root + "/" + mActivity.getString(R.string.app_name) + "/temp").mkdirs()
        val state = Environment.getExternalStorageState()
        mFileTemp = if (Environment.MEDIA_MOUNTED == state) {
            File(
                root + "/" + mActivity.getString(R.string.app_name) + "/temp/",
                TEMP_PHOTO_FILE_NAME
            )
        } else {
            File(mActivity.filesDir, TEMP_PHOTO_FILE_NAME)
        }
        return mFileTemp
    }

    fun toTitleCase(str: String?): String? {
        if (str == null) {
            return null
        }
        var space = true
        val builder = StringBuilder(str)
        val len = builder.length
        for (i in 0 until len) {
            val c = builder[i]
            if (space) {
                if (!Character.isWhitespace(c)) {
                    // Convert to title case and switch out of whitespace mode.
                    builder.setCharAt(i, Character.toTitleCase(c))
                    space = false
                }
            } else if (Character.isWhitespace(c)) {
                space = true
            } else {
                builder.setCharAt(i, Character.toLowerCase(c))
            }
        }
        return builder.toString()
    }

    //get complete address in string
    fun getCompleteAddressString(context: Context?, LATITUDE: Double, LONGITUDE: Double): String? {
        var strAdd = ""
        val geocoder = Geocoder(context, Locale.getDefault())
        try {
            val addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1)
            if (addresses != null) {
                val returnedAddress = addresses[0]
                val strReturnedAddress = StringBuilder("")
                for (i in 0..returnedAddress.maxAddressLineIndex) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n")
                }
                strAdd = strReturnedAddress.toString()
                Log.w("Location", strReturnedAddress.toString())
            } else {
                Log.w("Location", "No Address returned!")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.w("Location", "Canont get Address!")
        }
        return strAdd
    }

     fun shareAppOnWhatsApp(mActivity: Activity, mobile: String?, msg: String?) {
         val packageManager = mActivity!!.packageManager
        val i = Intent(Intent.ACTION_VIEW)
        try {
            val url = "https://api.whatsapp.com/send?phone=" + mobile + "&text=" + URLEncoder.encode(msg, "UTF-8")+"\n"
            i.setPackage("com.whatsapp")
            i.data = Uri.parse(url)
            if (i.resolveActivity(packageManager) != null) {
                mActivity!!.startActivity(i)
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

    }

    fun shareApp(mActivity: Activity, msg: String?) {
        try {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "CareAlls")
            var shareMessage = "\nCareAlls App:\n"
            shareMessage = shareMessage + msg+"\n"
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
            startActivity(mActivity,Intent.createChooser(shareIntent, "choose one"),null)
        } catch (e: java.lang.Exception) {
            //e.toString();
        }
    }

    @SuppressLint("ObsoleteSdkInt", "ShowToast")
    public fun copyTextClipboard(context: Context, text: String) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            clipboard.text = text
        } else {
            val clipboard =
                context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
            val clip = ClipData.newPlainText("Copied Text", text)
            clipboard.setPrimaryClip(clip)
        }
        Toast.makeText(context, "Copied Text", Toast.LENGTH_SHORT).show()
    }

    fun getChangeDate(ourDate: String?): String? {
        var ourDate = ourDate
        try {
            val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            //formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
            val value = formatter.parse(ourDate)
            val dateFormatter = SimpleDateFormat("dd/MM/yyyy") //this format changeable
            dateFormatter.timeZone = TimeZone.getDefault() //set local time zone
            ourDate = dateFormatter.format(value)
        } catch (e: java.lang.Exception) {
            ourDate = ""
        }
        return ourDate
    }

    fun getCurrentDate(): String? {
        val c = Calendar.getInstance().time
        val df = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val formattedDate = df.format(c)
        return formattedDate
    }

     fun getReadableFileSize(size: Long): String {
        if (size <= 0) {
            return "0"
        }
        val units = arrayOf("B", "KB", "MB", "GB", "TB")
        val digitGroups = (log10(size.toDouble()) / log10(1024.0)).toInt()
        return DecimalFormat("#,##0.#").format(size / 1024.0.pow(digitGroups.toDouble())) + " " + units[digitGroups]
    }

}