package com.example.doorstep.utilities

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Handler
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewTreeLifecycleOwner
import com.example.doorstep.R

class Dialogs(val context: Context, val viewLifecycleOwner: LifecycleOwner) {
    val dialog = Dialog(context)

    fun showLoadingDialog(isLoadingDialogVisible: LiveData<Boolean>): Dialogs{
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.layout_progress)
        dialog.setCancelable(false)
        dialog.show()

        isLoadingDialogVisible.observe(viewLifecycleOwner, Observer {
            if(!it) {
                this.dismissDialog()
            }
        })
        val handler = Handler()
        handler.postDelayed(Runnable{
            this.dismissDialog()
        },R.string.loadingDuarationInMillis.toLong())

        return this
    }

    fun dismissDialog(){
        if(dialog.isShowing)
            dialog.dismiss()
    }
}