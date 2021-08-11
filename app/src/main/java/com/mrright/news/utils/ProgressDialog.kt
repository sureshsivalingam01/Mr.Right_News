package com.mrright.news.utils

import android.app.Activity
import androidx.appcompat.app.AlertDialog
import com.mrright.news.databinding.ProgressDialogBinding


class ProgressDialog {


    private lateinit var bindDialog: ProgressDialogBinding


    fun setMsg(msg: String) {
        bindDialog.textReason.text = msg
    }

    /**
     * Show progress
     *
     * @param activity
     */
    fun instance(activity: Activity): AlertDialog {
        return AlertDialog.Builder(activity).apply {
            bindDialog = ProgressDialogBinding.inflate(activity.layoutInflater)
            setView(bindDialog.root)
            setCancelable(false)
        }.create()
    }

}