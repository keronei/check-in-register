package com.keronei.utils

import android.view.Gravity
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.keronei.keroscheckin.instance.KORegisterApplication

object ToastUtils {
    fun showShortToast(message: String) {
        showToast(message, Toast.LENGTH_SHORT)
    }

    fun showShortToast(messageResource: Int) {
        showToast(messageResource, Toast.LENGTH_SHORT)
    }

    fun showLongToast(message: String) {
        showToast(message, Toast.LENGTH_LONG)
    }

    fun showLongToast(messageResource: Int) {
        showToast(messageResource, Toast.LENGTH_LONG)
    }

    fun showShortToastOnTop(messageResource: Int) {
        showToastOnTop(messageResource, Toast.LENGTH_SHORT)
    }

    fun showLongToastOnTop(messageResource: Int) {
        showToastOnTop(messageResource, Toast.LENGTH_LONG)
    }

    private fun showToast(message: String, duration: Int) {
        Toast.makeText(KORegisterApplication.instance , message, duration).show()
    }

    private fun showToast(messageResource: Int, duration: Int) {
        Toast.makeText(
            KORegisterApplication.instance,
            KORegisterApplication.instance.getString(messageResource),
            duration
        ).show()
    }

    private fun showToastOnTop(messageResource: Int, position: Int) {
        showToastOnTop(KORegisterApplication.instance.getString(messageResource), position)
    }


    fun showShortToastInMiddle(messageResource: Int) {
        showToastInMiddle(
            KORegisterApplication.instance.getString(messageResource),
            Toast.LENGTH_SHORT
        )
    }

    fun showShortToastInMiddle(message: String) {
        showToastInMiddle(message, Toast.LENGTH_SHORT)
    }

    fun showLongToastInMiddle(messageResource: Int) {
        showToastInMiddle(
            KORegisterApplication.instance.getString(messageResource),
            Toast.LENGTH_LONG
        )
    }

    private fun showToastInMiddle(message: String, duration: Int) {
        val toast: Toast = Toast.makeText(KORegisterApplication.instance, message, duration)
        try {
            val group: ViewGroup = toast.getView() as ViewGroup
            val messageTextView = group.getChildAt(0) as TextView
            messageTextView.textSize = 21f
            messageTextView.gravity = Gravity.CENTER
        } catch (ignored: Exception) {
            // ignored
        }
        toast.setGravity(Gravity.CENTER, 0, 0)
        toast.show()
    }

    private fun showToastOnTop(message: String, duration: Int) {
        val toast = Toast.makeText(KORegisterApplication.instance, message, duration)
        toast.setGravity(Gravity.TOP, 0, 0)
        toast.show()

    }


}