package com.keronei.koregister.fragments

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.keronei.kiregister.R

class YetToCheckedInFragment : Fragment() {

    companion object {
        fun newInstance() = YetToCheckedInFragment()
    }

    private lateinit var viewModel: YetToCheckedInViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.yet_to_checked_in_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(YetToCheckedInViewModel::class.java)
        // TODO: Use the ViewModel
    }

}