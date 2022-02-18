package com.keronei.keroscheckin.fragments.members

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import cn.pedant.SweetAlert.SweetAlertDialog
import com.chivorn.smartmaterialspinner.SmartMaterialSpinner
import com.keronei.domain.entities.MemberEntity
import com.keronei.domain.entities.RegionEntity
import com.keronei.keroscheckin.R
import com.keronei.keroscheckin.databinding.CreateMemberFragmentBinding
import com.keronei.keroscheckin.models.AttendeePresentation
import com.keronei.keroscheckin.models.toMemberEntity
import com.keronei.keroscheckin.viewmodels.MemberViewModel
import com.keronei.keroscheckin.viewmodels.RegionViewModel
import com.keronei.utils.ToastUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.create_member_fragment.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CreateMemberFragment : Fragment() {

    lateinit var layoutBinding: CreateMemberFragmentBinding
    lateinit var regionsSpinner: SmartMaterialSpinner<RegionEntity>

    private var selectedRegion: RegionEntity? = null

    private var selectedAttendee: AttendeePresentation? = null

    private var isEditing = false

    private val args: CreateMemberFragmentArgs by navArgs()

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
            DataBindingUtil.inflate(inflater, R.layout.create_member_fragment, container, false)

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

        return layoutBinding.root
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
        layoutBinding.createMemberButton.text = "Update"
        layoutBinding.createMemberToolBar.title = "Update Member Info"

        layoutBinding.firstNameEdittext.setText(selectedAttendee?.firstName)
        layoutBinding.secondNameEdittext.setText(selectedAttendee?.secondName)
        layoutBinding.otherNamesEdittext.setText(selectedAttendee?.otherNames)
        layoutBinding.maleSelector.isChecked = selectedAttendee?.sex == 1
        layoutBinding.femaleSelector.isChecked = selectedAttendee?.sex == 0
        layoutBinding.otherSexSelector.isChecked = selectedAttendee?.sex == 2
        layoutBinding.ageEdittext.setText(selectedAttendee?.age.toString())
        layoutBinding.phoneEdittext.setText(selectedAttendee?.phoneNumber)

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


            if (selectedRegion == null) {
                regionsSpinner.errorText = "Please select region!"
                return@setOnClickListener
            }

            if (firstName?.isEmpty() == true || firstName?.length ?: 0 < 2) {
                layoutBinding.firstNameEdittext.error = "Provider this field!"
                return@setOnClickListener
            }

            if (secondName?.isEmpty() == true || secondName?.length ?: 0 < 2) {
                layoutBinding.secondNameEdittext.error = "Provider this field!"
                return@setOnClickListener
            }

            if (!(maleSex.isChecked || femaleSex.isChecked || otherSex.isChecked)) {
                ToastUtils.showShortToastInMiddle("Select sex identity")
                return@setOnClickListener
            }


            if (yob?.isEmpty() == true) {
                layoutBinding.ageEdittext.error = "Provider this field!"
                return@setOnClickListener
            }

            if (yob?.length ?: 0 < 4) {
                layoutBinding.ageEdittext.error = "Needs 4 digits eg. 1999."
                return@setOnClickListener
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
                    sexSelection,
                    yob!!.trim().toString().toInt(),
                    layoutBinding.married.isChecked,
                    phoneNumber?.trim().toString(),
                    if (isEditing) layoutBinding.memberActivityStatus.isChecked else true,
                    selectedRegion!!.id
                )

                if (isEditing) {
                    memberViewModel.updateMember(
                        subjectMember
                    )
                } else {
                    memberViewModel.createNewMember(
                        listOf(
                            subjectMember
                        )
                    )
                }
            }

            ToastUtils.showLongToastInMiddle(if (isEditing) R.string.member_updated else R.string.member_created)

            //this.dismiss()

            findNavController().popBackStack()
        }

        layoutBinding.deleteMemberButton.setOnClickListener {
            SweetAlertDialog(requireContext(), SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Completely Delete")
                .setContentText(
                    "${selectedAttendee?.firstName}'s data will be completely wiped out. \n " +
                            "This is irreversible, are you sure you want to proceed?"
                )
                .setConfirmText("No")
                .setConfirmClickListener { sDialog ->

                    sDialog.dismissWithAnimation()
                }
                .setCancelButton(
                    "Yes"
                ) { sDialog ->

                    memberViewModel.deleteMember(selectedAttendee!!.toMemberEntity())

                    sDialog.dismissWithAnimation()

                    //this.dismiss()

                    childFragmentManager.popBackStack()


                    showDeletionSuccess()
                }
                .show()
        }
    }

    private fun showDeletionSuccess() {
        SweetAlertDialog(
            requireContext(), SweetAlertDialog.SUCCESS_TYPE
        )
            .setTitleText("Deleted")
            .setContentText("${selectedAttendee!!.firstName} removed completely!")
            .show()
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