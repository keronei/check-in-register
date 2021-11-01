package com.keronei.keroscheckin

import android.content.SharedPreferences
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

        val window = this.window

// clear FLAG_TRANSLUCENT_STATUS flag:

// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

// finally change the color

// finally change the color
        window.statusBarColor = ContextCompat.getColor(this, android.R.color.white)

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

    override fun onResume() {
        super.onResume()

        val isFirstTime = sharedPreferences.getBoolean(IS_FIRST_TIME_KEY, false)

        if (!isFirstTime) {

            initiateAuth()

        }
    }

    private fun initiateAuth() {

        unlocker = UnlockDialogBuilder(this)


        unlocker?.onUnlocked { dismissedWithAuth = true }
        unlocker?.onCanceled { onUserCancelAuth() }
        //val necessary = unlocker?.showIfRequiredOrSuccess(TimeUnit.MINUTES.toMillis(15))

        unlocker?.show()

    }

    private fun onUserCancelAuth() {
        val cancelAuthAlert = SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
        cancelAuthAlert.titleText = "Data Safety"
        cancelAuthAlert.contentText =
            "The data in this application is sensitive and cannot be accessed without authentication."
        cancelAuthAlert.confirmText = "Proceed"
        cancelAuthAlert.setConfirmClickListener { sDialog ->

            sDialog.dismissWithAnimation()
            initiateAuth()

        }
        cancelAuthAlert.setCancelButton(
            "Exit"
        ) { sDialog ->

            sDialog.dismissWithAnimation()

            this.finish()

        }
        cancelAuthAlert.setCanceledOnTouchOutside(false)
        cancelAuthAlert.show()


    }

    private fun createAuth() {
        lockCreator = LockCreationDialogBuilder(this)

        lockCreator?.onCanceled {

            onUserCancelAuthCreation()

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

            createAuth()
        }

        authCreationAlert.setCancelButton(
            "Exit"
        ) { sDialog ->

            sDialog.dismissWithAnimation()

            this.finish()

        }

        authCreationAlert.setCanceledOnTouchOutside(false)
        authCreationAlert.show()

    }

    override fun onPostResume() {
        super.onPostResume()

        AppLock.onActivityResumed(this)
    }

}