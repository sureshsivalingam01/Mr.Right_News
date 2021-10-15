package com.mrright.news.utils

import android.app.Activity
import androidx.appcompat.app.AlertDialog
import com.mrright.news.databinding.ProgressDialogBinding


class ProgressDialog {

    private lateinit var bindDialog: ProgressDialogBinding

    private lateinit var dialog: AlertDialog

    /**
     * Instance
     *
     * @param activity
     * @return [AlertDialog]
     */
    fun instance(activity: Activity): AlertDialog {
        return AlertDialog.Builder(activity)
            .apply {
				bindDialog = ProgressDialogBinding.inflate(activity.layoutInflater)
				setView(bindDialog.root)
				setCancelable(false)
			}
			.create()
	}


    /**
     * Set msg
     *
     * @param msg
     */
    fun setMsg(msg: String) {
        bindDialog.textReason.text = msg
    }


    fun show() {
        dialog.show()
    }


}