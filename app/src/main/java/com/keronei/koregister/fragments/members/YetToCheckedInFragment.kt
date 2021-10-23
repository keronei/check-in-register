package com.keronei.koregister.fragments.members

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.keronei.kiregister.R
import com.keronei.koregister.viewmodels.AllMembersViewModel

class YetToCheckedInFragment : Fragment() {

    companion object {
        fun newInstance() = YetToCheckedInFragment()
    }

    private lateinit var viewModel: AllMembersViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.yet_to_checked_in_fragment, container, false)
    }

}