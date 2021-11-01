
package com.keronei.keroscheckin.fragments.security

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.keronei.keroscheckin.R

class InputPinFragment : Fragment() {

    companion object {
        fun newInstance() = InputPinFragment()
    }

    private lateinit var viewModel: InputPinViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.input_pin_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(InputPinViewModel::class.java)
        // TODO: Use the ViewModel
    }

}