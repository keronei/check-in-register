package com.keronei.keroscheckin.fragments.members

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import cn.pedant.SweetAlert.SweetAlertDialog
import com.chivorn.smartmaterialspinner.SmartMaterialSpinner
import com.google.android.material.snackbar.Snackbar
import com.keronei.domain.entities.MemberEntity
import com.keronei.domain.entities.RegionEntity
import com.keronei.keroscheckin.R
import com.keronei.keroscheckin.databinding.FragmentCreateMemberBinding
import com.keronei.keroscheckin.models.AttendeePresentation
import com.keronei.keroscheckin.models.toMemberEntity
import com.keronei.keroscheckin.viewmodels.MemberViewModel
import com.keronei.keroscheckin.viewmodels.RegionViewModel
import com.keronei.utils.ToastUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class CreateMemberFragment : Fragment() {

    lateinit var layoutBinding: FragmentCreateMemberBinding
    lateinit var regionsSpinner: SmartMaterialSpinner<RegionEntity>

    private var selectedRegion: RegionEntity? = null

    private var selectedAttendee: AttendeePresentation? = null

    private var isEditing = false

    private val args: CreateMemberFragmentArgs by navArgs()

    @Inject
    lateinit var coroutineScope: CoroutineScope

    companion object {
        fun newInstance() = CreateMemberFragment()
    }

    private val memberViewModel: MemberViewModel by activityViewModels()

    private val regionsViewModel: RegionViewModel by activityViewModels()

    private val regionsList = mutableListOf<RegionEntity>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        layoutBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_create_member, container, false)

        regionsSpinner =
            layoutBinding.searchRegionSpinner as SmartMaterialSpinner<RegionEntity>

        isEditing = args.isEditing

        configureToolBar()

        selectedAttendee = args.selectedMember


        watchRegions()

        setUpOnClickListeners()

        if (isEditing) {
            populateEditFields()
        }

        watchAgeInput()

        return layoutBinding.root
    }

    private fun watchAgeInput() {
        val currentDate = Calendar.getInstance()

        val currentYear = currentDate.get(Calendar.YEAR)

        with(layoutBinding.ageEdittext) {
            doOnTextChanged { text, _, _, _ ->
                try {
                    if (text.isNullOrEmpty()) {
                        return@doOnTextChanged
                    }
                    val birthYear = text.toString().toInt()
                    val approxAge = currentYear - birthYear
                    if (approxAge > 17) {
                        toggleMaritalStatusSelection(true)
                        layoutBinding.maritalStatus.clearCheck()

                        if (approxAge > 19) {
                            toggleIdNumberVisibility(true)
                        } else {
                            toggleIdNumberVisibility(false)
                        }
                    } else {
                        toggleMaritalStatusSelection(false)
                        toggleIdNumberVisibility(false)
                    }
                } catch (exception: Exception) {
                    ToastUtils.showShortToast(exception.message.toString())
                    exception.printStackTrace()
                }
            }
        }
    }

    private fun toggleIdNumberVisibility(status: Boolean) {
        layoutBinding.identificationNumberEdittext.visibility =
            if (status) View.VISIBLE else View.GONE
        layoutBinding.identificationNumberLayout.visibility =
            if (status) View.VISIBLE else View.GONE
    }

    private fun toggleMaritalStatusSelection(status: Boolean) {
        layoutBinding.maritalStatus.visibility = if (status) View.VISIBLE else View.GONE
    }

    private fun configureToolBar() {
        layoutBinding.createMemberToolBar.setNavigationIcon(R.drawable.ic_navigate_back_24)
        layoutBinding.createMemberToolBar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun populateEditFields() {
        layoutBinding.deleteMemberButton.visibility = View.VISIBLE
        layoutBinding.memberActivityStatus.visibility = View.VISIBLE
        layoutBinding.createMemberButton.text = getString(R.string.update_member_header)
        layoutBinding.createMemberToolBar.title = getString(R.string.update_member_title)

        layoutBinding.firstNameEdittext.setText(selectedAttendee?.firstName)
        layoutBinding.secondNameEdittext.setText(selectedAttendee?.secondName)
        layoutBinding.otherNamesEdittext.setText(selectedAttendee?.otherNames)
        layoutBinding.maleSelector.isChecked = selectedAttendee?.sex == 1
        layoutBinding.femaleSelector.isChecked = selectedAttendee?.sex == 0
        layoutBinding.otherSexSelector.isChecked = selectedAttendee?.sex == 2

        val birthYear = Calendar.getInstance().get(Calendar.YEAR) - (selectedAttendee?.age ?: 0)

        toggleIdNumberVisibility(selectedAttendee?.age ?: 0 > 19)

        layoutBinding.ageEdittext.setText(birthYear.toString())
        layoutBinding.phoneEdittext.setText(selectedAttendee?.phoneNumber)

        layoutBinding.married.isChecked = selectedAttendee?.isMarried == true

        layoutBinding.notMarried.isChecked = selectedAttendee?.isMarried == false

        layoutBinding.identificationNumberEdittext.setText(
            selectedAttendee?.identificationNumber ?: ""
        )

        layoutBinding.memberActivityStatus.isChecked = selectedAttendee?.isActive ?: true

        layoutBinding.memberActivityStatus.textOn = selectedAttendee?.firstName + " is Active."
        layoutBinding.memberActivityStatus.textOff =
            selectedAttendee?.firstName + " is no longer Active."

    }

    private fun setUpOnClickListeners() {
        layoutBinding.createMemberButton.setOnClickListener {

            val firstName = layoutBinding.firstNameEdittext.text
            val secondName = layoutBinding.secondNameEdittext.text
            val otherNames = layoutBinding.otherNamesEdittext.text

            val yob = layoutBinding.ageEdittext.text
            val phoneNumber = layoutBinding.phoneEdittext.text
            val maleSex = layoutBinding.maleSelector
            val femaleSex = layoutBinding.femaleSelector
            val otherSex = layoutBinding.otherSexSelector

            val identificationNumber = layoutBinding.identificationNumberEdittext.text


            if (selectedRegion == null) {
                regionsSpinner.errorText = getString(R.string.select_region_prompt)
                ToastUtils.showLongToastOnTop(R.string.select_region_prompt)
                return@setOnClickListener
            }

            if (firstName?.isEmpty() == true || firstName?.length ?: 0 < 2) {
                layoutBinding.firstNameEdittext.error = getString(R.string.provide_field_prompt)
                return@setOnClickListener
            }

            if (secondName?.isEmpty() == true || secondName?.length ?: 0 < 2) {
                layoutBinding.secondNameEdittext.error = getString(R.string.provide_field_prompt)
                return@setOnClickListener
            }

            if (!(maleSex.isChecked || femaleSex.isChecked || otherSex.isChecked)) {
                ToastUtils.showShortToastInMiddle(getString(R.string.prompt_select_sex_message))
                return@setOnClickListener
            }


            if (yob?.isEmpty() == true) {
                layoutBinding.ageEdittext.error = getString(R.string.provide_field_prompt)
                return@setOnClickListener
            }

            if (yob?.length ?: 0 < 4) {
                layoutBinding.ageEdittext.error = getString(R.string.prompt_input_age)
                return@setOnClickListener
            }

            if (layoutBinding.identificationNumberEdittext.isVisible) {
                if (identificationNumber?.isBlank() == true) {
                    ToastUtils.showLongToastInMiddle(R.string.id_is_required_message)
                    return@setOnClickListener
                }
            }

            if (layoutBinding.maritalStatus.isVisible) {
                if (!layoutBinding.married.isChecked && !layoutBinding.notMarried.isChecked) {
                    ToastUtils.showLongToastInMiddle(R.string.marital_status_constraint)
                    return@setOnClickListener
                }
            }

            lifecycleScope.launch {

                var sexSelection = 1

                when {
                    maleSex.isChecked -> {
                        sexSelection = 1
                    }
                    femaleSex.isChecked -> sexSelection = 0
                    otherSex.isChecked -> sexSelection = 2
                }

                val subjectMember = MemberEntity(
                    if (isEditing) selectedAttendee!!.memberId else 0,
                    firstName!!.trim().toString(),
                    secondName!!.trim().toString(),
                    otherNames!!.trim().toString(),
                    identificationNumber!!.trim().toString(),
                    sexSelection,
                    yob!!.trim().toString().toInt(),
                    layoutBinding.married.isChecked,
                    phoneNumber?.trim().toString(),
                    if (isEditing) layoutBinding.memberActivityStatus.isChecked else true,
                    selectedRegion!!.id
                )

                var createMemberCount = listOf<Long>()

                var updatedMemberCount = 0

                if (isEditing) {
                    updatedMemberCount = memberViewModel.updateMember(
                        subjectMember
                    )
                } else {
                    createMemberCount = memberViewModel.createNewMember(
                        listOf(
                            subjectMember
                        )
                    )
                }

                if (createMemberCount.isNotEmpty() || updatedMemberCount > 0) {
                    ToastUtils.showLongToastInMiddle(if (isEditing) R.string.member_updated else R.string.member_created)
                    findNavController().popBackStack()

                } else {
                    ToastUtils.showLongToast(
                        if (isEditing) getString(
                            R.string.failed_member_update_message,
                            selectedAttendee!!.firstName
                        ) else getString(R.string.failed_create_member_message, firstName.trim())
                    )
                }
            }
        }

        layoutBinding.deleteMemberButton.setOnClickListener {
            SweetAlertDialog(requireContext(), SweetAlertDialog.WARNING_TYPE)
                .setTitleText(getString(R.string.delete_member_dialog_prompt_title))
                .setContentText(
                    "${selectedAttendee?.firstName}'s data will be completely wiped out. \n " +
                            "This is irreversible, are you sure you want to proceed?"
                )
                .setConfirmText(getString(R.string.no_option_dialog))
                .setConfirmClickListener { sDialog ->

                    sDialog.dismissWithAnimation()
                }
                .setCancelButton(
                    getString(R.string.yes_option_dialog)
                ) { sDialog ->

                    lifecycleScope.launch {

                        val deletionCount =
                            memberViewModel.deleteMember(selectedAttendee!!.toMemberEntity())

                        if (deletionCount > 0) {
                            withContext(Dispatchers.Main) {
                                showDeletionSuccess()
                            }
                        } else {
                            ToastUtils.showShortToast("${selectedAttendee!!.firstName} not removed.")
                        }

                    }

                    sDialog.dismissWithAnimation()

                    findNavController().popBackStack()


                }
                .show()
        }
    }

    private fun showDeletionSuccess() {
        val deletionSnack = Snackbar.make(
            layoutBinding.root,
            "${selectedAttendee!!.firstName} removed.",
            Snackbar.LENGTH_LONG
        ).setAction(R.string.undo_deletion) {
            lifecycleScope.launch {
                memberViewModel.createNewMember(listOf(selectedAttendee!!.toMemberEntity()))
            }
        }

        deletionSnack.show()

    }

    private fun watchRegions() {
        lifecycleScope.launch {
            regionsViewModel.queryAllRegions().collect { collectedRegionsList ->
                regionsList.clear()

                regionsList.addAll(collectedRegionsList)

                prepareSpinner()
            }
        }
    }


    private fun prepareSpinner() {

        regionsSpinner.item = regionsList

        if (isEditing) {

            try {
                val userRegion =
                    regionsList.first { regionEntity -> regionEntity.id == selectedAttendee?.regionId }

                val position = regionsList.indexOf(userRegion)

                regionsSpinner.setSelection(position)


            } catch (exception: Exception) {
                exception.printStackTrace()
            }


        }

        regionsSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

                selectedRegion = regionsList[p2]

                regionsSpinner.errorText = ""
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

        }
    }


}