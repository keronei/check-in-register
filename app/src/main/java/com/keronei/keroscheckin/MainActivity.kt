package com.keronei.keroscheckin

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import cn.pedant.SweetAlert.SweetAlertDialog
import com.guardanis.applock.AppLock
import com.guardanis.applock.dialogs.LockCreationDialogBuilder
import com.keronei.android.common.Constants.ACCEPTED_IMPORT_FILE_TYPE
import com.keronei.data.remote.Constants.IS_FIRST_TIME_KEY
import com.keronei.keroscheckin.databinding.ActivityMainBinding
import com.keronei.keroscheckin.fragments.importexport.ImportExportSheet
import com.keronei.keroscheckin.viewmodels.ImportExportViewModel
import com.keronei.keroscheckin.viewmodels.MemberViewModel
import com.keronei.utils.ToastUtils
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    private var navController: NavController? = null

    private var lockCreator: LockCreationDialogBuilder? = null

    private var dismissedWithAuth = false

    private val importExportViewModel: ImportExportViewModel by viewModels()

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

        handleOpenedFileIfFromOpenWith()
    }


    private fun handleOpenedFileIfFromOpenWith() {
        val launcherIntentAction = intent.action

        val launcherIntentType = intent.type

        if (Intent.ACTION_VIEW == launcherIntentAction && launcherIntentType != null) {
            if (launcherIntentType == ACCEPTED_IMPORT_FILE_TYPE) {
                startImportAttempt()
            } else {
                ToastUtils.showLongToast(getString(R.string.bad_intent))
            }
        }
    }

    private fun startImportAttempt() {
        val data = intent.data

        try {
            val inputStream = this.contentResolver.openInputStream(data!!)

            importExportViewModel.launchedIntentInputStream.value = inputStream

        } catch (exception: Exception) {
            Timber.log(
                Log.ERROR,
                "User attempted to open import file directly from files.",
                exception
            )
            SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                .setContentText(getString(R.string.exception_message_in_open_with))
                .setTitleText(getString(R.string.import_failed_title))
                .show()

            exception.printStackTrace()
        }
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
        authCreationAlert.titleText = getString(R.string.enforce_auth_creation_header)

        authCreationAlert.contentText =
            getString(R.string.auth_creation_prompt)

        authCreationAlert.confirmText = getString(R.string.create_code_btn_text)
        authCreationAlert.setConfirmClickListener { sDialog ->
            sDialog.dismissWithAnimation()

            dismissedWithAuth = true

            createAuth()
        }

        authCreationAlert.setCancelButton(
            getString(R.string.exit_app_option)
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