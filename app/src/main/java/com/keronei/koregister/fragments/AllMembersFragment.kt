package com.keronei.koregister.fragments

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.keronei.kiregister.R

class AllMembersFragment : Fragment() {

    companion object {
        fun newInstance() = AllMembersFragment()
    }

    private lateinit var viewModel: AllMembersViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.all_members_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(AllMembersViewModel::class.java)
        // TODO: Use the ViewModel
    }

}