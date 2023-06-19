package com.example.fragments.shopping

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.adapters.CartProductAdapter
import com.example.aroom.R
import com.example.aroom.databinding.FragmentCartBinding
import com.example.firebase.FirebaseCommon
import com.example.util.Resource
import com.example.util.VerticalItemDecoration
import com.example.viewmodel.shopping.CartViewModel
import kotlinx.coroutines.flow.collectLatest

class CartFragment :Fragment(R.layout.fragment_cart) {
    lateinit var binding : FragmentCartBinding
    private val cartAdapter by lazy {CartProductAdapter()}
    private val viewModel by activityViewModels<CartViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCartBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupCartRv()
        var totalPrice =0f


        lifecycleScope.launchWhenStarted {
            viewModel.productPrice.collectLatest { price->
            price?.let {
                totalPrice = it
                binding.tvTotalPrice.text = "LE ${price}"
            }

            }
        }

        cartAdapter.onProductClick ={
            val b = Bundle().apply { putParcelable("product",it.product) }
            findNavController().navigate(R.id.action_cartFragment_to_productDetailsFragment,b)
        }

        cartAdapter.onPlusClick ={
            viewModel.changeQuantity(it,FirebaseCommon.QuantityChanging.INCREASE)
        }

        cartAdapter.onMinusClick ={
            viewModel.changeQuantity(it,FirebaseCommon.QuantityChanging.DECREASE)
        }

        lifecycleScope.launchWhenStarted {
            viewModel.deleteDialog.collectLatest {
                val alertDialog = AlertDialog.Builder(requireContext()).apply {
                    setTitle("Delete item from cart")
                    setMessage("Do you want to delete this item from your cart?")
                    setNegativeButton("Cancel"){ dialog,_ ->
                        dialog.dismiss()
                        dialog.cancel()
                    }
                    setPositiveButton("Yes"){ dialog,_ ->
                       viewModel.deleteCartProduct(it)
                    }
                }
                alertDialog.create()
                alertDialog.show()
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.cartProducts.collectLatest {
                when(it){
                    is Resource.Loading ->{
                      showProgressBar()
                    }
                    is Resource.Success ->{
                        hideProgressBar()
                        if(it.data!!.isEmpty()){
                            showEmptyCart()
                            hideOtherViews()
                        } else {
                            hideEmptyCart()
                            showOtherViews()
                            cartAdapter.differ.submitList(it.data)
                        }
                    }
                    is Resource.Error ->{
                        hideProgressBar()
                        Toast.makeText(requireContext() , it.message , Toast.LENGTH_SHORT)
                    }
                    else -> Unit
                }
            }
        }

        binding.apply {
            btnCheckoutCart.setOnClickListener{
               val action = CartFragmentDirections.actionCartFragmentToBillingFragment(totalPrice,cartAdapter.differ.currentList.toTypedArray(),true)
                findNavController().navigate(action)
            }
        }
    }

    private fun hideOtherViews() {
        binding.apply {
            rvCart.visibility = View.GONE
            toolbarCart.visibility = View.GONE
            btnCheckoutCart.visibility = View.GONE
            totalBoxContainer.visibility = View.GONE
        }
    }

    private fun showOtherViews() {
        binding.apply {
            rvCart.visibility = View.VISIBLE
            toolbarCart.visibility = View.VISIBLE
            btnCheckoutCart.visibility = View.VISIBLE
            totalBoxContainer.visibility = View.VISIBLE
        }
    }

    private fun hideEmptyCart() {
        binding.apply {
            layoutEmptyCart.visibility = View.GONE
        }
    }

    private fun showEmptyCart() {
        binding.apply {
            layoutEmptyCart.visibility = View.VISIBLE
        }
    }

    private fun hideProgressBar() {
        binding.progressBarCart.visibility = View.INVISIBLE
    }

    private fun showProgressBar() {
        binding.progressBarCart.visibility = View.VISIBLE
    }

    private fun setupCartRv() {
        binding.rvCart.apply {
            layoutManager = LinearLayoutManager(requireContext(),RecyclerView.VERTICAL , false)
            adapter = cartAdapter
            addItemDecoration(VerticalItemDecoration())
        }
    }
}