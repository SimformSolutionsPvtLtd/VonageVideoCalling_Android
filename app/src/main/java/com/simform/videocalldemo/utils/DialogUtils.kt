package com.simform.videocalldemo.utils

import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog
import com.simform.videocalldemo.R

class DialogUtils {

    companion object {
        fun showDialog(context: Context, message: String, positiveButtonListener: DialogInterface.OnClickListener) {
            AlertDialog.Builder(context).apply {
                setIcon(R.drawable.ic_outline_error_outline_24)
                setMessage(message)
                setCancelable(true)
                setPositiveButton(context.getString(R.string.dialog_ok), positiveButtonListener)
            }.create().apply {
                setTitle(context.getString(R.string.title_error_dialog))
            }.show()
        }
    }
}