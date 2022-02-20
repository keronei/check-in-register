package com.keronei.keroscheckin.fragments.reports

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.slider.LabelFormatter
import com.keronei.domain.entities.AttendanceEntity
import com.keronei.keroscheckin.databinding.FragmentReportsOutputListDialogBinding
import kotlinx.android.synthetic.main.fragment_reports_output_list_dialog.*
import timber.log.Timber
import java.util.*


class ReportsOutputFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentReportsOutputListDialogBinding? = null

    private var attendanceListData = mutableListOf<AttendanceEntity>()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val currentDate = Calendar.getInstance()

    private val currentYear = currentDate.get(Calendar.YEAR)


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentReportsOutputListDialogBinding.inflate(inflater, container, false)

        sliderListener()

        prepareSliderLimitValues()


        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        splitToAgeGroups(
            binding.childYouthAdultSlider.values[0].toInt(),
            binding.childYouthAdultSlider.values[1].toInt()
        )
    }

    private fun sliderListener() {
        binding.childYouthAdultSlider.addOnChangeListener { slider, _, _ ->

            binding.kidsRangeIndicator.text =
                "Kids ${slider.valueFrom.toInt()} - ${slider.values[0].toInt()}"

            binding.youthRangeIndicator.text =
                "Youths ${(slider.values[0] + 1).toInt()} - ${slider.values[1].toInt()}"

            binding.adultRangeIndicator.text =
                "Adults ${(slider.values[1] + 1).toInt()} - ${slider.valueTo.toInt()}"

            splitToAgeGroups(slider.values[0].toInt(), slider.values[1].toInt())
        }
    }

    private fun prepareSliderLimitValues() {

        val lowestBirthYearAsOldest =
            attendanceListData.minByOrNull { entry -> entry.memberEntity.birthYear }?.memberEntity?.birthYear

        val highestBirthYearAsYoungest =
            attendanceListData.maxByOrNull { entry -> entry.memberEntity.birthYear }?.memberEntity?.birthYear

        binding.childYouthAdultSlider.valueTo = if (lowestBirthYearAsOldest != null) {

            (currentYear - lowestBirthYearAsOldest).toFloat()
        } else {
            100F
        }

        binding.childYouthAdultSlider.valueFrom = if (highestBirthYearAsYoungest != null) {
            (currentYear - highestBirthYearAsYoungest).toFloat()
        } else {
            0F
        }

        binding.childYouthAdultSlider
    }

    private fun splitToAgeGroups(endOfKidsAge: Int, endOfYouthAge: Int) {
        val (kids, youthsAndAdults) = attendanceListData.partition { attendee ->
            attendee.memberEntity.birthYear >= (currentYear - endOfKidsAge)

        }

        val (youths, adults) = youthsAndAdults.partition { attendee ->
            attendee.memberEntity.birthYear <= (currentYear - endOfKidsAge) && attendee.memberEntity.birthYear >= (currentYear - endOfYouthAge) && !binding.checkboxMarriedYouth.isChecked//1987,
        }

        //post values to gender partitioner

        splitBySexOrientation(kids, youths, adults)

    }

    private fun splitBySexOrientation(
        kids: List<AttendanceEntity>,
        youths: List<AttendanceEntity>,
        adults: List<AttendanceEntity>
    ) {

        val (femaleKids, remainingKids) = kids.partition { allKids -> allKids.memberEntity.sex == 0 }

        val (maleKids, otherSexKids) = remainingKids.partition { unsorted -> unsorted.memberEntity.sex == 1 }

        val (femaleYouths, remainingYouths) = youths.partition { allYouths -> allYouths.memberEntity.sex == 0 }

        val (maleYouths, otherSexYouths) = remainingYouths.partition { unsorted -> unsorted.memberEntity.sex == 1 }

        val (femaleAdults, remainingAdults) = adults.partition { allAdults -> allAdults.memberEntity.sex == 0 }

        val (maleAdults, otherSexAdults) = remainingAdults.partition { unsorted -> unsorted.memberEntity.sex == 1 }

        val summaryText = "Total ${attendanceListData.size} \n\n" +
                "Kids ${kids.size} \n" +
                "Male kids ${maleKids.size}\n" +
                "Female kids ${femaleKids.size}\n" +
                "Other sex kids ${otherSexKids.size} \n\n" +
                "" +
                "Youths ${youths.size}\n" +
                "Male youths ${maleYouths.size}\n" +
                "Female youths ${femaleYouths.size}\n" +
                "Other sex youths ${otherSexYouths.size}\n\n" +
                "" +
                "Adults ${adults.size}\n" +
                "Male adults ${maleAdults.size}\n" +
                "Female adults ${femaleAdults.size}\n" +
                "Other sex adults ${otherSexAdults.size}"

        text_view_summary.text = summaryText

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "ReportsOutputFragment"
    }

}