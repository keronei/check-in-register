package com.keronei.keroscheckin.fragments.reports

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.keronei.keroscheckin.databinding.FragmentReportsOutputListDialogBinding
import com.keronei.keroscheckin.models.AttendeePresentation
import timber.log.Timber
import java.util.*


class ReportsOutputFragment : Fragment() {

    private var _binding: FragmentReportsOutputListDialogBinding? = null

    private var attendanceListData = mutableListOf<AttendeePresentation>()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val currentDate = Calendar.getInstance()

    private val currentYear = currentDate.get(Calendar.YEAR)

    val args : ReportsOutputFragmentArgs by navArgs()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentReportsOutputListDialogBinding.inflate(inflater, container, false)

        sliderListener()

        prepareSliderLimitValues()

        attendanceListData = args.attendanceList.toMutableList()

        closeOutputsWindow()


        return binding.root

    }

    private fun closeOutputsWindow() {
        binding.closeWindow.setOnClickListener {
            findNavController().popBackStack()
        }
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
            attendanceListData.minByOrNull { entry -> entry.age }?.age

        val highestBirthYearAsYoungest =
            attendanceListData.maxByOrNull { entry -> entry.age}?.age

        binding.childYouthAdultSlider.valueTo = lowestBirthYearAsOldest?.toFloat() ?: 100F

        binding.childYouthAdultSlider.valueFrom = highestBirthYearAsYoungest?.toFloat() ?: 0F

        binding.childYouthAdultSlider
    }

    private fun splitToAgeGroups(endOfKidsAge: Int, endOfYouthAge: Int) {
        val (kids, youthsAndAdults) = attendanceListData.partition { attendee ->
            attendee.age <  endOfKidsAge

        }

        val (youths, adults) = youthsAndAdults.partition { attendee ->
            attendee.age in endOfKidsAge..endOfYouthAge+1 && (!attendee.isMarried && binding.checkboxMarriedYouth.isChecked )
        }

        //post values to gender partitioner

        splitBySexOrientation(kids, youths, adults)

    }

    private fun splitBySexOrientation(
        kids: List<AttendeePresentation>,
        youths: List<AttendeePresentation>,
        adults: List<AttendeePresentation>
    ) {

        val (femaleKids, remainingKids) = kids.partition { allKids -> allKids.sex == 0 }

        val (maleKids, otherSexKids) = remainingKids.partition { unsorted -> unsorted.sex == 1 }

        val (femaleYouths, remainingYouths) = youths.partition { allYouths -> allYouths.sex == 0 }

        val (maleYouths, otherSexYouths) = remainingYouths.partition { unsorted -> unsorted.sex == 1 }

        val (femaleAdults, remainingAdults) = adults.partition { allAdults -> allAdults.sex == 0 }

        val (maleAdults, otherSexAdults) = remainingAdults.partition { unsorted -> unsorted.sex == 1 }

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

        binding.textViewSummary.text = summaryText

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "ReportsOutputFragment"
    }

}