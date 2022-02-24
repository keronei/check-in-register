package com.keronei.keroscheckin

import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import cn.pedant.SweetAlert.SweetAlertDialog
import com.guardanis.applock.AppLock
import com.guardanis.applock.dialogs.LockCreationDialogBuilder
import com.guardanis.applock.dialogs.UnlockDialogBuilder
import com.keronei.data.remote.Constants.IS_FIRST_TIME_KEY
import com.keronei.keroscheckin.databinding.ActivityMainBinding
import com.keronei.utils.ToastUtils
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    private var navController: NavController? = null

    private var unlocker: UnlockDialogBuilder? = null

    private var lockCreator: LockCreationDialogBuilder? = null

    private var dismissedWithAuth = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //determine if it's first time, create pin

        val hasRun = sharedPreferences.getBoolean(IS_FIRST_TIME_KEY, true)

        if (hasRun) {
            createAuth()
        }

        navController =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_main)
                ?.findNavController()


    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(com.keronei.keroscheckin.R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun createAuth() {
        lockCreator = LockCreationDialogBuilder(this)

        lockCreator?.onCanceled {

            onUserCancelAuthCreation()

            dismissedWithAuth = false

        }
        lockCreator?.onLockCreated {

            dismissedWithAuth = true

            val editor = sharedPreferences.edit()
            editor.putBoolean(IS_FIRST_TIME_KEY, false)
            editor.apply()
        }

        lockCreator?.show()
    }

    private fun onUserCancelAuthCreation() {

        val authCreationAlert = SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
        authCreationAlert.titleText = "Data Safety"

        authCreationAlert.contentText =
            "Authentication is required to secure information to be stored in this application."

        authCreationAlert.confirmText = "Create"
        authCreationAlert.setConfirmClickListener { sDialog ->
            sDialog.dismissWithAnimation()

            dismissedWithAuth = true

            createAuth()
        }

        authCreationAlert.setCancelButton(
            "Exit"
        ) { sDialog ->

            sDialog.dismissWithAnimation()

            this.finish()

        }

        authCreationAlert.setOnDismissListener {
            if (!dismissedWithAuth) {
                createAuth()
            }
        }

        authCreationAlert.setCanceledOnTouchOutside(false)
        authCreationAlert.show()

    }

    override fun onPostResume() {
        super.onPostResume()
        AppLock.onActivityResumed(this)
    }


}