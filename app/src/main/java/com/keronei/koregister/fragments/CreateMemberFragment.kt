package com.keronei.koregister.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.chivorn.smartmaterialspinner.SmartMaterialSpinner
import com.keronei.domain.entities.MemberEntity
import com.keronei.domain.entities.RegionEntity
import com.keronei.kiregister.R
import com.keronei.kiregister.databinding.CreateMemberFragmentBinding
import com.keronei.koregister.models.AttendeePresentation
import com.keronei.koregister.viewmodels.MemberViewModel
import com.keronei.koregister.viewmodels.RegionViewModel
import com.keronei.utils.ToastUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.lang.Exception

@AndroidEntryPoint
class CreateMemberFragment : DialogFragment() {

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

        selectedAttendee = args.selectedMember

        watchRegions()

        prepareSpinner()

        setUpOnClickListeners()

        if(isEditing){
            populateEditFields()
        }

        return layoutBinding.root
    }

    private fun populateEditFields() {

    }

    private fun setUpOnClickListeners() {
        layoutBinding.createMemberButton.setOnClickListener {

            val firstName = layoutBinding.firstNameEdittext.text
            val secondName = layoutBinding.secondNameEdittext.text
            val otherNames = layoutBinding.otherNamesEdittext.text

            val age = layoutBinding.ageEdittext.text
            val phoneNumber = layoutBinding.phoneEdittext.text

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

            if (age?.isEmpty() == true) {
                layoutBinding.ageEdittext.error = "Provider this field!"
                return@setOnClickListener
            }


            lifecycleScope.launch {
                memberViewModel.createNewMember(
                    MemberEntity(
                        0,
                        firstName!!.trim().toString(),
                        secondName!!.trim().toString(),
                        otherNames!!.trim().toString(),
                        age!!.trim().toString().toInt(),
                        phoneNumber?.trim().toString(),
                        true,
                        selectedRegion!!.id
                    )
                )
            }

            ToastUtils.showLongToastInMiddle(R.string.member_created)

            this.dismiss()
        }
    }

    private fun watchRegions() {
        lifecycleScope.launch {
            regionsViewModel.queryAllRegions().collect { collectedRegionsList ->
                regionsList.clear()

                regionsList.addAll(collectedRegionsList)

            }
        }
    }


    private fun prepareSpinner() {

        Toast.makeText(requireContext(), "$regionsList", Toast.LENGTH_SHORT).show()

        regionsSpinner.item = regionsList

        if (isEditing) {

            try {
                val userRegion =
                    regionsList.first { regionEntity -> regionEntity.id == selectedAttendee?.regionId }

                val position = regionsList.indexOf(userRegion)

                regionsSpinner.setSelection(position)

            } catch (exception: Exception) {

            }


        }

        regionsSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                Toast.makeText(
                    requireContext(),
                    "${regionsList[p2].name} selected.",
                    Toast.LENGTH_SHORT
                ).show()

                selectedRegion = regionsList[p2]

                regionsSpinner.errorText = ""
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

        }
    }

    override fun onResume() {
        super.onResume()

        val params = dialog?.window?.attributes
        params?.width = ViewGroup.LayoutParams.MATCH_PARENT

        dialog?.window?.attributes = params
    }

}