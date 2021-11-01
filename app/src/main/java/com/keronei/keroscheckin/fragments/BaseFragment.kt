package com.keronei.keroscheckin.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.keronei.kiregister.R
import com.keronei.kiregister.databinding.BaseFragmentBinding


class BaseFragment : Fragment() {

    private lateinit var baseMembersBinding: BaseFragmentBinding

    private var navController: NavController? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        baseMembersBinding =
            DataBindingUtil.inflate(inflater, R.layout.base_fragment, container, false)

        setUpBottomNav()

        return baseMembersBinding.root
    }

    private fun setUpBottomNav() {
        navController = childFragmentManager.findFragmentById(R.id.nav_host_fragment)
            ?.findNavController()
        navController?.let { navC ->
            baseMembersBinding.bottomNavigation.setupWithNavController(navC)

            baseMembersBinding.bottomNavigation.setOnNavigationItemSelectedListener { item ->
                if (item.itemId != baseMembersBinding.bottomNavigation.selectedItemId)
                    NavigationUI.onNavDestinationSelected(item, navC)
                true
            }
        }
    }


}