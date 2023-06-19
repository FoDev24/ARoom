package com.example.fragments.shopping

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.util.Resource

import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.aroom.databinding.FragmentAddressBinding
import com.example.data.Address
import com.example.viewmodel.shopping.AddressViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest


@AndroidEntryPoint
class AddressFragment() : Fragment() {
    lateinit var binding:FragmentAddressBinding
    val viewModel by viewModels<AddressViewModel>()
    val args by navArgs<AddressFragmentArgs>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launchWhenStarted {
            viewModel.ddNewAddress.collectLatest {
                when(it){
                    is Resource.Loading -> {
                        showPrgressBar()
                    }
                    is Resource.Success -> {
                        hidePrgressBar()
                        findNavController().navigateUp()
                    }
                    is Resource.Error -> {
                        hidePrgressBar()
                        Toast.makeText(requireContext(),it.message,Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.error.collectLatest {
                Toast.makeText(requireContext(),it,Toast.LENGTH_SHORT).show() }
        }
    }

    private fun showPrgressBar() {
        binding.apply {
            progressAddress.visibility = View.VISIBLE
        }
    }

    private fun hidePrgressBar() {
        binding.apply {
            progressAddress.visibility = View.GONE
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddressBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val address = args.address

        if(address == null){
            binding.btnDelete.visibility = View.GONE
        }else{
            binding.apply {
                edAddressTitle.setText(address.addressTitle)
                edFullName.setText(address.fullName)
                edState.setText(address.state)
                edPhone.setText(address.phoneNumb)
                edCity.setText(address.city)
                edStreet.setText(address.street)
            }
        }

        binding.apply {
            btnSave.setOnClickListener{
                val addressFullName = edFullName.text.toString()
                val state = edState.text.toString()
                val phone = edPhone.text.toString()
                val city = edCity.text.toString()
                val street = edStreet.text.toString()
                val title = edAddressTitle.text.toString()

                val address = Address(title,addressFullName,street,phone,city,state)
                viewModel.addAddress(address)
            }
            imgAddressClose.setOnClickListener {
                findNavController().navigateUp()
            }
        }
    }
}