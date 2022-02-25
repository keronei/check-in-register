package com.keronei.keroscheckin.fragments.reports

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import cn.pedant.SweetAlert.SweetAlertDialog
import com.keronei.keroscheckin.R
import com.keronei.keroscheckin.databinding.FragmentReportsOutputSummaryBinding
import com.keronei.keroscheckin.models.AttendeePresentation
import com.keronei.keroscheckin.viewmodels.ReportsViewModel
import com.keronei.utils.ToastUtils
import timber.log.Timber


class ReportsOutputFragment : Fragment() {

    private var _binding: FragmentReportsOutputSummaryBinding? = null

    private var attendanceListData = mutableListOf<AttendeePresentation>()

    private val reportsViewModel: ReportsViewModel by activityViewModels()

    private var summaryText = ""


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val args: ReportsOutputFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentReportsOutputSummaryBinding.inflate(inflater, container, false)

        sliderListener()

        prepareSliderLimitValues()

        attendanceListData = args.attendanceList.toMutableList()

        onclickListeners()


        return binding.root

    }

    private fun onclickListeners() {
        binding.closeWindow.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.imgBtnShareReportSummary.setOnClickListener {
            val shareSummaryIntent = Intent(Intent.ACTION_SEND)
            shareSummaryIntent.type = "text/plain"
            shareSummaryIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.report_summary_heading))
            shareSummaryIntent.putExtra(Intent.EXTRA_TEXT, summaryText)
            startActivity(Intent.createChooser(shareSummaryIntent, getString(R.string.share_via)))

        }

        binding.imgBtnCopyReportSummary.setOnClickListener {
            val clipboard =
                getSystemService(requireContext(), ClipboardManager::class.java) as ClipboardManager

            val clip = ClipData.newPlainText( getString(R.string.report_summary_heading), summaryText)

            clipboard.setPrimaryClip(clip)

            ToastUtils.showShortToastInMiddle(R.string.copied_to_clipboard)
        }


        binding.exportFullReport.setOnClickListener {

            val generatedReportIntent = reportsViewModel.preparedShareReportIntent.value

            SweetAlertDialog(requireContext(), SweetAlertDialog.NORMAL_TYPE)
                .setTitleText(getString(R.string.report_is_ready_header))
                .setContentText(
                    getString(R.string.ready_report_options)
                ).setNeutralButton(getString(R.string.dialog_view_option)) {dialog ->

                    generatedReportIntent.action = Intent.ACTION_VIEW

                    openGeneratedReport(generatedReportIntent)

                    dialog.dismissWithAnimation()

                }.setConfirmButton(getString(R.string.dialog_share_option)) { dialog ->

                    generatedReportIntent.action = Intent.ACTION_SEND

                    startActivity(Intent.createChooser(generatedReportIntent, getString(R.string.share_report_intent_message)))

                    dialog.dismissWithAnimation()
                }
                .show()

        }
    }

    private fun openGeneratedReport(generatedReportIntent: Intent) {
        try {
            startActivity(generatedReportIntent)
        } catch (openingException: Exception) {

            openingException.printStackTrace()

            SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
                .setTitleText(getString(R.string.error_dialog_header))
                .setContentText(
                    getString(R.string.no_app_to_open_report)
                )
                .show()

            Timber.d(getString(R.string.no_app_to_open_report))
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val kidsCeil = binding.childYouthAdultSlider.values[0].toInt()
        val youthCeil = binding.childYouthAdultSlider.values[1].toInt()

        splitToAgeGroups(
            kidsCeil,
            youthCeil
        )

        binding.checkboxMarriedYouth.setOnCheckedChangeListener { _, _ ->
            splitToAgeGroups(
                kidsCeil,
                youthCeil
            )
        }
    }

    private fun sliderListener() {
        binding.childYouthAdultSlider.addOnChangeListener { slider, _, _ ->

            val kidsCeil = slider.values[0].toInt()
            val youthsCeil = slider.values[1].toInt()

            binding.kidsRangeIndicator.text = getString(
                R.string.kids_template,
                slider.valueFrom.toInt(),
                kidsCeil
            )

            binding.youthRangeIndicator.text = getString(
                R.string.youths_template,
                kidsCeil + 1, youthsCeil
            )


            binding.adultRangeIndicator.text = getString(
                R.string.adults_template,
                youthsCeil + 1, slider.valueTo.toInt()
            )

            splitToAgeGroups(kidsCeil, youthsCeil)
        }
    }

    private fun prepareSliderLimitValues() {

        val lowestBirthYearAsOldest =
            attendanceListData.minByOrNull { entry -> entry.age }?.age

        val highestBirthYearAsYoungest =
            attendanceListData.maxByOrNull { entry -> entry.age }?.age

        binding.childYouthAdultSlider.valueTo = lowestBirthYearAsOldest?.toFloat() ?: 100F

        binding.childYouthAdultSlider.valueFrom = highestBirthYearAsYoungest?.toFloat() ?: 0F

        binding.childYouthAdultSlider
    }

    private fun splitToAgeGroups(endOfKidsAge: Int, endOfYouthAge: Int) {
        val (kids, youthsAndAdults) = attendanceListData.partition { attendee ->
            attendee.age < endOfKidsAge

        }

        val (youths, adults) = youthsAndAdults.partition { attendee ->
            attendee.age in endOfKidsAge..endOfYouthAge + 1 && (attendee.isMarried && !binding.checkboxMarriedYouth.isChecked)
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

        summaryText = getString(
            R.string.summary_report_text,
            attendanceListData.size,

            kids.size,
            maleKids.size,
            femaleKids.size,
            otherSexKids.size,

            youths.size,
            maleYouths.size,
            femaleYouths.size,
            otherSexYouths.size,

            adults.size,
            maleAdults.size,
            femaleAdults.size,
            otherSexAdults.size
        )

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