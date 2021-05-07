package com.carealls.utils

import android.app.Activity
import android.content.ContentUris
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.text.TextUtils
import androidx.annotation.RequiresApi
import java.io.File
import java.util.*
import java.util.regex.Pattern

object ImagePathUtility {
    private var bitmap: Bitmap? = null

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    fun getImagePath(activity: Activity, uri: Uri): String? {
        var strImagePath: String? = null
        var isImageFromGoogleDrive = false
        val isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
        if (isKitKat && DocumentsContract.isDocumentUri(activity, uri)) {
            if ("com.android.externalstorage.documents" == uri.authority) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).toTypedArray()
                val type = split[0]
                if ("primary".equals(type, ignoreCase = true)) {
                    strImagePath =
                        Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                } else {
                    val DIR_SEPORATOR = Pattern.compile("/")
                    val rv: MutableSet<String> = HashSet()
                    val rawExternalStorage = System.getenv("EXTERNAL_STORAGE")
                    val rawSecondaryStoragesStr = System.getenv("SECONDARY_STORAGE")
                    val rawEmulatedStorageTarget = System.getenv("EMULATED_STORAGE_TARGET")
                    if (TextUtils.isEmpty(rawEmulatedStorageTarget)) {
                        if (TextUtils.isEmpty(rawExternalStorage)) {
                            rv.add("/storage/sdcard0")
                        } else {
                            rv.add(rawExternalStorage)
                        }
                    } else {
                        val rawUserId: String
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
                            rawUserId = ""
                        } else {
                            val path = Environment.getExternalStorageDirectory().absolutePath
                            val folders = DIR_SEPORATOR.split(path)
                            val lastFolder = folders[folders.size - 1]
                            var isDigit = false
                            try {
                                Integer.valueOf(lastFolder)
                                isDigit = true
                            } catch (ignored: NumberFormatException) {
                            }
                            rawUserId = if (isDigit) lastFolder else ""
                        }
                        if (TextUtils.isEmpty(rawUserId)) {
                            rv.add(rawEmulatedStorageTarget)
                        } else {
                            rv.add(rawEmulatedStorageTarget + File.separator + rawUserId)
                        }
                    }
                    if (!TextUtils.isEmpty(rawSecondaryStoragesStr)) {
                        val rawSecondaryStorages =
                            rawSecondaryStoragesStr.split(File.pathSeparator.toRegex())
                                .toTypedArray()
                        Collections.addAll(rv, *rawSecondaryStorages)
                    }
                    val temp = rv.toTypedArray()
                    for (i in temp.indices) {
                        val tempf = File(temp[i] + "/" + split[1])
                        if (tempf.exists()) {
                            strImagePath = temp[i] + "/" + split[1]
                        }
                    }
                }
            } else if ("com.android.providers.downloads.documents" == uri.authority) {
                val id = DocumentsContract.getDocumentId(uri)
                val contentUri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(id)
                )
                var cursor: Cursor? = null
                val column = "_data"
                val projection = arrayOf(column)
                try {
                    cursor = activity.contentResolver.query(
                        contentUri, projection, null, null,
                        null
                    )
                    if (cursor != null && cursor.moveToFirst()) {
                        val column_index = cursor.getColumnIndexOrThrow(column)
                        strImagePath = cursor.getString(column_index)
                    }
                } finally {
                    cursor?.close()
                }
            } else if ("com.android.providers.media.documents" == uri.authority) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).toTypedArray()
                val type = split[0]
                var contentUri: Uri? = null
                if ("image" == type) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                } else if ("video" == type) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                } else if ("audio" == type) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }
                val selection = "_id=?"
                val selectionArgs = arrayOf(split[1])
                var cursor: Cursor? = null
                val column = "_data"
                val projection = arrayOf(column)
                try {
                    cursor = activity.contentResolver.query(
                        contentUri!!, projection, selection, selectionArgs,
                        null
                    )
                    if (cursor != null && cursor.moveToFirst()) {
                        val column_index = cursor.getColumnIndexOrThrow(column)
                        strImagePath = cursor.getString(column_index)
                    }
                } finally {
                    cursor?.close()
                }
            } else if ("com.google.android.apps.docs.storage" == uri.authority) {
                isImageFromGoogleDrive = true
            }
        } else if ("content".equals(uri.scheme, ignoreCase = true)) {
            var cursor: Cursor? = null
            val column = "_data"
            val projection = arrayOf(column)
            try {
                cursor = activity.contentResolver.query(uri, projection, null, null, null)
                if (cursor != null && cursor.moveToFirst()) {
                    val column_index = cursor.getColumnIndexOrThrow(column)
                    strImagePath = cursor.getString(column_index)
                }
            } finally {
                cursor?.close()
            }
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            strImagePath = uri.path
        }
        if (isImageFromGoogleDrive) {
            try {
                bitmap = BitmapFactory.decodeStream(activity.contentResolver.openInputStream(uri))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            val f = File(strImagePath)
            val bmOptions = BitmapFactory.Options()
            bitmap = BitmapFactory.decodeFile(f.absolutePath, bmOptions)
        }
        return strImagePath
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    fun getMultipleImagePaths(activity: Activity?, data: Intent): ArrayList<Uri>? {
        val mArrayUri = ArrayList<Uri>()
        if (data.clipData != null) {
            val mClipData = data.clipData
            for (i in 0 until mClipData!!.itemCount) {
                val item = mClipData.getItemAt(i)
                val uri = item.uri
                mArrayUri.add(uri)
            }
        }
        return mArrayUri
    }

    fun getImageBitmap(): Bitmap? {
        return bitmap
    }
}