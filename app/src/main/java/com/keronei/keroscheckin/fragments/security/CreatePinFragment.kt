package com.keronei.keroscheckin.fragments.security

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.keronei.keroscheckin.R

class CreatePinFragment : Fragment() {

    companion object {
        fun newInstance() = CreatePinFragment()
    }

    private lateinit var viewModel: CreatePinViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.create_pin_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(CreatePinViewModel::class.java)
        // TODO: Use the ViewModel
    }

}