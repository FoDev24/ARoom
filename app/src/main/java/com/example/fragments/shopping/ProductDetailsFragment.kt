package com.example.fragments.shopping

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.adapters.ColorsAdapter
import com.example.adapters.SizesAdapter
import com.example.adapters.ViewPager2ImagesAdapter
import com.example.aroom.R
import com.example.aroom.databinding.FragmentProductDetailsBinding
import com.example.data.CartProduct
import com.example.util.Resource
import com.example.util.dpToPx
import com.example.util.hideBottomNavigationView
import com.example.viewmodel.shopping.DetailsViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class ProductDetailsFragment : Fragment(R.layout.fragment_product_details) {

    private val args by navArgs<ProductDetailsFragmentArgs>()
    lateinit var binding: FragmentProductDetailsBinding
    private val viewPagerAdapter by lazy{ ViewPager2ImagesAdapter()}
    private val sizesAdapter by lazy { SizesAdapter () }
    private val colorsAdapter by lazy { ColorsAdapter () }
    private var selectedColor: Int? = null
    private var selectedSize: String? = null
    private val viewModel by viewModels<DetailsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        hideBottomNavigationView()

        binding = FragmentProductDetailsBinding.inflate(inflater)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val product = args.product

        setupSizesRv()
        setupColorsRv()
        setupViewPager()

        sizesAdapter.onItemClick = {
            selectedSize = it
        }
        colorsAdapter.onItemClick = {
            selectedColor = it
        }

        binding.apply {
            tvProductName.text = product.name
            tvProductPrice.text = "LE ${product.price}"

            product.offerPercentage?.let {
                val remainingPricePercentage = 1f - it
                val priceAfterOffer = remainingPricePercentage * product.price
                tvProductPrice.text = "LE ${String.format("%.2f",priceAfterOffer)}"
            }

            tvProductDescription.text = product.description

            imgClose.setOnClickListener {
                findNavController().navigateUp()
            }

            arIcon.setOnClickListener {
                if(product.model == null){
                    Toast.makeText(requireContext(), "Sadly,there is no available model yet.", Toast.LENGTH_SHORT).show()
                }else{
                    val b = Bundle().apply { putParcelable("product" , product) }
                    findNavController().navigate(R.id.action_productDetailsFragment_to_vrFragment,b)
                }
            }

            btnAddToCart.setOnClickListener {
                viewModel.addUpdateProductInCart(CartProduct(product,1,selectedColor,selectedSize))
            }


            if(product.colors.isNullOrEmpty()){
                tvProductColor.visibility = View.GONE
            }
            if(product.sizes.isNullOrEmpty()){
                tvProductSize.visibility = View.GONE
            }
        }

        lifecycleScope.launchWhenStarted {

            viewModel.addToCart.collectLatest {
                when(it){
                    is Resource.Loading -> {
                        binding.btnAddToCart.startAnimation()
                }
                    is Resource.Success -> {
                        binding.btnAddToCart.revertAnimation()
                        binding.btnAddToCart.setBackgroundColor(resources.getColor(R.color.black))
                    }
                    is Resource.Error -> {
                        binding.btnAddToCart.stopAnimation()
                        Toast.makeText(requireContext(),it.message,Toast.LENGTH_SHORT).show()

                    }
                    else -> Unit
            }
            }
        }

        viewPagerAdapter.differ.submitList(product.images)

        product.colors?.let{
            colorsAdapter.differ.submitList(it)
        }

        product.sizes?.let{
            sizesAdapter.differ.submitList(product.sizes)
        }


    }

    private fun setupViewPager() {
        binding.apply {
            viewPagerProductImage.adapter = viewPagerAdapter

            TabLayoutMediator(tabLayout, viewPagerProductImage) { tab, position ->
                val customView = ImageView(requireContext()).apply {
                    layoutParams = LinearLayout.LayoutParams(16.dpToPx(), 16.dpToPx()).apply {
                    }
                    setBackgroundResource(R.drawable.tab_indicator)
                }
                tab.customView = customView
            }.attach()

                // Set the selected tab indicator drawable
            tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    tab?.customView?.setBackgroundResource(R.drawable.selected_dot)
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {
                    tab?.customView?.setBackgroundResource(R.drawable.tab_indicator)
                }

                override fun onTabReselected(tab: TabLayout.Tab?) {
                    // Do nothing
                }
            })
        }

    }

    private fun setupColorsRv() {
        binding.rvColors.apply {
           adapter = colorsAdapter
            layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
        }
    }

    private fun setupSizesRv() {
        binding.rvSizes.apply {
            adapter = sizesAdapter
            layoutManager = LinearLayoutManager(requireContext() , LinearLayoutManager.HORIZONTAL,false)
        }
    }


}