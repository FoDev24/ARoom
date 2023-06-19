package com.example.fragments.categories

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.adapters.BestDealsAdapter
import com.example.adapters.BestProductsAdapter
import com.example.adapters.SpecialProductsAdapter
import com.example.aroom.R
import com.example.aroom.databinding.FragmentMainCategoryBinding
import com.example.data.CartProduct
import com.example.util.Resource
import com.example.util.showBottomNavigationView
import com.example.viewmodel.shopping.DetailsViewModel
import com.example.viewmodel.shopping.MainCategoryViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

private val TAG = "MainCategoryFragment"
@AndroidEntryPoint
class MainCategoryFragment : Fragment(R.layout.fragment_main_category) {
    private lateinit var binding : FragmentMainCategoryBinding
    private lateinit var specialProductAdapter : SpecialProductsAdapter
    private lateinit var bestDealsProductAdapter: BestDealsAdapter
    private lateinit var bestProductAdapter : BestProductsAdapter
    private val viewModel by viewModels<MainCategoryViewModel>()
    private val cartViewModel by viewModels<DetailsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainCategoryBinding.inflate(inflater)
        return binding.root
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSpecialProductRv()
        setupBestDealsProductRv()
        setupBestProductRv()

        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.fetchSpecialProducts()
            viewModel.fetchBestDealsProducts()
            viewModel.fetchBestProducts()
        }

        specialProductAdapter.onClick = {
            val b = Bundle().apply { putParcelable("product" , it) }
            findNavController().navigate(R.id.action_homeFragment_to_productDetailsFragment,b)
        }
        specialProductAdapter.onBtnClick = {

            val b = Bundle().apply { putParcelable("product" , it) }
            cartViewModel.addUpdateProductInCart(CartProduct(it,1))
            Toast.makeText(requireContext(),"Added to cart",Toast.LENGTH_SHORT).show()
        }

        bestProductAdapter.onClick = {
            val b = Bundle().apply { putParcelable("product" , it) }
            findNavController().navigate(R.id.action_homeFragment_to_productDetailsFragment,b)
        }


        bestDealsProductAdapter.onBtnClick ={
            val b = Bundle().apply { putParcelable("product",it) }
            findNavController().navigate(R.id.action_homeFragment_to_productDetailsFragment,b)
        }

        bestDealsProductAdapter.onClick = {
            val b = Bundle().apply { putParcelable("product" , it) }
            findNavController().navigate(R.id.action_homeFragment_to_productDetailsFragment,b)
        }

        lifecycleScope.launchWhenStarted {
            viewModel.specialProducts.collectLatest {
                when(it){
                    is Resource.Loading -> {
                        showLoading()
                    }
                    is Resource.Success -> {
                        specialProductAdapter.differ.submitList(it.data)
                        hideLoading()
                        binding.swipeRefreshLayout.isRefreshing = false
                    }
                    is Resource.Error -> {
                        hideLoading()
                        Log.e(TAG,it.message.toString())
                        Toast.makeText(requireContext(),it.message,Toast.LENGTH_SHORT).show()

                    }
                    else -> Unit
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.bestProducts.collectLatest {
                when(it){
                    is Resource.Loading -> {
                        binding.bestProductsProgressBar.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        bestProductAdapter.differ.submitList(it.data)
                        binding.bestProductsProgressBar.visibility = View.GONE
                        binding.swipeRefreshLayout.isRefreshing = false
                    }
                    is Resource.Error -> {

                        Log.e(TAG,it.message.toString())
                        Toast.makeText(requireContext(),it.message,Toast.LENGTH_SHORT).show()
                        binding.bestProductsProgressBar.visibility = View.GONE

                    }
                    else -> Unit
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.bestDealsProducts.collectLatest {
                when(it){
                    is Resource.Loading -> {
                        showLoading()
                    }
                    is Resource.Success -> {
                        bestDealsProductAdapter.differ.submitList(it.data)
                        hideLoading()
                        binding.swipeRefreshLayout.isRefreshing = false
                    }
                    is Resource.Error -> {
                        hideLoading()
                        Log.e(TAG,it.message.toString())
                        Toast.makeText(requireContext(),it.message,Toast.LENGTH_SHORT).show()

                    }
                    else -> Unit
                }
            }
        }

        binding.nestedScrollMainCategory.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener{ v,_,scrollY,_,_ ->
            if(v.getChildAt(0).bottom <= v.height + scrollY){
                viewModel.fetchBestProducts()
            }

        })
    }

    private fun setupBestProductRv() {
        bestProductAdapter = BestProductsAdapter()
        binding.rvBestProducts.apply {
            layoutManager = GridLayoutManager(requireContext(),2,GridLayoutManager.VERTICAL,false   )
            adapter = bestProductAdapter
        }
    }

    private fun setupBestDealsProductRv() {
        bestDealsProductAdapter = BestDealsAdapter()
        binding.rvBestDealsProducts.apply {
            layoutManager = LinearLayoutManager(requireContext() , LinearLayoutManager.HORIZONTAL,false)
            adapter = bestDealsProductAdapter
        }
    }

    private fun showLoading() {
        binding.mainCategoryProgressBar.visibility = View.VISIBLE
    }
    private fun hideLoading() {
        binding.mainCategoryProgressBar.visibility = View.GONE
    }

    private fun setupSpecialProductRv() {
        specialProductAdapter = SpecialProductsAdapter()
        binding.rvSpecialProducts.apply {
            layoutManager = LinearLayoutManager(requireContext() , LinearLayoutManager.HORIZONTAL,false)
            adapter = specialProductAdapter
        }
    }

    override fun onResume() {
        super.onResume()

        showBottomNavigationView()
    }
}