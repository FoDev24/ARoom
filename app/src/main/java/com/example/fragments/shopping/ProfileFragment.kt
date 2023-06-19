package com.example.fragments.shopping

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.activities.LoginRegisterActivity
import com.example.aroom.BuildConfig
import com.example.aroom.R
import com.example.aroom.databinding.FragmentProfileBinding
import com.example.util.Resource
import com.example.util.showBottomNavigationView
import com.example.viewmodel.shopping.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class ProfileFragment :Fragment(R.layout.fragment_profile) {
    lateinit var binding : FragmentProfileBinding
    val viewModel by viewModels<ProfileViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.apply {
            constraintProfile.setOnClickListener {
                findNavController().navigate(R.id.action_profileFragment_to_userAccountFragment)
            }
            linearAllOrders.setOnClickListener {
                findNavController().navigate(R.id.action_profileFragment_to_ordersFragment)
            }
            linearBilling.setOnClickListener {
                val action = ProfileFragmentDirections.actionProfileFragmentToBillingFragment(0f,
                    emptyArray(),false
                )
                findNavController().navigate(action)
            }

            linearLogOut.setOnClickListener {
                viewModel.signOut()
                val intent = Intent(requireActivity(), LoginRegisterActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
                Toast.makeText(requireContext() , "Logged out" , Toast.LENGTH_SHORT)
            }

            tvVersion.text = "Version ${BuildConfig.VERSION_CODE}"
        }


        lifecycleScope.launchWhenStarted {
            viewModel.user.collectLatest {
                when(it){
                    is Resource.Loading ->{
                        showProgressBar()

                    }
                    is Resource.Success ->{
                        hideProgressBar()
                        val user = it.data
                        binding.tvUserName.text = "${it.data!!.firstName} ${it.data.lastName}"
                        Glide.with(requireContext()).load(user!!.imagePath).error(ColorDrawable(
                            Color.BLACK)).into(binding.imageUser)
                    }
                    is Resource.Error ->{
                        hideProgressBar()
                        Toast.makeText(requireContext() , it.message , Toast.LENGTH_SHORT)
                    }
                    else -> Unit
                }
            }
        }

    }

    private fun hideProgressBar() {
        binding.progressbarSettings.visibility = View.GONE
    }

    private fun showProgressBar() {
        binding.progressbarSettings.visibility = View.VISIBLE
    }

    override fun onResume() {
        super.onResume()
        showBottomNavigationView()
    }
}